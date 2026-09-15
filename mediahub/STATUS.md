# MEDIA•HUB transformation status

Branch work is intentionally isolated from the known-working Android baseline.

## Implemented in this branch

- independent MEDIA•HUB platform directory and architectural boundary;
- public discovery contract;
- dependency-light backend bootstrap service;
- device-bound, expiring QR pairing flow with hashed challenge secrets and rate limiting;
- account/device session bootstrap;
- profiles and sync API bootstrap;
- provider boundary for PT•HUB and other Stremio-compatible providers;
- explicit Live TV/Media3 seek/timeshift contract;
- backend syntax + end-to-end smoke test CI;
- reproducible container deployment files;
- stability gate and Android source migration plan.

## Deliberately not claimed complete

The backend currently keeps runtime state in memory, the Android source has not yet been rewired to these endpoints, and production signing/deployment are not configured. Therefore this branch is **not yet a stable MEDIA•HUB release** and no APK from it should be presented as such.

Next engineering boundary: durable persistence + Android repository integration, followed by source-level visual identity and player/live-TV implementation.
