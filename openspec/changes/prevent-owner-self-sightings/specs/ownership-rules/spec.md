## ADDED Requirements

### Requirement: Ownership Does Not Create Reporter Eligibility
The app SHALL treat post ownership and sighting reporter eligibility as separate contextual permissions derived from the authenticated Firebase `uid` and the referenced post `ownerId`.

#### Scenario: Owner can manage but not report own post
- **GIVEN** an authenticated user's Firebase `uid` equals a post's `ownerId`
- **WHEN** the user views that post
- **THEN** the app may display owner-only controls
- **AND** the app MUST NOT allow the user to report a sighting for that same post

#### Scenario: Non-owner can report but not manage post
- **GIVEN** an authenticated user's Firebase `uid` differs from a post's `ownerId`
- **WHEN** the user views that post
- **THEN** the app hides owner-only controls
- **AND** the app may allow the user to report a sighting if the post status and sighting validation rules allow it

#### Scenario: Same account has different permissions per post
- **GIVEN** user B owns post B
- **AND** user A owns post A
- **WHEN** user B views both posts
- **THEN** user B has owner permissions on post B and reporter eligibility on post A

### Requirement: Discovery Feed Excludes Own Posts
The app SHALL exclude posts owned by the authenticated user from the main discovery feed while keeping those posts available in the user's profile publications list.

#### Scenario: User A opens main feed
- **GIVEN** user A owns post A
- **AND** user B owns post B
- **WHEN** user A opens the main feed
- **THEN** user A sees post B
- **AND** user A does not see post A

#### Scenario: User B opens main feed
- **GIVEN** user A owns post A
- **AND** user B owns post B
- **WHEN** user B opens the main feed
- **THEN** user B sees post A
- **AND** user B does not see post B

#### Scenario: User C has no posts
- **GIVEN** user A owns post A
- **AND** user B owns post B
- **AND** user C owns no posts
- **WHEN** user C opens the main feed
- **THEN** user C sees post A and post B

#### Scenario: Owner views own publications in profile
- **GIVEN** user A owns post A
- **WHEN** user A opens the profile screen
- **THEN** the profile shows post A in "Mis Mascotas Publicadas"
- **AND** the profile item includes the pet name and status
