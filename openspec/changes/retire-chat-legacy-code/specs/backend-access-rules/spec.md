## ADDED Requirements

### Requirement: New Chat Writes Are Denied

Firestore SHALL deny client creates, updates and deletes for `chatSessions`, nested `messages` and Chat-scoped contact records after the Chat retirement, without deleting historical documents.

#### Scenario: Legacy client creates a session

- **GIVEN** an authenticated client submits a new `chatSessions/{chatId}` document
- **WHEN** Firestore evaluates the write
- **THEN** the backend denies the create

#### Scenario: Legacy client sends a message

- **GIVEN** an authenticated participant submits a nested message
- **WHEN** Firestore evaluates the write
- **THEN** the backend denies the create

#### Scenario: Historical Chat is not deleted

- **GIVEN** an existing session or message is stored remotely
- **WHEN** the new rules are deployed
- **THEN** no cleanup request deletes it

### Requirement: Non-Chat Access Rules Remain Unchanged

The Chat retirement SHALL not relax or remove authorization for users, posts, sightings, notifications, content reports or user blocks.

#### Scenario: Sighting and moderation continue

- **GIVEN** an authenticated user performs a valid sighting or authorized moderation action
- **WHEN** Firestore evaluates the operation
- **THEN** the existing non-Chat ownership and identity rules remain applicable
