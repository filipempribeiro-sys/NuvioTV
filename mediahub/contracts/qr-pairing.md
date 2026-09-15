# MEDIA•HUB QR Device Pairing Contract

## Goal

Pair a TV/device with a MEDIA•HUB account without placing a reusable account credential in the QR code.

## Flow

1. TV requests a pairing challenge from `POST /v1/device-pairing`.
2. Backend returns an opaque `pairingId`, a short user code, approval URL, QR payload and expiry.
3. The QR payload contains only the approval URL/challenge reference; it contains no access token or administrative secret.
4. TV polls `GET /v1/device-pairing/{pairingId}` or subscribes to the equivalent event channel.
5. An authenticated web/mobile client approves the challenge.
6. Backend atomically consumes the challenge and returns a device session to the TV.
7. Expired, consumed or revoked challenges cannot be reused.

## Security invariants

- Pairing challenges are short-lived and single-use.
- Persist challenge secrets only as cryptographic hashes.
- Rate-limit challenge creation, status polling and approval attempts.
- Bind the issued session to the requesting device identifier and declared client metadata.
- Never expose service-role/database/admin credentials to Android clients.
- Revoking a device invalidates its refresh/session credentials without affecting other devices.
