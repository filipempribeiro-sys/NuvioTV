package com.mediahub.contracts

/** Pure policy used to prevent the old `isLive => no seek` behaviour. */
object MediaHubLivePlaybackPolicy {
    fun seekBackwardTarget(timeline: LiveTimelineCapability, incrementMs: Long): Long? {
        if (!timeline.isSeekable) return null
        return (timeline.currentPositionMs - incrementMs).coerceAtLeast(timeline.windowStartMs)
    }

    fun seekForwardTarget(timeline: LiveTimelineCapability, incrementMs: Long): Long? {
        if (!timeline.isSeekable) return null
        return (timeline.currentPositionMs + incrementMs).coerceAtMost(timeline.windowEndMs)
    }

    fun liveTarget(timeline: LiveTimelineCapability): Long? =
        if (timeline.isLive && timeline.isSeekable) timeline.windowEndMs else null
}
