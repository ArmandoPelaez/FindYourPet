## Context

The release build enables R8 minification in `app/build.gradle.kts`. `FirestoreUserProfileRepository` passes `UserProfileDocument::class.java` to Firestore `toObject`, so Firebase constructs the DTO and resolves its properties reflectively. The generated release mapping confirms the app model is renamed to `qu4`, while the R8 usage output records the no-argument constructor as removed. This produces a runtime failure after sign-in; `PetViewModel` forwards the raw exception message through `authMessage`, which makes the issue visible on Reportar/Publicar and other screens.

The current Firestore schema and profile document shape are valid and must remain unchanged. The fix must be limited to reflective release compatibility, controlled error presentation, and regression evidence.

## Goals / Non-Goals

**Goals:**

- Preserve the profile DTO class, no-argument constructor, and property access needed by Firestore reflection in minified release builds.
- Keep the existing `users` document fields and authentication/profile flow compatible with already stored documents.
- Prevent raw R8/Firestore implementation text from becoming the user-facing profile error.
- Prove the fix with source/static regression checks and a release build that produces usable R8 output.

**Non-Goals:**

- Changing the Firestore schema, security rules, authentication provider, or profile data ownership.
- Redesigning Reportar/Publicar or changing location/photo behavior.
- Replacing all Firestore map conversions in the application.
- Adding a new serialization library or a new runtime dependency.
- Migrating or deleting existing user profiles.

## Decisions

### Preserve the reflective DTO contract with a targeted R8 rule

Add the narrowest keep configuration that preserves `UserProfileDocument` and the constructor/properties consumed by Firestore, without disabling minification for the whole application or package. This is selected because it directly addresses the observed failure, keeps the current repository API and Firestore schema intact, and has a small release-size impact.

Alternative considered: annotate the DTO with an Android keep annotation. This may work, but the existing project already centralizes release reflection policy in `proguard-rules.pro`; a targeted rule is easier to audit in the release configuration and avoids adding source-level annotations solely for this DTO.

Alternative considered: replace `toObject` with manual `DocumentSnapshot.data` mapping. This removes reflection risk but expands the change into a new mapper path and can create field-default/typing drift. It remains a future option if more reflective Firestore DTOs appear.

### Preserve field/property names used by Firestore

The keep contract must preserve the public accessors or fields required to map `uid`, `displayName`, `email`, `createdAt`, and `updatedAt`. Keeping only the class name or only the constructor is insufficient if R8 renames or removes the members Firestore resolves.

### Keep error translation at the ViewModel boundary

Profile-load failures remain diagnostic internally, but the UI receives a controlled message that does not expose obfuscated class names, Firestore implementation text, or raw exception details. This prevents the same failure from leaking through every screen while preserving the underlying cause for logs/Crashlytics when configured.

### Verify the minified artifact, not only debug compilation

Regression validation must include the minified release path and inspect R8 outputs for the DTO constructor. Debug unit tests and `assembleDebug` remain necessary, but cannot prove that R8 preserved reflective members.

## Risks / Trade-offs

- [The keep rule is too narrow] → Firestore may still fail on a renamed/removed accessor; verify both constructor and required properties in release output and exercise profile deserialization.
- [The keep rule is too broad] → Release size or obfuscation quality may regress; scope it to `UserProfileDocument` rather than the full profile or data package.
- [Controlled error text hides useful diagnostics] → Log the original exception through the existing diagnostic path without exposing it to users.
- [A release build cannot be signed in the environment] → Run R8/minification tasks and inspect outputs, then mark distribution validation blocked until signing credentials are available.
- [Existing documents contain missing/null fields] → Keep DTO defaults and verify missing-field deserialization remains non-crashing.

## Migration Plan

1. Add the OpenSpec regression contract and targeted implementation tasks.
2. Update the R8 configuration and profile error boundary.
3. Add or update regression tests that verify the reflective contract and controlled message behavior.
4. Run `testDebugUnitTest`, `assembleDebug`, and the relevant release/minification task; inspect mapping/usage outputs.
5. If a signed release APK is available, install and verify sign-in/profile loading before opening Reportar/Publicar.

Rollback is limited to reverting the code and R8 rule changes. No Firestore migration, user-data rewrite, or backend rollback is required.

## Open Questions

- Is the final distribution candidate signed with the configured release keystore, or should validation stop at unsigned/minified artifact verification in this environment?
- Should the controlled profile-load message be Spanish-only for the current UI, or follow an existing centralized localization strategy?
