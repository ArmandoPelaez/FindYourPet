## 1. Google Maps setup

- [x] 1.1 Keep fixed, compatible Maps SDK for Android and Maps Compose versions in the version catalog and app dependencies.
- [x] 1.2 Keep the Secrets Gradle Plugin configuration, `MAPS_API_KEY` manifest metadata and placeholder key without committing real credentials.
- [x] 1.3 Document Maps SDK activation, API-key restrictions, quotas and local fallback configuration.
- [x] 1.4 Declare optional Apache HTTP legacy compatibility for Maps renderer variants that require it.

## 2. Location selection state and services

- [x] 2.1 Keep the normalized location selection model with display text, latitude, longitude and `LocationSource`.
- [x] 2.2 Reuse `DeviceLocationProvider` and the existing runtime permission flow for current location, including denied and unavailable states.
- [x] 2.3 Keep a Google Maps Compose picker with one pending marker and confirmation before returning coordinates.
- [x] 2.4 Remove Places client initialization, Places Autocomplete, address search code and all Places dependencies.
- [x] 2.5 Provide a safe non-empty map label without opening an automatic manual-reference fallback or persisting an empty location.
- [x] 2.6 Attempt asynchronous Android Geocoder reverse geocoding after a map tap and GPS capture, preserving safe source labels on unavailable, error or empty results.

## 3. Create-post UI

- [x] 3.1 Keep the Design System label `¿Donde fue vista por ultima vez?` and placeholder `Seleccionar ubicacion`.
- [x] 3.2 Expose only `Usar mi ubicacion actual`, `Elegir en el mapa` and `Escribir una referencia`.
- [x] 3.3 Integrate GPS, map and manual-reference results while preserving light/dark theme, scrolling, spacing and field hierarchy.
- [x] 3.4 Show selected public-safe location text and avoid the decorative last-location label or icon from the reference image.
- [x] 3.5 Keep publication disabled or validation-visible until photo, pet name and a valid location selection are present.

## 4. Persistence, privacy and documentation

- [x] 4.1 Pass the normalized selection through `createNewPetPost` and existing repository/mappers.
- [x] 4.2 Persist `lastSeenLocation`, coordinates and `locationSource` consistently for GPS, map and manual selections.
- [x] 4.3 Ensure public presentation never renders precise coordinates.
- [x] 4.4 Allow valid lost-pet coordinates in Firestore rules while continuing to reject owner coordinates and contact fields.
- [x] 4.5 Remove Places from README, privacy, data-safety, permissions and Maps setup documentation.

## 5. Automated tests

- [x] 5.1 Update presentation tests for the three location choices and absence of Places/search markers.
- [x] 5.2 Keep unit tests for source transitions, coordinate clearing, denied permissions and empty selections.
- [x] 5.3 Keep mapper tests for GPS, map and manual selections.
- [x] 5.4 Remove Places-specific tests and test fixtures.
- [x] 5.5 Cover automatic map/GPS references and safe-label states without automatic manual fallback in static or unit tests.

## 6. Validation

- [x] 6.1 Run `openspec validate "replace-free-text-location-input" --strict`.
- [x] 6.2 Run `./gradlew.bat testDebugUnitTest` and `./gradlew.bat assembleDebug`.
- [x] 6.3 Run a manual device/emulator check with a restricted Maps key for GPS, map tap, manual reference, denied permission, offline/API error and light/dark themes.
- [x] 6.4 Verify no Places dependency, import, initialization, API key or precise coordinates are present in public UI or notifications.
