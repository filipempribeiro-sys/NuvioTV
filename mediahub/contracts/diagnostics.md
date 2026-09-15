# MEDIA•HUB diagnostics

A diagnostics surface should make failures actionable without leaking secrets. It may show:

- app version/build commit;
- Android/device version and architecture;
- MEDIA•HUB discovery reachability and API version;
- backend health status;
- enabled provider ids and last non-sensitive status;
- PT•HUB manifest reachability;
- player engine/version;
- current live timeline flags (`isLive`, `isSeekable`, live-edge offset, buffer source);
- sync revision/time;
- signing certificate fingerprint for development/release verification.

It must never display or copy bearer tokens, refresh tokens, QR device secrets, pairing codes after use, IPTV passwords, private provider URLs with embedded credentials, database URLs or CI secrets.
