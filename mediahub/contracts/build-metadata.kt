package com.mediahub.contracts

data class MediaHubBuildMetadata(
    val versionName: String,
    val versionCode: Int,
    val commitSha: String?,
    val discoveryUrl: String,
    val buildType: String,
    val signingCertificateSha256: String? = null
) {
    fun publicSummary(): Map<String, String> = buildMap {
        put("version", "$versionName ($versionCode)")
        commitSha?.let { put("commit", it) }
        put("discovery", discoveryUrl)
        put("buildType", buildType)
        signingCertificateSha256?.let { put("signing", it) }
    }
}
