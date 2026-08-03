## ADDED Requirements

### Requirement: Sighting Alert Message Writes Are Validated
Firestore rules SHALL allow `sighting_alert` message creation only for an authenticated reporter in a valid sighting fan-out tied to the matching post, chat and owner.

#### Scenario: Valid sighting alert message is written
- **GIVEN** user B is authenticated and submits a valid sighting for user A's post
- **WHEN** the client creates the sighting, chat session, `sighting_alert` message and owner notification in one authorized batch
- **THEN** Firestore allows the message only when `senderId` equals user B, the chat contains user A and user B as participants, and the `sightingId`, `postId`, `ownerId` and `reporterId` match across documents

#### Scenario: Sender attempts spoofed alert
- **GIVEN** user C is authenticated
- **WHEN** user C attempts to create a `sighting_alert` message with `senderId` equal to user B
- **THEN** Firestore denies the write

#### Scenario: Alert references mismatched sighting
- **GIVEN** user B is authenticated
- **WHEN** user B attempts to create a `sighting_alert` message whose `sightingId`, `postId`, `ownerId`, `reporterId` or `chatId` does not match the accepted sighting and chat session
- **THEN** Firestore denies the write

#### Scenario: Alert includes prohibited contact fields
- **GIVEN** user B is authenticated and writes a `sighting_alert` message
- **WHEN** the payload includes phone, email, contact address, public contact reveal flags or contact-grant fields
- **THEN** Firestore denies the write

### Requirement: Sighting Alert Reads Are Participant-Only
Firestore rules SHALL allow `sighting_alert` reads only to authenticated participants of the matching chat and sighting.

#### Scenario: Participant reads alert
- **GIVEN** user A or user B is authenticated and belongs to the chat created from the sighting
- **WHEN** that user opens the chat
- **THEN** Firestore allows reading the `sighting_alert` message and the linked authorized sighting data

#### Scenario: Non-participant reads alert
- **GIVEN** user C is not the owner or reporter participant in the chat
- **WHEN** user C attempts to read the `sighting_alert` message or linked sighting data
- **THEN** Firestore denies access
