## ADDED Requirements

### Requirement: Reproducible Gradle Entry Point
The project SHALL include Gradle wrapper files that allow developers to build and test the Android app without requiring a globally installed Gradle.

#### Scenario: Developer checks the Gradle wrapper
- **GIVEN** a clean checkout of the repository
- **WHEN** the developer runs the repo-local Gradle wrapper version command
- **THEN** the command succeeds and reports the Gradle version used by the project

#### Scenario: Developer runs unit tests through the wrapper
- **GIVEN** a clean checkout of the repository
- **WHEN** the developer runs `testDebugUnitTest` through the repo-local Gradle wrapper
- **THEN** Gradle resolves the project and executes the debug unit test task

### Requirement: Debug Unit Tests Compile And Pass
The Android project SHALL keep `testDebugUnitTest` compiling and passing against existing app code, resources, and themes.

#### Scenario: Unit tests reference current resources
- **GIVEN** the app resource `app_name` is defined as the current product name
- **WHEN** the debug unit test suite runs
- **THEN** no unit test expects stale template values such as `My Application`

#### Scenario: Unit tests reference existing Kotlin symbols
- **GIVEN** the current app code does not define template symbols such as `Greeting` or `MyApplicationTheme`
- **WHEN** the debug unit test suite compiles
- **THEN** no test imports or invokes classes, functions, or themes that do not exist

### Requirement: Screenshot Test Uses Real App UI
The screenshot test SHALL render a real, small, deterministic existing Compose UI surface using the app theme, without depending on Room, navigation, or seeded data.

#### Scenario: Screenshot test renders current theme
- **GIVEN** `MascotasPerdidasTheme` is the current app theme
- **WHEN** the screenshot test sets Compose content
- **THEN** it uses `MascotasPerdidasTheme` or another existing app theme, not removed template theme names

#### Scenario: Screenshot output is generated
- **GIVEN** the screenshot test is executed with Robolectric and Roborazzi
- **WHEN** the rendered UI reaches an idle state
- **THEN** a screenshot is captured to the configured test screenshot path without compile-time or runtime symbol errors

#### Scenario: Status chips cover known and unknown states
- **GIVEN** the app renders status chips for pet post states
- **WHEN** the debug unit test suite runs local Compose tests
- **THEN** `PERDIDO`, `AVISTADO`, `REUNIDO`, and an unknown status are verified as visible UI labels

#### Scenario: Protected contact card covers hidden and visible states
- **GIVEN** owner phone and email are demo contact fields
- **WHEN** the debug unit test suite renders `ProtectedContactCard`
- **THEN** the hidden state masks phone and email, and the visible state shows the owner contact values

### Requirement: Package Naming Is Project-Specific
The Android namespace, application id, Kotlin package declarations, source paths, imports, and test expectations SHALL use `com.findyourpet.app` instead of Android Studio template naming or AI Studio-derived naming.

#### Scenario: Package migration is completed to project namespace
- **GIVEN** the project currently contains `com.example` or `com.aistudio.mascotasperdidas.petfind` identifiers
- **WHEN** the project is built and tested
- **THEN** Kotlin sources, source paths, imports, namespace, application id, and test expectations use `com.findyourpet.app`

### Requirement: Template And Dependency Noise Is Removed
The project SHALL remove broken template tests, unused imports, and unused active dependencies that obscure what the current demo actually uses.

#### Scenario: Template tests are removed or replaced
- **GIVEN** the repository contains generated example tests
- **WHEN** the stabilization change is implemented
- **THEN** tests that only verify template behavior are removed or replaced with app-specific smoke tests

#### Scenario: Active dependencies match current code
- **GIVEN** a dependency is active in Gradle configuration
- **WHEN** the implementation reviews dependency usage
- **THEN** dependencies not used by source, tests, generated code, or current tooling are removed or documented for a near-term change

#### Scenario: Future-feature plugins and dependencies stay inactive
- **GIVEN** Firebase, Google Services, Secrets, Retrofit, Moshi, camera, and location integrations are future features
- **WHEN** the debug unit test suite scans active Gradle configuration files
- **THEN** those plugins and dependencies are not applied or declared as active project dependencies

### Requirement: User-Facing Demo Text Is Legible And Honest
The app SHALL keep user-facing Spanish labels legible and make demo-only behavior clear where the current implementation remains local or hardcoded.

#### Scenario: Contact labels are legible
- **GIVEN** screens display owner contact fields
- **WHEN** labels for owner, phone, email, address, or related contact information are shown
- **THEN** Spanish copy uses correct UTF-8 text such as "Dueño", "Teléfono", and "Dirección" without mojibake

#### Scenario: Demo behavior remains explicit
- **GIVEN** the app still uses local seed data, hardcoded users, preset images, or simulated location values
- **WHEN** developers read the relevant code or tests
- **THEN** those values are identifiable as demo/local behavior and are not presented as production authentication, privacy, GPS, or backend behavior

#### Scenario: Source text guardrail rejects mojibake and unsupported claims
- **GIVEN** app source text can regress through encoding mistakes or premature marketing copy
- **WHEN** the debug unit test suite scans main Kotlin and XML sources
- **THEN** mojibake markers and unsupported privacy, encryption, or realtime claims are rejected

### Requirement: Stabilization Does Not Expand Sensitive Data Access
The change SHALL NOT introduce new read, create, modify, or delete capabilities for personal data, location data, photos, messages, or sightings.

#### Scenario: No new sensitive-data permission is added
- **GIVEN** this change is limited to build, tests, naming, dependency, and text cleanup
- **WHEN** the Android manifest and Gradle dependencies are reviewed
- **THEN** no new runtime permission or backend data access path is introduced

#### Scenario: Manifest permissions remain constrained
- **GIVEN** the stabilized app currently needs only internet access
- **WHEN** the debug unit test suite parses `AndroidManifest.xml`
- **THEN** the manifest declares `android.permission.INTERNET` and no additional Android permissions

#### Scenario: Privacy claims remain bounded
- **GIVEN** the app contains demo contact-sharing UI
- **WHEN** text cleanup is completed
- **THEN** the UI and code do not claim production-grade privacy, encryption, realtime delivery, or authorization that the implementation does not provide

#### Scenario: Main activity starts locally
- **GIVEN** the app should open its primary activity before device-only validation is available
- **WHEN** a Robolectric debug unit test starts `MainActivity`
- **THEN** the activity is created under `com.findyourpet.app` without requiring an emulator
