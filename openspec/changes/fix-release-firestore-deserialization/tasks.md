## 1. Reproduce and define the release contract

- [x] 1.1 Confirm the current Firestore profile read path uses `toObject(UserProfileDocument::class.java)` and record the affected screens and error propagation.
- [x] 1.2 Reproduce or inspect the minified release failure and record the obfuscated DTO name, removed constructor, and relevant R8 mapping/usage evidence.
- [x] 1.3 Add regression coverage that asserts the release configuration preserves the `UserProfileDocument` reflective constructor and required properties.

## 2. Preserve Firestore reflection under R8

- [x] 2.1 Add a targeted R8/ProGuard rule for `UserProfileDocument` that preserves its class, no-argument constructor, and Firestore-readable properties.
- [x] 2.2 Verify the rule does not disable minification for unrelated app packages or introduce a new runtime dependency.
- [x] 2.3 Verify existing `users` documents with complete and missing optional fields remain compatible with the DTO defaults.

## 3. Bound profile-load errors

- [x] 3.1 Map profile deserialization failures to a controlled Spanish user-facing message at the ViewModel boundary.
- [x] 3.2 Preserve diagnostic details for logs or Crashlytics without exposing obfuscated class names, constructor text, or raw Firebase errors in the UI.
- [x] 3.3 Add or update tests covering successful profile initialization and a controlled profile-load failure.

## 4. Build and release verification

- [x] 4.1 Run `\.gradlew.bat testDebugUnitTest` and record the result.
- [x] 4.2 Run `\.gradlew.bat assembleDebug` and record the result.
- [x] 4.3 Run the minified release build task available in the environment and record signing or credential blockers explicitly.
- [x] 4.4 Inspect release `mapping.txt`, `usage.txt`, and generated artifacts to verify the DTO constructor is not removed and the reflective contract remains available.
- [x] 4.5 Signed-candidate install/E2E verification is deferred because `assembleRelease` is blocked by missing `STORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD`; minified R8 verification passed.

## 5. Final scope and handoff

- [x] 5.1 Review the diff to confirm only R8/profile error handling/tests and OpenSpec evidence changed; no schema, UI redesign, permissions, or unrelated dependency changes are included.
- [x] 5.2 Run `openspec validate "fix-release-firestore-deserialization" --strict`.
- [x] 5.3 Run `openspec instructions apply --change "fix-release-firestore-deserialization" --json` and update orchestration evidence with completed tasks, risks, and release verification status.
