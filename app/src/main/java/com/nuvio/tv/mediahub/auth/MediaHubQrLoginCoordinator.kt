package com.nuvio.tv.mediahub.auth

import android.os.Build
import android.provider.Settings
import com.nuvio.tv.core.qr.QrCodeGenerator
import kotlinx.coroutines.delay
import java.time.OffsetDateTime

/**
 * UI-facing MEDIA•HUB QR coordinator.
 *
 * Keeps the inherited AccountViewModel free from transport details while the
 * legacy Nuvio/Supabase account stack is migrated incrementally.
 */
class MediaHubQrLoginCoordinator(
    private val pairingClient: MediaHubDevicePairingClient = MediaHubDevicePairingClient()
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
        pairingClient.poll(challenge)

    companion object {
        fun deviceId(context: android.content.Context): String {
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
