# backend-access-rules Specification

## Purpose
Define default-deny Firestore access rules and role-limited backend writes for production data.
## Requirements
### Requirement: Default Deny Backend Rules
The backend SHALL deny all reads and writes that are not explicitly allowed by a collection rule.

#### Scenario: Unknown collection access
- **GIVEN** a client requests a collection without an explicit allow rule
- **WHEN** the request reaches Firestore rules
- **THEN** the backend denies the read or write

### Requirement: Authenticated Access Required
The backend SHALL require Firebase Authentication for all production user, post, sighting, chat and notification access.

#### Scenario: Anonymous read attempt
- **GIVEN** no Firebase user is authenticated
- **WHEN** the client attempts to read production backend data
- **THEN** the backend denies access

### Requirement: Ownership Fields Are Immutable
The backend SHALL prevent clients from changing ownership or membership fields after document creation.

#### Scenario: Owner reassignment attempt
- **GIVEN** a pet post exists with `ownerId` equal to user A
- **WHEN** user A or another user attempts to update `ownerId`
- **THEN** the backend denies the update

### Requirement: Create Payloads Are Validated
The backend SHALL validate create payloads for required ownership, participant and recipient fields before accepting writes.

#### Scenario: Message sender mismatch
- **GIVEN** user A is signed in
- **WHEN** user A attempts to create a chat message with `senderId` equal to user B
- **THEN** the backend denies the write

#### Scenario: Initial sighting message in atomic batch
- **GIVEN** user B is signed in and reports a sighting for user A's post
- **WHEN** the client creates the sighting, chat session, first chat message and owner notification in one Firestore batch
- **THEN** the backend allows the first message only when the resulting chat session contains user A and user B as participants and the message `senderId` equals user B

### Requirement: Sensitive Writes Are Role-Limited
The backend SHALL restrict contact grants, post status and notification read-state updates to the authorized role for each resource.

#### Scenario: Unauthorized contact-sharing update
- **GIVEN** user B is not the owner participant of a chat
- **WHEN** user B attempts to update contact-sharing fields or contact grant records
- **THEN** the backend denies the write

#### Scenario: Owner creates contact grant
- **GIVEN** user A is the owner participant of a chat
- **WHEN** user A creates an active contact grant for that chat
- **THEN** the backend allows the write only when the grant `ownerId`, `reporterId`, `postId`, and `chatId` match the chat session

#### Scenario: Owner revokes contact grant
- **GIVEN** user A is the owner participant of a chat with an active contact grant
- **WHEN** user A revokes the grant
- **THEN** the backend allows the grant to become inactive or be deleted without exposing contact values in other records

#### Scenario: Notification read-state update
- **GIVEN** user A has a notification addressed to user A
- **WHEN** user A marks the notification as read
- **THEN** the backend updates only that notification's read state

### Requirement: Public Post Contact Writes Are Denied
The backend SHALL prevent clients from writing public pet post fields that expose owner phone, email, address, precise coordinates as contact data, or post-level public contact reveal state.

#### Scenario: Owner writes public reveal flag
- **GIVEN** user A owns a pet post
- **WHEN** user A attempts to create or update the post with `isContactRevealedToAll`
- **THEN** the backend denies the write

#### Scenario: Owner writes direct contact into public post
- **GIVEN** user A owns a pet post
- **WHEN** user A attempts to create or update the shared post with `ownerPhone`, `ownerEmail`, or `ownerAddress`
- **THEN** the backend denies the write

### Requirement: Contact Grant Reads Are Participant-Only
The backend SHALL allow active chat contact grant reads only to authenticated participants of the matching chat.

#### Scenario: Reporter reads active grant
- **GIVEN** user B is the reporter participant in a chat with an active contact grant
- **WHEN** user B reads that chat contact grant
- **THEN** the backend allows the read

#### Scenario: Non-participant reads grant
- **GIVEN** user C is not a participant in the chat
- **WHEN** user C attempts to read the chat contact grant
- **THEN** the backend denies the read

