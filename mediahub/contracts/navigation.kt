package com.mediahub.contracts

enum class MediaHubDestination { HOME, TV, MOVIES, SERIES, SEARCH, LIBRARY, SETTINGS }

data class MediaHubNavigationState(
    val destination: MediaHubDestination = MediaHubDestination.HOME,
    val drawerOpen: Boolean = false,
    val previousDestination: MediaHubDestination? = null
)
