# Kotlin reference contracts

The `.kt` files in this contracts directory are intentionally small, dependency-free reference boundaries for the Android migration. They are **not yet wired into the app module** and therefore must not be counted as completed runtime features.

Their purpose is to make the target domain explicit before replacing existing Nuvio repositories/UI/player paths: account/QR, session storage, providers, Home/search, EPG, live timeline, timeshift, settings and diagnostics.

During integration these models should be moved/adapted into the existing module/package structure rather than adding a parallel architecture that duplicates working NuvioTV abstractions.
