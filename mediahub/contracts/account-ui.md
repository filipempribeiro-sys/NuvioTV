# MEDIA•HUB account / QR UX

The TV account screen must never depend on an unexplained spinner.

States:

- **Preparing** — bounded request to discovery/pairing service.
- **Ready/Waiting** — QR, short code, expiry indicator and regenerate action.
- **Approved** — transition to profile/Home without requiring app restart.
- **Expired** — clear regenerate action.
- **Offline/service error** — concise explanation, retry and optional continue-without-cloud when media features can operate locally.

The QR contains the MEDIA•HUB approval URL only. Account access tokens, refresh tokens and device secrets are never rendered in the QR. Device management later exposes name/platform/last-seen/revoke without exposing credentials.
