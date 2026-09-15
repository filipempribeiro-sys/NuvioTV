# MEDIA•HUB Android public configuration

The MEDIA•HUB Android flavor needs one public bootstrap value:

`MEDIAHUB_DISCOVERY_URL=https://<host>/.well-known/mediahub`

It must not require `NUVIO_SUPABASE_URL` or `NUVIO_SUPABASE_ANON_KEY` for MEDIA•HUB authentication.

The discovery URL is public configuration and may be compiled into BuildConfig. Secrets, database credentials, service-role keys and signing passwords must never be compiled into the APK.

The Android repository layer should fetch discovery, cache it with a bounded TTL, negotiate capabilities, then call the returned API base URL. A discovery outage may use a previously validated cached document for a bounded period; it must not silently fall back to Nuvio cloud.
