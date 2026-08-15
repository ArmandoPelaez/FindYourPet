## ADDED Requirements

### Requirement: New Chat Documents Are Not Part Of The Active Model

The active backend model SHALL not define or write new Chat sessions, messages or Chat contact grants, while preserving historical remote documents without deletion.

#### Scenario: Valid sighting is persisted

- **GIVEN** a reporter submits a valid sighting
- **WHEN** the backend persists the sighting and owner notification
- **THEN** no new `chatSessions` or nested `messages` document is written
- **AND** the notification target remains the `sightingId`

#### Scenario: Historical documents remain available for retention

- **GIVEN** Firestore contains legacy Chat documents
- **WHEN** the cleanup is deployed
- **THEN** no migration or deletion job changes those documents

### Requirement: Legacy Chat Identifiers Are Not Active Routing Data

Nullable legacy `chatId` fields may remain only for historical decoding and SHALL NOT be used to route, authorize or create active app flows.

#### Scenario: Legacy notification is decoded

- **GIVEN** a historical notification contains `chatId`
- **WHEN** it is mapped locally
- **THEN** the data can be retained without opening Chat or using it as a new target
