# MEDIA•HUB rollback policy

The known-working Android baseline remains untouched while `media-hub-platform` evolves. Every architectural milestone is committed independently so regressions can be bisected or reverted without reconstructing earlier state.

Do not force-update the baseline branch to a development commit. Promotion occurs only after the exact candidate passes the release checklist. Backend schema migrations intended for production require a rollback/restore plan and backup before destructive changes.

A failed experiment is reverted at its boundary rather than covered with another runtime/build patch.
