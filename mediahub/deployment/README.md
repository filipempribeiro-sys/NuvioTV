# MEDIA•HUB deployment

The backend can be run locally with Docker Compose or any compatible container host. The design intentionally avoids a dependency on Nuvio cloud services.

## Local verification

```sh
cd mediahub/backend
npm run check
npm test
```

or:

```sh
cd mediahub/deployment
docker compose up --build
```

The service exposes `/health` and `/.well-known/mediahub`.

## Production requirements

The current in-memory implementation is a contract/bootstrap service only. It is deliberately **not** classified as production/stable storage. Before the Android client is promoted to a stable release, accounts, profiles, device sessions, refresh tokens and sync state must use durable storage with migrations/backups. The production deployment must use HTTPS and a fixed public base URL.

Secrets must be supplied by the host secret store. Do not commit production values or Android signing credentials.
