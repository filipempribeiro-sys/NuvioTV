package com.mediahub.contracts

data class MediaHubProviderConfig(
    val id: String,
    val name: String,
    val manifestUrl: String,
    val enabled: Boolean = true,
    val primary: Boolean = false
)

fun ptHubProvider(manifestUrl: String) = MediaHubProviderConfig(
    id = "pt-hub",
    name = "PT•HUB",
    manifestUrl = manifestUrl,
    enabled = true,
    primary = true
)
