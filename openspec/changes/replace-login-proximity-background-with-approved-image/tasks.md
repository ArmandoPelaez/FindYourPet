## 1. Scope and Asset Preparation

- [x] 1.1 Confirm the tracked approved resource `app/src/main/res/drawable-nodpi/imagen_fondo_pantalla_login.png` is the only background asset used for SCRUM-38; do not generate or convert another image.
- [x] 1.2 Inspect all references to `LoginProximityBackground` and confirm whether the Canvas component can be removed without affecting unrelated screens.

## 2. Login Background Replacement

- [x] 2.1 Replace the Login Canvas background call with a local `painterResource` image layer behind the existing functional content.
- [x] 2.2 Configure aspect-ratio-preserving responsive scaling with upper-composition prioritization and no distortion or unintended empty space.
- [x] 2.3 Preserve or add only an existing tokenized theme-aware surface/scrim treatment when needed for Light/Dark legibility; do not add arbitrary color or opacity values.
- [x] 2.4 Ensure the image layer has no click, gesture, focus, or pointer behavior and is exposed as decorative content with `contentDescription = null`.
- [x] 2.5 Remove the obsolete Login Canvas component only if the reference audit confirms it has no remaining consumers; keep unrelated Canvas utilities untouched.

## 3. Regression Tests

- [x] 3.1 Update or add focused Login presentation tests for the approved resource reference, image layering, responsive content-scale strategy, and removal of the old Canvas call.
- [x] 3.2 Add coverage that verifies the decorative image is non-accessible/non-interactive while Email, Contraseña, Entrar, Google, Crear una cuenta, and error content remain present.
- [x] 3.3 Verify authentication callbacks, loading/error behavior, navigation semantics, Design System tokens, and Light/Dark theme-aware components remain unchanged.

## 4. Verification and Handoff Evidence

- [x] 4.1 Run `openspec validate "replace-login-proximity-background-with-approved-image" --strict`.
- [x] 4.2 Run `./gradlew.bat testDebugUnitTest` and record the result.
- [x] 4.3 Run `./gradlew.bat assembleDebug` and record the result.
- [x] 4.4 Perform manual checks on a small screen, larger window, keyboard open, Email focus, Contraseña focus, Entrar, Google, Crear una cuenta, error messages, Light Theme, Dark Theme, and basic TalkBack/accessibility behavior.
- [x] 4.5 Review the diff against SCRUM-38, run `git diff --check`, and prepare the implementer report with any runtime limitations.
