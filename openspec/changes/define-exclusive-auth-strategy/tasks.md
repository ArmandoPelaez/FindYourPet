## 1. Authentication contract and repository

- [x] 1.1 Define domain outcomes for `EmailPasswordRequired`, `GoogleRequired`, generic authentication failure, and successful authentication without exposing Firebase exceptions to the UI.
- [x] 1.2 Remove pending Google credential state and every `linkWithCredential` path from `FirebaseAuthRepository`; discard a conflicting credential and preserve the unauthenticated state.
- [x] 1.3 Implement provider decision handling using explicit Firebase provider information when available and a safe generic fallback when the provider list is empty or inconclusive.
- [x] 1.4 Ensure email sign-up/sign-in and Google sign-in preserve the existing UID/data, never create duplicates, and never convert or replace the original provider.

## 2. User-facing state and tests

- [x] 2.1 Map domain outcomes to localizable functional messages for known Email/Password and Google conflicts plus the non-disclosing fallback.
- [x] 2.2 Replace the current linking contract tests with exclusivity tests proving no pending credential, `linkWithCredential`, or raw Firebase error reaches the Login UI.
- [x] 2.3 Add unit/contract coverage for new Email/Password accounts, new Google accounts, existing-provider sign-in, both provider conflicts, cancellation, generic failure, and unchanged UID/session state.
- [x] 2.4 Clear Email and Contraseña after a failed Email/Password Login that shows the Google-oriented fallback, preserving the message and leaving sign-up unchanged.

## 3. Manual Firebase validation

- [ ] 3.1 Verify with a real Email/Password account that Google Sign-In is rejected with the functional password message and does not link or alter the account.
- [ ] 3.2 Verify with a real Google account that Email/Password sign-up/sign-in is rejected with the functional Google message when the provider is known and never creates a duplicate.
- [ ] 3.3 Verify new-user Email/Password and Google flows, cancellation, recoverable errors, logout, re-login, Light Theme, and Dark Theme.
- [ ] 3.4 Confirm Firebase is configured for one account per email and record the observed Email Enumeration Protection behavior and the generic fallback result.

## 4. Final verification

- [x] 4.1 Run `openspec validate "define-exclusive-auth-strategy" --strict`.
- [x] 4.2 Run `./gradlew.bat testDebugUnitTest --no-daemon --console=plain`.
- [x] 4.3 Run `./gradlew.bat assembleDebug --no-daemon --console=plain`.
- [x] 4.4 Review the diff to confirm the change is limited to authentication behavior, messages, tests, and OpenSpec artifacts; no unrelated UI redesign or backend migration is included.
