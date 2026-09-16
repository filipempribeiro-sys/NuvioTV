package com.nuvio.tv.mediahub.cloud

import android.util.Log
import com.nuvio.tv.data.local.AddonPreferences
import com.nuvio.tv.mediahub.auth.MediaHubAuthStateStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

/**
 * Bridges the MEDIA•HUB /sync addon payload to the existing local addon engine.
 *
 * The playback/catalog stack continues to consume AddonPreferences, so cloud
 * accounts can use the proven inherited source resolver without introducing a
 * second stream engine. Unknown cloud payload shapes are ignored safely.
 */
@Singleton
class MediaHubAddonCloudSync @Inject constructor(
    private val cloudApi: MediaHubCloudApiClient,
    private val authState: MediaHubAuthStateStore,
    private val addonPreferences: AddonPreferences
) {
    data class PullResult(
        val applied: Boolean,
        val addonCount: Int
    )

    suspend fun pullIntoLocal(): Result<PullResult> {
        if (!authState.isAuthenticated) return Result.success(PullResult(false, 0))

        return cloudApi.getSync().mapCatching { snapshot ->
            val addons = parseAddons(snapshot.addons)
            if (addons == null) {
                Log.d(TAG, "MEDIA-HUB sync has no compatible addon payload; keeping local sources")
                return@mapCatching PullResult(false, 0)
            }

            // An empty remote list is not allowed to erase a working local setup.
            if (addons.isEmpty()) {
                Log.d(TAG, "MEDIA-HUB remote addon list is empty; keeping local sources")
                return@mapCatching PullResult(false, 0)
            }

            addonPreferences.setAddonOrder(addons.map { it.url })
            addonPreferences.setAddonEnabledStates(addons.associate { it.url to it.enabled })
            addonPreferences.setUserSetNames(
                addons.mapNotNull { addon ->
                    addon.name?.takeIf(String::isNotBlank)?.let { addon.url to it }
                }.toMap()
            )
            Log.d(TAG, "Applied ${addons.size} MEDIA-HUB cloud addon(s) to local playback sources")
            PullResult(true, addons.size)
        }
    }

    suspend fun pushLocal(): Result<Int> {
        if (!authState.isAuthenticated) return Result.success(0)

        return runCatching {
            val urls = addonPreferences.installedAddonUrls.first()
            val enabled = addonPreferences.addonEnabledStates.first()
            val names = addonPreferences.userSetNames.first()
            val payload = JsonArray(urls.mapIndexed { index, url ->
                JsonObject(buildMap {
                    put("url", JsonPrimitive(url))
                    put("sort_order", JsonPrimitive(index))
                    put("enabled", JsonPrimitive(enabled[url] ?: true))
                    names[url]?.takeIf(String::isNotBlank)?.let { put("name", JsonPrimitive(it)) }
                })
            })

            val current = cloudApi.getSync().getOrThrow()
            cloudApi.putSync(current.copy(addons = payload)).getOrThrow()
            Log.d(TAG, "Pushed ${urls.size} local addon(s) to MEDIA-HUB cloud")
            urls.size
        }.onFailure { error ->
            Log.e(TAG, "Failed to push local addons to MEDIA-HUB cloud", error)
        }
    }

    private data class CloudAddon(
        val url: String,
        val enabled: Boolean,
        val name: String?,
        val sortOrder: Int?,
        val sourceIndex: Int
    )

    private fun parseAddons(element: JsonElement?): List<CloudAddon>? {
        val array = when (element) {
            is JsonArray -> element
            is JsonObject -> element["addons"] as? JsonArray
                ?: element["items"] as? JsonArray
                ?: element["sources"] as? JsonArray
            else -> null
        } ?: return null

        val parsed = array.mapIndexedNotNull { index, item ->
            when (item) {
                is JsonPrimitive -> item.contentOrNull?.takeIf(String::isNotBlank)?.let {
                    CloudAddon(it, true, null, null, index)
                }
                is JsonObject -> {
                    val url = (item["url"] as? JsonPrimitive)?.contentOrNull
                        ?.takeIf(String::isNotBlank) ?: return@mapIndexedNotNull null
                    CloudAddon(
                        url = url,
                        enabled = (item["enabled"] as? JsonPrimitive)?.booleanOrNull ?: true,
                        name = (item["name"] as? JsonPrimitive)?.contentOrNull,
                        sortOrder = (item["sort_order"] as? JsonPrimitive)?.intOrNull,
                        sourceIndex = index
                    )
                }
                else -> null
            }
        }

        // Match the inherited sync contract: explicit sort_order wins. Payloads
        // without it keep their original array order for backwards compatibility.
        return parsed.sortedWith(
            compareBy<CloudAddon> { it.sortOrder ?: it.sourceIndex }
                .thenBy { it.sourceIndex }
        )
    }

    private companion object {
        const val TAG = "MediaHubAddonCloudSync"
    }
}
