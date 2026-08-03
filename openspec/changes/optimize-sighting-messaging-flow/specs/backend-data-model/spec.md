## ADDED Requirements

### Requirement: Sighting Alert Chat Messages
The backend model SHALL represent the initial sighting chat content with a stable chat message type linked to the canonical sighting.

#### Scenario: Sighting alert message is created
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the fan-out creates the first chat message
- **THEN** the message includes `type = sighting_alert`, `chatId`, `sightingId`, `postId`, `ownerId`, `reporterId`, `senderId = reporterId`, `createdAt` and participant authorization fields required by rules

#### Scenario: Alert has authorized snapshot
- **GIVEN** the sighting contains notes, location display and optional photo metadata
- **WHEN** the `sighting_alert` message is persisted
- **THEN** the message may store a minimal authorized snapshot for chat rendering
- **AND** the canonical sighting remains linked by `sightingId`

#### Scenario: Alert has no optional photo
- **GIVEN** user B submits a sighting without photo evidence
- **WHEN** the `sighting_alert` message is persisted
- **THEN** the message stores no placeholder demo media and marks the photo field absent or empty

#### Scenario: Alert excludes contact fields
- **GIVEN** the backend persists a `sighting_alert` message
- **WHEN** the write is validated
- **THEN** the message document does not contain owner or reporter phone, email, contact address, public contact reveal flags or contact-grant state
