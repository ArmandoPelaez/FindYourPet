## ADDED Requirements

### Requirement: Retired Chat Has No Active Sync State

The app SHALL not expose loading, cache, pending-write or error states for Chat screens because no active Chat screen, listener or write path remains.

#### Scenario: User opens current navigation

- **GIVEN** a signed-in user opens the authenticated app
- **WHEN** the current destinations load
- **THEN** sync state is provided only for active product flows such as posts, sightings, Activity and notifications
- **AND** no Chat sync state is collected or rendered
