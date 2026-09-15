package com.nuvio.tv.mediahub.auth

import android.content.Context
import com.nuvio.tv.domain.model.AuthState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application-wide MEDIA•HUB authentication source of truth.
 *
 * This deliberately remains separate from the inherited Nuvio/Supabase
 * AuthManager. Consumers that are migrated to MEDIA•HUB can observe this state
 * without importing or fabricating a legacy Supabase session.
 */
@Singleton
class MediaHubAuthStateStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sessionStore = MediaHubSessionStore(context.applicationContext)

    private val _authState = MutableStateFlow(resolveStoredState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentSession: MediaHubSessionStore.StoredSession?
        get() = sessionStore.read()?.takeUnless { it.isExpired }

    val isAuthenticated: Boolean
        get() = currentSession != null

    fun refresh() {
        _authState.value = resolveStoredState()
    }

    fun onSessionApproved() {
        refresh()
    }

    fun signOut() {
        sessionStore.clear()
        _authState.value = AuthState.SignedOut
    }

    private fun resolveStoredState(): AuthState {
        val session = sessionStore.read()?.takeUnless { it.isExpired }
            ?: return AuthState.SignedOut
        return AuthState.FullAccount(
            userId = session.accountId,
            email = ""
        )
    }
}
