# MEDIA•HUB Live TV / Timeshift Contract

Live TV is a first-class MEDIA•HUB domain, independent from movie/series VOD.

## Channel

A channel has a stable provider-qualified id, display name, optional logo, stream candidates and EPG identity. PT•HUB/IPTV sources remain providers; MEDIA•HUB does not rewrite their identity.

## Playback timeline

The player exposes these runtime facts rather than assuming all live streams are seekable:

- `isLive`
- `isSeekable`
- `windowStartMs`
- `windowEndMs`
- `currentPositionMs`
- `liveEdgeOffsetMs`

Pause is allowed whenever the underlying player supports it. Rewind/forward are enabled only when the active Media3 timeline exposes a seekable live window. `Return to LIVE` seeks to the current live edge.

## Extended local timeshift

A requested DVR window longer than the upstream HLS/DASH sliding window requires a local recording/cache layer. The target architecture is:

`IPTV/provider -> local timeshift buffer -> Media3 player`

The client must never pretend a 30-minute rewind exists when no local/upstream buffer exists. Capability is surfaced explicitly and controls adapt to the actual timeline.

## Safety and compatibility

MEDIA•HUB does not bypass DRM or access controls. Provider headers/cookies may only be forwarded when legitimately supplied by the configured source. Failure of timeshift must fall back to ordinary live playback rather than break the channel.
