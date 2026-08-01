## ADDED Requirements

### Requirement: Release Build Is Signed And Minified
The Android app SHALL produce a release build that is signed with release credentials stored outside the repository and minified with the configured Android shrinker rules.

#### Scenario: Release build uses external signing secrets
- **GIVEN** release signing environment variables and the release keystore are available on the build machine
- **WHEN** the release build is generated
- **THEN** the build uses the release signing configuration without reading secrets from tracked source files

#### Scenario: Missing signing secrets fail clearly
- **GIVEN** one or more required release signing values are unavailable
- **WHEN** the release build or release preflight is run
- **THEN** the command fails before publication with a clear message naming the missing configuration

#### Scenario: Release build is minified
- **GIVEN** the release build type is inspected
- **WHEN** release readiness is validated
- **THEN** minification is enabled and project ProGuard/R8 rules are applied

### Requirement: Release Validation Covers Critical Flows
The project SHALL validate critical repository, ViewModel and user flows before a release build is considered ready for controlled publication.

#### Scenario: Repository and mapper tests cover production data paths
- **WHEN** local unit tests are run for repositories, remote mappers and validation logic
- **THEN** publication, sighting, chat-only contact, profile and empty-state data paths are covered by deterministic tests or documented as manual validation gaps

#### Scenario: ViewModel tests cover critical UI states
- **WHEN** ViewModel tests run for primary app flows
- **THEN** loading, success, empty, validation error and backend error states are verified for the affected flows

#### Scenario: Main flows are manually validated on release build
- **GIVEN** a signed release build is installed on a supported device or emulator
- **WHEN** feed, detail, create post, report sighting, auth, chat-only contact, notification and profile flows are exercised
- **THEN** validation evidence records pass/fail result, device or emulator, build version and any blockers

### Requirement: Basic Accessibility Gate
The release process SHALL include a basic accessibility gate for the primary Compose screens and controls.

#### Scenario: Primary controls expose accessible labels
- **WHEN** primary screens are reviewed or tested
- **THEN** image buttons, permission actions, navigation actions, form fields and chat controls expose meaningful accessible labels or text

#### Scenario: Critical screens support readable interaction
- **WHEN** primary screens are checked with common Android accessibility settings or inspection tools
- **THEN** critical controls remain reachable, visible and understandable without relying only on color, icon shape or placeholder text

### Requirement: Crash Reporting Is Production Ready
The release SHALL include crash reporting that can diagnose production errors without collecting sensitive user content in crash metadata.

#### Scenario: Crash reporting dependency is configured
- **WHEN** release readiness is inspected
- **THEN** the Android app includes the crash reporting SDK and Gradle plugin required for release crash reports and mapping upload

#### Scenario: Release crashes include build identity
- **WHEN** a crash report is captured from a release build
- **THEN** the report identifies the app versionCode and versionName used by that build

#### Scenario: Crash metadata excludes sensitive data
- **WHEN** crash reporting custom keys, logs and exception messages are reviewed
- **THEN** they do not include phone numbers, emails, addresses, precise coordinates, private message bodies, full sighting notes, photo URLs or authentication secrets

### Requirement: Privacy Policy Is Ready For Store Review
The project SHALL include a privacy policy suitable for Google Play review before controlled publication.

#### Scenario: Privacy policy describes implemented data handling
- **WHEN** the privacy policy is reviewed
- **THEN** it describes collected data, purpose, permissions, storage, third-party processors, retention, user choices and contact channel in terms that match implemented app behavior

#### Scenario: Privacy policy avoids unsupported guarantees
- **WHEN** the privacy policy and user-facing copy are reviewed
- **THEN** they do not claim encryption, anonymity, realtime guarantees or privacy protections that the implementation does not provide

### Requirement: Release Evidence And Rollback Are Documented
The release SHALL have documented evidence for build, tests, monitoring, permissions, privacy, accessibility and rollback before it is considered ready.

#### Scenario: Release checklist is complete
- **WHEN** release readiness is reviewed
- **THEN** the checklist records release build path, versionCode, versionName, test results, manual validation, permissions review, privacy policy location, crash reporting status and accessibility review

#### Scenario: Rollback path is documented
- **WHEN** a severe issue is found during controlled release
- **THEN** the release notes identify the previous valid build or corrective release path and the monitoring signal used to decide rollback
