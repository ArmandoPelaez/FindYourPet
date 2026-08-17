## 1. Login brand composition

- [x] 1.1 Replace the centered `AccountCircle`/large avatar block in `AuthScreen.kt` with a non-interactive horizontal brand signature aligned to the top-left of the Login content.
- [x] 1.2 Reuse the existing transparent brand vector, `AppSpacing.headerLogo`, existing typography and spacing tokens; do not use the opaque PNG or introduce visual constants.
- [x] 1.3 Preserve the centered headline/supporting text, continuous background, scroll/IME behavior, Light/Dark rendering and all authentication controls.

## 2. Regression coverage

- [x] 2.1 Update or add static Login presentation assertions for the horizontal top-left signature, official existing resource, absent large centered avatar and preserved hierarchy.
- [x] 2.2 Review the final diff to confirm ViewModels, repositories, Firebase, navigation, authentication callbacks, texts and background asset remain unchanged.

## 3. Validation

- [x] 3.1 Run `openspec validate move-login-identity-to-top-left-header --strict` and `openspec instructions apply --change move-login-identity-to-top-left-header --json`.
- [x] 3.2 Run `./gradlew.bat testDebugUnitTest` and `./gradlew.bat assembleDebug`.
- [x] 3.3 Validate visually on the available `Medium_Phone` in dark mode: confirmed top-left placement, horizontal logo/name, headline hierarchy, no overlap, form focus and keyboard behavior. `Small_Phone` was attempted but exited during startup, so a second viewport remains an environment limitation.
