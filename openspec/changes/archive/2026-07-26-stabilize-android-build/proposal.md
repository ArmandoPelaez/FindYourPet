## Why

The Android project is not ready to serve as a reliable base for production work because the Gradle wrapper is missing, unit tests reference template code that no longer exists, and package/text cleanup is incomplete. This change establishes a stable, understandable baseline before adding privacy, authentication, backend, media, location, or release features.

## What Changes

- Add or regenerate the Gradle wrapper so the project can be built with a repo-local command.
- Make the debug unit test suite compile and pass with the current app code.
- Remove or replace broken Android Studio template tests that assert stale package names or app labels.
- Correct `GreetingScreenshotTest` so it targets a real, small, deterministic Compose surface under `MascotasPerdidasTheme` instead of removed `Greeting` / `MyApplicationTheme` symbols.
- Add local guardrail tests for Manifest permissions, active future-feature Gradle dependencies/plugins, demo text claims/mojibake, contact visibility states, status chip states, and `MainActivity` startup.
- Fully rename `com.example` and the AI Studio-derived application id to the project-specific package `com.findyourpet.app`.
- Remove unused imports, stale template references, and unused/commented dependency clutter that obscures the actual demo implementation.
- Correct mojibake or unaccented Spanish labels for user-facing contact fields such as "Dueño", "Teléfono", and "Dirección" where present.
- Keep demo-only behavior explicit where it remains, including hardcoded users, local seed data, preset photos, and simulated GPS.

## Capabilities

### New Capabilities
- `release-readiness`: Covers the minimum Android build, test, naming, dependency hygiene, and demo clarity requirements needed before later production-focused changes can be implemented safely.

### Modified Capabilities
- None.

## Impact

- Affects Gradle wrapper files, root/module Gradle configuration, test sources, Android package/namespace/application id declarations, and user-visible Spanish copy.
- No backend, database schema, authentication, runtime permission, or network behavior is introduced by this change.
- Privacy impact is limited to clarity: demo data and protected-contact UI must not imply production privacy guarantees that are not implemented yet.
- Existing users are unaffected because this is a pre-production stabilization change with no production data migration.
- Rollback strategy: revert the wrapper/test/package/text cleanup commits and return to the prior prototype state; no remote services or persisted production data are involved.
- Applies guardrails for build validation, demo transparency, and avoiding unsupported privacy/security claims.
- Device/emulator instrumentation validation remains outside local unit tests and is covered by `.\gradlew.bat connectedDebugAndroidTest` when a target device is available.
