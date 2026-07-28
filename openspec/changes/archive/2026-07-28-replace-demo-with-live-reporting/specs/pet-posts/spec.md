## ADDED Requirements

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
