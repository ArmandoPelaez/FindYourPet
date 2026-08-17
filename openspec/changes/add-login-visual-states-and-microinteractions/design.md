## Context

`AuthScreen` is a Jetpack Compose screen that already observes `authState` and `authMessage`, uses existing form and button tokens, and has separate local Google loading state. The Login must now make state changes easier to perceive while preserving the authentication callbacks and the existing navigation behavior driven by the authenticated state.

Constraints:

- Use stable Jetpack Compose and Material 3 APIs only.
- Reuse `MaterialTheme`, `AppFormTypography`, `AppSpacing`, `AppShapes`, `AppElevation`, `AppOpacity`, and existing components; do not add arbitrary visual constants.
- Preserve Light Theme and Dark Theme behavior.
- Keep the change in the presentation layer and its tests. Do not alter `PetViewModel`, Firebase Auth, Credential Manager, repositories, navigation, permissions, or domain contracts.
- Do not add an animation loop or a new dependency solely for motion.

## Goals / Non-Goals

**Goals:**

- Make focus, password visibility, loading, recoverable error, and successful-authentication transitions observable without delaying user actions.
- Keep email/password and Google operations mutually exclusive from the first user action until the current operation is resolved.
- Preserve accessible labels, password semantics, enabled/disabled semantics, and current authentication behavior.
- Make the visual response testable through deterministic presentation-state tests and manual Light/Dark review.

**Non-Goals:**

- Change authentication APIs, Firebase behavior, credential acquisition, error contracts, or navigation.
- Introduce a new design language, new tokens, or decorative animations.
- Add a separate success screen or delay navigation after `AuthUiState.SignedIn`.

## Decisions

1. **Keep operation state in the Login presentation layer.**
   - Use the existing `AuthUiState.Loading` for email/password and the existing local Google operation state, combining them into one operation-in-progress guard for all fields and actions.
   - Rationale: this closes the duplicate-submit gap without changing the ViewModel or authentication repository contracts.
   - Alternative rejected: changing `AuthUiState` or the ViewModel to introduce a new domain state, because the requirement is visual/presentation-only.

2. **Use stable Compose state animation primitives for state changes.**
   - Apply token-backed animated color/visibility/content changes to focused fields, the password visibility affordance, loading feedback, and recoverable error feedback.
   - Rationale: these transitions communicate state changes while retaining the existing component hierarchy and interaction targets.
   - Alternative rejected: custom animation clocks, infinite shimmer, or a third-party motion library because they add complexity and can consume resources unnecessarily.

3. **Treat successful authentication as an observed state transition.**
   - When `authState` becomes `AuthUiState.SignedIn`, provide only a brief non-blocking visual confirmation if it is already compatible with the existing screen lifecycle; do not delay or replace the existing navigation decision.
   - Rationale: navigation remains the responsibility of the existing authenticated-session flow.
   - Alternative rejected: adding a new success route or timer, which would alter navigation behavior and could trap users on the Login screen.

4. **Handle reduced motion conservatively.**
   - Keep all transitions short, finite, and optional; when the available platform motion signal indicates reduced motion, render the final state without animated interpolation.
   - Rationale: reduced-motion behavior is part of the acceptance criteria while the app should not add a new accessibility dependency.
   - Alternative rejected: assuming every device has the same motion setting or implementing a custom global preference in this scoped change.

5. **Keep errors recoverable and non-sensitive.**
   - Preserve the current error message sources and display them with a finite appearance transition; clear local errors when a new operation or mode change begins.
   - Rationale: users receive feedback without changing backend error handling or exposing additional data.

## Risks / Trade-offs

- [Animation causes layout movement or obscures controls] → Use existing component bounds, tokenized spacing, and non-blocking transitions; verify with keyboard open and both themes.
- [Google credential flow outlives the composable] → Keep the existing coroutine scope/lifecycle behavior and reset local loading state in `finally`; do not create a second credential flow.
- [Loading state is cleared before Firebase finishes] → Keep Google loading until credential acquisition returns/fails and rely on the existing auth loading state for the Firebase operation; tests must cover both phases.
- [Reduced-motion handling is unavailable on a target device] → Ensure the default animation is finite and subtle, and document manual verification evidence rather than inventing a new platform contract.
- [Existing Login changes are still represented by in-progress OpenSpec folders] → Limit the diff to Work Item 6 presentation feedback/tests and avoid reworking action hierarchy, form fields, or header design.

## Migration Plan

1. Implement the presentation-only behavior in `AuthScreen` and extend focused presentation tests.
2. Run strict OpenSpec validation, unit tests, and debug assembly.
3. Manually verify email login, sign-up, Google, cancellation/error recovery, focus, password visibility, duplicate taps, success navigation, Light Theme, Dark Theme, and reduced-motion behavior where available.
4. Roll back by reverting the change branch; no database, Firebase, permission, or navigation migration is required.

## Open Questions

- The existing app does not expose a separate transient success state; implementation should use the existing `AuthUiState.SignedIn` transition without adding a new domain state.
- The exact platform reduced-motion signal available in the current Compose/runtime versions must be confirmed during implementation. If it is not available without an experimental API or new dependency, use finite subtle transitions and document the limitation for verification.
