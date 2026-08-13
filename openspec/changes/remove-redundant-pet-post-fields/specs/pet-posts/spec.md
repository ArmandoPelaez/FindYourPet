## MODIFIED Requirements

### Requirement: Authenticated Pet Post Creation
The system SHALL create production pet posts only for signed-in users, SHALL set `ownerId` to the signed-in Firebase `uid`, and SHALL omit the retired redundant attributes `characteristics` and `particularMarks` from new post creation contracts and writes.

#### Scenario: Signed-in owner creates a post
- **GIVEN** a signed-in user completes the create-post form
- **WHEN** the post is saved
- **THEN** the backend document is created with `ownerId` equal to that user's Firebase `uid`
- **THEN** the new post does not write `characteristics` or `particularMarks`

#### Scenario: Signed-out user attempts to create a post
- **GIVEN** no user is signed in
- **WHEN** the create-post action is submitted
- **THEN** the app blocks the write and presents an authentication-required state

### Requirement: Simplified Create-Post Form Presentation
The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow inspired by the provided reference while preserving existing responsive behavior, production validation and backend-compatible field mapping. The recognition flow SHALL retain `Descripción adicional` as the single visible recognition-details input and MUST NOT present `Características` or `Señas particulares`.

#### Scenario: User views simplified create-post form
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents photo upload, pet name, `Descripción adicional`, manual last-seen location and publish controls without showing `Características` or `Señas particulares` as separate inputs

#### Scenario: Photo upload remains production media
- **GIVEN** the simplified form shows a single primary photo upload area
- **WHEN** the user adds a photo from the supported camera or gallery flows
- **THEN** the form stores the selected real media using the existing production media state and does not introduce preset demo media or a new media source

#### Scenario: Simplified fields preserve post creation
- **GIVEN** the user has selected a real photo, entered a pet name, entered useful recognition details in `Descripción adicional` and entered a manual last-seen location
- **WHEN** the user submits the form
- **THEN** the app creates the pet post through the existing creation path with authenticated owner identity, existing validation and backend-compatible values for fields not shown as separate inputs
- **THEN** the creation path does not transport or persist `characteristics` or `particularMarks`

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

## ADDED Requirements

### Requirement: Retired Pet Post Attributes Are Non-Operational
The system SHALL no longer model or write `characteristics` or `particularMarks` in the current local and remote pet-post contracts, while preserving the remaining pet-post fields and allowing legacy remote documents to be read without failure.

#### Scenario: Local database upgrades from version 7
- **GIVEN** a local Room database is at schema version 7 and contains a `pet_posts` row
- **WHEN** the app upgrades the database to the new schema
- **THEN** the upgrade succeeds without losing the row's remaining fields
- **THEN** the resulting `pet_posts` schema no longer contains `characteristics` or `particularMarks`

#### Scenario: Legacy remote document is read
- **GIVEN** a Firestore pet-post document contains legacy `characteristics` or `particularMarks` keys
- **WHEN** the app maps the document to the current pet-post model
- **THEN** mapping succeeds
- **THEN** the remaining modeled fields, including `features`, are preserved
- **THEN** the legacy keys are not reintroduced into the current model or subsequent writes

#### Scenario: New remote document is serialized
- **GIVEN** a user creates a new pet post through the current form
- **WHEN** the repository serializes the post for Firestore
- **THEN** the payload contains the current modeled fields and does not contain `characteristics` or `particularMarks`

