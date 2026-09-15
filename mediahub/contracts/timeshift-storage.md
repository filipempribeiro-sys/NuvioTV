# Local timeshift storage policy

Extended timeshift is a bounded local cache, not permanent recording.

- default target window: 30 minutes when enabled and storage permits;
- configurable hard ceiling: 120 minutes;
- enforce both duration and byte quotas;
- write off the main/UI thread;
- segment/index updates must be crash-tolerant;
- evict oldest playable segments first;
- release channel resources on channel change/player teardown;
- purge invalid/orphaned buffers on startup;
- never persist DRM keys or bypass protected-stream restrictions;
- if quota, filesystem or stream format prevents buffering, continue normal live playback.

Actual implementation must be benchmarked on target Android storage before stable promotion.
