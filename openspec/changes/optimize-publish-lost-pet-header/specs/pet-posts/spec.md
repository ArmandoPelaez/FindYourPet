## MODIFIED Requirements

### Requirement: Simplified Create-Post Form Presentation
The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow inspired by the provided reference while preserving existing responsive behavior, production validation and backend-compatible field mapping. Its primary content flow SHALL remove the redundant top app bar, show the title `Publicar mascota perdida` inside the screen content after the safe area, and place the photo upload surface immediately after that title. The screen SHALL keep the system status bar visible with the screen surface background and SHALL preserve the fixed bottom navigation behavior.

#### Scenario: User views simplified create-post form
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents the title `Publicar mascota perdida`, photo upload, pet name, recognition details, manual last-seen location and publish controls without requiring separate visible breed and color inputs

#### Scenario: Create-post header uses the content surface
- **GIVEN** a signed-in user opens the create-post screen from the primary bottom navigation
- **WHEN** the screen header is rendered
- **THEN** no top app bar or top navigation arrow is shown, the status bar remains visible with the screen surface background, the title appears after the safe-area inset with the Design System's equivalent of the requested 22–24 sp Semibold treatment, and the photo upload surface follows the title

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
- **WHEN** the simplified visual form is displayed, scrolled, or the IME is visible
- **THEN** the content begins below the status-bar safe area with the tokenized top spacing, the title and photo surface remain ordered and readable, the fixed bottom navigation does not obscure the final content, and established field heights and submit-button sizing remain stable with no overlapping or clipped text
