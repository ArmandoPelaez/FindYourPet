## ADDED Requirements

### Requirement: Firestore Source Of Truth For Authenticated Data
The app SHALL treat Firestore as the source of truth for authenticated production profiles, pet posts, owner actions, sightings, and chat records introduced by this change.

#### Scenario: Authenticated user creates a post
- **WHEN** an authenticated user creates a pet post
- **THEN** the production record is written to Firestore with `ownerId` equal to the user's Firebase `uid`

#### Scenario: Local Room record conflicts with Firestore
- **WHEN** a Room record conflicts with the Firestore record for authenticated production data
- **THEN** owner-sensitive behavior follows Firestore and Firebase Auth, not Room

### Requirement: Room Is Cache Or Demo Storage Only
Room SHALL NOT grant production permissions or become the authority for authenticated owner identity after this change.

#### Scenario: Unauthenticated app uses demo data
- **WHEN** demo or seed data is shown before authentication
- **THEN** the app labels or scopes it as local/demo behavior and does not expose owner-only production actions

#### Scenario: Cached data is available offline
- **WHEN** authenticated Firestore data is cached locally
- **THEN** the cache is treated as a display optimization and does not override Firebase Auth or Firestore rules

### Requirement: Demo Data Migration Boundary
The app SHALL require explicit authenticated ownership assignment before a seeded Room record becomes a production Firestore record.

#### Scenario: Seed post is imported by signed-in user
- **WHEN** a seeded Room post is converted to a production Firestore post
- **THEN** the resulting Firestore document uses the signed-in user's Firebase `uid` as `ownerId`

#### Scenario: Seed post retains demo owner id
- **WHEN** a seeded Room post still contains a demo owner id such as `owner_1`
- **THEN** the app does not treat it as a production-owned record
