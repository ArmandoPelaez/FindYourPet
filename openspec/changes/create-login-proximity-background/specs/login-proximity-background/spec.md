## ADDED Requirements

### Requirement: Login shows an abstract proximity background

The Login screen SHALL render a locally generated abstract visual that communicates proximity using lines, nodes, circular areas, a primary marker and discreet connections.

#### Scenario: Proximity visual is visible on Login

- **WHEN** an unauthenticated user opens the Login screen
- **THEN** the screen displays the abstract proximity visual as part of the presentation without requiring network access or external map services

#### Scenario: Visual adapts to available size

- **WHEN** the Login screen is rendered at different phone or window sizes
- **THEN** the visual scales or positions its geometry within the available bounds without depending on a fixed device resolution

### Requirement: Proximity background does not expose real geographic data

The Login visual SHALL remain decorative and SHALL NOT use Google Maps, Maps SDK, Places API, network data, real coordinates or identifiable geographic locations.

#### Scenario: Offline rendering

- **WHEN** the Login screen is rendered without an Internet connection
- **THEN** the proximity visual renders using only local resources and does not request map, place or location data

#### Scenario: No real location disclosure

- **WHEN** the visual is inspected or rendered
- **THEN** it contains no user location, pet location, street, address or other real geographic coordinate

### Requirement: Proximity background preserves Login usability and theme compatibility

The visual SHALL use the existing Design System and theme-aware colors, remain non-interactive, and preserve the readability and availability of all Login controls and messages in Light Theme and Dark Theme.

#### Scenario: Authentication content remains usable

- **WHEN** a user views or interacts with the Login form
- **THEN** email/password fields, Login, Google Sign-In, account creation and error messages remain visible, reachable and behaviorally unchanged

#### Scenario: Light Theme remains legible

- **WHEN** the Login screen is rendered in Light Theme
- **THEN** the proximity visual uses theme-aware tokenized colors and does not reduce the contrast of authentication content below a readable level

#### Scenario: Dark Theme remains legible

- **WHEN** the Login screen is rendered in Dark Theme
- **THEN** the proximity visual uses theme-aware tokenized colors and does not reduce the contrast of authentication content below a readable level

#### Scenario: Decorative layer does not intercept input

- **WHEN** the user taps or focuses a Login control over the visual area
- **THEN** the control receives the interaction normally and the decorative visual does not intercept or alter input behavior
