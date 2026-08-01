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
The backend SHALL restrict post status and notification read-state updates to the authorized role for each resource, and SHALL deny all app-managed contact-sharing writes because contact grants and contact-sharing fields are retired.

#### Scenario: Unauthorized contact-sharing update
- **GIVEN** any authenticated user attempts to update contact-sharing fields or contact grant records
- **WHEN** the request reaches Firestore rules
- **THEN** the backend denies the write

#### Scenario: Owner creates contact grant
- **GIVEN** user A is the owner participant of a chat
- **WHEN** user A attempts to create an active contact grant for that chat
- **THEN** the backend denies the write because app-managed contact grants are no longer allowed

#### Scenario: Owner revokes contact grant
- **GIVEN** user A is the owner participant of a chat with a legacy contact grant
- **WHEN** user A attempts to update the grant state from the client
- **THEN** the backend denies the client write and the updated app does not rely on the grant for contact visibility

#### Scenario: Notification read-state update
- **GIVEN** user A has a notification addressed to user A
- **WHEN** user A marks the notification as read
- **THEN** the backend updates only that notification's read state

### Requirement: Public Post Contact Writes Are Denied
The backend SHALL prevent clients from writing public pet post or chat session fields that expose owner phone, email, address, precise coordinates as personal contact data, post-level public contact reveal state, or chat-level contact sharing state.

#### Scenario: Owner writes public reveal flag
- **GIVEN** user A owns a pet post
- **WHEN** user A attempts to create or update the post with `isContactRevealedToAll`
- **THEN** the backend denies the write

#### Scenario: Owner writes direct contact into public post
- **GIVEN** user A owns a pet post
- **WHEN** user A attempts to create or update the shared post with `ownerPhone`, `ownerEmail`, or `ownerAddress`
- **THEN** the backend denies the write

#### Scenario: Participant writes chat contact flag
- **GIVEN** user A or user B is a participant in a chat session
- **WHEN** the user attempts to create or update the chat session with `isContactSharedByOwner`
- **THEN** the backend denies the write

### Requirement: Contact Grant Reads Are Participant-Only
The backend SHALL retire contact grant reads. No authenticated participant or non-participant SHALL be allowed to read direct personal contact values from contact grant documents through production rules.

#### Scenario: Reporter reads active grant
- **GIVEN** user B is the reporter participant in a chat with a legacy active contact grant
- **WHEN** user B attempts to read that chat contact grant
- **THEN** the backend denies the read or the updated client ignores the document without rendering direct contact values

#### Scenario: Non-participant reads grant
- **GIVEN** user C is not a participant in the chat
- **WHEN** user C attempts to read the chat contact grant
- **THEN** the backend denies the read

#### Scenario: Owner reads own legacy grant
- **GIVEN** user A owns the post related to a legacy contact grant
- **WHEN** user A attempts to read the contact grant as a production document
- **THEN** the backend denies the read or the client does not expose the grant as current app state

### Requirement: Media References Are Validated
The backend SHALL accept production media references only when they are Cloudinary image references written through an authorized post or sighting flow.

#### Scenario: Owner creates post with Cloudinary photo
- **GIVEN** user A is signed in
- **WHEN** user A creates a pet post with uploaded media metadata
- **THEN** Firestore rules allow the write only when owner identity matches user A and the media provider, public ID, content type and Cloudinary URL are valid

#### Scenario: Sighting without optional photo
- **GIVEN** user B reports a sighting without photo evidence
- **WHEN** user B creates the sighting
- **THEN** Firestore rules allow empty media metadata only for the optional sighting photo path

### Requirement: Precise Location Writes Are Validated
The backend SHALL accept precise location fields only from authorized create/update paths and SHALL deny unauthorized changes to those fields.

#### Scenario: Reporter creates sighting coordinates
- **GIVEN** user B is signed in and reporting user A's post
- **WHEN** user B creates a sighting with precise coordinates
- **THEN** backend rules allow the create only when `reporterId` matches user B and `ownerId` matches the referenced post owner

#### Scenario: User attempts coordinate reassignment
- **GIVEN** a production sighting exists
- **WHEN** any client attempts to update its precise coordinates after creation
- **THEN** backend rules deny the update

### Requirement: Backend Denies Self-Sighting Writes
Firestore rules SHALL deny production sighting and sighting-derived chat writes when `ownerId` and `reporterId` identify the same authenticated user.

#### Scenario: Owner creates sighting for own post
- **GIVEN** user A is signed in
- **AND** user A owns the referenced pet post
- **WHEN** user A attempts to create a sighting with `ownerId` equal to user A and `reporterId` equal to user A
- **THEN** Firestore denies the sighting create

#### Scenario: Owner and reporter are distinct
- **GIVEN** user A owns the referenced pet post
- **AND** user B is signed in
- **WHEN** user B creates a valid sighting with `ownerId` equal to user A and `reporterId` equal to user B
- **THEN** Firestore allows the create according to existing authentication, media, and location rules

#### Scenario: Self-chat from sighting fan-out
- **GIVEN** user A is signed in
- **WHEN** a client attempts to create a chat session from a sighting where `ownerId` equals `reporterId`
- **THEN** Firestore denies the chat session create and any dependent message create in the batch

