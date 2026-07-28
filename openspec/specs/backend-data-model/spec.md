# backend-data-model Specification

## Purpose
Define canonical Firestore collections, identity fields, timestamps and sensitive backend data classification.
## Requirements
### Requirement: Canonical Backend Collections
The system SHALL define Firestore collections for users, pet posts, sightings, chat sessions, chat messages, chat-scoped contact grants and per-user notifications with stable ids, ownership fields and timestamps.

#### Scenario: Backend documents contain required identity fields
- **GIVEN** an authenticated user creates or participates in a backend record
- **WHEN** the record is persisted
- **THEN** the record includes the relevant `uid`, `ownerId`, `reporterId`, `senderId`, `recipientId` or `chatId` needed to authorize later reads and writes

#### Scenario: Shared pet post excludes direct contact
- **GIVEN** an owner creates or updates a pet post
- **WHEN** the shared pet post document is persisted
- **THEN** it does not include owner phone, email, address or post-level public contact reveal fields

### Requirement: Auth Uid Is The Production Identity
The system SHALL use Firebase Authentication `uid` as the only production identity for ownership, membership and per-user data.

#### Scenario: Demo ids do not grant production permissions
- **GIVEN** local demo data contains ids such as `owner_1` or `finder_1`
- **WHEN** the app evaluates production ownership or chat membership
- **THEN** those ids do not grant access unless they match the signed-in Firebase `uid`

### Requirement: Backend Timestamps Are Consistent
The system SHALL record creation and update timestamps for backend documents using server timestamps where supported by the write path.

#### Scenario: Remote document is created
- **GIVEN** a production document is created in Firestore
- **WHEN** the write succeeds
- **THEN** the persisted document exposes a backend-created timestamp for sorting and synchronization

### Requirement: Sensitive Fields Are Classified
The system SHALL classify phone, email, address, exact coordinates, photos, messages, sighting history and contact grant data as sensitive fields in the backend model.

#### Scenario: Shared feed reads a pet post
- **GIVEN** a signed-in user reads the shared pet post feed
- **WHEN** the post contains sensitive owner contact or exact location data in legacy storage
- **THEN** the app and rules expose only fields allowed by the post visibility and chat-scoped contact-sharing policy

#### Scenario: Contact grant stores approved values
- **GIVEN** the owner shares contact in a chat
- **WHEN** the contact grant is active
- **THEN** only authenticated participants of that chat can read the approved contact values

#### Scenario: Contact grant is inactive
- **GIVEN** a contact grant has been revoked
- **WHEN** the reporter reads chat-related backend data
- **THEN** phone, email, address and precise location values from the revoked grant are not exposed

### Requirement: Contact Grants Are Chat-Scoped
The system SHALL represent owner contact disclosure as a chat-scoped grant tied to exactly one `chatId`, `postId`, `ownerId`, and `reporterId`.

#### Scenario: Active grant is created
- **GIVEN** an owner shares contact in a chat
- **WHEN** the grant is persisted
- **THEN** it includes the matching `chatId`, `postId`, `ownerId`, `reporterId`, `sharedBy`, `sharedAt`, active state, and only the approved contact fields for that chat

#### Scenario: Grant is revoked
- **GIVEN** a chat contact grant is active
- **WHEN** the owner revokes contact sharing
- **THEN** the grant is deleted or marked inactive and no longer exposes phone or email values to the reporter

