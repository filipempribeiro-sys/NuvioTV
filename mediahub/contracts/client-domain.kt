package com.mediahub.contracts

/** Platform-neutral reference models for the Android integration boundary. */
data class MediaHubDiscovery(
    val product: String,
    val version: String,
    val apiBaseUrl: String,
    val capabilities: Map<String, Boolean>
)

data class DevicePairingChallenge(
    val pairingId: String,
    val deviceSecret: String,
    val userCode: String,
    val approvalUrl: String,
    val qrPayload: String,
    val expiresAt: String,
    val pollIntervalSeconds: Int
)

data class LiveTimelineCapability(
    val isLive: Boolean,
    val isSeekable: Boolean,
    val windowStartMs: Long,
    val windowEndMs: Long,
    val currentPositionMs: Long,
    val liveEdgeOffsetMs: Long
) {
    val canSeekBackward: Boolean get() = isSeekable && currentPositionMs > windowStartMs
    val canSeekForward: Boolean get() = isSeekable && currentPositionMs < windowEndMs
    val canReturnToLive: Boolean get() = isLive && liveEdgeOffsetMs > 0
}
