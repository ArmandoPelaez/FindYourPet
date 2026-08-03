## ADDED Requirements

### Requirement: Sighting Notification Opens Active Chat
The system SHALL create an owner notification for a new sighting alert and SHALL route that notification to the active chat between owner and reporter.

#### Scenario: Owner receives new sighting as message notification
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the fan-out succeeds
- **THEN** user A receives an unread notification linked to the resulting `chatId`, `sightingId` and `postId`
- **AND** the notification represents a new chat/message event from user B

#### Scenario: Owner opens notification
- **GIVEN** user A has a sighting notification from user B
- **WHEN** user A taps the notification target
- **THEN** the app opens the active chat between user A and user B
- **AND** the sighting alert item is visible in the conversation

#### Scenario: Unrelated user cannot follow notification target
- **GIVEN** user C is not a participant in the sighting-derived chat
- **WHEN** user C attempts to open the notification target by id
- **THEN** the backend denies access to the notification, chat and sighting data

### Requirement: Sighting Notification Content Is Minimized
The system SHALL keep sighting notification records, local notification text and push payloads free of full sensitive sighting details.

#### Scenario: Notification preview is generated
- **GIVEN** a sighting includes notes, location, optional photo and private message context
- **WHEN** the owner notification or push payload is created
- **THEN** it uses generic text and routing ids instead of exposing the full notes, photo, exact coordinates, phone, email, address or full message body

#### Scenario: Chat loads after authentication
- **GIVEN** user A opens the notification while authenticated
- **WHEN** the app navigates to the chat
- **THEN** the app loads the full authorized sighting alert inside the protected chat context
