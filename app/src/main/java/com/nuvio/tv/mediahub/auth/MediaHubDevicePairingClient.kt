package com.nuvio.tv.mediahub.auth

import com.nuvio.tv.mediahub.cloud.MediaHubDiscoveryClient
import com.nuvio.tv.mediahub.cloud.MediaHubDiscoveryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Serializable
data class MediaHubPairingStartRequest(
    val deviceId: String,
    val deviceName: String,
    val platform: String = "android-tv"
)

@Serializable
data class MediaHubPairingChallenge(
    val pairingId: String,
    val deviceSecret: String,
    val userCode: String,
    val approvalUrl: String,
    val qrPayload: String,
    val expiresAt: String,
    val pollIntervalSeconds: Int = 3
)

@Serializable
data class MediaHubDevice(
    val id: String = "",
    val name: String = "",
    val platform: String = "android-tv"
)

@Serializable
data class MediaHubSession(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 3600
)

@Serializable
data class MediaHubPairingPollResponse(
    val status: String,
    val expiresAt: String? = null,
    val session: MediaHubSession? = null,
    val accountId: String? = null,
    val device: MediaHubDevice? = null
)

sealed interface MediaHubPairingPollResult {
    data class Pending(val expiresAt: String?) : MediaHubPairingPollResult
    data class Approved(
        val session: MediaHubSession,
        val accountId: String,
        val device: MediaHubDevice?
    ) : MediaHubPairingPollResult
    data object Expired : MediaHubPairingPollResult
}

/**
 * MEDIA•HUB-owned QR/device pairing transport.
 *
 * This intentionally does not depend on Supabase or the inherited Nuvio auth
 * endpoints. The device secret authenticates only the short-lived pairing
 * challenge; the access token is returned only after approval.
 */
class MediaHubDevicePairingClient(
    private val discoveryClient: MediaHubDiscoveryClient = MediaHubDiscoveryClient(),
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .build(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun start(
        deviceId: String,
        deviceName: String
    ): Result<MediaHubPairingChallenge> = withContext(Dispatchers.IO) {
        runCatching {
            val discovery = requireDiscovery()
            require(discovery.capabilities.qrPairing) { "MEDIA•HUB Cloud does not advertise QR pairing" }

            val payload = json.encodeToString(
                MediaHubPairingStartRequest(
                    deviceId = deviceId,
                    deviceName = deviceName
                )
            )
            val request = Request.Builder()
                .url(discovery.apiBaseUrl.trimEnd('/') + "/device-pairing")
                .header("Accept", "application/json")
                .header("X-MediaHub-Client", "android-tv")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            httpClient.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                check(response.isSuccessful) { "Pairing start HTTP ${response.code}" }
                check(body.isNotBlank()) { "Empty pairing start response" }
                json.decodeFromString<MediaHubPairingChallenge>(body)
            }
        }
    }

    suspend fun poll(
        challenge: MediaHubPairingChallenge
    ): Result<MediaHubPairingPollResult> = withContext(Dispatchers.IO) {
        runCatching {
            val discovery = requireDiscovery()
            val request = Request.Builder()
                .url(discovery.apiBaseUrl.trimEnd('/') + "/device-pairing/" + challenge.pairingId)
                .header("Accept", "application/json")
                .header("X-MediaHub-Client", "android-tv")
                .header("X-MediaHub-Device-Secret", challenge.deviceSecret)
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.code == 404) return@use MediaHubPairingPollResult.Expired
                val body = response.body?.string().orEmpty()
                check(response.isSuccessful) { "Pairing poll HTTP ${response.code}" }
                check(body.isNotBlank()) { "Empty pairing poll response" }
                val result = json.decodeFromString<MediaHubPairingPollResponse>(body)
                when (result.status.lowercase()) {
                    "pending" -> MediaHubPairingPollResult.Pending(result.expiresAt)
                    "approved" -> {
                        val session = requireNotNull(result.session) { "Approved pairing has no session" }
                        val accountId = requireNotNull(result.accountId) { "Approved pairing has no account" }
                        MediaHubPairingPollResult.Approved(session, accountId, result.device)
                    }
                    "expired", "expired_or_unknown" -> MediaHubPairingPollResult.Expired
                    else -> error("Unknown pairing status: ${result.status}")
                }
            }
        }
    }

    private suspend fun requireDiscovery() = when (val result = discoveryClient.discover()) {
        is MediaHubDiscoveryResult.Available -> result.document
        is MediaHubDiscoveryResult.Unavailable -> error(result.reason)
    }
}
