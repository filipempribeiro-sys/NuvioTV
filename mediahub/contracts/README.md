# MEDIA•HUB contracts

These files define the boundary between the Android client, MEDIA•HUB Cloud and media providers.

The client starts from one public discovery URL. The discovery document gives the API base URL and supported capabilities. This removes the previous requirement for Nuvio Supabase configuration from the MEDIA•HUB authentication path.

`client-config.example.json` intentionally contains placeholders. Real deployment URLs are configuration, not source secrets.
