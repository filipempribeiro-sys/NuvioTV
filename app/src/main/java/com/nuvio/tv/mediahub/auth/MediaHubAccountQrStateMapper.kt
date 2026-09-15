package com.nuvio.tv.mediahub.auth

import com.nuvio.tv.ui.screens.account.AccountUiState

/**
 * Compatibility boundary between the new MEDIA•HUB pairing state machine and
 * the inherited account screen state. This keeps the UI contract stable while
 * the legacy Nuvio/Supabase QR implementation is removed from AccountViewModel.
 */
object MediaHubAccountQrStateMapper {
    fun apply(
        current: AccountUiState,
        mediaHub: MediaHubQrLoginController.State,
        preparingText: String,
        scanText: String,
        pendingText: String,
        approvedText: String,
        expiredText: String,
        failedText: String
    ): AccountUiState {
        val challenge = mediaHub.challenge
        val statusText = when (mediaHub.status) {
            MediaHubQrLoginController.Status.Idle -> null
            MediaHubQrLoginController.Status.Preparing -> preparingText
            MediaHubQrLoginController.Status.Pending -> if (challenge == null) preparingText else pendingText.ifBlank { scanText }
            MediaHubQrLoginController.Status.Approved -> approvedText
            MediaHubQrLoginController.Status.Expired -> expiredText
            MediaHubQrLoginController.Status.Failed -> failedText
        }

        return current.copy(
            isLoading = mediaHub.loading,
            error = mediaHub.error,
            qrLoginCode = challenge?.pairingId,
            qrLoginUserCode = challenge?.userCode,
            qrLoginUrl = challenge?.approvalUrl,
            qrLoginVerificationUri = challenge?.approvalUrl,
            qrLoginNonce = null,
            qrLoginBitmap = mediaHub.qrBitmap,
            qrLoginStatus = statusText,
            qrLoginExpiresAtMillis = mediaHub.expiresAtMillis,
            qrLoginPollIntervalSeconds = challenge?.pollIntervalSeconds?.coerceAtLeast(2)
                ?: current.qrLoginPollIntervalSeconds
        )
    }
}
