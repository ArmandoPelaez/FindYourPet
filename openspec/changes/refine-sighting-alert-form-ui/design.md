## Context

`SightingAlertScreen` is a Jetpack Compose flow for eligible non-owners to report a sighting against an existing lost-pet post. The current adaptive content shows the referenced pet media/status header and a separate pet summary card before the actual report controls; on a phone this duplicates information from the previous/detail context and pushes the report form down the screen.

The screen already has the production behavior this change must preserve: camera/gallery launchers, optional real sighting photo state, manual/coarse location text, current-location capture action, notes, reporter eligibility checks, ViewModel submission, backend fan-out and validation errors. The lost-pet publication screen already uses the desired photo pattern: one bordered upload surface that contains the empty state, camera/gallery choices and selected image preview.

## Goals / Non-Goals

**Goals:**

- Remove the duplicated lost-pet media/header block and the duplicated "Reportando avistamiento de..." summary card from the report form path.
- Make the sighting form read first as: optional evidence photo, sighting location, current-location action, extra details and send alert.
- Align the optional sighting photo presentation with the create-post photo upload surface while keeping the existing camera and gallery callbacks.
- Preserve current validation, reporter eligibility, submit enablement, media upload handling, location consent behavior and backend-compatible sighting fields.
- Keep compact, centered and expanded layouts stable with no clipped text, overlapping bottom action bar or first-viewport visual clutter.

**Non-Goals:**

- No Firestore, Room, repository, ViewModel, validator, entity or backend security-rule changes.
- No new Android runtime permissions or new media provider.
- No change to whether sighting photos are optional.
- No change to current-location capture behavior, location-source semantics or location privacy guardrails.
- No new pet-detail card, owner contact section, exact coordinates, phone, email or address exposure in the sighting form.

## Decisions

### Android Client

- Refactor `SightingAlertAdaptiveContent` so the removed pet media/header and summary card are not rendered in compact, centered or expanded variants. In expanded layouts, reuse the freed visual area for the report form or the sighting photo upload surface rather than a referenced-pet preview column.
- Replace `SightingPhotoAttachment` plus the detached gallery/camera button row with a single upload surface modeled after `CreatePetPostScreen`: rounded 16dp shape, bordered or surface-backed panel, centered camera/gallery iconography when empty, and an image preview with integrated change/camera actions when a real photo URI is selected.
- Keep `onGalleryClick`, `onCameraClick`, `selectedPhotoUri` and content descriptions specific to sighting evidence. If a reusable Compose helper is practical, extract a small photo upload surface shared by create-post and sighting screens; otherwise mirror the existing create-post pattern locally without introducing a new abstraction.
- Keep the existing location text field, current-location action and notes field order after the upload surface. This preserves the current production behavior while making the form scan like a report instead of a pet profile.
- Keep validation and submit state computed from existing state. Because sighting photo evidence is optional, the send action must not become dependent on `selectedPhotoUri`.
- Update tests that currently assert `sighting-media-header`, `sighting-info-card` or "Reportando avistamiento de:" in the main form. Those assertions should become absence checks or be replaced with checks for the new upload-surface tags and existing location/submit controls.

### Backend

- No backend changes. The UI continues to submit through the existing `PetViewModel.submitSightingAlert` path and repository fan-out behavior.
- Authorization remains unchanged: only signed-in users whose Firebase `uid` differs from the referenced post `ownerId` can submit a sighting.

### Local Storage

- No local persistence or migration changes. Existing Room/cache behavior and sighting entity fields remain unchanged.

### Privacy, Errors And Permissions

- The removed pet context must not be replaced by owner phone, email, address or exact coordinate details.
- Existing camera, gallery, upload, authentication, self-report and location failure messages remain visible in the form message area.
- Existing camera/gallery and location permission flows remain the only runtime permission surfaces involved in this screen.

## Risks / Trade-offs

- Loss of repeated pet context -> the previous route and app bar already identify the sighting flow; do not reintroduce a large pet card. If minimal context is still needed, use concise plain text outside any card and avoid the removed image/status treatment.
- Shared upload component could accidentally change create-post behavior -> prefer a narrow helper with the same parameters or keep a local mirrored implementation if extraction increases risk.
- Optional photo may look required because the upload surface is prominent -> keep the label/copy explicit that the evidence photo is optional and ensure submit enablement remains independent from photo state.
- Current-location action can crowd the lower mobile viewport -> preserve it, but keep spacing compact and verify it does not collide with the fixed bottom submit bar.

## Migration Plan

Implement as a Compose-only presentation change in `SightingAlertScreen.kt`, update focused sighting UI/static tests, run OpenSpec validation, run affected unit/Compose tests and run the Android debug build. Rollback is a normal code revert of the screen/tests because no data model, backend or local migration is introduced.

## Open Questions

- None for this proposal. Changes to sighting business logic, required fields or location behavior are intentionally out of scope.
