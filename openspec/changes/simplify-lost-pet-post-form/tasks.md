## 1. Create-Post Visual Simplification

- [x] 1.1 Refactor `CreatePetPostScreen` so the form reads as photo upload, pet data, location and publish while keeping the existing `Scaffold`, scroll container, outer padding rhythm and submit-button sizing.
- [x] 1.2 Replace the separate photo title, preview box and gallery/camera row with a single upload surface inspired by the reference that still uses the existing camera and gallery launchers.
- [x] 1.3 Replace the visible breed/color row and characteristics field with one recognition-details field, mapping it into the existing post creation call while keeping backend-compatible defaults for hidden optional values.
- [x] 1.4 Restyle the manual last-seen-location input to match the simplified visual hierarchy without adding current-location capture, new location permissions or GPS state to the create-post screen.
- [x] 1.5 Keep publish enablement and submission behavior tied to the existing required state: real photo, non-blank pet name, non-blank manual location and not submitting.

## 2. Tests

- [x] 2.1 Add or update Compose/static tests that assert the simplified form shows the essential fields and no longer requires separate visible breed/color inputs.
- [x] 2.2 Add or update tests that assert the create-post screen does not contain current-location capture UI or request new post-location permission behavior.
- [x] 2.3 Add or update tests that assert publish remains unavailable until photo, pet name and location are present.
- [x] 2.4 Run the focused affected tests, including create-post UI/static tests and existing release-readiness guardrails.

## 3. Verification

- [x] 3.1 Run `openspec validate simplify-lost-pet-post-form --strict`.
- [x] 3.2 Run `./gradlew.bat testDebugUnitTest`.
- [x] 3.3 Run `./gradlew.bat assembleDebug`.
- [x] 3.4 Manually inspect the create-post screen on supported phone sizes to confirm no text overlap/clipping, stable scrolling, unchanged top bar behavior and no new GPS/current-location action.
