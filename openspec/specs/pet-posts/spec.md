# pet-posts Specification

## Purpose
Define backend-backed pet post feed, creation and owner-only mutation behavior.
## Requirements
### Requirement: Shared Pet Post Feed
The system SHALL read pet publications from the backend source of truth so authenticated users can see eligible posts created by other users.

#### Scenario: User sees another user's post
- **GIVEN** user A creates an eligible pet post in the backend
- **WHEN** user B signs in and opens the feed
- **THEN** user B sees user A's post according to feed filters and visibility rules

### Requirement: Authenticated Pet Post Creation
The system SHALL create production pet posts only for signed-in users and SHALL set `ownerId` to the signed-in Firebase `uid`.

#### Scenario: Signed-in owner creates a post
- **GIVEN** a signed-in user completes the create-post form
- **WHEN** the post is saved
- **THEN** the backend document is created with `ownerId` equal to that user's Firebase `uid`

#### Scenario: Signed-out user attempts to create a post
- **GIVEN** no user is signed in
- **WHEN** the create-post action is submitted
- **THEN** the app blocks the write and presents an authentication-required state

### Requirement: Owner-Only Post Mutation
The system SHALL allow only the post owner to update status, visibility or contact-sharing fields for a production pet post.

#### Scenario: Non-owner attempts to update status
- **GIVEN** user B is not the owner of user A's post
- **WHEN** user B attempts to change that post's status
- **THEN** the backend denies the write and the app shows an error state without changing cached authority

### Requirement: Post Delete Is Owner-Only
The system SHALL allow only the owner to delete or archive a production pet post.

#### Scenario: Owner deletes a post
- **GIVEN** user A owns a pet post
- **WHEN** user A deletes or archives the post
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
- **WHEN** the user submits a production post
- **THEN** the app rejects the submission before any backend write

