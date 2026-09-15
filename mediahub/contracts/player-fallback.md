# MEDIA•HUB player fallback rules

The player is a shared stable subsystem; feature additions must degrade without taking it down.

- VOD remains ordinary seekable playback and is not routed through timeshift logic.
- Live with a seekable upstream window uses that window directly.
- Live without a seekable upstream window may start an optional local buffer; playback starts immediately rather than waiting for buffer accumulation.
- If local buffering cannot initialize, continue the original live stream and hide/disable unavailable rewind controls.
- EPG/subtitle/artwork failures are non-fatal to video.
- Switching stream/provider tears down only resources owned by the old playback session.
- A player error returns to the existing details/live context rather than resetting application navigation.
