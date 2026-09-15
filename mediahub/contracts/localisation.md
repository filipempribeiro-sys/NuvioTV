# MEDIA•HUB localisation

MEDIA•HUB follows the Android device language by default. Portuguese (Portugal) and Portuguese (Brazil) remain distinct locales; the product name `MEDIA•HUB` and descriptor `TV • FILMES • SÉRIES` are brand strings and are not translated.

Source migration must audit locale-specific `app_name`, onboarding, account/QR and navigation resources so an old Nuvio product label cannot override the default resource. Do not solve this by globally forcing pt-PT.

Missing translations fall back through Android's normal resource resolution. User-selected language override, if retained from the base app, is explicit and reversible.
