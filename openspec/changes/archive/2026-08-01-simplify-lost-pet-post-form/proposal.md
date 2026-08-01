## Why

The lost-pet post form currently asks users to move through several separate inputs and explanatory blocks before submitting, which makes the posting flow feel heavier than necessary when someone needs to publish quickly. This change simplifies the visual hierarchy of the create-post screen using the provided reference as a guide, while preserving the existing responsive layout constraints, validation rules, privacy guardrails and data model.

## What Changes

- Restyle the create-post screen so the primary photo action, pet data and location sections read as a shorter, easier-to-scan flow.
- Replace the separate photo label plus gallery/camera action row with a single photo upload area styled like the reference, reusing the current camera and gallery behavior without adding new media sources.
- Reduce the visible pet-data inputs to the essential publishing fields: pet name and one useful-details field for recognition notes such as color, marks, collar or temperament.
- Keep optional backend/model values such as breed, color, species and reward populated through the existing defaults or existing field mapping so submission behavior remains compatible.
- Keep the location input as a manual/coarse last-seen-location field styled like the reference, but do not introduce a current-location button or any new GPS permission behavior in this visual-only change.
- Remove or visually de-emphasize non-essential explanatory UI in the primary form path, without exposing contact details or changing the internal-chat privacy model.
- Preserve existing screen responsiveness, established component sizes, scrolling behavior and submit-button behavior.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `pet-posts`: The create-post form presentation is simplified visually while preserving production post validation, authenticated ownership and backend-compatible field mapping.

## Impact

- Affected code: `app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt`.
- Tests/validation: add or update UI/static tests that assert the simplified form keeps required fields, keeps the submit button disabled until photo/name/location are present, omits unsupported current-location UI in the post screen, and does not alter production validation.
- Privacy/security: no new sensitive data exposure, no new contact sharing, no new permissions and no change to backend access rules.
- Data/API: no Firestore, Room, repository, ViewModel or model schema changes expected.
- Existing users: users see a simpler posting form, but saved post behavior and required submission conditions remain the same.
- Rollback: revert the screen presentation and related tests; no migration or backend rollback is required.
