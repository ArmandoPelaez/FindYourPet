## MODIFIED Requirements

### Requirement: Sighting Creates Conversation Path
The system SHALL persist an accepted sighting and notify the post owner without creating or reusing a Chat session or Chat message as part of the new sighting submission flow.

#### Scenario: Sighting creates only sighting and notification
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the backend write succeeds
- **THEN** the system creates the `SightingAlertEntity`/sighting document and an owner notification, and creates no `ChatSessionEntity` or `ChatMessageEntity`

#### Scenario: Sighting fan-out is atomic
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the backend accepts the fan-out
- **THEN** the sighting and owner notification are committed together or neither is committed

### Requirement: Sighting Validation Precedes Fan-Out
The app SHALL validate sighting fields, media upload result, location consent/fallback state and authenticated user identity before creating the sighting or owner notification, and SHALL create no Chat records when validation fails.

#### Scenario: Invalid sighting does not notify owner
- **GIVEN** a reporter submits a sighting with missing required location information
- **WHEN** validation fails
- **THEN** the app creates no sighting, Chat message, Chat session or owner notification

## ADDED Requirements

### Requirement: Sighting Is The Canonical Alert Record
The system SHALL preserve the accepted `SightingAlertEntity`/sighting document as the source of truth for the report, including its notes, stable identifier and idempotency key.

#### Scenario: Notes remain on the sighting
- **GIVEN** user B submits a valid sighting with a comment
- **WHEN** the sighting is persisted
- **THEN** `SightingAlertEntity.notes` contains the submitted comment and no Chat message is used as its source

#### Scenario: Stable retry keeps the sighting identity
- **GIVEN** the same valid submission is retried with the same `idempotencyKey`
- **WHEN** the repository derives the sighting identifier
- **THEN** it uses the same stable `sightingId` and does not require a Chat identifier

