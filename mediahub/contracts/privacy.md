# MEDIA•HUB data minimisation

MEDIA•HUB Cloud stores only data required for account/device continuity and explicitly enabled synchronisation: account identifier, profiles, registered devices, library/favourites, watch progress, settings and addon/provider configuration selected for sync.

Sensitive provider credentials should remain local whenever cross-device sync is not essential. If credential sync is later introduced, it requires dedicated encrypted-at-rest handling and a separate threat review.

Operational logs must redact bearer/refresh tokens, pairing codes, device secrets, IPTV usernames/passwords and configured URLs containing credentials. Device revocation must invalidate its server-side sessions. Account deletion must define deletion of associated profiles, devices and sync state.
