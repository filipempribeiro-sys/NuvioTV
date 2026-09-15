# MEDIA•HUB Release Candidate Checklist

This checklist must be completed with evidence for the exact candidate commit.

### Build
- clean checkout
- backend `npm run check` and `npm test`
- Android compile/test
- universal APK artifact
- persistent signing fingerprint matches previous candidate/release

### Identity
- launcher label/icon
- splash
- onboarding
- Home/navigation
- account/QR
- settings
- player
- pt-PT locale and device-language fallback

### Functional smoke
- cold launch
- close/reopen
- Home catalogs
- search
- details
- movie playback
- series playback
- subtitles
- PT•HUB provider
- IPTV/live channel
- pause live
- rewind/forward when seekable
- Return to LIVE
- non-seekable live graceful behavior
- settings persistence
- account QR pairing
- profile/sync round-trip

### Failure paths
- backend unavailable
- PT•HUB unavailable
- provider timeout
- invalid/expired QR
- stream failure
- artwork failure
- network loss/recovery

A failed mandatory item blocks the `stable` label.
