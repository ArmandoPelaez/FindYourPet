## Context

The signed-in shell in `MainActivity.kt` owns the fixed `BottomPrimaryActionBanner`, while `CreatePetPostScreen.kt` currently owns the form state, validation and the `Publicar ficha` submission callback. The same publication intent is therefore represented by the circular center navigation action and by a second full-width form button.

SCRUM-17 requires the shell to remain fixed and to show one contextual center action while the create-post flow is active. The change is presentation and interaction wiring only: existing validation, `PetViewModel.createNewPetPost`, navigation destinations and persistence remain unchanged.

## Goals / Non-Goals

**Goals:**

- Keep the five authenticated destinations visible: Inicio, Perfil, Publicar, Mensajes and Alertas.
- Render the regular circular `+ Publicar` action outside the create-post flow.
- Render a wider `Publicar ficha` action in the same center navigation slot inside the create-post flow.
- Reuse the existing form validity, submitting state and publication callback.
- Remove the duplicate in-form button and keep the action visible while the form scrolls.
- Preserve existing Design System tokens, responsive behavior, Light/Dark Theme, accessibility and touch targets.

**Non-Goals:**

- Do not change form validation rules, ViewModels, repositories, Firebase, Room, persistence or domain behavior.
- Do not change the five navigation destinations or their routes.
- Do not add a second navigation row, a new dependency or a new publication flow.
- Do not redesign unrelated screens or change the existing bottom navigation identity.

## Decisions

### 1. Keep the shell as the owner of the contextual center action

`BottomPrimaryActionBanner` remains rendered beside the `NavHost` in `SignedInPetAppNavigation`. It receives an optional contextual create action; when absent it renders the existing circular `+ Publicar` action, and when present it renders `Publicar ficha` in the same center slot.

Alternative considered: render a second CTA from `CreatePetPostScreen` above the shell. Rejected because it preserves the duplicate action and violates the requirement that the CTA be integrated into the Bottom Navigation.

### 2. Lift only presentation state and the existing submit callback

`CreatePetPostScreen` exposes a narrow registration/update boundary for the center action containing label, enabled/busy state and the existing submit callback. `SignedInPetAppNavigation` stores the latest presentation state while the create route is active and clears it when the route leaves or the screen is disposed. The screen continues to own all form fields, validation and `PetViewModel.createNewPetPost` invocation.

Alternative considered: move the complete form state and submission logic into `MainActivity`. Rejected because it expands the change into navigation/domain ownership and increases the risk of changing business behavior.

### 3. Use one shared responsive slot with weighted secondary items

The banner keeps one row and the existing tokenized height, margins, surface, safe-area handling and secondary item layout. The contextual CTA uses a wider weighted center slot, approximately two normal item widths, while the four secondary destinations receive the remaining space. Text, colors, disabled state, shape, icon policy and elevation use existing Material 3/component tokens; no screen-local dimensions or colors are introduced.

Alternative considered: add a second row or allow the CTA to overlap neighboring items. Rejected because Jira explicitly forbids an additional row and requires no overlap across supported widths.

### 4. Preserve regular navigation semantics

The contextual center CTA invokes the same `onPostCreated`/submit path currently used by the form button. Tapping Inicio, Perfil, Mensajes or Alertas keeps their current navigation callbacks. Leaving the create route removes the contextual state, so the banner immediately returns to the regular circular `+ Publicar` action.

## Risks / Trade-offs

- [Risk] The form can be recomposed while the shell holds a callback → use a current-state-safe callback registration and clear it on disposal so the shell never retains a stale screen action.
- [Risk] `Publicar ficha` can be clipped on compact widths → use weighted layout, existing typography tokens, max-lines/overflow behavior and screenshot coverage for compact and tall supported viewports.
- [Risk] The CTA may be triggered while submission is in progress → derive enabled state from the existing `isSubmitting` flag and preserve the current progress indicator behavior.
- [Risk] Parallel navigation changes may touch the same shell files → keep the diff limited to contextual action wiring and coordinate any conflicts against the current `main` base before integration.

## Migration Plan

1. Add the OpenSpec contract and focused presentation tests.
2. Add the contextual action model/parameter to the shared bottom navigation component.
3. Register the existing create-post submit action and validity state with the shell.
4. Remove only the duplicate in-form button and retain its existing submit body through the registered callback.
5. Run strict OpenSpec validation, unit tests, debug build and focused UI/screenshot tests.
6. Rollback by reverting the change branch; no data migration or backend rollback is required.

## Open Questions

- None blocking. The issue explicitly prioritizes the repository Design System when its approximate two-slot width guidance conflicts with a tokenized responsive layout.
