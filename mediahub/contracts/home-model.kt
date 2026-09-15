package com.mediahub.contracts

data class MediaHubHomeSection(
    val id: String,
    val title: String,
    val type: String,
    val providerId: String,
    val items: List<ProviderItem> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

data class MediaHubHomeState(
    val sections: List<MediaHubHomeSection> = emptyList()
) {
    val hasContent: Boolean get() = sections.any { it.items.isNotEmpty() }
    val loading: Boolean get() = sections.any { it.loading }
}
