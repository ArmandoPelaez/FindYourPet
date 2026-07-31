## ADDED Requirements

### Requirement: Sightings Require A Distinct Reporter
The app SHALL allow a signed-in user to report a sighting only when the user's Firebase `uid` differs from the referenced pet post `ownerId`.

#### Scenario: User reports another user's pet
- **GIVEN** user A owns a lost-pet post
- **AND** user B is signed in
- **WHEN** user B submits a valid sighting for user A's post
- **THEN** the app creates the sighting with `ownerId` equal to user A and `reporterId` equal to user B

#### Scenario: Owner attempts to report own pet
- **GIVEN** user A owns a lost-pet post
- **AND** user A is signed in
- **WHEN** user A attempts to submit a sighting for that same post
- **THEN** the app blocks the submission before creating a sighting, chat message, chat session, or owner notification

#### Scenario: User can publish and report different posts
- **GIVEN** user A owns post A
- **AND** user B owns post B
- **WHEN** user A reports a valid sighting for post B and user B reports a valid sighting for post A
- **THEN** both sightings are accepted because each reporter differs from the referenced post owner

### Requirement: Sighting Entry Points Respect Reporter Eligibility
The app SHALL expose the sighting report entry point only for posts where the current user is not the post owner and the post state otherwise permits sightings.

#### Scenario: Owner views own active post
- **GIVEN** user A is signed in and views user A's non-reunited post
- **WHEN** the post detail or feed card is displayed
- **THEN** the "Lo he visto" sighting action is unavailable for that post

#### Scenario: Non-owner views active post
- **GIVEN** user B is signed in and views user A's non-reunited post
- **WHEN** the post detail or feed card is displayed
- **THEN** the "Lo he visto" sighting action is available according to the existing post status rules

#### Scenario: Direct sighting route for owned post
- **GIVEN** user A is signed in and owns a post
- **WHEN** user A reaches the sighting form route for that post directly
- **THEN** the app blocks submission and shows an invalid self-report state instead of allowing the form to fan out records
