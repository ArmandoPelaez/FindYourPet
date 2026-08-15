## MODIFIED Requirements

### Requirement: Create Payloads Are Validated

The backend SHALL validate create payloads for required ownership, participant, recipient and moderation fields before accepting writes, and SHALL reject sighting creation when the owner has an active block for the authenticated reporter.

#### Scenario: Message sender mismatch

- **GIVEN** user A is signed in
- **WHEN** user A attempts to create a chat message with `senderId` equal to user B
- **THEN** the backend denies the write

#### Scenario: Initial sighting message in atomic batch

- **GIVEN** user B is signed in and reports a sighting for user A's post
- **WHEN** the client creates the sighting, chat session, first chat message and owner notification in one Firestore batch
- **THEN** the backend allows the first message only when the resulting chat session contains user A and user B as participants and the message `senderId` equals user B

#### Scenario: Owner creates an authorized content report

- **GIVEN** user B is authenticated and owns the post referenced by `sighting_123`
- **WHEN** user B creates a content report for `sighting_123`
- **THEN** Firestore allows the write only when the report identifies user B as `reportingUserId`, the sighting reporter as `reportedUserId` when present, the selected reason/status fields are valid and the report is linked to the existing sighting

#### Scenario: Non-owner creates a content report

- **GIVEN** user C is authenticated but does not own the post for `sighting_123`
- **WHEN** user C attempts to create a report for that sighting
- **THEN** Firestore denies the write

#### Scenario: Owner creates an authorized user block

- **GIVEN** user B is authenticated and owns a sighting whose reporter is user A
- **WHEN** user B creates the block relation from user B to user A
- **THEN** Firestore allows the write only when blocker/blocked identities and the originating sighting match the authorized owner/reporting-user relationship

#### Scenario: Client bypasses the UI after a block

- **GIVEN** user B has an active block for user A
- **WHEN** user A directly invokes a sighting create for a post owned by user B
- **THEN** Firestore denies the sighting write before any dependent notification, chat session or message write can succeed

