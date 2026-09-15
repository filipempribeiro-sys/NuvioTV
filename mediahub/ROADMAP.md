# MEDIA•HUB execution roadmap

## Foundation — active

- [x] Dedicated transformation branch
- [x] Platform boundary
- [x] Discovery contract
- [x] Device-bound QR pairing contract/bootstrap
- [x] Profiles and sync API contract/bootstrap
- [x] Provider boundary
- [x] Live TV/timeshift contract
- [x] Backend CI smoke test
- [x] Reproducible backend container
- [ ] Durable database/session persistence

## Android source transformation

- [ ] Replace Nuvio cloud auth repository with MEDIA•HUB discovery/pairing client
- [ ] Replace Nuvio sync/account endpoints
- [ ] Move MEDIA•HUB identity into runtime source resources/components
- [ ] Remove build-time branding substitution
- [ ] Integrate PT•HUB as first-class provider configuration
- [ ] Preserve Trakt/SIMKL as independent optional integrations

## Player and Live TV

- [ ] Enable live seeking when Media3 timeline is seekable
- [ ] Add Return to LIVE and live-edge state
- [ ] EPG domain/UI
- [ ] Local extended timeshift buffer with graceful fallback

## Product UX

- [ ] MEDIA•HUB splash/onboarding
- [ ] Home/navigation redesign
- [ ] Search/details/cards redesign
- [ ] Settings/account/devices/profiles redesign
- [ ] Player controls redesign

## Release candidate

- [ ] Persistent signing identity
- [ ] Clean Android CI build
- [ ] Universal APK
- [ ] Installation/update verification
- [ ] Core navigation/playback smoke tests
- [ ] Stability gate fully green
