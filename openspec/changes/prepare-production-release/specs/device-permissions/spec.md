## ADDED Requirements

### Requirement: Google Play Permission Review Is Documented
The project SHALL maintain a Google Play permission review that maps every Android permission declared by the manifest to an implemented user-visible flow, affected data and validation evidence.

#### Scenario: Declared permissions have product justification
- **WHEN** the Android manifest is inspected for release readiness
- **THEN** every declared permission is listed with its feature flow, user trigger, data accessed, Play Console justification and validation evidence

#### Scenario: Unused sensitive permission blocks release
- **GIVEN** a declared sensitive permission has no implemented flow or validation evidence
- **WHEN** release readiness is reviewed
- **THEN** the release is blocked until the permission is removed or the implemented flow and validation evidence are added

#### Scenario: Permission denial handling is evidenced
- **WHEN** camera, media, location, notification or other runtime permission flows are validated
- **THEN** granted, denied, permanently denied and unavailable states are recorded for each permission touched by the release

### Requirement: Permission Checklist Matches Store Declarations
The permissions documented for release SHALL match the app manifest and Google Play data safety/store declarations.

#### Scenario: Store declarations match manifest
- **WHEN** the release checklist is completed
- **THEN** the manifest permission inventory, privacy policy and Google Play declarations describe the same permissions and data categories

#### Scenario: Permission copy avoids unsupported claims
- **WHEN** permission rationale copy is reviewed
- **THEN** it explains the concrete feature need and does not claim broader privacy or safety guarantees than the app implements
