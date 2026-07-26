## 1. Firebase Project And Gradle Setup

- [x] 1.1 Add Firebase/Google Services Gradle plugin usage and dependencies for Firebase BoM, Auth, Firestore, Google Sign-In/Credential Manager, and coroutine Task interop.
- [x] 1.2 Add documented local setup for Spark-plan Firebase project registration, debug/release SHA fingerprints, and uncommitted `google-services.json`.
- [x] 1.3 Update dependency guardrail tests so Auth/Firestore/Google Services are allowed only for this change's real implementation.

## 2. Authentication State And UI

- [x] 2.1 Add auth domain models and an auth repository/service backed by Firebase Auth.
- [x] 2.2 Implement email/password sign-up and sign-in with loading and recoverable error states.
- [x] 2.3 Implement Google Sign-In through Firebase Auth with cancellation/error handling.
- [x] 2.4 Implement logout and clear authenticated UI/profile state.
- [x] 2.5 Gate owner-only screens/actions behind authenticated session state.

## 3. User Profile

- [x] 3.1 Add Firestore user profile model mapped to `users/{uid}`.
- [x] 3.2 Create the profile document after first authentication when it does not exist.
- [x] 3.3 Load existing profile on app start/session restore and expose it to profile UI.
- [x] 3.4 Update profile UI/actions so signed-out users cannot see stale authenticated profile data.

## 4. Firestore Data And Ownership

- [x] 4.1 Add Firestore data models/repository methods for authenticated pet posts with immutable `ownerId`.
- [x] 4.2 Wire create post flow so new production posts use the signed-in Firebase `uid`.
- [x] 4.3 Replace owner-only edit/close/contact-sharing checks with `currentUid == ownerId`.
- [x] 4.4 Remove hardcoded owner grants such as `user_1`, `owner_1`, and `id.startsWith("owner")` from production logic.
- [x] 4.5 Add participant checks for chat/session/message reads and writes in repository/UI flow.

## 5. Room Cache And Demo Data Boundary

- [x] 5.1 Document and implement Firestore as source of truth for authenticated production data.
- [x] 5.2 Keep Room seed data clearly scoped as demo/cache data and prevent it from granting production owner permissions.
- [x] 5.3 Add explicit conversion/import behavior if any seeded Room record can become a Firestore production record.

## 6. Firestore Security Rules

- [x] 6.1 Add Firestore rules with deny-by-default behavior and `users/{uid}` self-access.
- [x] 6.2 Add pet post rules for owner-only create/update/delete and immutable `ownerId`.
- [x] 6.3 Add sightings/chat rules for participant-only private reads and writes.
- [x] 6.4 Add owner-only contact-sharing/contact-field mutation rules.
- [x] 6.5 Add emulator tests or documented rules validation covering allowed and denied cases.

## 7. Tests And Validation

- [x] 7.1 Add unit tests for auth/profile state transitions and logout clearing.
- [x] 7.2 Add guardrail tests rejecting hardcoded owner permission strings and demo-owner grants.
- [x] 7.3 Add repository/ViewModel tests for owner vs non-owner behavior.
- [x] 7.4 Run `gradlew.bat testDebugUnitTest`.
- [x] 7.5 Run `gradlew.bat assembleDebug`.
- [x] 7.6 Document manual validation for email/password login, Google Sign-In, logout, profile load, post ownership, denied non-owner edit/close, contact reveal, and Room cache/demo behavior.
