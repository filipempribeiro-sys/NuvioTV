# Phase 1 checkpoint — platform foundation

Phase 1 establishes a safe structural target without changing the known-working Android runtime yet.

Completed: architecture/ownership decisions, independent cloud discovery/API contract, QR/session bootstrap and tests, sync/profile bootstrap, PostgreSQL durable schema boundary, provider/PT•HUB boundary, Live TV/EPG/timeshift semantics, design/UX rules, signing/stability gates and Android reference domain boundaries.

This checkpoint is intentionally conservative: no claim is made that the Android app is already migrated or that the backend bootstrap is production-ready. The next phase must integrate these boundaries into the actual existing Android source and replace the Nuvio cloud/branding paths at source level while keeping the app buildable.
