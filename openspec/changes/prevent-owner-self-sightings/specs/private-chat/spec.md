## ADDED Requirements

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
