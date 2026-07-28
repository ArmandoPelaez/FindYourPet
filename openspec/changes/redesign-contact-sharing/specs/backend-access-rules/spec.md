## ADDED Requirements

### Requirement: Public Post Contact Writes Are Denied
The backend SHALL prevent clients from writing public pet post fields that expose owner phone, email, address, precise coordinates as contact data, or post-level public contact reveal state.

#### Scenario: Owner writes public reveal flag
- **GIVEN** user A owns a pet post
- **WHEN** user A attempts to create or update the post with `isContactRevealedToAll`
- **THEN** the backend denies the write

#### Scenario: Owner writes direct contact into public post
- **GIVEN** user A owns a pet post
- **WHEN** user A attempts to create or update the shared post with `ownerPhone`, `ownerEmail`, or `ownerAddress`
- **THEN** the backend denies the write

### Requirement: Contact Grant Reads Are Participant-Only
The backend SHALL allow active chat contact grant reads only to authenticated participants of the matching chat.

#### Scenario: Reporter reads active grant
- **GIVEN** user B is the reporter participant in a chat with an active contact grant
- **WHEN** user B reads that chat contact grant
- **THEN** the backend allows the read

#### Scenario: Non-participant reads grant
- **GIVEN** user C is not a participant in the chat
- **WHEN** user C attempts to read the chat contact grant
- **THEN** the backend denies the read

## MODIFIED Requirements

### Requirement: Sensitive Writes Are Role-Limited
The backend SHALL restrict contact grants, post status and notification read-state updates to the authorized role for each resource.

#### Scenario: Unauthorized contact-sharing update
- **GIVEN** user B is not the owner participant of a chat
- **WHEN** user B attempts to update contact-sharing fields or contact grant records
- **THEN** the backend denies the write

#### Scenario: Owner creates contact grant
- **GIVEN** user A is the owner participant of a chat
- **WHEN** user A creates an active contact grant for that chat
- **THEN** the backend allows the write only when the grant `ownerId`, `reporterId`, `postId`, and `chatId` match the chat session

#### Scenario: Owner revokes contact grant
- **GIVEN** user A is the owner participant of a chat with an active contact grant
- **WHEN** user A revokes the grant
- **THEN** the backend allows the grant to become inactive or be deleted without exposing contact values in other records

#### Scenario: Notification read-state update
- **GIVEN** user A has a notification addressed to user A
- **WHEN** user A marks the notification as read
- **THEN** the backend updates only that notification's read state
