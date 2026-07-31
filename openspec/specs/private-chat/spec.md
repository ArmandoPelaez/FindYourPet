# private-chat Specification

## Purpose
Define participant-only backend chat sessions, messages and contact-sharing control.
## Requirements
### Requirement: Chat Session Membership
The system SHALL represent each chat session with immutable `ownerId` and `reporterId` participant fields.

#### Scenario: Chat session is created
- **GIVEN** a sighting links a post owner and reporter
- **WHEN** the chat session is created
- **THEN** the session contains exactly those two participant ids for authorization

### Requirement: Participant-Only Chat Access
The system SHALL allow only chat participants to read the session and messages.

#### Scenario: Participant opens chat
- **GIVEN** user A is the owner or reporter in a chat session
- **WHEN** user A opens the chat
- **THEN** the app displays the session and its messages

#### Scenario: Non-participant opens chat
- **GIVEN** user C is not a participant in a chat session
- **WHEN** user C attempts to read the session or messages
- **THEN** the backend denies access

### Requirement: Participant-Only Message Sending
The system SHALL allow only chat participants to create messages and SHALL require `senderId` to match the signed-in Firebase `uid`.

#### Scenario: Reporter sends message
- **GIVEN** user B is the reporter participant in a chat session
- **WHEN** user B sends a message
- **THEN** the backend creates a message with `senderId` equal to user B's Firebase `uid`

#### Scenario: Reporter creates first message with new chat session
- **GIVEN** user B submits the first sighting for user A's post
- **WHEN** the first chat message is created in the same backend batch as the chat session
- **THEN** backend rules validate the post owner/reporter membership from the resulting chat session before accepting the message

### Requirement: Messages Are Immutable
The system SHALL prevent clients from updating or deleting production chat messages after creation.

#### Scenario: Sender attempts to edit message
- **GIVEN** a message exists in a chat session
- **WHEN** its sender attempts to modify the message text
- **THEN** the backend denies the update

### Requirement: Contact Sharing Is Owner-Controlled
The system SHALL allow only the post owner participant to create, update or revoke contact-sharing state for a specific chat session.

#### Scenario: Reporter attempts to reveal owner contact
- **GIVEN** user B is the reporter in a chat session
- **WHEN** user B attempts to enable owner contact sharing
- **THEN** the backend denies the update

#### Scenario: Owner shares contact in one chat
- **GIVEN** user A is the owner participant in chat A
- **WHEN** user A enables contact sharing for chat A
- **THEN** the system creates or activates contact sharing only for chat A

#### Scenario: Owner revokes contact in one chat
- **GIVEN** user A previously shared contact in chat A
- **WHEN** user A revokes contact sharing for chat A
- **THEN** the system deactivates contact sharing for chat A and does not change unrelated chats

#### Scenario: Contact shared in another chat is unavailable
- **GIVEN** user A shared contact in chat A
- **WHEN** a participant opens chat B without an active contact grant
- **THEN** the chat UI hides user A's direct contact data

### Requirement: Contact Share Events Are Auditable
The system SHALL record contact share and revoke actions as generic chat system events without embedding direct contact values in message text.

#### Scenario: Owner shares contact
- **GIVEN** the owner enables contact sharing in a chat
- **WHEN** the system records the chat event
- **THEN** the event identifies that contact availability changed without including phone, email, address or precise coordinates

#### Scenario: Owner revokes contact
- **GIVEN** the owner revokes contact sharing in a chat
- **WHEN** the system records the chat event
- **THEN** the event identifies that contact is no longer available without including prior contact values

### Requirement: Sighting Chats Require Distinct Participants
The system SHALL create or reuse sighting chat sessions only when the post owner and sighting reporter are two distinct authenticated users.

#### Scenario: Valid sighting chat has two users
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the chat session is created or reused
- **THEN** the session contains user A as `ownerId`, user B as `reporterId`, and both users in `participantIds`

#### Scenario: Self-sighting chat is blocked
- **GIVEN** user A owns a pet post
- **WHEN** user A attempts to create a sighting-derived chat for that post as reporter
- **THEN** the system creates no chat session and opens no private conversation for that self-report
