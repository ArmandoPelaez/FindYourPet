## ADDED Requirements

### Requirement: Authentication background is decorative only

The Login and Crear cuenta screens SHALL render a stable local background layer containing only visual/decorative graphics. The background SHALL NOT contain readable identity or logo content, slogan, hero, descriptive text, or other semantic authentication content.

#### Scenario: Authentication background is displayed

- **WHEN** an unauthenticated user opens Login or Crear cuenta
- **THEN** the background shows only the existing decorative visual treatment
- **AND** the semantic identity, slogan, hero, and descriptive content are rendered outside the background layer

#### Scenario: Authentication content changes while the screen is open

- **WHEN** the authentication content scrolls or the user changes between Login and Crear cuenta
- **THEN** the decorative background remains visually stable
- **AND** no semantic background element moves independently over the form

### Requirement: Authentication hero is adaptive interface content

The authentication screen SHALL render the existing branding, slogan, hero, and descriptive texts as ordered interface content before the authentication form, within the same adaptive content flow and with accessible semantics.

#### Scenario: Login displays the hero and form

- **WHEN** the Login mode is rendered with sufficient available space
- **THEN** the branding, slogan, hero, and descriptive texts appear as a grouped hero
- **AND** the form heading, fields, and authentication actions appear after the hero
- **AND** the existing visual identity and hierarchy are preserved

#### Scenario: Crear cuenta displays the shared authentication composition

- **WHEN** the user switches to Crear cuenta
- **THEN** the shared hero and the sign-up form remain part of the same adaptive content flow
- **AND** the existing sign-up fields, actions, and navigation behavior remain available

### Requirement: Reduced height keeps authentication usable

The authentication content SHALL adapt to reduced available height by reorganizing and/or scrolling as needed. The focused field and actions required to complete Login or Crear cuenta SHALL remain reachable, and hero, texts, fields, and buttons SHALL NOT overlap.

#### Scenario: Keyboard opens while editing a field

- **WHEN** the user focuses Email, Contraseña, or another sign-up field and the keyboard opens
- **THEN** the content uses the existing IME-aware scroll behavior
- **AND** the focused field remains visible or reachable
- **AND** the required authentication actions remain reachable without overlapping the hero or descriptive content

#### Scenario: Device has less available height

- **WHEN** Login or Crear cuenta is displayed on a device with insufficient height for the complete composition
- **THEN** the content remains usable through the existing vertical scrolling behavior
- **AND** the form takes priority over promotional hero content
- **AND** no layout decision depends on a specific device model or fixed coordinate

#### Scenario: Keyboard closes after a compact layout

- **WHEN** the user closes the keyboard
- **THEN** the screen restores its normal composition without leaving content overlapped, hidden, or permanently displaced

### Requirement: Authentication behavior remains unchanged

The presentation refactor SHALL preserve the existing authentication callbacks, field semantics, focus order, loading/error/success feedback, theme-aware rendering, and navigation behavior.

#### Scenario: User completes an existing authentication action

- **WHEN** the user submits email/password credentials, selects Continuar con Google, or switches between Login and Crear cuenta
- **THEN** the existing callbacks and navigation behavior are invoked without changes to Firebase Auth or Google Sign-In logic

#### Scenario: Authentication operation reports a state

- **WHEN** authentication is loading, succeeds, fails, or is unavailable due to configuration
- **THEN** the existing feedback and action-enabled/disabled behavior remain available and reachable in the adaptive content flow
- **AND** the behavior remains legible in Light Theme and Dark Theme
