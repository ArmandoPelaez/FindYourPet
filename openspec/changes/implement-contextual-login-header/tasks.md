## 1. Contextual Header Implementation

- [x] 1.1 Update the existing `AuthScreen` header composition to show the Jira-defined contextual headline, supporting text, and subdued `FindYourPet` identity in the required hierarchy.
- [x] 1.2 Apply only existing `AppTypography`, `MaterialTheme.colorScheme`, `AppSpacing`, `AppShapes`, and related design tokens; do not add hardcoded visual values or new dependencies.
- [x] 1.3 Preserve the existing sign-in/sign-up toggle, form fields, email/password actions, Google Sign-In action, and error-state rendering while integrating the contextual header.

## 2. Verification Coverage

- [x] 2.1 Add or update focused static/unit coverage to verify the contextual copy, typography-token usage, absence of hardcoded screen typography/colors, and preservation of existing authentication controls.
- [x] 2.2 Review the header at small-screen constraints in Light Theme and Dark Theme, confirming wrapping, contrast, hierarchy, and absence of clipping or overlap.

## 3. Validation

- [x] 3.1 Run `openspec validate "implement-contextual-login-header" --strict` and confirm all requirements and scenarios are valid.
- [x] 3.2 Run `./gradlew.bat testDebugUnitTest` and resolve only failures caused by this change.
- [x] 3.3 Run `./gradlew.bat assembleDebug` and confirm the debug build succeeds.
- [x] 3.4 Review the final diff against the change scope and run `openspec instructions apply --change "implement-contextual-login-header" --json` to confirm all tasks are complete.
