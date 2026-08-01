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
The system SHALL retire owner-controlled contact sharing. No chat participant SHALL be able to enable, revoke, read, or depend on an app-managed contact-sharing state for direct personal data.

#### Scenario: Reporter attempts to reveal owner contact
- **GIVEN** user B is the reporter in a chat session
- **WHEN** user B attempts to access a retired owner contact sharing path
- **THEN** the backend denies the request and the chat UI shows no contact reveal state

#### Scenario: Owner opens chat actions
- **GIVEN** user A is the owner participant in chat A
- **WHEN** user A opens the chat actions
- **THEN** the app provides messaging actions only and no contact-sharing toggle

#### Scenario: Legacy contact sharing state exists
- **GIVEN** chat A contains a legacy active contact-sharing flag
- **WHEN** either participant opens chat A
- **THEN** the system ignores that flag and does not change unrelated chats or messages

### Requirement: Contact Share Events Are Auditable
The system SHALL stop creating contact share or revoke system events because app-managed contact sharing is retired. Existing legacy events SHALL be displayed, if at all, as generic historical system messages without exposing contact values or active availability.

#### Scenario: Owner would have shared contact
- **GIVEN** the owner is in a chat
- **WHEN** the owner opens current chat controls
- **THEN** no action exists that records a contact share event

#### Scenario: Legacy share event is present
- **GIVEN** a prior chat history contains a contact share or revoke event
- **WHEN** the chat message list renders
- **THEN** the event does not expose phone, email, address, precise coordinates, or current contact availability

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

### Requirement: Chat Is The Only App-Mediated Contact Path
The app SHALL provide in-app private chat as the only app-mediated contact path between a pet owner and a reporter.

#### Scenario: Reporter contacts owner
- **GIVEN** a reporter submits or opens a sighting-derived conversation
- **WHEN** the reporter wants to communicate with the owner
- **THEN** the app provides the private chat and does not provide phone, email, address, or external-contact actions

#### Scenario: Owner contacts reporter
- **GIVEN** an owner opens a private chat with a reporter
- **WHEN** the owner wants to follow up
- **THEN** the app provides the private chat and does not provide an app-managed personal-data sharing control

