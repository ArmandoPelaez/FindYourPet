## Context

FindYourPet is an Android native prototype using Kotlin, Jetpack Compose, Room, Gradle Kotlin DSL, Robolectric, and Roborazzi. The current tree still contains Android Studio template leftovers and test references that no longer match the app: `GreetingScreenshotTest` imports `MyApplicationTheme` and calls `Greeting`, `ExampleRobolectricTest` expects the stale label `My Application`, and the instrumentation test asserts `com.example` while the application id still references the AI Studio starting point.

The project also lacks executable Gradle wrapper files, so a developer cannot rely on a repo-local build command. This change is intentionally a baseline stabilization step before production changes that touch auth, backend, privacy, media, location, notifications, or release distribution.

## Goals / Non-Goals

**Goals:**

- Restore a reproducible Gradle entrypoint through the Gradle wrapper.
- Make `testDebugUnitTest` compile and pass against the current Compose/Room app.
- Replace broken template tests with tests that verify real app behavior.
- Correct screenshot testing so it renders a real, small, deterministic composable under `MascotasPerdidasTheme`, without depending on Room, navigation, or seeded data.
- Fully rename Kotlin packages, Android namespace, and application id from `com.example` / `com.aistudio.mascotasperdidas.petfind` to `com.findyourpet.app`.
- Clean unused imports, stale commented dependency blocks, and dependencies that are demonstrably unused by the current app.
- Correct Spanish text encoding/copy issues for visible labels such as "Dueño", "Teléfono", and "Dirección".
- Make demo-only behavior explicit in names, comments, or test expectations where hardcoded local data remains.

**Non-Goals:**

- No backend, Firebase auth, Firestore, real chat, push notification, camera, gallery, or GPS implementation.
- No production privacy redesign beyond removing misleading claims and clarifying demo behavior.
- No Room schema migration unless package cleanup unexpectedly requires generated code changes.
- No release signing or Google Play deployment work.

## Decisions

1. Use the Gradle wrapper as the supported build entrypoint.
   - Rationale: `gradlew testDebugUnitTest` gives future contributors the same Gradle distribution and avoids relying on machine-global Gradle.
   - Alternative considered: document a local Gradle installation requirement. Rejected because it leaves the baseline less reproducible.

2. Replace template tests with app-specific smoke tests.
   - Rationale: tests should prove that the app resources, theme, and at least one real screen can be instantiated instead of testing `2 + 2` or stale generated examples.
   - Alternative considered: delete all tests to make the build pass. Rejected because the acceptance criteria require tests to pass and the codebase needs a useful safety net.

3. Keep screenshot coverage narrow and real.
   - Decision: `GreetingScreenshotTest` will render a real, small, deterministic existing UI component under `MascotasPerdidasTheme`, without Room, navigation, network, permissions, or seeded database timing.
   - Rationale: the screenshot baseline should validate current app UI while staying stable enough for a build-stabilization stage.
   - Alternative considered: recreate a `Greeting` composable only for the test. Rejected because it would preserve template code instead of validating the current app.

4. Fully rename the Android/Kotlin package baseline to `com.findyourpet.app`.
   - Decision: this stage will replace `com.example` package declarations/imports/source paths and the AI Studio-derived application id with `com.findyourpet.app`.
   - Rationale: the project is still in stabilization, so this is the right moment to remove template and AI Studio naming before production capabilities are added.
   - Alternative considered: only align namespace and tests while deferring the full rename. Rejected because it would leave avoidable technical debt in the base stage.

5. Remove only dependencies that are clearly unused by the current code.
   - Rationale: this stage should reduce noise without breaking later planned work. Commented future dependencies for auth, Firestore, camera, location, or DataStore can be removed from active configuration and reintroduced by their dedicated OpenSpec changes.
   - Alternative considered: aggressively prune the version catalog. Rejected because aliases may be planned but inactive; the implementation should focus on compile/runtime classpath clarity.

6. Do not introduce new sensitive-data capabilities.
   - Rationale: this change may touch labels around owner contact data, but it must not alter authorization or claim stronger privacy than implemented. Existing demo contact and location values remain demo data until later privacy/auth/backend changes.
   - Alternative considered: harden contact sharing in this change. Rejected because that belongs in the later `harden-local-privacy` and `redesign-contact-sharing` work.

7. Prefer local guardrail tests for stabilization regressions that do not require a device.
   - Decision: add debug unit tests for Manifest permissions, active Gradle future-feature dependencies/plugins, source text mojibake/unsupported claims, `PetStatusChip` states, `ProtectedContactCard` hidden/visible states, and a Robolectric `MainActivity` startup smoke.
   - Rationale: these checks catch likely regressions in the build-stabilization surface without requiring a phone or emulator.
   - Alternative considered: leave these as manual checklist items. Rejected because they are deterministic enough to enforce in `testDebugUnitTest`.
   - Boundary: `connectedDebugAndroidTest` remains device/emulator validation and is tracked separately from local tests.

## Risks / Trade-offs

- Package rename touches many files -> mitigate by using IDE-safe refactor or mechanical package move, then run `testDebugUnitTest` and a debug build.
- Screenshot tests can be brittle -> mitigate by rendering a simple deterministic UI state and keeping the golden file path stable.
- Gradle wrapper regeneration may update Gradle version unexpectedly -> mitigate by using a version compatible with the current Android Gradle Plugin and committing the wrapper properties with the generated scripts.
- Dependency cleanup may remove a dependency used only by generated code or previews -> mitigate by running compile and tests after each cleanup pass.
- Spanish copy fixes can create encoding problems on Windows consoles -> mitigate by storing files as UTF-8 and checking visible rendered text or source output with an encoding-aware viewer.

## Migration Plan

1. Generate or restore wrapper files and verify `.\gradlew.bat --version` on Windows.
2. Fix tests and package/resource expectations until `.\gradlew.bat testDebugUnitTest` passes.
3. Rename package declarations, imports, source paths, Android namespace, and application id to `com.findyourpet.app`.
4. Remove stale imports/dependencies and correct copy/encoding issues.
5. Run `.\gradlew.bat assembleDebug testDebugUnitTest`.

The final validation for this change ran `.\gradlew.bat testDebugUnitTest --console=plain`, `.\gradlew.bat assembleDebug --console=plain`, and `.\gradlew.bat connectedDebugAndroidTest --console=plain` successfully on 2026-07-25. The instrumented test ran against `Medium_Phone(AVD) - 16`.

Rollback is a normal source revert because no backend, local database schema, remote data, or production release artifact is changed.

## Resolved Decisions

- Package rename will be completed in this change: `com.example` and `com.aistudio.mascotasperdidas.petfind` will be replaced with `com.findyourpet.app`.
- Screenshot baseline will use a real, small, deterministic existing Compose component under `MascotasPerdidasTheme`, without relying on Room, navigation, or seeded data.
