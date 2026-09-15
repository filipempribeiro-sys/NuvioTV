package com.nuvio.tv.mediahub.auth

import android.content.Context
import com.nuvio.tv.R
import com.nuvio.tv.ui.screens.account.AccountUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Account-screen boundary for MEDIA•HUB authentication.
 *
 * AccountViewModel can consume this bridge without knowing the pairing
 * transport, persistence implementation or legacy Nuvio/Supabase endpoints.
 */
class MediaHubAccountQrBridge(
    context: Context,
    private val controller: MediaHubQrLoginController = MediaHubQrLoginController(context.applicationContext)
) {
    private val appContext = context.applicationContext

    fun states(current: () -> AccountUiState): Flow<AccountUiState> =
        controller.state.map { state ->
            MediaHubAccountQrStateMapper.apply(
                current = current(),
                mediaHub = state,
                preparingText = appContext.getString(R.string.qr_login_preparing),
                scanText = appContext.getString(R.string.qr_login_scan_prompt),
                pendingText = appContext.getString(R.string.qr_login_scan_prompt),
                approvedText = appContext.getString(R.string.qr_login_approved),
                expiredText = appContext.getString(R.string.qr_login_expired),
                failedText = appContext.getString(R.string.qr_login_start_failed)
            )
        }

    suspend fun start() = controller.start()

    suspend fun pollNow(): Result<MediaHubPairingPollResult> = controller.pollNow()

    fun hasSession(): Boolean = controller.currentSession() != null

    fun currentSession(): MediaHubSessionStore.StoredSession? = controller.currentSession()

    fun cancel() = controller.cancel()

    fun signOut() = controller.signOut()
}
