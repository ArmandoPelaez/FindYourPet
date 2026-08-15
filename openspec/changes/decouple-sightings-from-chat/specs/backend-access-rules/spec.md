## MODIFIED Requirements

### Requirement: Create Payloads Are Validated
The backend SHALL validate create payloads for required ownership, participant and recipient fields before accepting writes. A new sighting fan-out SHALL validate the sighting and owner notification directly and SHALL NOT require a new Chat session or initial Chat message.

#### Scenario: New sighting fan-out is authorized
- **GIVEN** user B is signed in and reports a valid sighting for user A's post
- **WHEN** the client creates the sighting and owner notification in one Firestore batch
- **THEN** the backend allows the batch only when the sighting owner/reporter/post fields match the referenced post and the notification recipient is the sighting owner

#### Scenario: New sighting fan-out has no Chat dependency
- **GIVEN** user B submits a valid sighting
- **WHEN** the batch is evaluated
- **THEN** the rules do not require a `chatSessions/{chatId}` or `messages/{messageId}` write for the sighting batch

#### Scenario: Message sender mismatch
- **GIVEN** user A is signed in
- **WHEN** user A attempts to create a chat message with `senderId` equal to user B
- **THEN** the backend denies the write

#### Scenario: New sighting submission omits initial message
- **GIVEN** user B submits a new valid sighting for user A's post
- **WHEN** the sighting fan-out is committed
- **THEN** no initial `sighting_alert` message is required or created

#### Scenario: Legacy sighting alert message is validated
- **GIVEN** a legacy Chat flow explicitly creates a `sighting_alert` message
- **WHEN** the message is written in a batch with its Chat session
- **THEN** backend rules validate its matching Chat, sighting, post, owner, reporter and sender fields as before

## ADDED Requirements

### Requirement: Sighting Notification References Are Authorized
The backend SHALL allow a new sighting notification only when its `sightingId`, `postId`, recipient and `targetId` consistently reference the sighting created in the same authorized flow.

#### Scenario: Valid sighting notification is accepted
- **GIVEN** a valid sighting document is created by authenticated reporter B for owner A's post
- **WHEN** the notification uses `recipientId == ownerId`, matching `postId`, `sightingId` and `targetId == sightingId`
- **THEN** Firestore allows the notification create

#### Scenario: Mismatched sighting notification is denied
- **GIVEN** user B attempts to notify owner A
- **WHEN** the notification target, sighting, post or recipient does not match the authorized sighting
- **THEN** Firestore denies the notification create
