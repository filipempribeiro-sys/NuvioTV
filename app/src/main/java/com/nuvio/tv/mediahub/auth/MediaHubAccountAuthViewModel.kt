package com.nuvio.tv.mediahub.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.data.local.AppOnboardingDataStore
import com.nuvio.tv.domain.model.AuthState
import com.nuvio.tv.ui.screens.account.AccountUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MEDIA•HUB-owned account authentication state.
 *
 * This is the replacement boundary for the inherited QR authentication path.
 * It intentionally does not depend on AuthManager, Postgrest, Supabase or the
 * legacy TV-login exchange endpoints. The old AccountViewModel can keep the
 * remaining legacy sync/email responsibilities while they are migrated.
 */
@HiltViewModel
class MediaHubAccountAuthViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val globalAuthState: MediaHubAuthStateStore,
    private val appOnboardingDataStore: AppOnboardingDataStore
) : ViewModel() {
    private val bridge = MediaHubAccountQrBridge(context.applicationContext)

    private val _uiState = MutableStateFlow(
        AccountUiState(authState = globalAuthState.authState.value)
    )
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()
    val authState: StateFlow<AuthState> = globalAuthState.authState

    val hasActiveSession: Boolean
        get() = globalAuthState.isAuthenticated

    init {
        globalAuthState.refresh()

        viewModelScope.launch {
            bridge.states { _uiState.value }.collect { mapped ->
                if (bridge.hasSession()) {
                    globalAuthState.onSessionApproved()
                }
                _uiState.value = mapped.copy(authState = globalAuthState.authState.value)
            }
        }

        viewModelScope.launch {
            globalAuthState.authState.collect { auth ->
                _uiState.update { it.copy(authState = auth) }
            }
        }
    }

    fun startQrLogin() {
        viewModelScope.launch { bridge.start() }
    }

    fun pollQrLogin() {
        viewModelScope.launch {
            bridge.pollNow()
                .onSuccess { result ->
                    if (result is MediaHubPairingPollResult.Approved) {
                        globalAuthState.onSessionApproved()
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun clearQrLoginSession() {
        bridge.cancel()
    }

    fun signOut() {
        bridge.signOut()
        globalAuthState.signOut()
        _uiState.value = AccountUiState(authState = AuthState.SignedOut)

        // During first-run MEDIA•HUB onboarding, "Continue without account"
        // deliberately signs out before invoking the host continuation callback.
        // Persist the guest decision here as well so the onboarding gate cannot
        // trap the user if the host-side continuation is delayed or interrupted.
        viewModelScope.launch {
            appOnboardingDataStore.setHasSeenAuthQrOnFirstLaunch(true)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        bridge.cancel()
        super.onCleared()
    }
}
