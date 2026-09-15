# MEDIA•HUB Provider Boundary

MEDIA•HUB consumes media through providers. PT•HUB is the primary configurable Portuguese hub/provider and remains independently deployable.

A provider can expose:

- catalogs
- metadata
- streams
- subtitles
- channels
- EPG references

Provider failures are isolated. Authentication/account state belongs to MEDIA•HUB Cloud and must not be coupled to PT•HUB or any external Stremio-compatible addon.

For Stremio-compatible providers, MEDIA•HUB preserves manifest/provider identity and consumes standard catalog/meta/stream/subtitle resources. External-player URLs remain external unless the provider legitimately supplies a direct playable resource.
