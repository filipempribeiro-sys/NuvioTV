package com.mediahub.contracts

data class MediaHubSearchProviderState(
    val providerId: String,
    val loading: Boolean = false,
    val results: List<ProviderItem> = emptyList(),
    val error: String? = null
)

data class MediaHubSearchState(
    val query: String = "",
    val providers: List<MediaHubSearchProviderState> = emptyList()
) {
    val results: List<ProviderItem> get() = providers.flatMap { it.results }
    val partialFailure: Boolean get() = providers.any { it.error != null } && results.isNotEmpty()
}
