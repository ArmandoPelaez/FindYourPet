## ADDED Requirements

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
