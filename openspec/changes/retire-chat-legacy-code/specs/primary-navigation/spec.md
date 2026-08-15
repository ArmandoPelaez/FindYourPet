## MODIFIED Requirements

### Requirement: Home primary actions banner

The authenticated navigation shell SHALL present the existing tokenized primary actions for Inicio, Perfil, Reportar, Actividad and Alertas, with no Chat/Mensajes destination.

#### Scenario: Authenticated user views primary navigation

- **GIVEN** a signed-in user is on the home screen
- **WHEN** the navigation shell is displayed
- **THEN** it exposes exactly Inicio, Perfil, Reportar, Actividad and Alertas
- **AND** it does not expose a chat icon or Mensajes destination

### Requirement: Primary action navigation

Each retained navigation action SHALL preserve its existing destination, and no action SHALL navigate to a Chat route.

#### Scenario: Activity action

- **GIVEN** a signed-in user taps Actividad
- **WHEN** the route is resolved
- **THEN** the app opens Activity and can navigate to Sighting Detail by `sightingId`

#### Scenario: Chat route is unavailable

- **GIVEN** an old deep link or callback contains `chatId`
- **WHEN** the navigation layer receives it
- **THEN** it does not create a Chat destination
- **AND** it handles the invalid destination without crashing
