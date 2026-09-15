# Android build invariants during Phase 2

- `applicationId` remains `com.mediahub.tv`.
- Existing namespace remains unchanged until individual package moves are justified and compiled.
- Existing working Media3/VOD/subtitle/addon paths remain available while adapters are introduced.
- No Android source commit may require a Nuvio cloud secret merely to compile.
- No real signing secret is committed.
- The universal APK remains the installation artifact for the target test device.
- Branding is migrated into source before the CI-time branding substitution is removed.
- Auth is migrated behind the existing repository/DI boundary before old Nuvio cloud wiring is deleted.
- Live seek changes are constrained to live playback behavior and must not alter VOD seeking.
