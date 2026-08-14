## MODIFIED Requirements

### Requirement: Home primary actions banner
The authenticated home screen SHALL present a floating bottom navigation surface containing exactly five primary destinations in this order: home on the left, profile, report in the center, chat, and notifications on the right. The central report destination SHALL use the existing filled circular action treatment, a pet icon, and the `Reportar` label.

#### Scenario: Authenticated user views home
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom navigation is displayed
- **THEN** it shows `Inicio`, `Perfil`, `Reportar`, `Mensajes`, and `Alertas` in that order
- **AND** the central destination uses a pet icon instead of a plus icon

#### Scenario: Report action is centered
- **GIVEN** a signed-in user views the floating bottom navigation
- **WHEN** the user looks at the navigation surface
- **THEN** the `Reportar` action is visually centered between the profile and chat destinations
- **AND** it retains the existing circular emphasis and position

### Requirement: Primary action navigation
Each bottom navigation destination SHALL preserve the existing destination for its corresponding feature, and the central `Reportar` action SHALL preserve the destination previously opened by `Publicar`.

#### Scenario: Profile action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the `Perfil` action
- **THEN** the app navigates to the profile screen

#### Scenario: Report action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the centered `Reportar` action
- **THEN** the app navigates to the create pet post/report screen

#### Scenario: Chat action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the `Mensajes` action
- **THEN** the app navigates to the private chat list screen

#### Scenario: Home and notifications actions
- **GIVEN** a signed-in user is viewing any screen that exposes the bottom navigation
- **WHEN** the user taps `Inicio` or `Alertas`
- **THEN** the app navigates to the corresponding home or notifications screen

## ADDED Requirements

### Requirement: Report action presentation and accessibility
The central `Reportar` action SHALL use the existing selected, unselected, pressed, and disabled behavior when applicable, use theme-derived colors in Light and Dark modes, and expose `Reportar` as its accessible name.

#### Scenario: Report action states
- **GIVEN** the central action is rendered in any supported navigation state
- **WHEN** its selected, unselected, pressed, or disabled state changes
- **THEN** it uses the same state tokens and circular treatment as the existing central action
- **AND** it does not introduce hardcoded visual values

#### Scenario: Report action accessibility
- **GIVEN** a user navigates the bottom navigation with an accessibility service
- **WHEN** the central action receives focus
- **THEN** it is announced as `Reportar`
- **AND** its icon is not announced as a generic plus or create action

#### Scenario: Light and Dark themes
- **GIVEN** the app is rendered in Light or Dark theme
- **WHEN** the bottom navigation is displayed
- **THEN** the `Reportar` icon, label, circular surface, and selected/unselected states remain legible and aligned with the existing theme tokens

### Requirement: Form publish CTA remains independent
The create pet post form SHALL retain `Publicar ficha` as its independent primary submission CTA, and the bottom navigation `Reportar` action SHALL not replace or duplicate that submission responsibility.

#### Scenario: Form retains publish CTA
- **GIVEN** a user is viewing the create pet post form
- **WHEN** the form is displayed
- **THEN** `Publicar ficha` remains inside the form as the primary submission action
- **AND** the bottom navigation central action is labeled `Reportar`

#### Scenario: Submission responsibility is unchanged
- **GIVEN** the form meets the conditions required for publication
- **WHEN** the user taps `Publicar ficha`
- **THEN** the existing publication callback executes
- **AND** changing the bottom navigation label or icon does not alter validation or publication logic
