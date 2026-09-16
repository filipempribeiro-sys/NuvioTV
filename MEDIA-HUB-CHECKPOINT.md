# MEDIA•HUB — Development Checkpoint

Saved: 2026-09-16
Branch: `media-hub-platform`
Repository: `filipempribeiro-sys/NuvioTV`

## Current continuation point

Latest code commit before this checkpoint:
- `3c2b5dbe20b4d57ac9f925d3cec7d3a55a371af3` — `build: resolve MEDIA-HUB native runtime collisions`

Recovery checkpoint from earlier in the session:
- `3fc2a8973a6712bec012a6c526529c62c5c53029`

## Current build state

GitHub Actions workflow: `MEDIA-HUB Android Check`.

Latest run started before this checkpoint:
- Run #30
- Run ID: `35114346400`
- Head SHA: `3c2b5dbe20b4d57ac9f925d3cec7d3a55a371af3`
- It was queued immediately after the latest build fix when this checkpoint was written.

Previous run #29 reached an important milestone:
- `Compile MEDIA-HUB full debug` succeeded.
- `Assemble MEDIA-HUB full debug APK` failed.
- Therefore the Kotlin/application compilation is already passing; the remaining blocker observed in #29 was APK packaging, not Kotlin compilation.

The #29 assemble failure was caused by duplicate native runtime files (`libc++_shared.so`) supplied by inherited native playback components (notably MPV/ASS). The latest commit applies the inherited Nuvio packaging strategy using `packaging { jniLibs { pickFirsts ... } }` so those native runtime collisions can be resolved without removing MPV or ASS support.

### FIRST ACTION IN THE NEXT CONVERSATION

Inspect GitHub Actions run **#30 / ID `35114346400`** immediately.

- If green: fetch its workflow artifacts, download the MEDIA•HUB APK artifact and give Filipe the installable test package as soon as possible.
- If red: inspect the exact failed job/step/log, fix only the genuine current blocker, commit, and inspect the new run.
- If still running: inspect step state and do not make a commit that would unnecessarily cancel/supersede it.

## Current architecture

- Preserve the inherited Nuvio playback engine instead of rebuilding the player.
- MEDIA•HUB cloud/auth is canonical under `com.nuvio.tv.mediahub`.
- `MediaHubCloudApiClient` provides authenticated account/profile/sync access.
- `MediaHubSessionStore` owns MEDIA•HUB session tokens/account/device state.
- `MediaHubAuthStateStore` is the MEDIA•HUB authentication boundary.
- `MediaHubAddonCloudSync` bridges authenticated MEDIA•HUB `/sync` addon data into inherited `AddonPreferences`.
- `NuvioApplication` observes MEDIA•HUB authentication state and pulls cloud addons at application lifecycle level.
- QR authentication remains focused on authentication; do not duplicate addon-sync observers in the QR ViewModel.
- Remote empty/unknown addon payloads must not erase a working local source configuration.
- Do not create a second Retrofit/session implementation for MEDIA•HUB.
- Do not replace/reinvent the existing Stream -> Player navigation/playback stack.

## Playback path already audited

`Home/Detail -> StreamScreen -> StreamScreenViewModel -> stream resolution -> PlayerScreen`

The inherited path already preserves stream URL/torrent data, headers, metadata, addon identity, language, season/episode, autoplay, return navigation and player state. MEDIA•HUB should feed this engine rather than replace it.

## Addon cloud sync state

`MediaHubAddonCloudSync` currently:
- supports the inherited addon object schema: `url`, `sort_order`, `enabled`, optional `name`;
- accepts wrapper payload keys such as `addons`, `items`, `sources` and direct arrays;
- sorts using remote `sort_order` with source order fallback;
- writes remote sources directly into `AddonPreferences`;
- refuses to erase a working local source configuration when remote payload is unknown/empty;
- wraps local push reads/API calls safely and returns `Result<Int>`.

The inherited `AddonSyncService` schema was checked and deliberately matched. Do not invent cloud profile-ID mapping: the inherited playback/profile path still uses numeric `Int` profile IDs.

## Next functional work after APK exists

1. Sofa test: Home -> detail -> Play -> stream list -> select stream -> Player.
2. Inspect the lower mutation methods in `AddonRepositoryImpl.kt`.
3. Wire a safe/debounced MEDIA•HUB `pushLocal()` after explicit local addon mutations, with guards to avoid cloud-pull/legacy-sync loops.
4. Continue cloud migration with watch-progress/library/favourites/settings through the existing `/sync` contract, without inventing backend endpoints.
5. Preserve inherited working functionality throughout the migration.

## Important build details

- MEDIA•HUB full debug uses `applicationId = "com.mediahub.tv"`.
- CI uses `CI_USE_DEBUG_SIGNING=true` and Android debug signing for the development APK.
- Workflow compiles `:app:compileFullDebugKotlin`, assembles `:app:assembleFullDebug`, then uploads `app/build/outputs/apk/full/debug/*.apk`.
- The inherited native player stack and repository AARs must remain intact.
- `libmpv-android` is a gitlink/submodule-like entry without `.gitmodules`; do not casually remove it.
- Do not expose or commit private keystores/passwords.
- Top-level compatibility dependencies are attached to base `implementation`; do not move them back to root-level `fullImplementation` because that configuration is not guaranteed to exist when the callback executes.
- The `fullImplementation` AAR declaration inside `app/build.gradle.kts` is different and valid because it is declared after Android flavor configuration.

## Working rules

- Filipe normally types `SIGA`; use each turn for substantial inspect -> modify -> commit -> validate progress.
- Routine/reversible fixes: proceed autonomously.
- Ask only for structural/high-risk decisions, destructive changes, credentials or major product choices.
- Create rollback/checkpoint commits at stable milestones.
- Never claim a commit/build/APK succeeded without tool evidence.
- Do not spend turns on old CI archaeology. Fix the exact current blocker and move forward.
- Do not promise background execution; ordinary chat only progresses when Filipe sends the next message.
- Keep MEDIA•HUB branding and progressive independence from Nuvio while preserving working functionality during migration.

## Resume instruction

In a new conversation, read this file first, inspect the current HEAD of `media-hub-platform`, then inspect Actions run **#30 (`35114346400`)**. Continue from there without restarting the architecture discussion. The immediate priority is obtaining the installable MEDIA•HUB APK and performing the sofa test.
