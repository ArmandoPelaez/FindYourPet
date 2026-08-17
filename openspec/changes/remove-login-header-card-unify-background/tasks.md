## 1. Login composition

- [x] 1.1 Remove the upper hero `Surface`/card from `AuthScreen.kt` while retaining its identity, headline, supporting text and form heading content.
- [x] 1.2 Preserve the existing tokenized spacing, typography, background layers, scroll/IME behavior and Light/Dark presentation without adding replacement surfaces or hardcoded visual values.

## 2. Regression coverage

- [x] 2.1 Inspect existing Login tests and update or add only structural assertions needed to verify that the upper card is absent while authentication controls and interactions remain unchanged.
- [x] 2.2 Review the final diff to confirm that ViewModels, repositories, Firebase, navigation, texts, background asset and authentication logic are untouched.

## 3. Validation

- [x] 3.1 Run `openspec validate remove-login-header-card-unify-background --strict` and `openspec instructions apply --change remove-login-header-card-unify-background --json`.
- [x] 3.2 Run `./gradlew.bat testDebugUnitTest` and `./gradlew.bat assembleDebug`.
- [x] 3.3 Validate Login manually in the available theme configuration and on `Medium_Phone`: confirm continuous background, no hero card, no overlap, scroll with keyboard open, field focus and existing authentication actions. `Small_Phone` could not start because its default snapshot exited early.
