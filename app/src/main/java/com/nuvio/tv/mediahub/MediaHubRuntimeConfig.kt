package com.nuvio.tv.mediahub

import com.nuvio.tv.BuildConfig

/**
 * Single Android-side boundary for MEDIA•HUB-owned runtime configuration.
 *
 * Nuvio package names remain temporarily while the namespace is migrated
 * incrementally; product/cloud identity must not depend on that package name.
 */
object MediaHubRuntimeConfig {
    const val productName: String = "MEDIA•HUB"
    const val descriptor: String = "TV • FILMES • SÉRIES"
    const val slogan: String = "TODO O ENTRETENIMENTO NUM SÓ LUGAR"

    /**
     * Empty means Cloud features are not configured for this build.
     * Media/provider functionality must be allowed to degrade independently.
     */
    val discoveryUrl: String
        get() = runCatching {
            BuildConfig::class.java.getField("MEDIAHUB_DISCOVERY_URL").get(null) as? String
        }.getOrNull().orEmpty().trim()

    val cloudConfigured: Boolean
        get() = discoveryUrl.startsWith("https://") ||
            (BuildConfig.IS_DEBUG_BUILD && discoveryUrl.startsWith("http://"))
}
