package com.nuvio.tv.mediahub.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    @ApplicationContext context: Context
) : ViewModel() {
    private val bridge = MediaHubAccountQrBridge(context.applicationContext)

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    val hasActiveSession: Boolean
        get() = bridge.hasSession()

    init {
        viewModelScope.launch {
            bridge.states { _uiState.value }.collect { mapped ->
                _uiState.value = mapped
            }
        }
    }

    fun startQrLogin() {
        viewModelScope.launch { bridge.start() }
    }

    fun pollQrLogin() {
        viewModelScope.launch {
            bridge.pollNow().onFailure { error ->
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }

    fun clearQrLoginSession() {
        bridge.cancel()
    }

    fun signOut() {
        bridge.signOut()
        _uiState.value = AccountUiState()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        bridge.cancel()
        super.onCleared()
    }
}
