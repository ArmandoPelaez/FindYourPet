## MODIFIED Requirements

### Requirement: Production Post Form Validation
The app SHALL validate required pet post fields, authenticated owner identity, production media state and an approved location selection before creating a backend pet post. An approved selection MAY come from device GPS, Google Maps map selection or a manual coarse reference. The selected source, display text and coordinates SHALL remain consistent when persisted.

The Firestore `petPosts` rules SHALL accept the lost pet's `latitude` and `longitude` only as numeric values within geographic bounds, while continuing to reject owner coordinates and public contact fields. Existing GPS consent validation for sighting documents SHALL remain enforced.

#### Scenario: Valid post is created with a map selection
- **GIVEN** a signed-in user completes all required pet post fields, attaches valid production media and confirms a point on the Google map
- **WHEN** the user submits the form
- **THEN** the app creates a backend pet post with the confirmed coordinates and a non-empty public-safe location label

#### Scenario: Valid post is created with a GPS selection
- **GIVEN** a signed-in user completes all required pet post fields, attaches valid production media and confirms the current device location after granting permission
- **WHEN** the user submits the form
- **THEN** the app creates a backend pet post with the selected coordinates and source `DEVICE_GPS`

#### Scenario: Pet coordinates are validated without exposing person coordinates
- **GIVEN** a signed-in user submits a pet post with latitude and longitude for the place where the pet was lost
- **WHEN** the Firestore create rule evaluates the document
- **THEN** the write is accepted only when both values are numbers within latitude/longitude bounds, and owner coordinates or contact fields remain rejected

#### Scenario: Missing post photo is blocked
- **GIVEN** production post creation requires a real photo
- **WHEN** a signed-in user submits the form without captured or selected media
- **THEN** the app blocks the write and identifies the missing photo field

#### Scenario: Demo media is rejected
- **GIVEN** a create-post form still contains a preset demo URI
- **WHEN** the user submits a production post
- **THEN** the app rejects the submission before any backend write

#### Scenario: Missing location selection is blocked
- **GIVEN** a signed-in user has not confirmed a GPS, map or manual location selection
- **WHEN** the user attempts to publish
- **THEN** the app keeps publication unavailable or identifies the missing location without creating a backend post

### Requirement: Simplified Create-Post Form Presentation
The create-post screen SHALL present lost-pet publication inputs as a simplified visual flow while preserving existing responsive behavior, production validation and backend-compatible field mapping. The last-seen location control SHALL use the label `¿Donde fue vista por ultima vez?` and placeholder `Seleccionar ubicacion` and SHALL expose the three approved location entry options.

#### Scenario: User views the guided location entry
- **GIVEN** a signed-in user opens the create-post screen
- **WHEN** the form is rendered
- **THEN** the primary visible flow presents photo upload, pet name, recognition details, the guided last-seen location control and publish controls

#### Scenario: User opens location choices
- **GIVEN** the user taps the `Seleccionar ubicacion` control
- **WHEN** the location choices are displayed
- **THEN** the app offers `Usar mi ubicacion actual`, `Elegir en el mapa` and `Escribir una referencia`

#### Scenario: User confirms a location choice
- **GIVEN** the user selects a valid GPS, map or manual reference option
- **WHEN** the selection is confirmed
- **THEN** the form displays the selected public-safe location text and keeps the corresponding source and coordinates for publication

#### Scenario: Current location requires consent
- **GIVEN** the user chooses `Usar mi ubicacion actual`
- **WHEN** the app requests location access
- **THEN** the app captures coordinates only after the user grants the runtime permission and offers map or manual alternatives if permission is denied or unavailable

#### Scenario: Responsive layout remains stable
- **GIVEN** the app renders the create-post screen on supported phone viewport sizes
- **WHEN** the guided location form is displayed and scrolled
- **THEN** existing top bar, scroll behavior, outer padding rhythm, established field heights and submit-button sizing remain stable with no overlapping or clipped text
