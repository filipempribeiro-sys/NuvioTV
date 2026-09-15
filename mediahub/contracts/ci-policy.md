# MEDIA•HUB CI policy

CI validates source; it must not manufacture the final product identity by rewriting source resources during the build.

Allowed CI actions include dependency setup, lint/tests, generated non-secret build metadata, decoding user-configured signing material into an ephemeral runner file, building variants, verifying resource/package identity and publishing artifacts.

Temporary workflow-based branding substitution from the early alpha is a migration aid only. Once source branding is complete it must be removed. Likewise, CI must validate `MEDIAHUB_DISCOVERY_URL` for release candidates rather than requiring Nuvio Supabase credentials.

A successful CI run proves only the checks it actually ran; it is not by itself proof of stable runtime behavior.
