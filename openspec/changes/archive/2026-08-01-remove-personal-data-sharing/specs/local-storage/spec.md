## ADDED Requirements

### Requirement: Local Contact Sharing Cache Is Retired
The local database SHALL NOT keep an authoritative or displayable cache of contact grants, contact-sharing flags, owner phone, owner email, or owner address values whose purpose is app-managed personal contact disclosure.

#### Scenario: Existing local contact grants are present
- **GIVEN** the installed app has a `contact_grants` table or cached contact grant rows from an older version
- **WHEN** the updated app migrates or opens the database
- **THEN** those rows are removed or ignored and cannot render phone, email, address, or contact availability

#### Scenario: Chat session cache is written
- **WHEN** a chat session is cached locally
- **THEN** the cached row does not include `isContactSharedByOwner` or equivalent contact-sharing state

## MODIFIED Requirements

### Requirement: Local Storage Policy Is Explicit
The project SHALL define what sensitive data is stored locally, why it is stored, whether it is demo data or durable app state, and whether it is encrypted by the application. The policy SHALL state that local contact-sharing caches and owner contact values used only for app-managed disclosure are retired.

#### Scenario: Local data policy lists sensitive fields
- **GIVEN** developers review the privacy/local-storage documentation or source comments for this stage
- **WHEN** local persistence of account identity, profile email, coordinates, photos, messages, sightings, or legacy contact values is described
- **THEN** each field is identified as sensitive, its local purpose is stated, and legacy contact-sharing storage is marked removed or ignored

#### Scenario: Encryption status is honest
- **GIVEN** application-level encryption is not implemented
- **WHEN** developers review user-facing copy and source strings
- **THEN** the app does not claim that local data is encrypted by FindYourPet

#### Scenario: Contact values are not cached for reveal
- **GIVEN** a production owner profile or auth session contains an email
- **WHEN** the app caches authenticated state or chat data locally
- **THEN** the cache does not copy that value into owner contact fields for later reveal
