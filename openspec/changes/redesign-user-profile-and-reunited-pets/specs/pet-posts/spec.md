## MODIFIED Requirements

### Requirement: Reunited Post Cascade Cleanup
The system SHALL cascade the owner-confirmed `PERDIDO` to `REUNIDO` transition by deleting every sighting whose `postId` matches the publication and every related owner notification/alert, from Firestore and local Room/cache. The cascade SHALL be authorized only for the publication owner.

#### Scenario: Reunification deletes related sightings
- **GIVEN** the owner confirms a `PERDIDO` publication as `REUNIDO`
- **WHEN** the transition is persisted
- **THEN** all backend and local sighting records with that `postId` are deleted

#### Scenario: Reunification deletes related notifications
- **GIVEN** the owner has notifications linked by `postId` or by the `sightingId` of a sighting for the publication
- **WHEN** the owner-confirmed reunification cleanup runs
- **THEN** those notifications are deleted from the owner's backend notification collection and local cache

#### Scenario: Unauthorized cascade is denied
- **GIVEN** a user is not the owner of the publication
- **WHEN** that user attempts the status transition or cascade cleanup
- **THEN** Firestore and the app reject the operation without deleting activity or notifications

### Requirement: Shared Pet Post Feed
The system SHALL read pet publications from the backend source of truth so authenticated users can see eligible posts created by other users according to feed filters and visibility rules. A publication with status `REUNIDO` SHALL NOT be eligible for another user's discovery feed or public search, while it SHALL remain available to its owner in the owner's publication list.

#### Scenario: User sees another user's public post
- **GIVEN** user A creates an eligible pet post with a public status in the backend
- **WHEN** user B signs in and opens the feed
- **THEN** user B sees user A's post according to feed filters and visibility rules

#### Scenario: Reunited post is hidden from another user
- **GIVEN** user A owns a pet post with status `REUNIDO`
- **WHEN** user B opens the feed or searches for posts
- **THEN** user B does not see that post

#### Scenario: Reunited post remains visible to its owner
- **GIVEN** the authenticated owner has a pet post with status `REUNIDO`
- **WHEN** the owner opens `Perfil → Mis publicaciones`
- **THEN** the owner sees that post and its `REUNIDO` status

### Requirement: Owner-Only Post Mutation
The system SHALL allow only the post owner to update status, visibility or contact-sharing fields for a production pet post. The profile reunification action SHALL allow only a one-way `PERDIDO` to `REUNIDO` transition and SHALL reject reactivation of a `REUNIDO` post.

#### Scenario: Owner marks a lost post reunited
- **GIVEN** user A owns a post with status `PERDIDO` and confirms the reunification action
- **WHEN** the status update is submitted
- **THEN** the backend and the local authority update the post to `REUNIDO`

#### Scenario: Reunited status is terminal
- **GIVEN** user A owns a post with status `REUNIDO`
- **WHEN** user A attempts to reactivate or change it through the profile reunification action
- **THEN** the operation is rejected and the post remains `REUNIDO`

#### Scenario: Non-owner attempts to update status
- **GIVEN** user B is not the owner of user A's post
- **WHEN** user B attempts to change that post's status
- **THEN** the backend denies the write and the app shows an error state without changing cached authority

### Requirement: Post Delete Is Owner-Only
The system SHALL allow only the owner to delete or archive a production pet post.

#### Scenario: Owner deletes a post
- **GIVEN** user A owns a pet post
- **WHEN** the user deletes or archives the post
- **THEN** the post is removed from or hidden in the shared feed

### Requirement: Production Post Form Validation
The app SHALL validate required pet post fields, authenticated owner identity, production media state and allowed location state before creating a backend pet post.

#### Scenario: Valid post is created
- **GIVEN** a signed-in user completes all required pet post fields and attaches valid production media
- **WHEN** the user submits the form
- **THEN** the app creates a backend pet post owned by the signed-in Firebase `uid`

#### Scenario: Missing post photo is blocked
- **GIVEN** production post creation requires a real photo
- **WHEN** a signed-in user submits the form without captured or selected media
- **THEN** the app blocks the write and identifies the missing photo field

#### Scenario: Demo media is rejected
- **GIVEN** a create-post form still contains a preset demo URI
- **WHEN** a signed-in user submits a production post
- **THEN** the app rejects the submission before any backend write

### Requirement: Simplified Create-Post Form Presentation
The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow inspired by the provided reference while preserving existing responsive behavior, production validation and backend-compatible field mapping.

#### Scenario: User views simplified create-post form
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents photo upload, pet name, recognition details, manual last-seen location and publish controls without requiring separate visible breed and color inputs

#### Scenario: Photo upload remains production media
- **GIVEN** the simplified form shows a single primary photo upload area
- **WHEN** the user adds a photo from the supported camera or gallery flows
- **THEN** the form stores the selected real media using the existing production media state and does not introduce preset demo media or a new media source

#### Scenario: Simplified fields preserve post creation
- **GIVEN** the user has selected a real photo, entered a pet name, entered useful recognition details and entered a manual last-seen location
- **WHEN** the user submits the form
- **THEN** the app creates the pet post through the existing creation path with authenticated owner identity, existing validation and backend-compatible values for fields not shown as separate inputs

#### Scenario: Required post fields are still enforced
- **GIVEN** the user leaves the photo, pet name or manual last-seen location empty
- **WHEN** the create-post screen evaluates whether the post can be submitted
- **THEN** the publish action remains unavailable or the existing validation error identifies the missing required field without creating a backend post

#### Scenario: Create-post screen does not add current-location capture
- **GIVEN** the simplified form uses the reference image for visual hierarchy
- **WHEN** the location section is rendered
- **THEN** the screen keeps manual/coarse last-seen-location input and does not present a current-location capture action or request new location permissions for post creation

#### Scenario: Responsive layout remains stable
- **GIVEN** the app renders the create-post screen on supported phone viewport sizes
- **WHEN** the simplified visual form is displayed and scrolled
- **THEN** existing top bar, scroll behavior, outer padding rhythm, established field heights and submit-button sizing remain stable with no overlapping or clipped text
