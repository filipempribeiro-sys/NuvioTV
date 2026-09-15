package com.mediahub.contracts

data class MediaHubSession(
    val accessToken: String,
    val refreshToken: String,
    val accountId: String,
    val deviceId: String,
    val expiresAtEpochSeconds: Long
)

interface MediaHubSessionStorage {
    suspend fun load(): MediaHubSession?
    suspend fun save(session: MediaHubSession)
    suspend fun clear()
}

/** Implementations must use the app's secure/encrypted local storage strategy. */
interface MediaHubDeviceIdentity {
    suspend fun stableDeviceId(): String
    suspend fun displayName(): String
}
