# MEDIA•HUB API v1

## Public

`GET /.well-known/mediahub` — product/API discovery and capability negotiation.

`POST /v1/device-pairing` — creates a short-lived TV pairing challenge and returns the device secret used to authenticate polling.

`GET /v1/device-pairing/{id}` — returns pending/approved state. Requires `X-MediaHub-Device-Secret`.

`POST /v1/device-pairing/{id}/approve` — bootstrap approval contract. Production implementation must require an authenticated account session in addition to the one-time code.

## Device session

`GET /v1/me` — account and current device.

`GET|PUT /v1/profiles` — profile collection, maximum eight profiles in the bootstrap contract.

`GET|PUT /v1/sync` — library, watch progress, favourites, settings and addon configuration snapshot.

Authenticated endpoints use `Authorization: Bearer <access-token>`.

## Versioning rule

Breaking wire-format changes require a new API version. Capability additions remain discoverable and clients must tolerate unknown capability keys.
