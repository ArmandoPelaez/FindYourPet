## MODIFIED Requirements

### Requirement: Home primary actions banner
The home screen SHALL present a floating bottom banner containing exactly five primary navigation actions: home, profile, report, activity, and notifications, in that order from left to right. All five actions SHALL occupy the same-height navigation item structure.

#### Scenario: Authenticated user views home
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the home content is displayed
- **THEN** the bottom banner shows `Inicio`, `Perfil`, `Reportar`, `Actividad`, and `Alertas` as five evenly distributed navigation items

#### Scenario: Report action is centered
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user looks at the bottom navigation banner
- **THEN** the `Reportar` action is visually centered between `Perfil` and `Actividad` and uses the existing paw icon

### Requirement: Floating banner presentation
The bottom banner SHALL appear as one floating surface without visible internal dividers, central notch, cutout, or arc around the report action.

#### Scenario: Banner has no visible separation lines
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom banner is rendered
- **THEN** `Inicio`, `Perfil`, `Reportar`, `Actividad`, and `Alertas` appear within one continuous floating surface without visible internal separator lines

#### Scenario: Banner respects system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** the home screen is displayed
- **THEN** the bottom banner is positioned above the system gesture area

## ADDED Requirements

### Requirement: Report action shares navigation structure
The `Reportar` action SHALL use the same vertical structure as the other four navigation items: a shared icon slot, the existing label gap, and a label aligned to the same baseline. It SHALL not use a floating-action-button well, vertical elevation offset, or dedicated elevated layout.

#### Scenario: Report action is aligned with peer icons
- **GIVEN** the bottom navigation banner is rendered
- **WHEN** the five navigation items are displayed
- **THEN** the center of the `Reportar` icon aligns with the centers of `Inicio`, `Perfil`, `Actividad`, and `Alertas`

#### Scenario: Report label shares the baseline
- **GIVEN** the bottom navigation banner is rendered
- **WHEN** the five labels are displayed
- **THEN** `Reportar` shares the same label baseline as `Inicio`, `Perfil`, `Actividad`, and `Alertas`

#### Scenario: Report action keeps its visual emphasis
- **GIVEN** the bottom navigation banner is rendered in either light or dark theme
- **WHEN** the `Reportar` action is displayed
- **THEN** it shows a `40.dp` circular `primary` background, a `22.dp` paw icon using `onPrimary`, and the label `Reportar`

#### Scenario: Report action remains accessible
- **GIVEN** the `Reportar` action is rendered
- **WHEN** a user targets the action
- **THEN** its interactive area is at least `48.dp` by `48.dp` even though the visible circular background is `40.dp`

### Requirement: Primary action navigation
Each bottom banner action SHALL preserve the existing destination for its corresponding feature.

#### Scenario: Home action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the `Inicio` action
- **THEN** the app navigates to the home screen

#### Scenario: Profile action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the `Perfil` action
- **THEN** the app navigates to the profile screen

#### Scenario: Report action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the `Reportar` action
- **THEN** the app navigates to the create pet post screen

#### Scenario: Activity action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the `Actividad` action
- **THEN** the app navigates to the activity screen

#### Scenario: Notifications action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the `Alertas` action
- **THEN** the app navigates to the notifications screen
