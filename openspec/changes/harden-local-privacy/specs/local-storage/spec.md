## ADDED Requirements

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
