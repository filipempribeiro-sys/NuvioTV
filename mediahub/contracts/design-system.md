# MEDIA•HUB Design System

## Identity

MEDIA•HUB is the product name. `TV • FILMES • SÉRIES` is the persistent descriptor where space permits. `TODO O ENTRETENIMENTO NUM SÓ LUGAR` is marketing/supporting copy, not a replacement for navigation labels.

## Core palette

- Green `#0B8F55`
- Dark green `#087044`
- Gold `#D2AA45`
- Red `#C92A35`
- Background `#090B0B`
- Elevated `#111514`
- Card `#171C1A`
- Surface `#121715`
- Surface variant `#1D2421`
- Panel `#101412`
- Field `#171D1A`
- Focus background `#17362B`
- Focus ring: gold
- Focus gradient: gold → green

## TV interaction

Focus must be obvious without resizing layouts unpredictably. Cards retain stable geometry; focus uses ring/elevation/controlled scale. Primary navigation is remote-first. Text must remain readable at television distance and not rely on hover.

## Product surfaces

Splash, onboarding, Home, navigation, search, details, profiles/account, settings and player share the same typography hierarchy, surfaces, focus language and MEDIA•HUB identity. Nuvio branding is not hidden by CI; it is replaced in the source components as each surface migrates.

## Motion/performance

Prefer short focus transitions and avoid effects that trigger expensive full-screen recomposition. Artwork loading must have placeholders and failure states. The renewed UI must not regress the performance of the known-working baseline.
