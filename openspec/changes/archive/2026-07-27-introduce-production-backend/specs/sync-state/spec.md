## ADDED Requirements

### Requirement: Loading And Empty States
The app SHALL expose loading and empty states for backend-backed feeds, detail screens, sightings, chats and notifications.

#### Scenario: Feed starts loading
- **GIVEN** a signed-in user opens the feed
- **WHEN** the backend listener has not returned data or an error
- **THEN** the UI displays a loading state instead of seeded production-looking data

### Requirement: Backend Error States
The app SHALL expose backend read and write errors to the relevant UI flow without silently falling back to authoritative local data.

#### Scenario: Post write denied
- **GIVEN** Firestore rules deny a post update
- **WHEN** the repository receives the write failure
- **THEN** the UI displays an error state and does not present the local cache as a successful production write

### Requirement: Cache And Remote Source State
The app SHALL distinguish cached data from confirmed remote data when the backend SDK reports source metadata.

#### Scenario: Cached chat data is displayed
- **GIVEN** a chat listener returns data from local cache
- **WHEN** the chat screen renders messages
- **THEN** the screen state identifies that the data is cached or still synchronizing

### Requirement: Pending Write State
The app SHALL identify records that are locally written but not yet confirmed by the backend.

#### Scenario: Message is pending
- **GIVEN** a user sends a chat message while connectivity is limited
- **WHEN** Firestore queues the local write
- **THEN** the chat UI marks the message or conversation as pending until the backend confirms it
