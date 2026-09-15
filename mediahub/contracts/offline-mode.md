# MEDIA•HUB degraded/offline mode

MEDIA•HUB Cloud availability must not be confused with media-provider availability.

When Cloud is unreachable after a previously valid session/configuration, the client may use cached non-sensitive account/profile/settings state and continue local/provider features that do not require a cloud mutation. Sync changes queue locally with bounded conflict metadata and reconcile when connectivity returns.

Fresh QR pairing/account creation requires Cloud and must show a retryable service/offline state. PT•HUB and other provider playback may continue independently when reachable. The client never falls back to Nuvio cloud as an offline strategy.
