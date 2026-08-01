# backend-data-model Specification

## Purpose
Define canonical Firestore collections, identity fields, timestamps and sensitive backend data classification.
## Requirements
### Requirement: Canonical Backend Collections
The system SHALL define Firestore collections for users, pet posts, sightings, chat sessions, chat messages, and per-user notifications with stable ids, ownership fields and timestamps. The system SHALL NOT define chat-scoped contact grant collections as part of the production data contract.

#### Scenario: Backend documents contain required identity fields
- **GIVEN** an authenticated user creates or participates in a backend record
- **WHEN** the record is persisted
- **THEN** the record includes the relevant `uid`, `ownerId`, `reporterId`, `senderId`, `recipientId` or `chatId` needed to authorize later reads and writes

#### Scenario: Shared pet post excludes direct contact
- **GIVEN** an owner creates or updates a pet post
- **WHEN** the shared pet post document is persisted
- **THEN** it does not include owner phone, email, address or post-level public contact reveal fields

#### Scenario: Chat session excludes contact-sharing state
- **GIVEN** an owner and reporter have a private chat
- **WHEN** the chat session document is persisted
- **THEN** it does not include `isContactSharedByOwner`, contact grant ids, or direct personal contact values

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
The system SHALL classify account identifiers, profile fields, phone, email, address, exact coordinates, photos, messages, sighting history, and legacy contact grant data as sensitive fields. Production models SHALL avoid storing phone, email, address, or contact grant data for app-managed personal contact disclosure.

#### Scenario: Shared feed reads a pet post
- **GIVEN** a signed-in user reads the shared pet post feed
- **WHEN** the post contains sensitive owner contact or exact location data in legacy storage
- **THEN** the app and rules expose only fields allowed by the current post, sighting, and chat-only privacy policy

#### Scenario: Legacy contact grant is encountered
- **GIVEN** a legacy contact grant document exists
- **WHEN** the app maps backend data to local state
- **THEN** the app does not map phone, email, address, or contact grant values into a renderable contact-disclosure model

#### Scenario: Chat messages contain user-entered personal data
- **GIVEN** a user voluntarily types phone, email, address, or similar personal data into a chat message
- **WHEN** the message is stored
- **THEN** the value is treated as sensitive message content and not as an app-managed contact grant

### Requirement: Contact Grants Are Chat-Scoped
The system SHALL retire chat-scoped contact grants. New production writes SHALL NOT create, update, activate, or depend on contact grant documents tied to `chatId`, `postId`, `ownerId`, or `reporterId`.

#### Scenario: Active grant creation is attempted
- **GIVEN** an owner and reporter are participants in a chat
- **WHEN** a client attempts to persist an active contact grant
- **THEN** the write is rejected or ignored by the production data layer

#### Scenario: Grant is present from legacy data
- **GIVEN** a chat contact grant exists from an earlier build
- **WHEN** the updated app syncs production backend data
- **THEN** the grant is not treated as part of the current production data model and does not expose phone or email values

### Requirement: Production Media References
The backend model SHALL store production media references as Cloudinary metadata associated with the owning post or sighting and SHALL classify photo data as sensitive.

#### Scenario: Post stores uploaded media reference
- **GIVEN** a signed-in owner uploads a pet photo
- **WHEN** the pet post document is created
- **THEN** the document references the uploaded media by Cloudinary secure URL, provider and public ID instead of a preset asset URI

#### Scenario: Sighting stores uploaded media reference
- **GIVEN** a reporter uploads sighting photo evidence
- **WHEN** the sighting document is created
- **THEN** the document references the uploaded media and links it to the sighting, post owner and reporter identities

### Requirement: Production Location Metadata
The backend model SHALL distinguish GPS-captured coordinates, manual/coarse location labels and public-safe location display fields.

#### Scenario: GPS sighting is stored
- **GIVEN** a reporter consents to GPS capture
- **WHEN** the sighting is persisted
- **THEN** the backend document records precise coordinates as sensitive fields and records the source as device-captured

#### Scenario: Coarse location is stored
- **GIVEN** a user provides an approved manual or approximate location
- **WHEN** the post or sighting is persisted
- **THEN** the backend document marks the location source separately from precise GPS coordinates

### Requirement: Personal Contact Disclosure Is Not Modeled
The backend model SHALL NOT define production documents, fields, or subcollections whose purpose is app-managed disclosure of user phone, email, address, or equivalent personal contact data between chat participants.

#### Scenario: New production chat is persisted
- **WHEN** a production chat session is created
- **THEN** it contains participant identity and message metadata but no contact grant reference, contact sharing flag, phone, email, or address field

#### Scenario: New production post is persisted
- **WHEN** a production pet post is created or updated
- **THEN** it contains post data needed for the listing and ownership but no direct personal contact fields or public reveal flags

