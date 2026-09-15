package com.mediahub.contracts

import java.time.Instant

data class MediaHubProgramme(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String? = null,
    val start: Instant,
    val end: Instant,
    val image: String? = null,
    val catchup: Boolean = false
) {
    fun isOnAir(now: Instant): Boolean = !now.isBefore(start) && now.isBefore(end)
}

data class MediaHubGuide(val channelId: String, val programmes: List<MediaHubProgramme>) {
    fun now(now: Instant): MediaHubProgramme? = programmes.firstOrNull { it.isOnAir(now) }
    fun next(now: Instant): MediaHubProgramme? = programmes.filter { it.start.isAfter(now) }.minByOrNull { it.start }
}
