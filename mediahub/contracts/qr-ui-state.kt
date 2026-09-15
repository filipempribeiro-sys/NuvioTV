package com.mediahub.contracts

sealed interface MediaHubPairingUiState {
    data object Preparing : MediaHubPairingUiState
    data class Ready(val challenge: DevicePairingChallenge) : MediaHubPairingUiState
    data class Waiting(val challenge: DevicePairingChallenge) : MediaHubPairingUiState
    data object Approved : MediaHubPairingUiState
    data object Expired : MediaHubPairingUiState
    data class Error(val message: String, val retryable: Boolean = true) : MediaHubPairingUiState
}

/** The UI must always leave Preparing through success, timeout or error. */
fun MediaHubPairingUiState.canRegenerate(): Boolean = this is MediaHubPairingUiState.Expired || this is MediaHubPairingUiState.Error
