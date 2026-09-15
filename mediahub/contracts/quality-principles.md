# MEDIA•HUB quality principles

1. Fix root causes in source, not symptoms in CI.
2. Preserve working behavior before extending it.
3. A capability is not complete until integrated into the runtime path that uses it.
4. Never label a mock/bootstrap/placeholder as production functionality.
5. Fail locally: provider, EPG, cloud or artwork failures must not collapse unrelated surfaces.
6. UI changes are performance changes; remote responsiveness is part of correctness.
7. Live playback reports real seek/buffer capabilities instead of promising unavailable DVR.
8. Secrets never enter source control or APK resources.
9. Every release statement must correspond to evidence from the exact commit/artifact.
10. Keep rollback possible at architectural boundaries.
