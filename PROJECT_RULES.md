# PROJECT RULES — GitHub Actions

## Mandatory operating rule

GitHub Actions minutes must be protected during normal development.

- Checkpoints, routine commits and rollbacks must NOT intentionally trigger APK builds, releases, wake jobs or other expensive CI jobs.
- APK/build/release workflows must remain manual (`workflow_dispatch`) unless Filipe explicitly authorizes an automatic trigger.
- Render/backend wake workflows must remain manual (`workflow_dispatch`) unless Filipe explicitly authorizes reactivation of a schedule.
- Do not add or reactivate `schedule`, broad `push`, `pull_request` or other automatic triggers for expensive jobs without explicit authorization from Filipe.
- Before changing `.github/workflows/`, audit the resulting automatic Actions impact.
- Prefer batching routine changes/checkpoints so that unavoidable deployment workflows are not triggered unnecessarily.
- A change that introduces new automatic GitHub Actions consumption is a CRITICAL CHANGE and requires Filipe's approval before implementation.

## Development principle

Normal coding and checkpointing should consume zero GitHub-hosted runner minutes whenever technically possible. Generate an APK/release only when explicitly requested.
