## MODIFIED Requirements

### Requirement: Sighting Creates Conversation Path
The system SHALL create or reuse a chat session between the post owner and reporter when a sighting is accepted by the backend, and SHALL create a first visible `sighting_alert` chat item containing the authorized sighting information.

#### Scenario: Sighting starts chat
- **GIVEN** user B submits the first sighting for user A's post
- **WHEN** the backend write succeeds
- **THEN** the app opens or offers an active chat session containing user A and user B as participants
- **AND** the first visible chat item is the sighting alert sent by user B
- **AND** the alert includes the `sightingId`, post/pet context, optional photo if provided, authorized location display, additional details and timestamp

#### Scenario: Sighting fan-out is atomic
- **GIVEN** user B submits a sighting for user A's post
- **WHEN** the backend accepts the sighting write
- **THEN** the sighting, chat session, `sighting_alert` chat message and owner notification are committed together

#### Scenario: Additional sighting uses same conversation path
- **GIVEN** user B already has a sighting-derived chat with user A for a post
- **WHEN** user B submits another valid sighting for that post
- **THEN** the system reuses the participant chat when appropriate
- **AND** the timeline receives another `sighting_alert` item for the new sighting instead of replacing prior chat history

## ADDED Requirements

### Requirement: Sighting Alert Chat Data Is Authorized
The system SHALL include only sighting data that the owner and reporter are authorized to read inside the initial chat alert.

#### Scenario: Alert contains submitted evidence
- **GIVEN** user B submits a valid sighting with notes, location display and optional photo evidence
- **WHEN** user A opens the sighting-derived chat
- **THEN** user A can view the notes, location display and optional photo inside the `sighting_alert` item

#### Scenario: Alert excludes direct contact fields
- **GIVEN** a sighting-derived chat message is created
- **WHEN** the alert snapshot is persisted or rendered
- **THEN** it does not include owner or reporter phone, email, contact address, external-contact actions or app-managed contact-sharing state

#### Scenario: Invalid sighting does not create alert
- **GIVEN** a reporter submits a sighting that fails validation
- **WHEN** validation fails before backend fan-out
- **THEN** the app creates no `sighting_alert` chat message and sends no owner notification
