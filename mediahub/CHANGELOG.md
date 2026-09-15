# MEDIA•HUB Platform Changelog

## Unreleased — structural transformation

### Added
- MEDIA•HUB platform architecture boundary and ADRs.
- Independent discovery/API contracts.
- QR device pairing bootstrap with expiry, hashed secrets, device-bound polling and rate limiting.
- Device session, profiles and sync bootstrap endpoints.
- Backend syntax, functional and security smoke tests with GitHub Actions validation.
- Docker deployment, PostgreSQL schema and durable persistence interface.
- PT•HUB provider boundary.
- EPG, live player and timeshift contracts.
- MEDIA•HUB design system, UI surface specification and error model.
- Release/stability and persistent-signing gates.

### Status
This is foundational platform work. Android source integration, durable persistence adapter, production deployment, renewed runtime UI and final player/timeshift implementation remain required before a stable APK can be declared.
