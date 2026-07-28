# local-storage-cache Specification

## Purpose
Define Room's role as a production cache and keep demo seed data isolated from authenticated backend flows.

## Requirements
### Requirement: Room Is Cache Only For Production Data
The system SHALL treat Room records as local cache for backend data and SHALL NOT use Room records as the source of production authorization.

#### Scenario: Cached post has stale owner
- **GIVEN** Room contains a cached pet post with an `ownerId`
- **WHEN** a protected production operation is requested
- **THEN** the operation is authorized by Firebase Auth and backend rules, not by the cached Room row

### Requirement: Remote Snapshots Update Cache
The system SHALL update Room cache from successful backend snapshots when local caching is enabled.

#### Scenario: Remote feed changes
- **GIVEN** another user creates a visible pet post in the backend
- **WHEN** the local app receives the backend feed snapshot
- **THEN** Room cache is updated to include the remote post projection

### Requirement: Demo Seed Is Isolated
The system SHALL keep demo seed data separate from authenticated production flows.

#### Scenario: Signed-in user starts with no remote posts
- **GIVEN** a signed-in user has no backend data available yet
- **WHEN** the app opens a production-backed screen
- **THEN** the app shows loading, empty or error state instead of automatically injecting demo posts as production data

### Requirement: Local Cache Can Be Cleared On Sign-Out
The system SHALL clear or partition sensitive cached data when the authenticated user changes.

#### Scenario: User signs out
- **GIVEN** user A has cached chats or notifications on the device
- **WHEN** user A signs out or user B signs in
- **THEN** user B cannot read user A's cached private chats, notifications or sightings
