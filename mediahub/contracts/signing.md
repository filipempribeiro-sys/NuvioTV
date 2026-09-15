# Android signing policy

MEDIA•HUB update compatibility requires one persistent signing identity. CI-generated ephemeral keystores are permitted only for disposable development artifacts and must never be used for a release candidate intended to update an installed candidate.

Required CI secrets when release signing is enabled:

- `MEDIA_HUB_KEYSTORE_BASE64`
- `MEDIA_HUB_KEY_ALIAS`
- `MEDIA_HUB_KEY_PASSWORD`
- `MEDIA_HUB_STORE_PASSWORD`

The keystore and passwords must never be committed. CI should record the public certificate SHA-256 fingerprint as release evidence without exposing private material. A changed fingerprint blocks in-place update promotion unless an intentional signing migration is performed.
