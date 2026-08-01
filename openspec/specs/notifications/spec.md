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
The system SHALL avoid storing or displaying full sensitive message, location or contact data in notification preview fields, local notification text, and push notification payloads. Notification schemas SHALL use only supported non-contact-sharing types for alerts and chat activity.

#### Scenario: Chat notification is created
- **GIVEN** a participant sends a private chat message
- **WHEN** the recipient notification is created
- **THEN** the notification preview uses generic text and links to the chat instead of exposing full message body or contact fields

#### Scenario: Sighting notification is created
- **GIVEN** a reporter submits a sighting for a pet post
- **WHEN** the owner notification or push payload is created
- **THEN** it uses generic sighting text and excludes phone, email, address, exact coordinates, private notes, photo URLs, and direct contact values

#### Scenario: Contact share notification is requested
- **GIVEN** an obsolete flow requests a contact availability notification
- **WHEN** the notification payload is generated or validated
- **THEN** the system does not create a `CONTACT_SHARED` notification

### Requirement: Notification Read State
The system SHALL allow the recipient to mark their own notifications as read.

#### Scenario: Recipient marks notification as read
- **GIVEN** user A has an unread notification
- **WHEN** user A marks it as read
- **THEN** the backend updates only that notification's read state

### Requirement: Contact Sharing Notifications Are Retired
The system SHALL NOT create, persist, display, or allow notification records whose type or copy represents app-managed personal contact sharing.

#### Scenario: Contact shared event is requested
- **GIVEN** legacy code attempts to create a contact-sharing notification
- **WHEN** the notification write is validated
- **THEN** the system rejects or omits the notification and does not show it to the recipient

#### Scenario: Legacy contact notification exists
- **GIVEN** a recipient has an older `CONTACT_SHARED` notification
- **WHEN** the updated notifications screen renders
- **THEN** the app does not present it as current contact availability and does not show phone, email, address, or direct contact values

