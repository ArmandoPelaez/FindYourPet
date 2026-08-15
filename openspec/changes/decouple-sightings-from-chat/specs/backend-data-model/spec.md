## ADDED Requirements

### Requirement: Sighting Fan-Out Does Not Require Chat Documents
The backend data model SHALL allow a new accepted sighting fan-out to consist of one sighting document and one per-user notification without a corresponding Chat session or Chat message.

#### Scenario: Remote fan-out has only two document paths
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** the remote batch is committed
- **THEN** it writes `sightings/{sightingId}` and `users/{ownerId}/notifications/{notificationId}`, and does not write `chatSessions/{chatId}` or `chatSessions/{chatId}/messages/{messageId}`

#### Scenario: Local cache mirrors the remote contract
- **GIVEN** the app uses the local fallback repository
- **WHEN** a valid sighting is submitted
- **THEN** Room inserts the sighting and notification without inserting a Chat session or Chat message

### Requirement: New Sighting Notifications Use Sighting References
The backend model SHALL represent a new sighting notification with `recipientId`, `sightingId`, `postId` and `targetId` equal to the sighting identifier, while leaving nullable Chat fields available for legacy records.

#### Scenario: New alert omits Chat dependency
- **GIVEN** a new sighting notification is serialized
- **WHEN** its document is written
- **THEN** the document contains the sighting and post references, uses the sighting as `targetId`, and does not need `chatId`

