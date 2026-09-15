package com.mediahub.contracts

data class MediaHubPlayerUiState(
    val title: String,
    val playing: Boolean,
    val buffering: Boolean,
    val positionMs: Long,
    val durationMs: Long?,
    val liveTimeline: LiveTimelineCapability? = null,
    val timeshiftMode: TimeshiftMode = TimeshiftMode.None,
    val error: String? = null
) {
    val showLiveBadge: Boolean get() = liveTimeline?.isLive == true
    val showReturnToLive: Boolean get() = liveTimeline?.canReturnToLive == true
    val canSeekBackward: Boolean get() = liveTimeline?.canSeekBackward ?: (durationMs != null)
    val canSeekForward: Boolean get() = liveTimeline?.canSeekForward ?: (durationMs != null)
}
