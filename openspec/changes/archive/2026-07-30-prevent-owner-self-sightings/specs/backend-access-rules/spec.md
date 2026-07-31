## ADDED Requirements

### Requirement: Backend Denies Self-Sighting Writes
Firestore rules SHALL deny production sighting and sighting-derived chat writes when `ownerId` and `reporterId` identify the same authenticated user.

#### Scenario: Owner creates sighting for own post
- **GIVEN** user A is signed in
- **AND** user A owns the referenced pet post
- **WHEN** user A attempts to create a sighting with `ownerId` equal to user A and `reporterId` equal to user A
- **THEN** Firestore denies the sighting create

#### Scenario: Owner and reporter are distinct
- **GIVEN** user A owns the referenced pet post
- **AND** user B is signed in
- **WHEN** user B creates a valid sighting with `ownerId` equal to user A and `reporterId` equal to user B
- **THEN** Firestore allows the create according to existing authentication, media, and location rules

#### Scenario: Self-chat from sighting fan-out
- **GIVEN** user A is signed in
- **WHEN** a client attempts to create a chat session from a sighting where `ownerId` equals `reporterId`
- **THEN** Firestore denies the chat session create and any dependent message create in the batch
