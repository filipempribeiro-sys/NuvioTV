# PT•HUB integration in MEDIA•HUB

PT•HUB is configured as a first-class provider using its Stremio-compatible manifest URL. MEDIA•HUB consumes only the resources the manifest advertises and preserves configuration encoded by the provider.

## Client behavior

- Discover/validate the manifest before exposing provider-backed rows.
- Cache manifest/catalog metadata with bounded TTLs.
- Isolate provider network failures from Home/navigation.
- Keep IPTV channel identity stable across EPG/favourites/player state.
- Preserve provider stream names and source identity.
- Do not convert external-player pages into fake internal streams.
- Do not bypass DRM, access controls, advertising gates or anti-bot protections.

## Account sync

MEDIA•HUB may sync the user's PT•HUB configuration/manifest URL as an addon setting. Credentials embedded in a user-supplied IPTV configuration must be treated as sensitive local/provider configuration and must not be logged.

## Player handoff

Playable direct streams are handed to the existing Media3 player pipeline. Live channels additionally expose the live timeline capability contract so seek/timeshift UI reflects reality.
