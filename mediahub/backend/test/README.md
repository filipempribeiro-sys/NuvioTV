# Backend tests

`npm test` exercises the current MEDIA•HUB bootstrap service as black-box HTTP flows:

- production configuration guard;
- discovery contract;
- QR creation, landing page, device-secret-bound polling and approval;
- session issuance and authenticated `/v1/me`;
- profiles and sync round trip;
- unauthenticated/wrong-secret/wrong-code rejection;
- CORS preflight.

These tests validate the bootstrap contract only. They do not replace Android instrumentation, persistent database integration, production deployment or real-device playback testing required by the stability gate.
