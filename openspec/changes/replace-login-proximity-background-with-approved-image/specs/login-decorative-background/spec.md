## ADDED Requirements

### Requirement: Login renders the approved decorative image background

The Login screen SHALL render the approved local asset `app/src/main/res/drawable-nodpi/imagen_fondo_pantalla_login.png` as a decorative background layer. The PNG is the permitted temporary resource while the Jira-named WEBP asset is absent.

#### Scenario: Login displays the approved background
- **WHEN** an unauthenticated user opens the Login screen
- **THEN** the screen displays the approved local bitmap behind the Login content without requiring network access or external map services

#### Scenario: No alternate background is generated
- **WHEN** the Login background implementation is inspected
- **THEN** it references the approved resource and does not recreate or replace it with Canvas drawing, a generated bitmap, Maps SDK, Places API, or a network resource

### Requirement: Decorative background preserves Login interaction and accessibility

The background SHALL remain below all functional Login content and SHALL not capture clicks, gestures, focus, text input, or accessibility focus. The asset SHALL be exposed as decorative content with no spoken description.

#### Scenario: Functional controls remain above the image
- **WHEN** a user views the Login screen
- **THEN** FindYourPet identity, headline, supporting text, Email, Contraseña, Entrar, Continuar con Google, Crear una cuenta, and error messages remain present in the functional content layer

#### Scenario: User interacts with controls over the image
- **WHEN** a user taps or focuses Email, Contraseña, Entrar, Continuar con Google, or Crear una cuenta
- **THEN** the selected control receives the interaction normally and the background does not intercept or alter it

#### Scenario: Accessibility services inspect the Login
- **WHEN** TalkBack or another accessibility service traverses the Login
- **THEN** the decorative image is omitted from announcements and focus order while all existing control semantics remain available

### Requirement: Decorative background adapts without harming legibility

The background SHALL preserve its aspect ratio, prioritize the asset's upper composition, and adapt to different Android window sizes without deforming the image, introducing unintended empty space, moving the functional form, or reducing readable contrast in Light Theme and Dark Theme.

#### Scenario: Small or tall phone displays the Login
- **WHEN** the Login is rendered on a small or tall phone, including with the keyboard open
- **THEN** the image fills the available background area responsively, the upper visual composition remains prioritized, and the form retains its existing scroll and focus behavior

#### Scenario: Larger window displays the Login
- **WHEN** the Login is rendered in a larger Android window
- **THEN** the image scales or crops proportionally without stretching, and all functional controls remain accessible and correctly positioned

#### Scenario: Light Theme remains legible
- **WHEN** the Login is rendered in Light Theme
- **THEN** the image and any separation treatment use existing theme-aware Design System tokens so labels, inputs, buttons, and messages remain readable

#### Scenario: Dark Theme remains legible
- **WHEN** the Login is rendered in Dark Theme
- **THEN** the image and any separation treatment use existing theme-aware Design System tokens so labels, inputs, buttons, and messages remain readable

### Requirement: Authentication behavior remains unchanged

Replacing the decorative background SHALL NOT modify authentication state, Firebase calls, ViewModel behavior, navigation, permissions, or domain contracts.

#### Scenario: Existing authentication actions are used
- **WHEN** a user submits Email/password, chooses Google, or navigates to account creation
- **THEN** the existing callbacks, loading/error behavior, and navigation continue to operate as before
