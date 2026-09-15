package com.nuvio.tv.mediahub.auth

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.nuvio.tv.core.qr.QrCodeGenerator
import java.time.OffsetDateTime

/**
 * UI-facing MEDIA•HUB QR coordinator.
 *
 * Keeps the inherited AccountViewModel free from transport details while the
 * legacy Nuvio/Supabase account stack is migrated incrementally.
 */
class MediaHubQrLoginCoordinator(
    context: Context,
    private val pairingClient: MediaHubDevicePairingClient = MediaHubDevicePairingClient(),
    private val sessionStore: MediaHubSessionStore = MediaHubSessionStore(context.applicationContext)
) {
    data class Started(
        val challenge: MediaHubPairingChallenge,
        val qrBitmap: android.graphics.Bitmap?,
        val expiresAtMillis: Long?
    )

    suspend fun start(androidId: String): Result<Started> =
        pairingClient.start(
            deviceId = androidId,
            deviceName = Build.MODEL.ifBlank { "Android TV" }
        ).map { challenge ->
            Started(
                challenge = challenge,
                qrBitmap = runCatching {
                    QrCodeGenerator.generate(challenge.qrPayload, 420, margin = 1)
                }.getOrNull(),
                expiresAtMillis = challenge.expiresAt.toEpochMillisOrNull()
            )
        }

    suspend fun poll(challenge: MediaHubPairingChallenge): Result<MediaHubPairingPollResult> =
        pairingClient.poll(challenge).onSuccess { result ->
            if (result is MediaHubPairingPollResult.Approved) {
                sessionStore.save(
                    session = result.session,
                    accountId = result.accountId,
                    device = result.device
                )
            }
        }

    fun currentSession(): MediaHubSessionStore.StoredSession? =
        sessionStore.read()?.takeUnless { it.isExpired }

    fun signOut() {
        sessionStore.clear()
    }

    companion object {
        fun deviceId(context: Context): String {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ).orEmpty().trim()
            return androidId.ifBlank { "mediahub-${Build.MODEL.hashCode()}" }
        }
    }
}

private fun String.toEpochMillisOrNull(): Long? =
    runCatching { OffsetDateTime.parse(trim()).toInstant().toEpochMilli() }.getOrNull()
