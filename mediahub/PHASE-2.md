# Phase 2 — Android source integration

Order is deliberately build-preserving:

1. inventory exact existing auth, TV-login, sync, branding and player files on the transformation branch;
2. introduce MEDIA•HUB discovery configuration into the existing Gradle/BuildConfig path;
3. adapt the existing repository/DI boundary to MEDIA•HUB QR/session contracts;
4. migrate runtime branding/resources directly in source and delete CI branding substitution;
5. adapt existing addon/provider path for PT•HUB first-class configuration;
6. modify existing player event handling so live seeking depends on the Media3 seekable window;
7. integrate EPG/live UI and then extended local timeshift;
8. run Android CI after each coherent boundary and revert root regressions rather than stacking patches.

The existing package namespace is not globally renamed during this phase. `applicationId com.mediahub.tv` remains the install identity while package migration can occur incrementally where it creates real value.
