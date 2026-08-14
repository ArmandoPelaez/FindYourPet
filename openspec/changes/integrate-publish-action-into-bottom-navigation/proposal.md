## Why

`CreatePetPostScreen` currently exposes the same publication action twice: the persistent `+ Publicar` action in the Bottom Navigation and a separate `Publicar ficha` button at the end of the form. This duplicates the primary action, consumes vertical space and makes the publication flow less clear; SCRUM-17 requests one contextual action that remains available while the form scrolls.

## What Changes

- Replace the central `+ Publicar` navigation action with a wider `Publicar ficha` CTA while the create-post flow is active.
- Remove the separate in-form `Publicar ficha` button without changing its existing validation or publication callback.
- Keep Inicio, Perfil, Mensajes and Alertas visible and functional in the fixed Bottom Navigation.
- Render the contextual CTA disabled/enabled from the existing form validity and submission state.
- Restore the regular `+ Publicar` action when the user leaves the create-post flow.
- Preserve responsive layout, Light/Dark Theme, accessibility, touch targets and existing Design System tokens.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `primary-navigation`: the central action changes contextually from `+ Publicar` to the wider `Publicar ficha` CTA on the create-post route while the five-destination shell remains fixed.
- `pet-posts`: the create-post flow moves its existing publication action into the navigation shell without changing validation, persistence or publication behavior.

## Impact

- Affected UI: `MainActivity.kt`, `CommonComponents.kt` and `CreatePetPostScreen.kt`.
- Affected presentation tests and screenshots for the bottom navigation and create-post screen.
- No new dependencies, APIs, permissions, persistence migrations, backend changes or domain logic.
- Existing users retain the same publication callback and required-field validation; only the placement and contextual presentation of the CTA change.
- Rollback: revert the change branch to restore the separate form button and circular `+ Publicar` presentation.
- Applicable guardrails: use stable Jetpack Compose Material 3, existing tokens, Light/Dark Theme, accessibility and no hardcoded visual values.
