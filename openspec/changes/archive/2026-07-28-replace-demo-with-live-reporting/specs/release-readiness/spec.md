## ADDED Requirements

### Requirement: Real Product Flow Validation
The change SHALL document validation evidence that real media, real location, in-app notification and no-demo-data flows work before the change is considered release-ready.

#### Scenario: Android build and unit tests pass
- **GIVEN** media, location, in-app notification or seed-removal code changes are completed
- **WHEN** the change is prepared for completion
- **THEN** the debug Android build and relevant unit tests pass or any blocker is documented

#### Scenario: Permission flows are manually validated
- **GIVEN** the app requests camera, media, location or notification permission
- **WHEN** validation is performed on a supported Android device or emulator
- **THEN** granted, denied and unavailable states are documented for each touched permission

#### Scenario: No demo data dependency remains
- **GIVEN** the backend has no seeded pet posts, sightings, chats or notifications for a signed-in test user
- **WHEN** the app starts and the user navigates through feed, post creation, sighting, chat and notifications
- **THEN** the app works with empty states or real submitted data and does not inject `seedInitialDataIfNeeded` records

#### Scenario: In-app alert privacy is validated
- **GIVEN** an in-app notification is generated for a sighting or chat-related event
- **WHEN** the notification record and displayed preview text are inspected
- **THEN** they contain no phone, email, address, exact coordinates, full notes, photo download URLs or private message bodies
