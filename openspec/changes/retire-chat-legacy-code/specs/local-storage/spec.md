## ADDED Requirements

### Requirement: Chat Room Tables Are Migrated Out

The app SHALL remove local Chat tables and DAO/entity access through a directed Room migration from version 9 to version 10.

#### Scenario: Migration preserves non-Chat data

- **GIVEN** Room version 9 contains posts, sightings, notifications, moderation records and Chat cache tables
- **WHEN** version 10 migration runs
- **THEN** only Chat cache tables are removed
- **AND** all non-Chat tables and records remain readable

#### Scenario: Destructive fallback is not used

- **GIVEN** a user upgrades from the existing Room database
- **WHEN** the database builder configures migrations
- **THEN** it uses the explicit 9-to-10 migration
- **AND** it does not call `fallbackToDestructiveMigration`
