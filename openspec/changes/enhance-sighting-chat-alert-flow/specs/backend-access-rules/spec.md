## MODIFIED Requirements

### Requirement: Create Payloads Are Validated
The backend SHALL validate create payloads for required ownership, participant and recipient fields before accepting writes, and SHALL validate the cross-document references of a `sighting_alert` message.

#### Scenario: Message sender mismatch
- **GIVEN** user A is signed in
- **WHEN** user A attempts to create a chat message with `senderId` equal to user B
- **THEN** the backend denies the write

#### Scenario: Initial sighting message in atomic batch
- **GIVEN** user B is signed in and reports a sighting for user A's post
- **WHEN** the client creates the sighting, chat session, first `sighting_alert` message and owner notification in one Firestore batch
- **THEN** the backend allows the first message only when the resulting chat session contains user A and user B, the message sender is user B, the sighting matches `postId`, `ownerId` and `reporterId`, and the notification recipient is user A

#### Scenario: Forged alert reference is denied
- **GIVEN** user B is signed in and is a valid reporter for post A
- **WHEN** user B creates an alert message referencing another post, owner, reporter or sighting
- **THEN** Firestore denies the message and dependent fan-out write

#### Scenario: Self-sighting alert is denied
- **GIVEN** user A owns the referenced post
- **WHEN** user A attempts to create a `sighting_alert` as both owner and reporter
- **THEN** Firestore denies the alert and any dependent sighting-derived chat write

## ADDED Requirements

### Requirement: Sighting Alert Reads Are Participant-Only
The backend SHALL allow a `sighting_alert` message and its linked sighting to be read only by the authenticated owner or reporter recorded by the matching chat/session.

#### Scenario: Owner reads alert
- **GIVEN** user A is the owner participant of the matching chat
- **WHEN** user A reads the chat timeline
- **THEN** Firestore allows the alert and its authorized snapshot

#### Scenario: Reporter reads alert
- **GIVEN** user B is the reporter participant of the matching chat
- **WHEN** user B reads the chat timeline
- **THEN** Firestore allows the alert and its authorized snapshot

#### Scenario: Non-participant reads alert
- **GIVEN** user C is not the owner or reporter for the matching chat
- **WHEN** user C attempts to read the alert or linked sighting
- **THEN** Firestore denies access

#### Scenario: Alert update or deletion is denied
- **GIVEN** a `sighting_alert` message has been persisted
- **WHEN** any client attempts to update or delete its references, snapshot or content
- **THEN** Firestore denies the write

