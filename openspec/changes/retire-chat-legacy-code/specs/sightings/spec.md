## MODIFIED Requirements

### Requirement: Sighting Creates Conversation Path

The system SHALL not create or reuse a Chat session or message when a sighting is accepted; the sighting and its owner notification remain the complete active delivery path.

#### Scenario: Sighting is accepted

- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the backend accepts the write
- **THEN** the sighting and owner notification are committed according to existing rules
- **AND** no Chat session or initial Chat message is created

#### Scenario: Sighting result is consumed

- **GIVEN** the repository returns a successful sighting result
- **WHEN** the ViewModel and form handle completion
- **THEN** the result is treated as a sighting result or completion signal
- **AND** no code stores it as `activeChatId`

### Requirement: Sighting Validation Precedes Fan-Out

The app SHALL validate sighting fields, media, location, identity and moderation before creating the sighting or owner notification, without any Chat side effect.

#### Scenario: Invalid sighting does not notify owner

- **GIVEN** a reporter submits a sighting with invalid required data
- **WHEN** validation fails
- **THEN** the app creates no sighting, notification, Chat session or Chat message

#### Scenario: Valid sighting has no Chat dependency

- **GIVEN** a reporter submits a valid sighting
- **WHEN** the repository persists the accepted result
- **THEN** it writes only the sighting and required notification records
- **AND** it does not construct or write Chat entities
