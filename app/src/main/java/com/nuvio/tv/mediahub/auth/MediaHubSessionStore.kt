package com.nuvio.tv.mediahub.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.time.Instant

/**
 * Small MEDIA•HUB session boundary used while the inherited account stack is
 * migrated. Tokens never belong in logs, diagnostics or source-controlled
 * configuration.
 *
 * This store deliberately owns only MEDIA•HUB session state. It does not copy
 * tokens into the legacy Nuvio/Supabase session.
 */
class MediaHubSessionStore(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    data class StoredSession(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String,
        val accountId: String,
        val deviceId: String?,
        val expiresAtEpochSeconds: Long
    ) {
        val isExpired: Boolean
            get() = Instant.now().epochSecond >= expiresAtEpochSeconds
    }

    fun save(
        session: MediaHubSession,
        accountId: String,
        device: MediaHubDevice?
    ) {
        val now = Instant.now().epochSecond
        preferences.edit(commit = true) {
            putString(KEY_ACCESS_TOKEN, session.accessToken)
            putString(KEY_REFRESH_TOKEN, session.refreshToken)
            putString(KEY_TOKEN_TYPE, session.tokenType)
            putString(KEY_ACCOUNT_ID, accountId)
            putString(KEY_DEVICE_ID, device?.id)
            putLong(KEY_EXPIRES_AT, now + session.expiresIn.coerceAtLeast(0L))
        }
    }

    fun read(): StoredSession? {
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null)?.takeIf { it.isNotBlank() }
            ?: return null
        val refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null).orEmpty()
        val accountId = preferences.getString(KEY_ACCOUNT_ID, null)?.takeIf { it.isNotBlank() }
            ?: return null
        return StoredSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = preferences.getString(KEY_TOKEN_TYPE, "Bearer").orEmpty().ifBlank { "Bearer" },
            accountId = accountId,
            deviceId = preferences.getString(KEY_DEVICE_ID, null),
            expiresAtEpochSeconds = preferences.getLong(KEY_EXPIRES_AT, 0L)
        )
    }

    fun clear() {
        preferences.edit(commit = true) { clear() }
    }

    companion object {
        private const val PREFS_NAME = "mediahub_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_ACCOUNT_ID = "account_id"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_EXPIRES_AT = "expires_at"
    }
}
