## ADDED Requirements

### Requirement: Sighting Alert Message Contract
The backend data model SHALL support a chat message type `sighting_alert` linked to one sighting and its post, with immutable participant identity fields and an authorized render snapshot.

#### Scenario: Alert message is persisted
- **GIVEN** an authenticated reporter submits a valid sighting for a post owned by another authenticated user
- **WHEN** the fan-out persists the initial chat message
- **THEN** the message contains `type`, `sightingId`, `postId`, `ownerId`, `reporterId`, `senderId`, `createdAt` and an authorized snapshot with optional photo metadata, location display and general details

#### Scenario: Alert references remain consistent
- **GIVEN** a `sighting_alert` message exists
- **WHEN** the system loads its linked sighting and chat session
- **THEN** `sightingId`, `postId`, `ownerId`, `reporterId` and chat participants identify the same report and cannot be reassigned by a client

#### Scenario: Optional photo is absent
- **GIVEN** a reporter submits a sighting without photo evidence
- **WHEN** the alert message is persisted
- **THEN** the photo attachment is absent or explicitly empty and the message remains valid

#### Scenario: Legacy message is mapped safely
- **GIVEN** an existing chat message has no `type` or sighting fields
- **WHEN** it is mapped into the local model
- **THEN** the mapper assigns the legacy message behavior without inventing a sighting reference or failing the conversation load

### Requirement: Sighting Alert Snapshot Excludes Direct Contact
The alert snapshot SHALL exclude owner and reporter phone, email, address, contact grants and exact coordinates; precise location remains only in the protected canonical sighting fields when authorized.

#### Scenario: Client attempts prohibited snapshot fields
- **GIVEN** a client submits an alert snapshot containing phone, email, address or contact-grant fields
- **WHEN** the backend validates the message
- **THEN** the payload is rejected

#### Scenario: Authorized chat displays location
- **GIVEN** a participant reads an alert with a coarse or user-provided location display
- **WHEN** the chat card is rendered
- **THEN** the participant sees only the approved conversational location display and not unapproved precise coordinates

