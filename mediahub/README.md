# MEDIA•HUB Platform

MEDIA•HUB is being built by structurally transforming the NuvioTV Android codebase while preserving its proven Kotlin/Compose/Media3 foundations.

**Identity:** MEDIA•HUB  
**Descriptor:** TV • FILMES • SÉRIES  
**Slogan:** TODO O ENTRETENIMENTO NUM SÓ LUGAR

## Boundaries

- `contracts/` — client/cloud/provider/live-TV contracts and Android reference models.
- `backend/` — independent MEDIA•HUB account/device/QR/profile/sync bootstrap service and tests.
- `deployment/` — container deployment, environment template and durable PostgreSQL schema.
- `core/` — capability declarations.

PT•HUB remains a separate first-class media provider. MEDIA•HUB Cloud owns account/device/sync concerns and does not depend on Nuvio cloud services.

## Current phase

Phase 1 platform foundation is checkpointed. Phase 2 is the build-preserving integration of these boundaries into the actual existing Android source. See `PHASE-1-CHECKPOINT.md`, `PHASE-2.md`, `ROADMAP.md` and `contracts/stability-gate.md`.

## Non-negotiable engineering rules

- root-cause source changes instead of CI/runtime masking;
- no fake QR, fake streams or fake timeshift capabilities;
- no secrets in source/APK;
- preserve rollback baseline;
- stable means the exact integrated candidate passes the evidence-based release gate;
- preserve GPLv3 obligations for reused NuvioTV code.
