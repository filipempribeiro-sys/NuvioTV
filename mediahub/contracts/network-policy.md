# MEDIA•HUB network policy

- HTTPS is mandatory for production MEDIA•HUB Cloud endpoints.
- Discovery and API calls use explicit connect/read timeouts and bounded retries.
- Retry only idempotent operations automatically unless an idempotency mechanism exists.
- QR polling follows the server-provided interval and stops at expiry.
- Provider requests are isolated and cancellable; one slow addon cannot block the full Home screen.
- Cache validated discovery/provider metadata with bounded TTL to improve startup resilience.
- Never send MEDIA•HUB account bearer tokens to PT•HUB or third-party providers.
- Never send provider/IPTV credentials to MEDIA•HUB Cloud unless a future explicitly encrypted sync feature requires and documents it.
