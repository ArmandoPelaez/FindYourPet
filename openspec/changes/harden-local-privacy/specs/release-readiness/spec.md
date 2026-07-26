## ADDED Requirements

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
- **WHEN** user-facing Kotlin and XML text is scanned
- **THEN** the app does not claim encryption, production privacy, realtime delivery, or authorization that the implementation does not provide

#### Scenario: Release includes privacy validation evidence
- **GIVEN** a change touches personal data, local storage, permissions, location, contact sharing, or messages
- **WHEN** the change is closed
- **THEN** the implementation notes include debug test results or documented manual validation for the affected privacy surfaces
