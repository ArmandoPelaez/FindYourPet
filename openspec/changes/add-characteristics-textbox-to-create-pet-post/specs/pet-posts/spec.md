## MODIFIED Requirements

### Requirement: Simplified Create-Post Form Presentation
The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow inspired by the provided reference while preserving existing responsive behavior, production validation and backend-compatible field mapping. The screen SHALL present `Características` as the label of a new independent optional input immediately after `Nombre`, without a required-field marker or label icon, with placeholder `Ej: color,raza,tamaño`, while keeping `Detalles adicionales` as a separate input.

#### Scenario: User views simplified create-post form
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents photo upload, pet name, a new multiline input labeled `Características` immediately after `Nombre`, placeholder `Ej: color,raza,tamaño`, the separate `Detalles adicionales` input, manual last-seen location and publish controls without requiring separate visible breed and color inputs

#### Scenario: Characteristics input is independent from additional details
- **GIVEN** the user enters values in both `Características` and `Detalles adicionales`
- **WHEN** the user submits the form with the other required inputs and valid media
- **THEN** the app keeps both values distinct, sending `Detalles adicionales` through `features` and `Características` through `characteristics`

#### Scenario: Characteristics input is persisted locally and remotely
- **GIVEN** the user submits a valid create-post form with a value in `Características`
- **WHEN** the existing post creation path stores the publication
- **THEN** Room and Firestore persist the value under the independent `characteristics` attribute and read it back without losing it

#### Scenario: Legacy posts remain readable
- **GIVEN** a local Room row or Firestore document created before `characteristics` existed
- **WHEN** the app reads that post after the change
- **THEN** the post maps successfully with an empty `characteristics` value and retains all existing fields

#### Scenario: Characteristics label supports both themes
- **GIVEN** the create-post screen is rendered in Light Theme or Dark Theme
- **WHEN** the `Características` input is displayed
- **THEN** its label, input text and supporting content remain legible using existing Material 3 and Design System tokens without hardcoded colors

#### Scenario: Characteristics field is optional
- **GIVEN** the user leaves the `Características` input empty
- **WHEN** the user completes the other required publication inputs and submits the form
- **THEN** the app does not block publication because of the empty `Características` value and persists it as an empty optional attribute

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
