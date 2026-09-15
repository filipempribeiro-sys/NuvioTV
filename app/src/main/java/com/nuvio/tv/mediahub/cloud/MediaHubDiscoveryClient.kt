package com.nuvio.tv.mediahub.cloud

import com.nuvio.tv.mediahub.MediaHubRuntimeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Serializable
data class MediaHubDiscoveryDocument(
    @SerialName("apiBaseUrl") val apiBaseUrl: String = "",
    @SerialName("pairingWebUrl") val pairingWebUrl: String = "",
    @SerialName("capabilities") val capabilities: List<String> = emptyList(),
    @SerialName("version") val version: String = "1"
) {
    val usable: Boolean
        get() = apiBaseUrl.startsWith("https://") ||
            (MediaHubRuntimeConfig.cloudConfigured && apiBaseUrl.startsWith("http://"))
}

sealed interface MediaHubDiscoveryResult {
    data class Available(val document: MediaHubDiscoveryDocument) : MediaHubDiscoveryResult
    data class Unavailable(val reason: String) : MediaHubDiscoveryResult
}

/**
 * Discovers MEDIA•HUB public cloud configuration without embedding service or
 * database secrets in the APK. Cloud failure must not disable local media use.
 */
class MediaHubDiscoveryClient(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(8, TimeUnit.SECONDS)
        .build(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    suspend fun discover(): MediaHubDiscoveryResult = withContext(Dispatchers.IO) {
        val url = MediaHubRuntimeConfig.discoveryUrl
        if (!MediaHubRuntimeConfig.cloudConfigured || url.isBlank()) {
            return@withContext MediaHubDiscoveryResult.Unavailable("MEDIA•HUB Cloud is not configured")
        }

        runCatching {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("X-MediaHub-Client", "android-tv")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@use MediaHubDiscoveryResult.Unavailable("Discovery HTTP ${response.code}")
                }

                val body = response.body?.string().orEmpty()
                if (body.isBlank()) {
                    return@use MediaHubDiscoveryResult.Unavailable("Empty discovery response")
                }

                val document = json.decodeFromString<MediaHubDiscoveryDocument>(body)
                if (!document.usable) {
                    MediaHubDiscoveryResult.Unavailable("Discovery returned an invalid API URL")
                } else {
                    MediaHubDiscoveryResult.Available(document)
                }
            }
        }.getOrElse { error ->
            MediaHubDiscoveryResult.Unavailable(error.message ?: "Discovery request failed")
        }
    }
}
