## ADDED Requirements

### Requirement: Publication Docs Follow Chat-Only Contact Policy
Store-facing, public, and release-readiness documentation SHALL describe owner/reporter contact as in-app chat only and SHALL NOT mention app-managed personal contact sharing, contact grants, reveal permissions, or contact availability toggles.

#### Scenario: Prepare production release docs are reviewed
- **GIVEN** `openspec/changes/prepare-production-release` contains proposal, design, tasks, specs, or validation notes
- **WHEN** release documentation is updated for this change
- **THEN** those artifacts no longer require or describe contact sharing, contact grants, contact controls, or contact-sharing notifications

#### Scenario: Privacy policy is reviewed
- **WHEN** `docs/privacy-policy.md` and `public/privacy-policy.html` are reviewed
- **THEN** they state that communication between owner and reporter happens through in-app chat and do not claim the app shares phone, email, address, or direct contact data

#### Scenario: Permission docs are reviewed
- **WHEN** Google Play permission and release validation docs are reviewed
- **THEN** they cover only actual Android/runtime permissions and backend authorization rules, not retired personal contact-sharing permissions

## MODIFIED Requirements

### Requirement: Screenshot Test Uses Real App UI
The screenshot test SHALL render a real, small, deterministic existing Compose UI surface using the app theme, without depending on Room, navigation, seeded data, or retired contact-sharing components.

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

#### Scenario: Retired contact card is not required
- **GIVEN** contact-sharing UI has been removed
- **WHEN** the debug unit test suite runs local Compose tests
- **THEN** no screenshot or Compose test requires `ProtectedContactCard`, contact reveal controls, or visible owner phone/email values

### Requirement: User-Facing Demo Text Is Legible And Honest
The app SHALL keep user-facing Spanish labels legible and make demo-only behavior clear where the current implementation remains local or hardcoded. User-facing contact copy SHALL match the chat-only contact policy.

#### Scenario: Contact labels are legible
- **GIVEN** screens display owner/reporter communication labels
- **WHEN** labels for owner, reporter, chat, privacy, or related contact information are shown
- **THEN** Spanish copy uses correct UTF-8 text such as "Dueño", "Teléfono" only when discussing user-entered content or removed personal-data examples, and "Dirección" only when not presented as an app-shared contact method

#### Scenario: Demo behavior remains explicit
- **GIVEN** the app still uses local seed data, hardcoded users, preset images, or simulated location values
- **WHEN** developers read the relevant code or tests
- **THEN** those values are identifiable as demo/local behavior and are not presented as production authentication, privacy, GPS, backend behavior, or app-managed contact sharing

#### Scenario: Source text guardrail rejects mojibake and unsupported claims
- **GIVEN** app source text can regress through encoding mistakes or premature marketing copy
- **WHEN** the debug unit test suite scans main Kotlin and XML sources
- **THEN** mojibake markers and unsupported privacy, encryption, realtime, contact-sharing, or authorization claims are rejected

### Requirement: Privacy Gate Before Production Release
The project SHALL pass a privacy gate before any production or store-facing build is considered release-ready.

#### Scenario: Release checks backup configuration
- **GIVEN** a release-readiness validation is run
- **WHEN** Android backup and data extraction configuration are inspected
- **THEN** sensitive local data is excluded from backup and transfer

#### Scenario: Release checks permissions
- **GIVEN** a release-readiness validation is run
- **WHEN** manifest permissions are inspected
- **THEN** no unused sensitive permission is declared

#### Scenario: Release checks unsupported privacy claims
- **GIVEN** a release-readiness validation is run
- **WHEN** user-facing Kotlin, XML, Markdown, HTML, and active OpenSpec release text is scanned
- **THEN** the app does not claim encryption, production privacy, realtime delivery, app-managed contact sharing, or authorization that the implementation does not provide

#### Scenario: Release includes privacy validation evidence
- **GIVEN** a change touches personal data, local storage, permissions, location, chat, or messages
- **WHEN** the change is closed
- **THEN** the implementation notes include debug test results or documented manual validation for the affected privacy surfaces
