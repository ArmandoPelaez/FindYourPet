## MODIFIED Requirements

### Requirement: Participant-Only Message Sending
The system SHALL allow only chat participants to create messages and SHALL require `senderId` to match the signed-in Firebase `uid`. A new sighting submission SHALL not create a first Chat message.

#### Scenario: Reporter sends message
- **GIVEN** user B is the reporter participant in a Chat session
- **WHEN** user B sends a message
- **THEN** the backend creates a message with `senderId` equal to user B's Firebase `uid`

#### Scenario: Reporter creates first message with new chat session
- **GIVEN** user B submits a new sighting for user A's post
- **WHEN** the sighting submission fan-out runs
- **THEN** it creates no first Chat message and does not create a Chat session as a prerequisite

### Requirement: Sighting Chats Require Distinct Participants
Legacy sighting-derived Chat sessions, when explicitly created by a compatible flow, SHALL contain distinct authenticated owner and reporter participants. The new sighting submission flow SHALL not create or reuse a sighting Chat session.

#### Scenario: New sighting does not create Chat
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the submission succeeds
- **THEN** the system creates no sighting-derived Chat session and opens no private conversation

#### Scenario: Legacy Chat has distinct participants
- **GIVEN** a compatible legacy flow explicitly creates a sighting-derived Chat
- **WHEN** the Chat session is persisted
- **THEN** it contains user A as `ownerId`, user B as `reporterId`, and distinct participant ids

