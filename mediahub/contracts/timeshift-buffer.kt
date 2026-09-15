package com.mediahub.contracts

/** Boundary for a future local DVR implementation; player remains usable when unavailable. */
interface MediaHubTimeshiftBuffer {
    val available: Boolean
    suspend fun start(channelId: String, sourceUrl: String): Result<Unit>
    suspend fun stop(): Result<Unit>
    suspend fun window(): TimeshiftWindow?
    suspend fun release()
}

data class TimeshiftWindow(
    val startMs: Long,
    val endMs: Long,
    val durationMs: Long,
    val bytesUsed: Long
)

sealed interface TimeshiftMode {
    data object None : TimeshiftMode
    data object UpstreamWindow : TimeshiftMode
    data class LocalBuffer(val window: TimeshiftWindow) : TimeshiftMode
}
