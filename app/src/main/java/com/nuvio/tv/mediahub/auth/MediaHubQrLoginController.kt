package com.nuvio.tv.mediahub.auth

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Complete MEDIA•HUB-owned QR login state machine.
 *
 * This controller is deliberately independent from the inherited Nuvio /
 * Supabase AuthManager. It can therefore be connected to the existing account
 * UI without making the MEDIA•HUB QR path depend on legacy cloud credentials.
 */
class MediaHubQrLoginController(
    context: Context,
    private val coordinator: MediaHubQrLoginCoordinator = MediaHubQrLoginCoordinator(
        context = context.applicationContext,
        sessionStore = MediaHubSessionStore(context.applicationContext)
    )
) {
    data class State(
        val loading: Boolean = false,
        val challenge: MediaHubPairingChallenge? = null,
        val qrBitmap: android.graphics.Bitmap? = null,
        val expiresAtMillis: Long? = null,
        val status: Status = Status.Idle,
        val error: String? = null
    )

    enum class Status {
        Idle,
        Preparing,
        Pending,
        Approved,
        Expired,
        Failed
    }

    private val appContext = context.applicationContext
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    @Volatile
    private var generation: Long = 0L

    suspend fun start() {
        val myGeneration = ++generation
        _state.value = State(loading = true, status = Status.Preparing)

        coordinator.start(MediaHubQrLoginCoordinator.deviceId(appContext)).fold(
            onSuccess = { started ->
                if (generation != myGeneration) return
                _state.value = State(
                    loading = false,
                    challenge = started.challenge,
                    qrBitmap = started.qrBitmap,
                    expiresAtMillis = started.expiresAtMillis,
                    status = Status.Pending
                )
                pollUntilTerminal(started.challenge, myGeneration)
            },
            onFailure = { error ->
                if (generation != myGeneration) return
                _state.value = State(
                    loading = false,
                    status = Status.Failed,
                    error = error.message ?: "MEDIA•HUB pairing could not be started"
                )
            }
        )
    }

    suspend fun pollNow(): Result<MediaHubPairingPollResult> {
        val challenge = _state.value.challenge
            ?: return Result.failure(IllegalStateException("No active MEDIA•HUB pairing"))
        return coordinator.poll(challenge)
    }

    fun currentSession(): MediaHubSessionStore.StoredSession? = coordinator.currentSession()

    fun signOut() {
        cancel()
        coordinator.signOut()
    }

    fun cancel() {
        generation++
        _state.value = State()
    }

    private suspend fun pollUntilTerminal(
        challenge: MediaHubPairingChallenge,
        myGeneration: Long
    ) {
        val intervalMs = challenge.pollIntervalSeconds.coerceAtLeast(2) * 1000L
        while (generation == myGeneration) {
            delay(intervalMs)
            if (generation != myGeneration) return

            coordinator.poll(challenge).fold(
                onSuccess = { result ->
                    when (result) {
                        is MediaHubPairingPollResult.Pending -> {
                            _state.update {
                                it.copy(
                                    loading = false,
                                    status = Status.Pending,
                                    error = null
                                )
                            }
                        }
                        is MediaHubPairingPollResult.Approved -> {
                            _state.update {
                                it.copy(
                                    loading = false,
                                    status = Status.Approved,
                                    error = null
                                )
                            }
                            generation++
                            return
                        }
                        MediaHubPairingPollResult.Expired -> {
                            _state.update {
                                it.copy(
                                    loading = false,
                                    status = Status.Expired,
                                    error = null
                                )
                            }
                            generation++
                            return
                        }
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            loading = false,
                            status = Status.Failed,
                            error = error.message ?: "MEDIA•HUB pairing failed"
                        )
                    }
                    generation++
                    return
                }
            )
        }
    }
}
