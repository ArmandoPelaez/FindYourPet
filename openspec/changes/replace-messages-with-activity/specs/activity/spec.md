## ADDED Requirements

### Requirement: Activity Shows Received Sightings For The Signed-In Owner
The app SHALL provide an authenticated Activity screen that reads sightings associated with the signed-in user's owned posts and SHALL not use Chat data as its source.

#### Scenario: Owner opens Activity with received sightings
- **GIVEN** user A is signed in and has received sightings for posts owned by user A
- **WHEN** user A opens Activity
- **THEN** the app displays those sightings from `SightingAlertEntity` or its authorized remote equivalent
- **AND** the app does not load Chat sessions or Chat messages to construct the list

#### Scenario: Unrelated sightings are excluded
- **GIVEN** user A is signed in and a sighting belongs to a post owned by user B
- **WHEN** user A opens Activity
- **THEN** that sighting is not displayed to user A
- **AND** the owner-scoped data query is constrained to user A's owner identity

### Requirement: Activity Items Preserve Sighting Identity And Information
Each Activity item SHALL retain its `sightingId` and SHALL show available sighting information without messaging concepts.

#### Scenario: Activity item has available metadata
- **GIVEN** a received sighting has a linked post, location, timestamp, and optional photo
- **WHEN** the Activity list renders the item
- **THEN** the item shows the available pet name, an avistamiento indicator, reported location, date/time, and permitted image
- **AND** the item retains the corresponding `sightingId`

#### Scenario: Activity item has incomplete optional metadata
- **GIVEN** a received sighting has no optional photo or linked pet metadata in the current cache
- **WHEN** the Activity list renders the item
- **THEN** the item remains readable using the available sighting fields
- **AND** the app does not crash or invent Chat/session data

#### Scenario: Activity excludes messaging content
- **GIVEN** a received sighting is displayed in Activity
- **WHEN** the item is rendered
- **THEN** it does not show a conversation name, `lastMessage`, Chat preview, online state, typing state, reply action, send input, or `chatId`

### Requirement: Activity Orders Sightings By Recency
The Activity list SHALL order received sightings from most recent to oldest using the existing sighting timestamp.

#### Scenario: Recent sighting appears first
- **GIVEN** user A has two received sightings with timestamps where sighting X is newer than sighting Y
- **WHEN** Activity renders the list
- **THEN** sighting X appears before sighting Y

### Requirement: Activity Provides Controlled Screen States
The Activity screen SHALL provide loading, success, empty, and error states without crashing and SHALL use existing Design System components and tokens.

#### Scenario: Activity is loading
- **GIVEN** the owner-scoped sightings query has not completed
- **WHEN** user A opens Activity
- **THEN** the screen shows the existing loading/synchronization pattern

#### Scenario: Activity is empty
- **GIVEN** the owner-scoped sightings query succeeds with no received sightings
- **WHEN** user A opens Activity
- **THEN** the screen shows an EmptyState explaining that new activity will appear there

#### Scenario: Activity query fails
- **GIVEN** the owner-scoped sightings query fails
- **WHEN** Activity is displayed
- **THEN** the screen shows the existing error state
- **AND** the app does not crash
- **AND** useful diagnostic information is available through existing synchronization/logging patterns

#### Scenario: Activity supports both themes
- **GIVEN** Activity is displayed in Light Theme or Dark Theme
- **WHEN** the list, empty state, loading state, or error state renders
- **THEN** the screen uses existing theme tokens and remains readable and accessible

## MODIFIED Requirements

None.
