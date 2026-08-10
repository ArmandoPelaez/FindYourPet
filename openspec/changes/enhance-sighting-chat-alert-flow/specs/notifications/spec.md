## MODIFIED Requirements

### Requirement: Per-User Notification Inbox
The system SHALL persist backend notification records under the recipient user's data so notifications survive app restarts and device changes, and a sighting notification SHALL link the owner to the relevant chat and sighting.

#### Scenario: Owner receives sighting notification
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the sighting write succeeds
- **THEN** user A receives a notification record in user A's notification inbox containing the authorized routing references for `chatId`, `sightingId` and `postId`

#### Scenario: User reads own notifications
- **GIVEN** user A is signed in
- **WHEN** user A opens the notifications screen
- **THEN** the app displays notifications addressed to user A, including the unread sighting notification when one exists

#### Scenario: User reads another user's notifications
- **GIVEN** user B is signed in
- **WHEN** user B attempts to read user A's notification inbox
- **THEN** the backend denies access

### Requirement: Notification Content Is Minimised
The system SHALL avoid storing or displaying full sensitive message, location, photo or contact data in notification preview fields, local notification text and push notification payloads, while still linking a sighting notification to its private chat.

#### Scenario: Chat notification is created
- **GIVEN** a participant sends a private chat message
- **WHEN** the recipient notification is created
- **THEN** the notification preview uses generic text and links to the chat instead of exposing full message body or contact fields

#### Scenario: Sighting notification is created
- **GIVEN** user B submits a sighting containing a photo, location name, coordinates and notes
- **WHEN** the notification for user A is created
- **THEN** it uses minimized text such as “Nuevo avistamiento”, stores only routing references and does not include the photo, exact coordinates, full notes, phone, email or address

#### Scenario: Contact share notification is created
- **GIVEN** an owner shares contact inside a chat
- **WHEN** the recipient notification or push payload is created
- **THEN** it uses generic contact availability text and links to the chat without phone, email, address or precise coordinates

#### Scenario: Contact revoke notification is created
- **GIVEN** an owner revokes contact sharing inside a chat
- **WHEN** the recipient notification or push payload is created
- **THEN** it uses generic contact availability text and does not include previously shared contact values

### Requirement: Notification Read State
The system SHALL allow the recipient to mark their own notifications as read, including when the recipient opens a sighting chat from its notification.

#### Scenario: Recipient marks notification as read
- **GIVEN** user A has an unread sighting notification
- **WHEN** user A opens its linked chat or marks it as read
- **THEN** the backend updates only that notification's read state

#### Scenario: Notification routes to chat
- **GIVEN** user A taps a valid sighting notification while authenticated
- **WHEN** the app resolves its routing references
- **THEN** the app opens the A/B private chat and preserves the notification read-state behavior

#### Scenario: Invalid notification target is handled
- **GIVEN** a sighting notification references a missing or unauthorized chat
- **WHEN** user A taps the notification
- **THEN** the app shows a recoverable unavailable-state and does not expose data from another user's chat

