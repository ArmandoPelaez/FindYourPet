## MODIFIED Requirements

### Requirement: Template And Dependency Noise Is Removed
The project SHALL remove broken template tests, unused imports, and unused active dependencies that obscure what the current app actually uses.

#### Scenario: Template tests are removed or replaced
- **GIVEN** the repository contains generated example tests
- **WHEN** stabilization and authentication changes are implemented
- **THEN** tests that only verify template behavior are removed or replaced with app-specific smoke tests

#### Scenario: Active dependencies match current code
- **GIVEN** a dependency is active in Gradle configuration
- **WHEN** the implementation reviews dependency usage
- **THEN** dependencies not used by source, tests, generated code, current tooling, or the active authentication/Firestore implementation are removed or documented for a near-term change

#### Scenario: Firebase auth and Firestore dependencies are allowed for this change
- **GIVEN** Firebase Authentication, Cloud Firestore, Google Services, Google Sign-In/Credential Manager, and coroutine Task interop are introduced by `add-user-authentication`
- **WHEN** the debug unit test suite scans active Gradle configuration files
- **THEN** those dependencies and plugins are allowed only when they are wired to real authentication, profile, Firestore, or rules-validation code

#### Scenario: Future-feature dependencies stay inactive
- **GIVEN** Firebase products beyond Auth/Firestore, Secrets, Retrofit, Moshi, camera, location, and unrelated future-feature integrations are not part of this change
- **WHEN** the debug unit test suite scans active Gradle configuration files
- **THEN** those plugins and dependencies are not applied or declared as active project dependencies
