## 1. Gradle Baseline

- [x] 1.1 Generate or restore Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`) using a Gradle version compatible with the current Android Gradle Plugin.
- [x] 1.2 Verify the wrapper entrypoint with `.\gradlew.bat --version`.
- [x] 1.3 Review `settings.gradle.kts`, root `build.gradle.kts`, `gradle.properties`, and `gradle/libs.versions.toml` for stale template or incompatible configuration that blocks debug compilation.

## 2. Unit And Screenshot Tests

- [x] 2.1 Remove or replace `ExampleUnitTest` if it only verifies template arithmetic.
- [x] 2.2 Update `ExampleRobolectricTest` so it asserts current app behavior, such as the `Mascotas Perdidas` app label or another real resource.
- [x] 2.3 Fix `GreetingScreenshotTest` to render a real, small, deterministic existing Compose component with `MascotasPerdidasTheme`, without depending on Room, navigation, or seeded data, and with no references to removed `Greeting` or `MyApplicationTheme` symbols.
- [x] 2.4 Update or remove `ExampleInstrumentedTest` so it no longer asserts the stale `com.example` runtime package when instrumentation tests are run later.
- [x] 2.5 Run `.\gradlew.bat testDebugUnitTest` and fix all compile or runtime failures.
- [x] 2.6 Add a local Manifest guardrail test that fails if sensitive Android permissions are reintroduced.
- [x] 2.7 Add a local Gradle guardrail test that fails if active Firebase, Google Services, Secrets, Retrofit, Moshi, location, camera, or similar future-feature dependencies/plugins are reintroduced.
- [x] 2.8 Expand `PetStatusChip` local Compose coverage to `PERDIDO`, `AVISTADO`, `REUNIDO`, and an unknown status.
- [x] 2.9 Add local Compose coverage for `ProtectedContactCard` hidden and visible contact states.
- [x] 2.10 Add a Robolectric smoke test that starts `MainActivity` without a device or emulator.

## 3. Package And Naming Cleanup

- [x] 3.1 Fully rename Kotlin packages from `com.example` to `com.findyourpet.app`.
- [x] 3.2 Update source paths, package declarations, imports, Android namespace, `applicationId`, and tests consistently to `com.findyourpet.app`.
- [x] 3.3 Remove or update any AI Studio-derived identifiers such as `com.aistudio.mascotasperdidas.petfind`.
- [x] 3.4 Confirm there are no references to classes, themes, or generated symbols that no longer exist.

## 4. Code And Dependency Hygiene

- [x] 4.1 Remove unused imports from Kotlin source and test files touched by this change.
- [x] 4.2 Remove stale template comments and active dependency declarations that are demonstrably unused by current source, tests, generated code, or tooling.
- [x] 4.3 Keep future-feature dependencies inactive until their dedicated OpenSpec changes introduce real auth, backend, camera, location, or notification flows.
- [x] 4.4 Ensure remaining demo seed data, hardcoded users, preset images, and simulated location values are clearly identifiable as demo/local behavior.

## 5. Text And Privacy Review

- [x] 5.1 Search for mojibake or unaccented contact labels related to owner, phone, address, and privacy copy.
- [x] 5.2 Correct visible Spanish labels to UTF-8 text such as "Dueño", "Teléfono", and "Dirección".
- [x] 5.3 Review contact-sharing UI text so it does not claim production-grade privacy, encryption, realtime behavior, or authorization beyond what the demo implements.
- [x] 5.4 Confirm the change does not add Android runtime permissions, backend access paths, or new read/create/modify/delete behavior for sensitive data.
- [x] 5.5 Add a source text guardrail test for mojibake markers and unsupported claims such as privacy, encryption, or realtime behavior.

## 6. Final Validation
- [x] 6.1 Run `.\gradlew.bat testDebugUnitTest --console=plain`.
- [x] 6.2 Run `.\gradlew.bat assembleDebug --console=plain`.
- [x] 6.3 Record any skipped validation with the exact command, failure reason, and follow-up needed.
- [x] 6.4 Manually review affected UI/source text for legible Spanish copy and clear demo-only behavior.
- [x] 6.5 Run `.\gradlew.bat connectedDebugAndroidTest --console=plain` against an Android emulator.

## Validation Notes
- `.\gradlew.bat --version` passed with Gradle 9.3.1 using a temporary JDK 17.
- `.\gradlew.bat testDebugUnitTest --console=plain` passed on 2026-07-25 with 10 local tests after adding guardrails for Manifest permissions, active Gradle future-feature dependencies/plugins, source text claims/mojibake, `PetStatusChip` states, `ProtectedContactCard` hidden/visible states, and `MainActivity` startup.
- `.\gradlew.bat assembleDebug --console=plain` passed on 2026-07-25.
- `.\gradlew.bat testDebugUnitTest` and `.\gradlew.bat assembleDebug` are blocked inside the restricted sandbox when the Gradle wrapper tries to download `https://services.gradle.org/distributions/gradle-9.3.1-bin.zip` (`java.net.SocketException: Permission denied: getsockopt`); both commands passed when run with network permission.
- Initial `.\gradlew.bat connectedDebugAndroidTest --console=plain` validation was blocked on 2026-07-25 because no device was connected; Gradle failed at execution with `com.android.builder.testing.api.DeviceException: No connected devices!`.
- Android Studio then created and launched `Medium_Phone(AVD)` using an API 36 emulator image. `adb devices` reported `emulator-5554	device`.
- `.\gradlew.bat connectedDebugAndroidTest --console=plain` passed on 2026-07-25 against `Medium_Phone(AVD) - 16`, running 1 instrumentation test successfully.
