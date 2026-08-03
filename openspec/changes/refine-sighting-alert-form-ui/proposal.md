## Why

The sighting alert screen currently repeats lost-pet detail UI above the actual report form, which consumes the first mobile viewport and distracts from the urgent task: reporting where the pet was seen. This change simplifies the visual hierarchy so reporters can attach evidence, enter location/details and send the alert faster, while preserving the existing sighting logic, validation, media handling and privacy guardrails.

## What Changes

- Remove the leading lost-pet media/header block and the "Reportando avistamiento de..." summary card from the sighting alert form path shown in the provided screenshot.
- Restyle the optional sighting photo section to follow the same single upload-surface pattern used by the lost-pet publication screen, reusing the current camera and gallery launchers.
- Keep the selected-photo preview, camera option and gallery option available through the existing real media paths without introducing new upload providers, preset demo media or new permissions.
- Keep the sighting location field, current-location action, notes field, validation messages and bottom submit action behavior intact.
- Tighten spacing and section hierarchy so the first mobile viewport prioritizes reporting controls instead of repeated pet information.
- Preserve existing ownership checks, reporter eligibility, backend write path, chat/notification fan-out, media upload validation and location consent behavior.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `sightings`: The sighting report form presentation is simplified and its optional photo input is aligned with the lost-pet publication photo pattern while preserving existing sighting submission behavior.

## Impact

- Affected code: `app/src/main/java/com/findyourpet/app/ui/screens/SightingAlertScreen.kt`.
- Tests/validation: update sighting UI/static tests to assert the removed pet summary/header are no longer part of the compact report form, the upload surface keeps camera/gallery access, and existing submit/location behavior remains unchanged.
- Privacy/security: no new personal data exposure, no new backend access rules, no new Android permissions and no change to consented location handling.
- Data/API: no Firestore, Room, repository, ViewModel, validator or model schema changes expected.
- Existing users: users see a shorter, clearer alert screen; submitted sighting records continue to use the same required/optional fields and backend behavior.
- Rollback: revert the Compose presentation and related tests; no data migration or backend rollback is required.
