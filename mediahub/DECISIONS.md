# MEDIA•HUB Architecture Decisions

## ADR-001 — NuvioTV is the client codebase, not the product identity
Accepted. Reuse its Kotlin/Compose/Media3 and working media architecture; progressively replace Nuvio-owned product/cloud boundaries.

## ADR-002 — Independent MEDIA•HUB cloud contract
Accepted. Client discovers an owned API through `/.well-known/mediahub`; no silent Nuvio cloud fallback.

## ADR-003 — PT•HUB remains a provider
Accepted. PT•HUB supplies configurable media/catalog/stream functionality but does not own MEDIA•HUB accounts or sync.

## ADR-004 — Live seek follows Media3 capability
Accepted. `isLive` alone never disables seeking. Seek controls depend on the actual seekable live timeline/window.

## ADR-005 — Extended timeshift is truthful
Accepted. A 30-minute DVR promise requires a real upstream or local buffer. UI must expose only capabilities that exist and degrade to normal live playback.

## ADR-006 — Stable requires evidence
Accepted. Installability is necessary but insufficient. Stable promotion requires the documented build, signing, backend, identity, navigation and playback gates.

## ADR-007 — Preserve rollback baseline
Accepted. Structural work stays isolated until release gates pass; the known-working installed baseline is not sacrificed during migration.
