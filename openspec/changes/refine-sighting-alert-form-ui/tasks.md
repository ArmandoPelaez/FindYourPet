## 1. Sighting Form UI

- [x] 1.1 Refactor `SightingAlertAdaptiveContent` so compact, centered and expanded layouts no longer render the referenced pet media/status header.
- [x] 1.2 Remove `SightingPetSummaryCard` from the main sighting report form and delete or de-scope unused UI helpers when they are no longer referenced.
- [x] 1.3 Replace the current `SightingPhotoAttachment` plus detached gallery/camera button row with a single optional upload surface aligned with the lost-pet publication photo pattern.
- [x] 1.4 Keep existing camera and gallery callbacks, selected-photo preview behavior, sighting-specific content descriptions and optional-photo semantics.
- [x] 1.5 Preserve the existing location text field, current-location action, notes field, auth/form messages and bottom `ENVIAR ALERTA` action behavior.
- [x] 1.6 Verify the new visual spacing keeps the first mobile viewport focused on report controls without adding owner contact, exact coordinates or new permission prompts.

## 2. Tests

- [x] 2.1 Update `SightingAlertAdaptiveLayoutTest` assertions that currently expect `sighting-media-header`, `sighting-info-card` or "Reportando avistamiento de:" in the enabled report form.
- [x] 2.2 Add or update tests that assert the simplified sighting form shows the optional upload surface and retains camera/gallery actions.
- [x] 2.3 Add or update tests that assert the removed pet media/header and summary card are absent across compact, centered and expanded layout variants.
- [x] 2.4 Add or update tests that assert self-report blocking, protected contact/coordinate absence and submit action behavior remain unchanged.

## 3. Verification

- [x] 3.1 Run `openspec validate refine-sighting-alert-form-ui --strict`.
- [x] 3.2 Run the focused affected tests, including `SightingAlertAdaptiveLayoutTest` and any sighting static/UI tests updated for this change.
- [x] 3.3 Run `./gradlew.bat testDebugUnitTest`.
- [x] 3.4 Run `./gradlew.bat assembleDebug`.
- [x] 3.5 Manually inspect the sighting alert screen on supported phone and wide layouts to confirm no overlapping/clipped text, no removed crossed-out content, intact camera/gallery/location flows, and unchanged bottom submit behavior.
