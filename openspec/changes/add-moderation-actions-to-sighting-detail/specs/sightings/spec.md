## MODIFIED Requirements

### Requirement: Sighting Validation Precedes Fan-Out

The app SHALL validate sighting fields, media upload result, location consent/fallback state, authenticated user identity and owner/reporting-user block state before creating chat, notification or backend fan-out records.

#### Scenario: Invalid sighting does not notify owner

- **GIVEN** a reporter submits a sighting with missing required location information
- **WHEN** validation fails
- **THEN** the app creates no sighting, chat message or owner notification

#### Scenario: Blocked reporter is rejected before side effects

- **GIVEN** user A is authenticated and the target post is owned by user B
- **AND** user B has an active block for user A
- **WHEN** user A submits a sighting for user B's post
- **THEN** the app rejects the operation before uploading optional media or creating `SightingAlertEntity`
- **AND** it creates no notification, chat session, chat message or other Chat-related record
- **AND** it shows controlled feedback without revealing unnecessary block details

#### Scenario: Block does not cross owner boundaries

- **GIVEN** user B has blocked user A
- **AND** user A submits a sighting for a post owned by user C
- **WHEN** the app validates the submission
- **THEN** the submission is not rejected because of user B's block

