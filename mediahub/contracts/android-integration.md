# Android integration plan — source-level transformation

The existing Android application is the MEDIA•HUB client chassis. Integration must replace Nuvio cloud dependencies at their source boundaries rather than masking them in CI.

## Authentication migration

1. Add a MEDIA•HUB discovery client for `/.well-known/mediahub`.
2. Replace the TV QR challenge repository with the `/v1/device-pairing` contract.
3. Persist the returned device session in encrypted/local application storage through the existing repository abstraction.
4. Replace Nuvio account/sync calls with `/v1/me`, `/v1/profiles` and `/v1/sync`.
5. Remove Nuvio Supabase URL/key as a requirement for the MEDIA•HUB build flavor.
6. Keep third-party integrations such as Trakt/SIMKL independent from MEDIA•HUB account authentication.

## Branding migration

Runtime resources and Compose components must directly reference MEDIA•HUB resources. CI must validate them, not copy/overwrite them during build. Locale resources retain their translations while the product name remains MEDIA•HUB.

## Player migration

For `channel` playback, derive seek controls from Media3's actual seekable live window. Do not reject seek solely because `isLive == true`. VOD behavior remains unchanged. Add an explicit Return-to-Live action and expose live-edge offset to UI.

## Release gate

A candidate is not called stable until Android compile/tests, backend contract tests, universal APK generation, persistent signing compatibility, launch smoke testing and the core playback/navigation paths have passed.
