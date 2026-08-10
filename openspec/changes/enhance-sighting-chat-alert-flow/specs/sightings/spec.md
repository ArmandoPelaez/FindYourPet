## MODIFIED Requirements

### Requirement: Sighting Delivery To Owner
The system SHALL make each sighting visible to the post owner and the reporter, SHALL deny it to unrelated users, and SHALL expose the accepted sighting to the owner as an alert message in the linked private chat.

#### Scenario: Owner receives sighting in chat
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** user A opens the notification target or the linked conversation
- **THEN** user A can read the sighting details as the first alert message in that conversation, including the authorized photo, location display and general information when present

#### Scenario: Reporter sees submitted sighting in chat
- **GIVEN** user B submits a valid sighting for user A's post
- **WHEN** user B opens the linked conversation
- **THEN** user B can read the same authorized alert data and continue the participant-only conversation

#### Scenario: Unrelated user cannot read sighting
- **GIVEN** user C is neither the owner nor reporter of a sighting
- **WHEN** user C attempts to read the sighting or its linked chat message
- **THEN** the backend denies access

### Requirement: Sighting Creates Conversation Path
The system SHALL create or reuse a chat session between the post owner and reporter when a sighting is accepted by the backend, SHALL create an enriched alert message for each accepted sighting, and SHALL notify the owner in the same atomic operation.

#### Scenario: First sighting starts an enriched chat
- **GIVEN** user B submits the first sighting for user A's post
- **WHEN** the backend write succeeds
- **THEN** the app creates or opens a chat session containing user A and user B, and the timeline contains a `sighting_alert` message linked to the sighting

#### Scenario: Sighting fan-out is atomic
- **GIVEN** user B submits a sighting for user A's post
- **WHEN** the backend accepts the sighting write
- **THEN** the sighting, chat session, enriched alert message and owner notification are committed together or none of them are committed

#### Scenario: Additional sighting reuses the conversation
- **GIVEN** an A/B conversation already exists for the same lost-pet post
- **WHEN** user B submits another valid sighting
- **THEN** the system reuses that conversation and appends one new `sighting_alert` message without replacing earlier alerts

#### Scenario: Retry does not duplicate the alert
- **GIVEN** a valid sighting submission is retried after an uncertain client response
- **WHEN** the backend receives the same idempotency key
- **THEN** it returns or preserves the original sighting, alert message and owner notification instead of creating duplicates

## ADDED Requirements

### Requirement: Sighting Confirmation Returns Home
The app SHALL navigate to the Home destination after a sighting confirmation has been committed successfully, and SHALL keep the user on the confirmation flow when the commit fails.

#### Scenario: Successful confirmation returns Home
- **GIVEN** user B has completed a valid sighting
- **WHEN** the user confirms and the backend fan-out succeeds
- **THEN** the app clears the confirmation flow and navigates automatically to Home

#### Scenario: Failed confirmation remains actionable
- **GIVEN** user B confirms a sighting
- **WHEN** validation, upload or backend persistence fails
- **THEN** the app does not navigate to Home, shows an actionable error and preserves enough form state to retry without starting a second report

#### Scenario: Double confirmation is idempotent
- **GIVEN** the confirmation action is being submitted
- **WHEN** user B taps confirm more than once
- **THEN** the app accepts one submission, disables duplicate submission while pending and navigates to Home only once after success

