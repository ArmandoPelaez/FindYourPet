## MODIFIED Requirements

### Requirement: Sensitive Contact Data Is Not Sent In Notifications
The app SHALL NOT include phone, email, address, precise coordinates, photo attachments or full private-message content in local notification text, backend notification records, push notification payloads or chat-list previews.

#### Scenario: Sighting notification avoids precise sensitive data
- **GIVEN** a sighting includes a reporter name, location name, latitude, longitude, photo and notes
- **WHEN** notification text, its backend record, push payload or chat-list preview is generated
- **THEN** the generated values avoid phone, email, address, exact coordinates, photo content and full private note content while retaining only generic routing information

#### Scenario: Contact-sharing notification avoids direct contact values
- **GIVEN** contact sharing changes state
- **WHEN** a notification, push payload or chat preview is generated
- **THEN** it does not expose phone or email values outside the protected in-app contact surface

#### Scenario: Revocation notification avoids previous contact values
- **GIVEN** contact sharing is revoked for a chat
- **WHEN** the recipient notification is created
- **THEN** the notification uses generic text and does not include the previously shared phone or email

## ADDED Requirements

### Requirement: Sighting Evidence Is Chat-Scoped
The app SHALL show sighting photo, authorized location display and general report details only inside the authenticated owner/reporter chat or another explicitly authorized sighting surface.

#### Scenario: Participant views evidence in chat
- **GIVEN** user A or B is an authenticated participant of the sighting chat
- **WHEN** the participant opens the `sighting_alert`
- **THEN** the app may display the authorized photo attachment, location display and general details within that chat

#### Scenario: Unrelated user views public surface
- **GIVEN** user C is not a participant of the sighting chat
- **WHEN** user C views a public post, notification or chat preview
- **THEN** the app does not expose the sighting photo, full notes, precise location or contact data

#### Scenario: Revoked or unavailable access
- **GIVEN** a participant's authorization to the linked sighting is missing, revoked or the media fails to load
- **WHEN** the alert is rendered
- **THEN** the app hides unauthorized data and shows a safe unavailable/error state without exposing cached contact or precise location values

