## ADDED Requirements

### Requirement: Sighting Notifications Target The Sighting
The system SHALL create a new sighting notification with the sighting identifier as its generic target, while preserving the recipient, post and sighting references.

#### Scenario: Owner receives a sighting-targeted notification
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the fan-out succeeds
- **THEN** user A receives an `ALERT` notification with `sightingId`, `postId` and `targetId` equal to the `sightingId`

#### Scenario: New sighting notification does not require Chat
- **GIVEN** a new valid sighting is being written
- **WHEN** the notification document is created
- **THEN** it does not require or produce a `chatId`, Chat session or Chat message

### Requirement: Legacy Chat Notification Compatibility Is Preserved
The system SHALL continue to read and write Chat notifications that contain a `chatId` according to their existing contract, without requiring new sighting notifications to use that field.

#### Scenario: Historical Chat notification remains mappable
- **GIVEN** a stored Chat notification contains `chatId` and `targetId` equal to that Chat identifier
- **WHEN** the app maps the notification
- **THEN** it preserves the Chat identifier and existing Chat notification behavior

