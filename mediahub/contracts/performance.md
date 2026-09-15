# MEDIA•HUB performance budget

The structural transformation must not repeat the earlier regression where additional UI behavior made the app noticeably slower.

Targets for release-candidate validation on the target Android TV/device class:

- navigation/focus input remains responsive without blocking network calls on the UI thread;
- Home/provider rows load progressively and independently;
- images use bounded caches and placeholders;
- opening the player does not wait for EPG beyond information required for playback;
- QR polling respects server interval and stops when screen/session is disposed;
- provider retries use bounded timeouts/backoff;
- timeshift disk I/O is bounded and performed off the main thread;
- background buffers, network requests and coroutines are cancelled/released on lifecycle transitions.

Performance regressions block stable promotion even when functional tests pass.
