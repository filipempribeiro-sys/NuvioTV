package com.mediahub.contracts

data class MediaHubDiagnostics(
    val appVersion: String,
    val buildCommit: String?,
    val discoveryReachable: Boolean,
    val apiVersion: String?,
    val backendHealthy: Boolean,
    val ptHubReachable: Boolean?,
    val providerStatus: Map<String, Boolean>,
    val playerEngine: String,
    val liveTimeline: LiveTimelineCapability? = null,
    val signingCertificateSha256: String? = null
)
