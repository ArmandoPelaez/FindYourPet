## 1. Presentation State Preparation

- [x] 1.1 Confirm the implementation remains limited to `AuthScreen` presentation state and existing design-system tokens; do not add dependencies or modify authentication contracts.
- [x] 1.2 Define the finite Login visual-state transitions and the reduced-motion fallback using stable APIs available in the current Compose/runtime configuration.

## 2. Focus and Password Feedback

- [x] 2.1 Add token-backed visual feedback for email and password focus changes without changing field size, labels, semantics, or interaction targets.
- [x] 2.2 Add a finite transition for password visibility affordance/content while preserving the field value, focus, and password semantics.

## 3. Authentication Operation Feedback

- [x] 3.1 Preserve and consolidate the presentation-level operation guard so email/password and Google actions cannot submit concurrently or repeatedly while an operation is pending.
- [x] 3.2 Add/adjust finite loading feedback for email sign-in, account creation, Google credential acquisition, and the resulting authentication operation using existing components and tokens.
- [x] 3.3 Add/adjust finite, recoverable error feedback that clears or updates at the start of a new attempt without exposing sensitive data.
- [x] 3.4 Observe the existing signed-in state for optional non-blocking success confirmation without delaying or replacing authenticated navigation.
- [x] 3.5 Keep any proximity-point motion optional, subtle, finite, and disabled or resolved immediately when reduced motion is active.

## 4. Tests

- [x] 4.1 Extend focused Login presentation tests for focus feedback, password visibility transition, loading labels/indicators, and recoverable errors.
- [x] 4.2 Add coverage that verifies email/password and Google actions share the duplicate-submit guard and re-enable after cancellation or failure.
- [x] 4.3 Add coverage that verifies successful authentication does not introduce a blocking delay or alter existing navigation contracts.
- [x] 4.4 Verify the changed Login presentation continues to use existing design tokens, stable APIs, accessible action labels, and password semantics.

## 5. Verification and Handoff Evidence

- [x] 5.1 Run `openspec validate "add-login-visual-states-and-microinteractions" --strict`.
- [x] 5.2 Run `./gradlew.bat testDebugUnitTest` and record the result.
- [x] 5.3 Run `./gradlew.bat assembleDebug` and record the result.
- [x] 5.4 Perform manual Login checks for focus, password visibility, email sign-in, sign-up, Google loading/cancellation/error, rapid repeated taps, successful navigation, Light Theme, Dark Theme, and reduced motion where available.
- [x] 5.5 Review the diff against the OpenSpec scope, run `git diff --check`, and prepare the implementer report with any environment limitations.
