package com.nuvio.tv.mediahub.auth

import android.content.Context
import android.content.SharedPreferences
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
 *
 * The store also listens to the MEDIA•HUB session SharedPreferences. This makes
 * the global auth state follow session creation, refresh and sign-out regardless
 * of which MEDIA•HUB component changed the persisted session.
 */
@Singleton
class MediaHubAuthStateStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context.applicationContext
    private val sessionStore = MediaHubSessionStore(appContext)
    private val sessionPreferences = appContext.getSharedPreferences(
        MEDIA_HUB_SESSION_PREFS,
        Context.MODE_PRIVATE
    )

    private val _authState = MutableStateFlow(resolveStoredState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val sessionPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> refresh() }

    init {
        sessionPreferences.registerOnSharedPreferenceChangeListener(sessionPreferenceListener)
        refresh()
    }

    val currentSession: MediaHubSessionStore.StoredSession?
        get() = sessionStore.read()?.takeUnless { it.isExpired }

    val isAuthenticated: Boolean
        get() = currentSession != null

    fun refresh() {
        _authState.value = resolveStoredState()
    }

    fun onSessionApproved() {
        // Kept as a compatibility hook while callers are migrated. Session
        // persistence already refreshes this store through the preference listener.
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

    private companion object {
        const val MEDIA_HUB_SESSION_PREFS = "mediahub_session"
    }
}
