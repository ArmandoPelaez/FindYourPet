## MODIFIED Requirements

### Requirement: Home primary actions banner
The authenticated home shell SHALL present a floating bottom banner containing exactly five primary destinations in this order: home on the left, profile, report/create in the center, Activity, and alerts on the right.

#### Scenario: Authenticated user views home
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the home content is displayed
- **THEN** the bottom banner shows `Inicio`, `Perfil`, `Reportar`, `Actividad`, and `Alertas` in that order

#### Scenario: Report action is centered
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user looks at the floating bottom banner
- **THEN** the report/create action is visually centered and retains its existing filled treatment

### Requirement: Primary action navigation
Each bottom banner destination SHALL preserve the existing destination for its corresponding feature, except that the fourth destination SHALL navigate to Activity instead of the legacy chat list.

#### Scenario: Home action
- **GIVEN** a signed-in user is viewing any authenticated screen
- **WHEN** the user taps `Inicio`
- **THEN** the app navigates to the home screen

#### Scenario: Profile action
- **GIVEN** a signed-in user is viewing any authenticated screen
- **WHEN** the user taps `Perfil`
- **THEN** the app navigates to the profile screen

#### Scenario: Report action
- **GIVEN** a signed-in user is viewing any authenticated screen
- **WHEN** the user taps `Reportar`
- **THEN** the app opens the existing create/report flow

#### Scenario: Activity action
- **GIVEN** a signed-in user is viewing any authenticated screen
- **WHEN** the user taps `Actividad`
- **THEN** the app navigates to the Activity screen
- **AND** the app does not use the Chat list as the Activity destination

#### Scenario: Alerts action
- **GIVEN** a signed-in user is viewing any authenticated screen
- **WHEN** the user taps `Alertas`
- **THEN** the app navigates to the notifications screen

### Requirement: Header action relocation
The home top app bar SHALL keep branding and notifications, and SHALL NOT duplicate profile, create-post, Activity, or alerts actions that are owned by the bottom banner.

#### Scenario: Header is simplified
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the top app bar is displayed
- **THEN** it shows the app branding and notifications action without profile, create-post, Activity, or alerts actions

#### Scenario: Notifications remain available
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the notification action in the top app bar
- **THEN** the app navigates to the notifications screen

### Requirement: Floating banner presentation
The bottom banner SHALL appear as one floating surface without visible internal dividers between its five destinations.

#### Scenario: Banner has no visible separation lines
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom banner is rendered
- **THEN** `Inicio`, `Perfil`, `Reportar`, `Actividad`, and `Alertas` appear within one continuous floating surface with no visible separator lines between them

#### Scenario: Banner respects system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** the home screen is displayed
- **THEN** the bottom banner is positioned above the system gesture area

## ADDED Requirements

None.
