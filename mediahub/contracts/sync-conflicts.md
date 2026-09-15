# MEDIA•HUB sync conflict policy

Durable sync uses monotonically increasing revisions. A client sends the revision it last observed with a mutation. If the server revision has advanced, it returns a conflict/current snapshot instead of silently overwriting unrelated device changes.

Merge policy may be domain-specific: watch progress prefers the newest timestamped progress event; favourites/library use idempotent set operations/tombstones; settings use per-key update timestamps; addon/provider configuration requires explicit whole-item replacement for sensitive structural changes.

The bootstrap `/v1/sync` snapshot endpoint does not yet implement revision conflicts and therefore is not production-complete.
