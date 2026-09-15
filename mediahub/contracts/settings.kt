package com.mediahub.contracts

data class MediaHubSettings(
    val languageOverride: String? = null,
    val subtitlesEnabled: Boolean = true,
    val preferredSubtitleLanguage: String? = null,
    val autoplayNextEpisode: Boolean = true,
    val ptHubManifestUrl: String? = null,
    val syncEnabled: Boolean = true,
    val extendedTimeshiftEnabled: Boolean = true,
    val extendedTimeshiftMinutes: Int = 30
) {
    val boundedTimeshiftMinutes: Int get() = extendedTimeshiftMinutes.coerceIn(0, 120)
}
