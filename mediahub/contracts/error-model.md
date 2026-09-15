# MEDIA•HUB error model

All client-facing API failures should converge on a small typed model:

- `network_unavailable`
- `service_unavailable`
- `unauthorized`
- `rate_limited`
- `expired`
- `invalid_request`
- `provider_unavailable`
- `playback_failed`
- `unknown`

Errors are scoped to the smallest affected surface. A provider timeout does not become an application-wide failure. QR expiry returns the user to a regenerate action. Loss of MEDIA•HUB Cloud does not disable local/provider playback that does not require cloud state. Playback failure preserves navigation state and offers another legitimate stream/provider when available.

Diagnostics may include request ids, provider ids and non-sensitive status information. Never log access tokens, refresh tokens, IPTV credentials, device secrets or one-time pairing codes.
