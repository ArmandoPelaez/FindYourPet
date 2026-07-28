# local-storage Specification

## Purpose
Define local persistence and Android backup constraints for sensitive app data.
## Requirements
### Requirement: Sensitive Local Data Is Excluded From Android Backup
The Android app SHALL prevent Android cloud backup and device transfer from copying local databases, shared preferences, files, cache, and external app-owned files that may contain sensitive user, pet, sighting, photo, location, or message data.

#### Scenario: Application backup is disabled
- **GIVEN** the Android manifest is parsed
- **WHEN** the application backup configuration is inspected
- **THEN** `android:allowBackup` is set to `false`

#### Scenario: Full backup rules exclude sensitive domains
- **GIVEN** `backup_rules.xml` is parsed
- **WHEN** the backup rules are inspected
- **THEN** databases, shared preferences, files, cache, and app-owned external files are excluded from full backup

#### Scenario: Data extraction rules exclude sensitive domains
- **GIVEN** `data_extraction_rules.xml` is parsed
- **WHEN** cloud backup and device transfer rules are inspected
- **THEN** databases, shared preferences, files, cache, and app-owned external files are excluded from extraction

### Requirement: Local Storage Policy Is Explicit
The project SHALL define what sensitive data is stored locally, why it is stored, whether it is demo data or durable app state, and whether it is encrypted by the application.

#### Scenario: Local data policy lists sensitive fields
- **GIVEN** developers review the privacy/local-storage documentation or source comments for this stage
- **WHEN** local persistence of owner name, phone, email, address, coordinates, photos, messages, or sightings is described
- **THEN** each field is identified as sensitive and its local purpose is stated

#### Scenario: Encryption status is honest
- **GIVEN** application-level encryption is not implemented
- **WHEN** developers review user-facing copy and source strings
- **THEN** the app does not claim that local data is encrypted by FindYourPet

### Requirement: Local Demo Data Is Not Presented As Production Privacy
The app SHALL distinguish local demo/cache behavior from production privacy guarantees when local Room data, seeded users, preset photos, or simulated locations are used.

#### Scenario: Demo data remains identifiable
- **GIVEN** the app still uses local seed data or hardcoded users
- **WHEN** developers inspect repository, ViewModel, or seed-data code
- **THEN** the data is identifiable as demo/local behavior and not production account data

#### Scenario: Unsupported privacy claims are rejected
- **GIVEN** source text can contain user-facing privacy claims
- **WHEN** the debug unit test suite scans main Kotlin and XML sources
- **THEN** unsupported claims of encryption, production privacy, realtime delivery, or authorization fail the test

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

### Requirement: Authenticated Production Flows Do Not Seed Demo Data
The app SHALL NOT call `seedInitialDataIfNeeded` or equivalent demo seeding from authenticated production startup, feed, notification, chat, post or sighting flows.

#### Scenario: Signed-in user opens empty backend
- **GIVEN** a signed-in user has no backend pet posts, sightings, chats or notifications
- **WHEN** the app opens the authenticated feed and related screens
- **THEN** the app shows empty states without inserting demo pets, preset photos, fake chats or fake notifications

#### Scenario: Demo mode is isolated
- **GIVEN** a non-production demo mode exists
- **WHEN** demo seed data is inserted
- **THEN** the data is clearly scoped away from authenticated production records and cannot grant production ownership or notification access

### Requirement: Local Media And Location Cache Is Non-Authoritative
Room or local file references SHALL NOT become the authority for production media ownership, precise location visibility or notification routing.

#### Scenario: Local media conflicts with backend media metadata
- **GIVEN** cached local media metadata differs from the backend Cloudinary reference
- **WHEN** the app renders a production post
- **THEN** the app treats backend media metadata as authoritative

#### Scenario: Local cached coordinates conflict with backend
- **GIVEN** cached coordinates differ from the authorized backend sighting
- **WHEN** an owner opens the sighting detail
- **THEN** the app follows the backend-authorized location fields

