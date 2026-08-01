## MODIFIED Requirements

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
