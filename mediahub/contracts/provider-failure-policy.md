# Provider failure policy

MEDIA•HUB treats every media provider as an independently failing dependency.

- Manifest failure disables only that provider and exposes retry/diagnostic state.
- Catalog failure affects only its row/section.
- Search aggregates successful providers even when another fails.
- Stream lookup failure keeps details/navigation state and may offer other legitimate providers.
- Subtitle failure does not abort video playback.
- EPG failure does not abort a live stream.
- PT•HUB/torrent-engine cold start may show bounded loading/retry but cannot freeze the whole UI.
- External-player-only results remain explicitly external.

No provider response may be trusted to supply account/session credentials for MEDIA•HUB Cloud.
