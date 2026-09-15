# Exact-source audit checklist before Android mutation

For the current `media-hub-platform` head, locate and read the exact existing files implementing:

- app Gradle BuildConfig fields and flavors;
- AndroidManifest label/icon/banner/theme;
- default + locale `app_name` resources;
- splash and onboarding composables/resources;
- TV login/QR view model, repository, API and DI binding;
- account/session/sync repositories and storage;
- addon manifest/provider repositories;
- Home/search/details view models and composables;
- player runtime controller event handling;
- live timeline state and player controls;
- GitHub Actions workflow currently performing MEDIA•HUB resource substitution.

Do not mutate a guessed path. Each source integration commit should identify the existing abstraction it replaces/adapts and preserve compile compatibility.
