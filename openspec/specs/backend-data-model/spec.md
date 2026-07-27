# backend-data-model Specification

## Purpose
Define canonical Firestore collections, identity fields, timestamps and sensitive backend data classification.

## Requirements
### Requirement: Canonical Backend Collections
The system SHALL define Firestore collections for users, pet posts, sightings, chat sessions, chat messages and per-user notifications with stable ids, ownership fields and timestamps.

#### Scenario: Backend documents contain required identity fields
- **GIVEN** an authenticated user creates or participates in a backend record
- **WHEN** the record is persisted
- **THEN** the record includes the relevant `uid`, `ownerId`, `reporterId`, `senderId` or `recipientId` needed to authorize later reads and writes

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
The system SHALL classify phone, email, address, exact coordinates, photos, messages and sighting history as sensitive fields in the backend model.

#### Scenario: Shared feed reads a pet post
- **GIVEN** a signed-in user reads the shared pet post feed
- **WHEN** the post contains sensitive owner contact or exact location fields
- **THEN** the app and rules expose only fields allowed by the post visibility and contact-sharing policy
