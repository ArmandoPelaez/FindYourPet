## MODIFIED Requirements

### Requirement: Production Post Form Validation
The app SHALL validate required pet post fields, authenticated owner identity, production media state and allowed location state before creating a backend pet post. The publication action SHALL be unavailable while those conditions are unmet or while a submission is in progress, regardless of whether it is rendered in the create-post form or in the contextual Bottom Navigation CTA.

#### Scenario: Valid post is created
- **GIVEN** a signed-in user completes all required pet post fields and attaches valid production media
- **WHEN** the user activates enabled `Publicar ficha` from the Bottom Navigation
- **THEN** the app creates the pet post through the existing creation path owned by the signed-in Firebase `uid`

#### Scenario: Missing post photo is blocked
- **GIVEN** production post creation requires a real photo
- **WHEN** a signed-in user has no captured or selected media
- **THEN** the `Publicar ficha` action remains disabled or the existing validation error identifies the missing photo field, without a backend write

#### Scenario: Missing required name or location is blocked
- **GIVEN** the create-post form has a blank required name or invalid last-seen location
- **WHEN** the user views the contextual publish action
- **THEN** `Publicar ficha` remains disabled and no post is created

#### Scenario: Submission cannot be duplicated
- **GIVEN** a create-post submission is in progress
- **WHEN** the user attempts to activate `Publicar ficha` again
- **THEN** the action remains disabled and the existing submission callback is not invoked a second time

### Requirement: Simplified Create-Post Form Presentation
The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow inspired by the provided reference while preserving existing responsive behavior, production validation and backend-compatible field mapping. Its primary publication action SHALL be integrated into the fixed Bottom Navigation during this flow; the screen SHALL NOT render a second independent `Publicar ficha` button.

#### Scenario: User views simplified create-post form
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents photo upload, pet name, recognition details and last-seen location while the fixed Bottom Navigation presents the single `Publicar ficha` action

#### Scenario: Contextual action remains visible while scrolling
- **GIVEN** a signed-in user scrolls the create-post form
- **WHEN** the form content moves
- **THEN** `Publicar ficha` remains visible in the fixed Bottom Navigation and no in-form duplicate is shown

#### Scenario: Simplified fields preserve post creation
- **GIVEN** the user has selected a real photo, entered a pet name, entered useful recognition details and selected a valid last-seen location
- **WHEN** the user activates enabled `Publicar ficha`
- **THEN** the app creates the pet post through the existing creation path with the existing validation and backend-compatible field mapping

#### Scenario: Required post fields are still enforced
- **GIVEN** the user leaves the photo, pet name or last-seen location invalid
- **WHEN** the create-post screen evaluates whether the post can be submitted
- **THEN** the Bottom Navigation publish action remains unavailable or the existing validation error identifies the missing required field without creating a backend post

#### Scenario: Responsive layout remains stable
- **GIVEN** the app renders the create-post screen on supported phone viewport sizes
- **WHEN** the form and contextual bottom CTA are displayed or scrolled
- **THEN** the established outer padding rhythm, field heights, fixed navigation insets and text remain readable with no overlapping or clipped content in Light or Dark Theme
