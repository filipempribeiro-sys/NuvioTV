# MEDIA•HUB device session lifecycle

A paired device receives short-lived access credentials and a longer-lived refresh credential. Production storage persists only cryptographic token hashes. Tokens are scoped to an account and device.

Required lifecycle:

1. pair and register device;
2. issue access + refresh credentials;
3. refresh rotates the refresh credential and invalidates the previous value;
4. device revocation invalidates all sessions for that device;
5. account security reset may revoke every device session;
6. expired/revoked credentials return `unauthorized` and never silently switch to another account/cloud provider.

The bootstrap service currently proves pairing/session issuance only. Refresh rotation and durable revocation are mandatory before stable promotion.
