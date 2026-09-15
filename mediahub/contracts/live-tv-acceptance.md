# Live TV acceptance cases

1. **Non-seekable live:** channel plays; pause/seek controls follow Media3 support; no fake rewind control is shown.
2. **Seekable upstream window:** rewind/forward work inside `windowStartMs..windowEndMs`; Return to LIVE reaches live edge.
3. **Local extended buffer:** buffer survives ordinary player UI transitions and exposes its actual duration; seeking never targets data already evicted.
4. **Buffer failure/full storage:** player continues ordinary live playback and explains that extended timeshift is unavailable.
5. **Channel change:** previous buffer is released according to retention policy; new channel starts without waiting for a full DVR window.
6. **App background/relaunch:** no corrupted buffer is reused; playback state is restored only when valid.
7. **EPG:** now/next follows channel id and timezone-aware timestamps; guide failure does not stop playback.
8. **Protected/inaccessible stream:** MEDIA•HUB does not bypass access controls; it reports playback failure and preserves navigation state.
