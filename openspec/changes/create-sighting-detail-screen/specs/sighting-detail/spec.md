## ADDED Requirements

### Requirement: Read-Only Sighting Detail
The app SHALL provide an authenticated read-only sighting detail surface identified by `sightingId`.

#### Scenario: Authorized owner opens a sighting detail
- **GIVEN** user A is the owner of the post referenced by a sighting
- **WHEN** the detail surface receives that sighting's `sightingId`
- **THEN** the app loads and displays the authorized `SightingAlertEntity` without opening or creating a Chat session

#### Scenario: Authorized reporter opens a sighting detail
- **GIVEN** user B is the reporter recorded in a sighting
- **WHEN** the detail surface receives that sighting's `sightingId`
- **THEN** user B can read the same authorized sighting data in read-only mode

#### Scenario: Unrelated user opens a sighting detail
- **GIVEN** user C is neither the owner nor reporter of a sighting
- **WHEN** user C attempts to load the sighting detail by `sightingId`
- **THEN** the backend error is surfaced and the app does not display the protected sighting data

### Requirement: Sighting Data Is Sourced From The Sighting
The detail surface SHALL use `SightingAlertEntity` as the source of truth for sighting content.

#### Scenario: Detail displays report content
- **GIVEN** an authorized sighting contains `locationName`, timestamp, notes and optional photo data
- **WHEN** the detail loads successfully
- **THEN** the app displays the location, date/time, notes when non-empty and photo when available from the sighting

#### Scenario: Detail does not use Chat content
- **GIVEN** a sighting has no Chat session or message
- **WHEN** the detail loads
- **THEN** the app still renders the sighting from `SightingAlertEntity` and never reads `ChatMessageEntity.generalDetails`

### Requirement: Pet Context Is Optional And Derived
The detail surface SHALL use the sighting's `postId` to resolve the associated pet context without duplicating sighting fields into Chat data.

#### Scenario: Associated pet is available
- **GIVEN** the referenced `petPosts/{postId}` document is readable
- **WHEN** the sighting detail loads
- **THEN** the app shows the associated pet name and available post photo as contextual information

#### Scenario: Associated pet context is unavailable
- **GIVEN** the sighting is readable but the associated post context cannot be loaded
- **WHEN** the sighting detail loads
- **THEN** the app preserves the sighting detail and shows a bounded missing-context state instead of treating the sighting as a Chat error

### Requirement: Read-Only Interaction And Location Action
The detail surface SHALL not provide bidirectional communication and SHALL expose location viewing only when authorized location data is available.

#### Scenario: Detail has location data
- **GIVEN** an authorized sighting contains a valid location label or coordinates
- **WHEN** the detail renders
- **THEN** it shows a `Ver ubicación` action that opens the existing map mechanism in read-only mode

#### Scenario: Detail has no usable location data
- **GIVEN** an authorized sighting has no usable location label or coordinates
- **WHEN** the detail renders
- **THEN** it omits or disables `Ver ubicación` without requesting a new runtime permission

#### Scenario: Detail is read-only
- **GIVEN** an authorized user is viewing a sighting detail
- **WHEN** the screen renders
- **THEN** it contains no message input, send action, reply action, chat bubbles, message history or Chat creation action

### Requirement: Detail Sync States
The detail surface SHALL expose loading, success and error states without presenting stale local data as a confirmed remote read.

#### Scenario: Detail is loading
- **GIVEN** the sighting read has not completed
- **WHEN** the detail screen is first shown
- **THEN** the UI displays a loading state

#### Scenario: Detail read fails
- **GIVEN** Firestore denies or fails the sighting read, or the document does not exist
- **WHEN** the repository reports the failure
- **THEN** the UI displays an error state and does not display unrelated cached sighting data as the requested record

#### Scenario: Optional notes are absent
- **GIVEN** the sighting notes are empty
- **WHEN** the detail renders successfully
- **THEN** the UI omits the empty comment block

### Requirement: Design System Compliance
The detail surface SHALL preserve the existing FindYourPet visual identity and support Light Theme and Dark Theme.

#### Scenario: Detail renders in supported themes
- **GIVEN** the user opens the detail in Light Theme or Dark Theme
- **WHEN** the screen renders
- **THEN** colors, typography, spacing, shapes and component states use the existing Design System tokens and remain legible

#### Scenario: Detail avoids unsupported UI APIs
- **GIVEN** the detail screen is compiled and reviewed
- **WHEN** its Compose implementation is inspected
- **THEN** it uses stable Material 3 APIs and does not introduce hardcoded visual values or alpha/beta/experimental UI APIs
