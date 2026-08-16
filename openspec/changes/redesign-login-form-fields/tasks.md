## 1. Form Field Implementation

- [x] 1.1 Update the Email and Password `OutlinedTextField` presentation with existing icons, non-empty placeholders, `AppFormTypography`, `FormFieldLabel`/`FormFieldPlaceholder`, `AppShapes`, spacing, and theme colors.
- [x] 1.2 Add an accessible show/hide password control that changes only visual transformation and preserves the submitted password value.
- [x] 1.3 Preserve existing input validation and expose focused, disabled, and field-level error states without changing Authentication, `PetViewModel`, Firebase, or repository contracts.
- [x] 1.4 Configure email/password keyboard types and IME actions, move focus from Email to Password, submit through the existing CTA action from Password, and preserve IME insets.

## 2. Verification Coverage

- [x] 2.1 Extend focused AuthScreen tests to verify icons, placeholders, token usage, password visibility control, keyboard configuration, field errors, and preserved authentication callbacks.
- [ ] 2.2 Review Login and Registration modes in Light Theme and Dark Theme, including focused, disabled, error, password-visible, and keyboard-open states; record any unavailable device/emulator checks.

Verification note: the Light/Dark and keyboard-open visual review was not executed because no device or emulator is available in this environment. Autofill is deferred by the Jira scope decision.

## 3. Validation

- [x] 3.1 Run `openspec validate "redesign-login-form-fields" --strict` and confirm all requirements and scenarios are valid.
- [x] 3.2 Run `./gradlew.bat testDebugUnitTest` and resolve only failures caused by this change.
- [x] 3.3 Run `./gradlew.bat assembleDebug` and confirm the debug build succeeds.
- [x] 3.4 Review the final diff against the SCRUM-35 scope and run `openspec instructions apply --change "redesign-login-form-fields" --json` to confirm the implementation tasks and validation are complete; the only remaining item is the documented device/emulator review.
