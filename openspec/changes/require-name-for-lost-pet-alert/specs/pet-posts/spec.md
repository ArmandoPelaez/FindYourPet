## MODIFIED Requirements

### Requirement: Production Post Form Validation

The app SHALL validate required pet post fields, authenticated owner identity, production media state and allowed location state before creating a backend pet post. The create-post form SHALL identify the pet name as required and SHALL present `Campo obligatorio` when a save attempt is made with an empty or whitespace-only name.

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

#### Scenario: Missing pet name is blocked with required-field feedback
- **GIVEN** a signed-in user has the other required publication inputs available
- **WHEN** the user attempts to save with a blank or whitespace-only pet name
- **THEN** the app does not call the publication path and presents `Campo obligatorio`

### Requirement: Simplified Create-Post Form Presentation

The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow inspired by the provided reference while preserving existing responsive behavior, production validation and backend-compatible field mapping. The pet-name field SHALL use a visible `Nombre` label and SHALL show an adjacent `*` indicator using the existing primary theme token.

#### Scenario: User views simplified create-post form
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents photo upload, a labeled required pet-name field, recognition details, manual last-seen location and publish controls without requiring separate visible breed and color inputs

#### Scenario: Required name marker supports both themes
- **GIVEN** the create-post screen is rendered in Light Theme or Dark Theme
- **WHEN** the pet-name field is displayed
- **THEN** the `Nombre` label and `*` indicator remain legible and use existing Material 3/theme tokens without hardcoded colors

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
