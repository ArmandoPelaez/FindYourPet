## MODIFIED Requirements

### Requirement: Simplified Create-Post Form Presentation
The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow inspired by the provided reference while preserving existing responsive behavior, production validation and backend-compatible field mapping. The existing additional-details input SHALL be presented as a multiline field labeled `Descripcion adicional`, SHALL show the placeholder `Contanos cómo reconocerla...` when empty, SHALL accept at most 500 characters, and SHALL show a discreet `actual/500` counter.

#### Scenario: User views simplified create-post form
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents photo upload, pet name, recognition details, manual last-seen location and publish controls without requiring separate visible breed and color inputs
- **AND** the existing additional-details field is labeled `Descripcion adicional`
- **AND** the field is multiline and shows `Contanos cómo reconocerla...` when it is empty

#### Scenario: Additional-details counter communicates the limit
- **GIVEN** the user opens the additional-details field
- **WHEN** the field contains `N` characters where `0 <= N <= 500`
- **THEN** the field shows a discreet counter in the format `N/500`

#### Scenario: Additional-details input enforces the maximum
- **GIVEN** the user enters text into the additional-details field
- **WHEN** the input would exceed 500 characters
- **THEN** the field retains no more than 500 characters
- **AND** the counter remains at `500/500`

#### Scenario: Photo upload remains production media
- **GIVEN** the simplified form shows a single primary photo upload area
- **WHEN** the user adds a photo from the supported camera or gallery flows
- **THEN** the form stores the selected real media using the existing production media state and does not introduce preset demo media or a new media source

#### Scenario: Simplified fields preserve post creation
- **GIVEN** the user has selected a real photo, entered a pet name, entered useful recognition details and entered a manual last-seen location
- **WHEN** the user submits the form
- **THEN** the app creates the pet post through the existing creation path with authenticated owner identity, existing validation and backend-compatible values for fields not shown as separate inputs
- **AND** the additional-details value is sent through the existing `features` mapping without creating a new persisted field

#### Scenario: Missing additional details remains allowed
- **GIVEN** the user leaves the additional-details field empty
- **WHEN** the user submits a form that has all existing required fields
- **THEN** the existing creation validation behavior is preserved and no new required-field error is introduced for additional details

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
- **AND** the multiline additional-details field and its counter remain readable in Light Theme and Dark Theme
