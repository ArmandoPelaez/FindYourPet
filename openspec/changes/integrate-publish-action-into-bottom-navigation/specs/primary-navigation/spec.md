## MODIFIED Requirements

### Requirement: Home primary actions banner
The signed-in app SHALL present a fixed floating bottom navigation surface containing exactly five destinations in this order: Inicio, Perfil, Publicar, Mensajes and Alertas. Outside the create-post flow, the central Publicar destination SHALL use the existing circular `+ Publicar` treatment. During the create-post flow, the same central slot SHALL show the contextual `Publicar ficha` CTA instead.

#### Scenario: Authenticated user views home
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the home content is displayed
- **THEN** the bottom navigation shows Inicio, Perfil, a circular `+ Publicar` action, Mensajes and Alertas in that order

#### Scenario: Create-post flow uses contextual action
- **GIVEN** a signed-in user is on the create-post screen
- **WHEN** the fixed bottom navigation is displayed
- **THEN** the central `+ Publicar` action is replaced by `Publicar ficha` in the same navigation row

#### Scenario: Contextual action is wider than secondary actions
- **GIVEN** the create-post screen is displayed
- **WHEN** the bottom navigation lays out its five positions
- **THEN** the `Publicar ficha` center slot is wider than a secondary destination and is approximately equivalent to two normal item positions without overlapping the four surrounding destinations

### Requirement: Primary action navigation
Each bottom navigation destination SHALL preserve its existing destination. The regular central Publicar action SHALL navigate to the create-post screen, and the contextual `Publicar ficha` action SHALL invoke the existing create-post publication callback without creating a second publication flow.

#### Scenario: Regular create-post action
- **GIVEN** a signed-in user is outside the create-post flow
- **WHEN** the user taps `+ Publicar`
- **THEN** the app navigates to the create pet post screen

#### Scenario: Contextual publish action
- **GIVEN** a signed-in user is on the create-post screen
- **WHEN** the user taps enabled `Publicar ficha`
- **THEN** the app invokes the existing form submission and publication logic

#### Scenario: Other destinations remain available
- **GIVEN** a signed-in user is on the create-post screen
- **WHEN** the fixed bottom navigation is displayed
- **THEN** Inicio, Perfil, Mensajes and Alertas remain visible and invoke their existing navigation callbacks

### Requirement: Floating banner presentation
The bottom navigation SHALL appear as one fixed floating surface above the system gesture area, with the contextual center action integrated into the same row and no additional CTA row.

#### Scenario: Banner remains fixed during form scroll
- **GIVEN** a signed-in user scrolls the create-post form
- **WHEN** the form content moves
- **THEN** the bottom navigation and `Publicar ficha` CTA remain visible and fixed

#### Scenario: Banner respects safe area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** any primary destination is displayed
- **THEN** the bottom navigation remains positioned above the system gesture area

#### Scenario: Regular action is restored
- **GIVEN** a signed-in user leaves the create-post flow
- **WHEN** another primary destination is displayed
- **THEN** the center slot returns to the circular `+ Publicar` treatment

## ADDED Requirements

### Requirement: Contextual publish action state
The contextual `Publicar ficha` action SHALL mirror the existing create-post form state: it MUST be disabled while required conditions are unmet or a submission is in progress, enabled when the form is valid and idle, and show the existing submission progress state while publishing.

#### Scenario: Invalid form disables CTA
- **GIVEN** the create-post form is missing a required name, photo or valid location
- **WHEN** the bottom navigation renders the contextual action
- **THEN** `Publicar ficha` is disabled

#### Scenario: Valid form enables CTA
- **GIVEN** the create-post form has a nonblank name, valid selected media and valid location
- **WHEN** no submission is in progress
- **THEN** `Publicar ficha` is enabled and uses the primary action styling

#### Scenario: Submission state prevents duplicate publishes
- **GIVEN** a valid create-post form has started publishing
- **WHEN** the bottom navigation renders the contextual action
- **THEN** the CTA is disabled and preserves the existing progress indication until completion or error
