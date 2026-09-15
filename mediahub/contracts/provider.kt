package com.mediahub.contracts

interface MediaHubProvider {
    val id: String
    val name: String
    suspend fun health(): ProviderHealth
    suspend fun search(query: String, type: String? = null): List<ProviderItem>
    suspend fun streams(type: String, id: String): List<ProviderStream>
}

data class ProviderHealth(val available: Boolean, val message: String? = null)
data class ProviderItem(val id: String, val type: String, val name: String, val poster: String? = null, val providerId: String)
data class ProviderStream(val name: String, val url: String? = null, val externalUrl: String? = null, val providerId: String, val isLive: Boolean = false)
