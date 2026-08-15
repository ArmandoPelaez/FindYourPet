## ADDED Requirements

### Requirement: Active Client Exposes No Chat

The authenticated FindYourPet client SHALL expose no Chat list, Chat detail, message composer, send action, reply action, Chat deep link or navigation destination.

#### Scenario: User opens the authenticated app

- **GIVEN** a signed-in user navigates through the primary destinations
- **WHEN** the app renders the navigation shell
- **THEN** the destinations are Inicio, Perfil, Reportar, Actividad and Alertas
- **AND** no destination opens a conversation or message screen

#### Scenario: User follows a historical Chat notification

- **GIVEN** a legacy notification contains `type = CHAT`, `chatId` or a historical Chat target
- **WHEN** the user selects or processes that notification
- **THEN** the app does not navigate to Chat
- **AND** it handles the unavailable route without crashing

### Requirement: Active Runtime Does Not Create Or Consume Chat

The active client SHALL not create, update, delete, read or subscribe to `chatSessions` or `messages`, and SHALL not use `chatId` to process sightings, Alertas, Actividad or Detalle.

#### Scenario: Reporter submits a new sighting

- **GIVEN** a valid sighting is submitted for another user's post
- **WHEN** the sighting and owner notification are persisted
- **THEN** no Chat session, Chat message or Chat listener is created
- **AND** the result and navigation continue to use `sightingId`

#### Scenario: Chat runtime symbols are audited

- **GIVEN** the production source is searched for Chat runtime operations
- **WHEN** references to Chat screens, send/read methods, listeners, Chat entities and `chatId` are reviewed
- **THEN** no executable reference remains unless it is explicitly documented as historical compatibility or a backend retention rule

### Requirement: Historical Remote Chat Data Is Retained Without New Client Writes

The system SHALL retain existing remote Chat documents without deleting or migrating them, while preventing new client writes to Chat sessions, messages and Chat-scoped contact records.

#### Scenario: Existing remote Chat data is retained

- **GIVEN** Firestore contains historical `chatSessions` or nested `messages`
- **WHEN** SCRUM-26 is deployed
- **THEN** the documents remain untouched
- **AND** no destructive cleanup operation is executed

#### Scenario: Client attempts a new Chat write

- **GIVEN** an authenticated or legacy client attempts to create or modify a Chat session or message
- **WHEN** Firestore evaluates the request
- **THEN** the write is denied by rules

### Requirement: Chat Local Tables Are Removed Safely

Room SHALL migrate from version 9 to version 10 by removing only local Chat tables and SHALL preserve posts, sightings, notifications, content reports and user blocks.

#### Scenario: Existing database migrates

- **GIVEN** a local Room database is at version 9
- **WHEN** the app opens the database after the change
- **THEN** only `chat_sessions` and `chat_messages` are removed
- **AND** non-Chat tables and their records remain available
- **AND** the app does not use destructive fallback migration

### Requirement: Active Sighting Flow Remains Chat-Independent

The existing sighting form, Alertas, Actividad, Detalle, Reportar contenido and Bloquear usuario flows SHALL remain functional and SHALL continue to use their current identifiers and contracts without Chat dependencies.

#### Scenario: Owner opens an alert or Activity item

- **GIVEN** an owner selects a sighting from Alertas or Actividad
- **WHEN** the app resolves the destination
- **THEN** it opens Sighting Detail with `sightingId`
- **AND** it does not construct or resolve a `chatId`

#### Scenario: Moderation remains available

- **GIVEN** an owner opens Sighting Detail after Chat cleanup
- **WHEN** the owner uses Reportar contenido or Bloquear usuario
- **THEN** both moderation actions retain their existing behavior
- **AND** no Chat model or route is required
