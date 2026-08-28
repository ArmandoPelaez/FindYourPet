## ADDED Requirements

### Requirement: Login uses the new local decorative background

The Login screen SHALL render `imagen_nuevo_fondo_pantalla.png` as its local decorative background and SHALL keep that image behind the existing login content.

#### Scenario: New background is rendered on login

- **WHEN** an unauthenticated user opens the Login screen
- **THEN** the screen renders the new local background resource
- **AND** the background covers the Login screen using the existing responsive image behavior
- **AND** no other screen is required to use the new resource

#### Scenario: Background remains behind the login composition

- **WHEN** the Login screen is rendered
- **THEN** the background image is placed below the existing legibility layer and interactive content
- **AND** the image does not replace, reorder, or remove the identity, hero, form, Google action, account toggle, or messages

### Requirement: Login controls remain accessible above the background

The Login screen SHALL keep all existing interactive controls available above the decorative background without allowing the image or its layers to intercept input.

#### Scenario: User interacts with email and password fields

- **WHEN** the user taps, focuses, edits, or submits the email and password fields
- **THEN** the fields remain reachable and operable exactly as before
- **AND** focus, validation, password visibility, keyboard actions, and field semantics remain unchanged

#### Scenario: User interacts with authentication actions

- **WHEN** the user taps Entrar, Continuar con Google, Crear una cuenta, or Ya tengo cuenta
- **THEN** the existing callback and authentication state transition are invoked
- **AND** the background change does not disable, cover, or intercept the action

### Requirement: Login presentation adapts to supported window sizes and themes

The Login screen SHALL preserve responsive layout behavior for compact and large phones, tablets, reduced heights, and the software keyboard, while supporting Light Theme and Dark Theme.

#### Scenario: Compact height or keyboard is visible

- **WHEN** the available Login viewport is reduced or the keyboard opens
- **THEN** the existing safe-drawing, IME insets, and vertical scrolling keep the authentication content reachable
- **AND** no control is permanently hidden by the background or by a new fixed-position layer

#### Scenario: Phone or tablet window is rendered

- **WHEN** the Login screen is displayed on a phone or tablet with a different window width
- **THEN** the background scales responsively without distortion
- **AND** the login content retains its existing tokenized width, spacing, alignment, and usable touch targets

#### Scenario: Light or Dark Theme is active

- **WHEN** the Login screen is rendered in Light Theme or Dark Theme
- **THEN** the existing theme-based overlay and content colors remain applied
- **AND** text, fields, buttons, and feedback messages remain legible and interactive

### Requirement: Background change does not alter authentication logic

The Login screen SHALL preserve the existing authentication contracts and behavior while changing only its decorative background presentation.

#### Scenario: Authentication state changes while the new background is visible

- **WHEN** email/password or Google authentication enters loading, error, success, sign-up, or cancellation state
- **THEN** the existing state-specific controls and messages are rendered as before
- **AND** no ViewModel, repository, Firebase, navigation, validation, permission, or data behavior changes because of the background
