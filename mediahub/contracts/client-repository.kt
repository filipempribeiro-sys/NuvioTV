package com.mediahub.contracts

/** Reference boundary to be adapted into the existing Android repository/DI structure. */
interface MediaHubAccountRepository {
    suspend fun discover(): MediaHubDiscovery
    suspend fun createPairing(deviceId: String, deviceName: String, platform: String = "android-tv"): DevicePairingChallenge
    suspend fun pollPairing(pairingId: String, deviceSecret: String): PairingState
    suspend fun getProfiles(): List<MediaHubProfile>
    suspend fun putProfiles(profiles: List<MediaHubProfile>): List<MediaHubProfile>
    suspend fun getSyncState(): MediaHubSyncState
    suspend fun putSyncState(state: MediaHubSyncState): MediaHubSyncState
    suspend fun signOutDevice()
}

sealed interface PairingState {
    data object Pending : PairingState
    data class Approved(val accountId: String) : PairingState
    data object Expired : PairingState
    data class Failed(val reason: String) : PairingState
}

data class MediaHubProfile(val id: String, val name: String, val avatarUrl: String? = null)
data class MediaHubSyncState(
    val library: List<String> = emptyList(),
    val watchProgress: Map<String, Long> = emptyMap(),
    val favourites: List<String> = emptyList(),
    val settings: Map<String, String> = emptyMap(),
    val addons: List<String> = emptyList(),
    val revision: Long = 0
)
