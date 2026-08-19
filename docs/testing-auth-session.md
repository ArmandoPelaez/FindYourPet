# Auth session testing strategy

## Scope

This strategy covers the authentication-session lifecycle correction for remote Firestore flows.
The authentication provider is not part of the backend lifecycle contract: Google and email/password
must both produce the same `SignedIn` session boundary.

## Production changes covered

- `AuthScopedFlow.kt` scopes a flow to the authenticated Firebase UID and cancels it on logout.
- `PetRepository.observePostFeedState()` creates a fresh Firestore listener for each active session.
- `PetViewModel.postFeedState` binds the feed to authentication state instead of retaining a listener
  across logout/login transitions.
- `AuthLinkingPolicy.kt` validates that a pending Google credential is linked only after a successful
  password sign-in for the same email.
- `FirebaseAuthRepository` retains the Google credential after an account collision and links it with
  `linkWithCredential` after the existing password account is authenticated.

## Unit cases

- Signed-out state uses an empty signed-out flow and never starts the authenticated flow.
- Signed-in state starts the flow with the Firebase UID.
- Logout cancels the previous listener.
- A subsequent login creates a fresh listener, including Google re-login.
- Repeated emissions for the same UID do not duplicate listeners.
- Loading and authentication-error states cancel the authenticated flow.

## Test stack

- JUnit 4
- `kotlinx-coroutines-test`
- Existing Robolectric/Compose test setup remains unchanged.

## Commands

```powershell
.\gradlew.bat testDebugUnitTest --tests com.findyourpet.app.AuthScopedFlowTest
.\gradlew.bat testDebugUnitTest --tests com.findyourpet.app.AuthLinkingPolicyTest
.\gradlew.bat testDebugUnitTest
```

Manual verification remains required for Google and email/password logout/login because the real
Firebase credential transition and Firestore security rules cannot be fully simulated by local unit tests.

## Firebase account migration note

If the same email already has two Firebase users, linking cannot merge their UIDs automatically. Keep
the account that owns the application data, migrate any required documents, and remove the duplicate
only after verifying its data. For a clean password-first test, the password user must exist in the
same Firebase project and the Google sign-in must return `account-exists-with-different-credential`.
