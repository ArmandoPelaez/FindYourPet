## Context

`CreatePetPostScreen` is a Jetpack Compose form that already handles real camera/gallery media, manual coarse location, authenticated post creation through `PetViewModel`, and production validation through the existing product validators. The requested change is intentionally visual: simplify the form to match the provided reference hierarchy without changing responsive behavior, established component sizes, data contracts, permissions or backend rules.

The current screen includes a privacy explainer card, a separate photo preview area, separate gallery and camera buttons, name, breed, color, characteristics, location and submit controls. The new presentation should make the first scan feel like: add photo, fill pet name, add useful recognition details, enter last-seen location, publish.

## Goals / Non-Goals

**Goals:**

- Make the create-post flow visually simpler and faster to scan.
- Keep the existing scroll container, padding rhythm, field heights and submit-button sizing unless implementation discovers an existing constant/helper that already controls them.
- Preserve existing camera/gallery launch behavior while presenting it through a single photo upload surface inspired by the reference.
- Preserve existing submit validation: photo, pet name and manual last-seen location are still required before publish.
- Keep backend-compatible values for fields no longer shown prominently.
- Keep contact, exact location and permission guardrails intact.

**Non-Goals:**

- No Firestore, Room, repository, ViewModel or validator contract changes.
- No new Android runtime permissions.
- No current-location capture action in the create-post screen.
- No changes to responsive layout strategy, navigation, top app bar behavior or submit-button behavior.
- No new media upload provider or photo storage behavior.

## Decisions

### Android Client

- Use the existing `Column` scroll layout and existing outer padding/spacing as the layout contract. This keeps the screen responsive behavior stable while changing only the visual composition of children.
- Convert the photo section into a single bordered upload panel with camera/gallery iconography and a "tap to add photo" style prompt. If both camera and gallery must remain separately selectable, expose them inside the upload interaction using existing launchers rather than adding new media code paths.
- Replace the visible breed/color row plus characteristics field with a single recognition-details field. The implementation should map that value into the existing descriptive post field and keep current defaults for fields that remain required by the existing `createNewPetPost` signature.
- Keep manual/coarse last-seen-location as the only post location input. The reference current-location action is intentionally excluded because adding it would introduce a sensitive permission surface and conflicts with existing release-readiness guardrails for this screen.
- Keep submit enablement tied to the same required state: real photo selected, non-blank pet name, non-blank location and not submitting.

### Backend

- No backend changes. The UI continues to call existing ViewModel/repository paths and writes the same production post document shape.

### Local Storage

- No local persistence or migration changes. Room/cache behavior remains untouched.

## Risks / Trade-offs

- Simplifying fields may reduce structured attribute detail in future posts -> keep backend-compatible defaults and preserve the free-text recognition details field for useful identifying information.
- A single photo panel could hide the camera/gallery distinction -> make the interaction explicit enough for users to choose or understand the available source while still reading visually as one upload area.
- Removing/de-emphasizing the privacy explainer could reduce user awareness -> keep privacy copy available in a compact, non-blocking place if product decides the message must remain on this screen.
- The visual reference includes current location capture -> keep that out of this change and handle it only through a separate approved location proposal if needed.

## Migration Plan

Implement as a Compose-only presentation change in `CreatePetPostScreen.kt`, update focused tests, run OpenSpec validation, run unit/UI tests affected by the screen, and run the Android debug build. Rollback is a normal code revert of the screen and tests because no data model or backend migration is introduced.

## Open Questions

- None for the proposal. The current-location action from the reference is treated as out of scope unless explicitly approved in a separate functionality change.
