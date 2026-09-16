package com.nuvio.tv.mediahub.cloud

import android.content.Context
import com.nuvio.tv.mediahub.auth.MediaHubSessionStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Serializable
data class MediaHubAccountSnapshot(
    val accountId: String,
    val device: MediaHubCloudDevice? = null
)

@Serializable
data class MediaHubCloudDevice(
    val id: String = "",
    val name: String = "",
    val platform: String = "android-tv"
)

@Serializable
data class MediaHubProfilesEnvelope(
    val profiles: List<JsonObject> = emptyList()
)

@Serializable
data class MediaHubSyncSnapshot(
    val library: JsonElement? = null,
    val watchProgress: JsonElement? = null,
    val favourites: JsonElement? = null,
    val settings: JsonElement? = null,
    val addons: JsonElement? = null
)

/**
 * Authenticated MEDIA•HUB Cloud transport boundary.
 *
 * This client is intentionally independent from Supabase/Postgrest and from the
 * inherited Nuvio SyncRepository. It is the transport that the profile and sync
 * migrations can move onto one feature at a time without changing their local
 * persistence models in the same commit.
 */
@Singleton
class MediaHubCloudApiClient @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context.applicationContext
    private val sessionStore = MediaHubSessionStore(appContext)
    private val discoveryClient = MediaHubDiscoveryClient()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(12, TimeUnit.SECONDS)
        .build()
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun me(): Result<MediaHubAccountSnapshot> = request(
        method = "GET",
        path = "/me"
    ) { body -> json.decodeFromString<MediaHubAccountSnapshot>(body) }

    suspend fun getProfiles(): Result<MediaHubProfilesEnvelope> = request(
        method = "GET",
        path = "/profiles"
    ) { body -> json.decodeFromString<MediaHubProfilesEnvelope>(body) }

    suspend fun putProfiles(profiles: List<JsonObject>): Result<MediaHubProfilesEnvelope> = request(
        method = "PUT",
        path = "/profiles",
        payload = json.encodeToString(MediaHubProfilesEnvelope(profiles))
    ) { body -> json.decodeFromString<MediaHubProfilesEnvelope>(body) }

    suspend fun getSync(): Result<MediaHubSyncSnapshot> = request(
        method = "GET",
        path = "/sync"
    ) { body -> json.decodeFromString<MediaHubSyncSnapshot>(body) }

    suspend fun putSync(snapshot: MediaHubSyncSnapshot): Result<MediaHubSyncSnapshot> = request(
        method = "PUT",
        path = "/sync",
        payload = json.encodeToString(snapshot)
    ) { body -> json.decodeFromString<MediaHubSyncSnapshot>(body) }

    private suspend fun <T> request(
        method: String,
        path: String,
        payload: String? = null,
        decode: (String) -> T
    ): Result<T> = withContext(Dispatchers.IO) {
        runCatching {
            val session = sessionStore.read()?.takeUnless { it.isExpired }
                ?: error("MEDIA•HUB session is missing or expired")
            val discovery = when (val result = discoveryClient.discover()) {
                is MediaHubDiscoveryResult.Available -> result.document
                is MediaHubDiscoveryResult.Unavailable -> error(result.reason)
            }
            val url = discovery.apiBaseUrl.trimEnd('/') + "/" + path.trimStart('/')
            val builder = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", "${session.tokenType} ${session.accessToken}")
                .header("X-MediaHub-Client", "android-tv")

            when (method) {
                "GET" -> builder.get()
                "PUT" -> builder.put(requireNotNull(payload).toRequestBody(jsonMediaType))
                else -> error("Unsupported MEDIA•HUB HTTP method: $method")
            }

            httpClient.newCall(builder.build()).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.code == 401) {
                    sessionStore.clear()
                    error("MEDIA•HUB session is no longer authorized")
                }
                check(response.isSuccessful) { "MEDIA•HUB $method $path HTTP ${response.code}" }
                check(body.isNotBlank()) { "MEDIA•HUB $method $path returned an empty response" }
                decode(body)
            }
        }
    }
}
