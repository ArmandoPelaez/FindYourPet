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
The system SHALL allow only the post owner participant to modify contact-sharing fields on a chat session.

#### Scenario: Reporter attempts to reveal owner contact
- **GIVEN** user B is the reporter in a chat session
- **WHEN** user B attempts to enable owner contact sharing
- **THEN** the backend denies the update
