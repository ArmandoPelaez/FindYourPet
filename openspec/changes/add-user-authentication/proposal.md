## Why

FindYourPet still depends on demo identities such as `user_1` and `owner_1`, so sensitive owner actions are only approximated in the client and cannot be trusted for real users. This change introduces real authentication and user-owned data before expanding backend, chat, contact sharing, media, location, or release workflows.

## What Changes

- Add Firebase Authentication on the Spark plan as the identity provider for the Android app.
- Support email/password sign-up and sign-in from the first auth release.
- Support Google Sign-In from the first auth release.
- Create and maintain a real user profile tied to the Firebase `uid`.
- Associate pet posts, sightings, chat sessions, and owner-controlled actions with the authenticated user's `uid`.
- Add Cloud Firestore as the backend for authenticated user profiles and user-owned production data needed by this change.
- Add Firestore Security Rules that enforce `uid`-based access and prevent owner reassignment.
- Define whether Room remains a local cache or is replaced for authenticated production data.
- Remove owner permissions based on hardcoded strings such as `user_1`, `owner_1`, or `currentUser.id == "owner_1"`.
- Exclude SMS authentication from this stage.

## Capabilities

### New Capabilities

- `auth`: Covers Firebase Authentication setup, email/password login, Google Sign-In, logout, authenticated session state, and unauthenticated UI behavior.
- `user-profile`: Covers the authenticated user's profile document, profile loading, profile creation, and profile updates tied to Firebase `uid`.
- `ownership-rules`: Covers Firestore ownership requirements, owner-only mutations, and removal of hardcoded owner logic.

### Modified Capabilities

- `local-storage`: Defines Room's role after authentication and which data moves to Firestore as source of truth.
- `contact-privacy`: Updates contact exposure requirements so owner-controlled contact sharing is evaluated against authenticated identity and backend rules, not local demo assumptions.
- `release-readiness`: Allows Firebase Auth, Firestore, Google Services, and credentials dependencies/plugins when introduced by this authenticated production change.

## Impact

- Android dependencies and Gradle plugins: Firebase BoM, Firebase Auth, Cloud Firestore, Google Services, Google Sign-In/Credential Manager support, and coroutine Task interop where needed.
- App configuration: Firebase project configuration for the Spark plan and Android app registration.
- Data layer: new auth/profile services, Firestore repositories, and a clear boundary for any remaining Room cache.
- UI: login, sign-up, Google Sign-In, logout, profile state, and owner-only controls.
- Security: Firestore Security Rules and rule tests/emulator validation for `users/{uid}`, pet posts, sightings, chats, and contact-sharing fields.
- Existing users/data: current seeded Room demo data may be retained only as demo/sample content or migrated into authenticated Firestore-owned records after sign-in; production ownership must not depend on existing local owner strings.
- Rollback: Firebase-authenticated features can be disabled by reverting the Gradle/configuration and restoring local demo session state, but any created Firebase project data remains external and should be cleaned up from the Firebase console if abandoning the change.
