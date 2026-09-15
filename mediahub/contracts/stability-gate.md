# MEDIA•HUB Stability Gate

`stable` is a release state, not a version label.

A build may be promoted to a stable release candidate only when all applicable gates pass:

- source identity contains MEDIA•HUB without CI-time branding substitution;
- Android application compiles from a clean checkout;
- backend syntax and contract smoke tests pass;
- discovery endpoint is reachable from the target client environment;
- QR pairing is short-lived, single-use and device-bound;
- account/profile/sync persistence uses durable storage in the deployed backend;
- no Nuvio cloud credential is required for MEDIA•HUB authentication;
- PT•HUB provider failures cannot crash navigation or unrelated providers;
- VOD seek behavior remains unchanged;
- live seek controls reflect the actual Media3 seekable window;
- local extended timeshift degrades safely to ordinary live playback;
- universal APK is generated and installs on the target ARM device;
- release/update builds use the same persistent signing identity;
- cold launch, navigation, search, details, playback, subtitles, settings and relaunch smoke paths pass.

Until these gates pass, artifacts are development builds and must not be represented as stable.
