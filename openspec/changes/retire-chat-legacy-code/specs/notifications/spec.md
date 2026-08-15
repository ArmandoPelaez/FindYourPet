## MODIFIED Requirements

### Requirement: Notification Content Is Minimised

The system SHALL keep notification previews privacy-safe and SHALL route new sighting alerts by `sightingId`; it SHALL not create or route active Chat notifications.

#### Scenario: Sighting notification is created

- **GIVEN** a sighting is accepted for another user's post
- **WHEN** the owner notification is persisted
- **THEN** it contains the sighting target fields required by the existing flow
- **AND** it does not require `chatId` or a Chat session

#### Scenario: Historical Chat notification is encountered

- **GIVEN** a legacy notification contains Chat type or `chatId`
- **WHEN** the current app processes it
- **THEN** it does not navigate to Chat or create a new Chat record
- **AND** sensitive message/contact data is not exposed
