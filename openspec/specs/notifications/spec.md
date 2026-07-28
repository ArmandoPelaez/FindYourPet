# notifications Specification

## Purpose
Define backend notification inbox behavior and privacy-safe notification previews.
## Requirements
### Requirement: Per-User Notification Inbox
The system SHALL persist backend notification records under the recipient user's data so notifications survive app restarts and device changes.

#### Scenario: Owner receives sighting notification
- **GIVEN** user B submits a sighting for user A's post
- **WHEN** the sighting write succeeds
- **THEN** user A receives a notification record in user A's notification inbox

### Requirement: Recipient-Only Notification Access
The system SHALL allow users to read and update only their own notification records.

#### Scenario: User reads own notifications
- **GIVEN** user A is signed in
- **WHEN** user A opens the notifications screen
- **THEN** the app displays notifications addressed to user A

#### Scenario: User reads another user's notifications
- **GIVEN** user B is signed in
- **WHEN** user B attempts to read user A's notification inbox
- **THEN** the backend denies access

### Requirement: Notification Content Is Minimised
The system SHALL avoid storing or displaying full sensitive message, location or contact data in notification preview fields, local notification text, and push notification payloads.

#### Scenario: Chat notification is created
- **GIVEN** a participant sends a private chat message
- **WHEN** the recipient notification is created
- **THEN** the notification preview uses generic text and links to the chat instead of exposing full message body or contact fields

#### Scenario: Contact share notification is created
- **GIVEN** an owner shares contact inside a chat
- **WHEN** the recipient notification or push payload is created
- **THEN** it uses generic contact availability text and links to the chat without phone, email, address or precise coordinates

#### Scenario: Contact revoke notification is created
- **GIVEN** an owner revokes contact sharing inside a chat
- **WHEN** the recipient notification or push payload is created
- **THEN** it uses generic contact availability text and does not include previously shared contact values

### Requirement: Notification Read State
The system SHALL allow the recipient to mark their own notifications as read.

#### Scenario: Recipient marks notification as read
- **GIVEN** user A has an unread notification
- **WHEN** user A marks it as read
- **THEN** the backend updates only that notification's read state

