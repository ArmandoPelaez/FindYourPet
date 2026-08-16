## Why

The minified release candidate can compile successfully but fail at runtime when Firebase Firestore deserializes `UserProfileDocument`. R8 removes or obfuscates the reflective construction path, producing the `Class qu4 does not define a no-argument constructor` error that surfaces while opening the Reportar/Publicar flow after authentication.

## What Changes

- Preserve the Firestore profile DTO and the reflective constructor/property contract required by `toObject(UserProfileDocument::class.java)` in minified release builds.
- Add regression coverage for the release reflection keep contract and the user-profile deserialization path.
- Verify the fix with the relevant unit tests, a minified release build, and inspection of the generated R8 mapping/usage output.
- Keep the existing Firestore schema, authentication flow, UI, and user data unchanged.
- Do not expose raw Firestore/R8 implementation errors as a user-facing message when a profile load fails.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `release-readiness`: A minified release candidate must preserve reflective Firestore profile deserialization and provide controlled failure behavior.

## Impact

- Affected code/configuration: `UserProfileDocument`, `FirestoreUserProfileRepository`, `PetViewModel` profile error handling, `app/proguard-rules.pro`, and release/build regression tests.
- Affected runtime: authenticated startup/profile initialization and any screen that displays the shared authentication message, including Reportar/Publicar.
- Security/privacy: no new permissions or data access are introduced; profile fields remain limited to the existing Firestore `users` document.
- Existing users: no migration is required. Existing profile documents remain compatible.
- Rollback: revert the keep-rule/error-boundary change if necessary, understanding that doing so restores the release deserialization risk; no database rollback is needed.
