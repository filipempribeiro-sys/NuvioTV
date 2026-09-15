# MEDIA•HUB Platform

This directory is the structural boundary for the MEDIA•HUB platform built on the NuvioTV Android codebase.

## Product identity

- **Name:** MEDIA•HUB
- **Subtitle:** TV • FILMES • SÉRIES
- **Slogan:** TODO O ENTRETENIMENTO NUM SÓ LUGAR

## Architecture

The Android client remains based on the proven NuvioTV Kotlin/Compose/Media3 implementation while Nuvio-specific infrastructure is progressively replaced by MEDIA•HUB-owned contracts and services.

- `contracts/` — stable client/server API contracts and discovery schemas.
- `backend/` — MEDIA•HUB-owned authentication, QR device pairing, profiles, devices, library, progress, favourites, settings and synchronisation services.
- `core/` — shared MEDIA•HUB domain concepts and capability definitions.
- `deployment/` — reproducible self-host deployment, environment templates, migrations and health checks.

PT•HUB remains a separate media/provider engine and is integrated through explicit provider boundaries. It is not coupled to authentication or account storage.

## Engineering rules

1. Do not depend on Nuvio cloud services for MEDIA•HUB accounts or QR login.
2. Never embed service-role, database or administrative secrets in the APK.
3. Preserve working NuvioTV capabilities while replacing infrastructure incrementally.
4. Keep `applicationId` independent (`com.mediahub.tv`). Namespace migration is intentionally gradual.
5. Live TV and timeshift are first-class player capabilities, not build-time patches.
6. Build-time resource replacement is not the final branding mechanism; MEDIA•HUB identity belongs in source resources and UI components.
7. Every release candidate must be reproducibly buildable and use persistent release signing for update compatibility.
8. Preserve GPLv3 obligations for reused NuvioTV code.
