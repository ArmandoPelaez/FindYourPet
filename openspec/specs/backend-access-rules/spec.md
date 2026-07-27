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
The backend SHALL restrict contact-sharing, post status and notification read-state updates to the authorized role for each resource.

#### Scenario: Unauthorized contact-sharing update
- **GIVEN** user B is not the owner participant of a chat
- **WHEN** user B attempts to update contact-sharing fields
- **THEN** the backend denies the write
