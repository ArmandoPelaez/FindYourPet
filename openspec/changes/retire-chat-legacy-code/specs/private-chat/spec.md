## MODIFIED Requirements

### Requirement: Chat Session Membership

The system SHALL retain historical participant fields for existing Chat sessions, but the active client SHALL not create or reuse new Chat sessions.

#### Scenario: Historical session remains protected

- **GIVEN** a historical Chat session contains owner and reporter participant ids
- **WHEN** a participant accesses the remote document through an explicitly retained compatibility path
- **THEN** participant authorization remains enforced

#### Scenario: New session creation is retired

- **GIVEN** any client attempts to create a Chat session for a new sighting
- **WHEN** the request reaches the active app or backend rules
- **THEN** no new Chat session is created

### Requirement: Participant-Only Chat Access

The backend MAY retain participant-only reads for historical Chat data, but the active app SHALL expose no screen or action that opens those sessions or messages.

#### Scenario: Non-participant accesses historical data

- **GIVEN** user C is not a participant in a historical Chat session
- **WHEN** user C attempts to read the session or messages
- **THEN** the backend denies access

#### Scenario: Participant navigates the active app

- **GIVEN** user A was a participant in a historical Chat session
- **WHEN** user A uses the current app navigation
- **THEN** the app exposes no Chat destination for that session

### Requirement: Participant-Only Message Sending

The active system SHALL reject all new client message creates, regardless of participant identity, while preserving historical records.

#### Scenario: Participant attempts to send a new message

- **GIVEN** user B is a historical Chat participant
- **WHEN** user B attempts to create a new message
- **THEN** Firestore denies the write
- **AND** the app exposes no composer or retry path

### Requirement: Messages Are Immutable

Historical Chat messages SHALL remain immutable, and the active client SHALL not update or delete them.

#### Scenario: Client edits or deletes a historical message

- **GIVEN** a historical message exists
- **WHEN** any client attempts to modify or delete it
- **THEN** the backend denies the operation

### Requirement: Contact Sharing Is Owner-Controlled

The active client SHALL expose no Chat-scoped contact-sharing action, and new Chat contact grant writes SHALL be denied; historical contact data remains governed by existing privacy rules until a separate retention decision.

#### Scenario: User searches the active UI for Chat contact sharing

- **GIVEN** a signed-in user opens Profile, Alertas, Actividad or Detalle
- **WHEN** the UI renders
- **THEN** it exposes no action to share, revoke or reveal contact through Chat

### Requirement: Sighting Chats Require Distinct Participants

The sighting flow SHALL no longer create or reuse a Chat session; distinct owner/reporter validation remains a sighting rule rather than a Chat creation rule.

#### Scenario: Valid sighting has distinct users

- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the sighting is accepted
- **THEN** the sighting and notification are processed without creating a Chat session

#### Scenario: Self-sighting remains blocked

- **GIVEN** user A owns a pet post
- **WHEN** user A submits a sighting for that post
- **THEN** the sighting is rejected before any sighting, notification or Chat write
