# NuvioTV → MEDIA•HUB migration map

| Existing concern | MEDIA•HUB target |
|---|---|
| Nuvio app identity/resources | MEDIA•HUB source resources + design system |
| Nuvio Supabase auth requirement | MEDIA•HUB discovery + account/device API |
| Nuvio TV login/link URLs | MEDIA•HUB QR pairing approval URL |
| Nuvio account/sync | MEDIA•HUB `/v1/me`, profiles and sync |
| Existing addon engine | preserved, with PT•HUB first-class provider config |
| Existing catalog/search/details | preserved domain behavior, renewed MEDIA•HUB UI |
| Existing Media3/ExoPlayer | preserved and extended for truthful live seek/timeshift |
| `isLive` seek rejection | actual Media3 seekable-window policy |
| Existing Room/DataStore | local cache/session/settings adapters retained where suitable |
| Trakt/SIMKL | independent optional integrations retained |
| CI-time branding overwrite | removed after source identity migration |
| Ephemeral release signing | persistent signing identity for release candidates |

Migration is incremental at repository boundaries; it is not a global package rename. This minimizes regression risk while still replacing product ownership structurally.
