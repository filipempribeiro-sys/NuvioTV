# MEDIA•HUB — Development Checkpoint

Saved: 2026-09-16
Branch: `media-hub-platform`
Repository: `filipempribeiro-sys/NuvioTV`

## Stable continuation point

Last functional checkpoint before this document:
- `b0dee04b36f6a4932826f764512338ff486bec69` — `feat: bridge MEDIA-HUB cloud addons into playback stack`

Last build-fix checkpoint before this document:
- `5aebba7fb69e745355964c4a379c457896ddcc20` — `build: remove unavailable Sentry Timber dependency`

## Current architecture

- Preserve the inherited Nuvio playback engine instead of rebuilding the player.
- MEDIA•HUB cloud/auth is canonical under `com.nuvio.tv.mediahub`.
- `MediaHubCloudApiClient` provides authenticated account/profile/sync access.
- `MediaHubSessionStore` owns MEDIA•HUB session tokens/account/device state.
- `MediaHubAuthStateStore` is the MEDIA•HUB authentication boundary.
- `MediaHubAddonCloudSync` bridges the authenticated MEDIA•HUB `/sync` addon payload into the inherited `AddonPreferences` source engine.
- Remote empty/unknown addon payloads must not erase a working local source configuration.
- Do not create a second Retrofit/session implementation for MEDIA•HUB.
- Do not replace/reinvent the existing Stream -> Player navigation/playback stack.

## Playback path already audited

`Home/Detail -> StreamScreen -> StreamScreenViewModel -> stream resolution -> PlayerScreen`

The existing navigation preserves stream URL/torrent data, headers, metadata, addon identity, language, season/episode and player return/autoplay behavior. Future MEDIA•HUB work should feed this engine rather than replacing it.

## Build state

- CI uses `CI_USE_DEBUG_SIGNING=true`.
- `app/build.gradle.kts` conditionally uses Android debug signing in CI while retaining the existing release signing path outside CI.
- The unavailable `io.sentry:sentry-timber` dependency was removed; Sentry Android/Compose/OkHttp and normal Timber remain.
- Continue resolving only genuine current build blockers; do not return to old CI archaeology.

## Next execution target

1. Validate the build after the Sentry Timber cleanup and fix any current compile/assemble blocker.
2. Wire `MediaHubAddonCloudSync` into a safe authenticated lifecycle/sync point so cloud addon/source configuration actually reaches the existing catalog/stream engine.
3. Preserve inherited numeric profile handling until MEDIA•HUB cloud profile ID compatibility is explicitly verified.
4. Validate the end-to-end sofa path: Home -> content detail -> source/stream -> Player.
5. Produce an installable MEDIA•HUB development APK.

## Working rules

- Routine/reversible changes: proceed autonomously.
- Ask Filipe only for structural/high-risk decisions, destructive changes, credentials or major product choices.
- Create rollback/checkpoint commits at stable milestones.
- Never claim a commit/build/APK succeeded without GitHub/tool evidence.
- Do not spend a full turn only watching CI; functional application progress has priority.
- Keep MEDIA•HUB branding and progressive independence from Nuvio while preserving working functionality during migration.

## Resume instruction

If a chat is lost, read this file first, inspect the current HEAD of `media-hub-platform`, inspect the latest Actions run, and continue from the **Next execution target** without restarting the architecture discussion.
