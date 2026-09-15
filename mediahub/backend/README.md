# MEDIA•HUB Backend

Independent service boundary for MEDIA•HUB accounts, devices, QR pairing, profiles and sync.

Current implementation is deliberately dependency-light so the API contract can be validated immediately in CI. It is a bootstrap service: state is currently in memory and therefore does not yet satisfy the durable-storage stability gate.

Run:

```sh
npm run check
npm test
npm start
```

Public discovery: `GET /.well-known/mediahub`

Device pairing: `POST /v1/device-pairing` → authenticated approval → device-secret-bound polling → session.

Authenticated bootstrap endpoints: `GET /v1/me`, `GET/PUT /v1/profiles`, `GET/PUT /v1/sync`.

No Nuvio cloud credential is used by this service.
