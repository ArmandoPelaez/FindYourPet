## ADDED Requirements

### Requirement: Continuous Login Composition

The Login screen SHALL render its existing hero and authentication content over the continuous screen background without a large upper card or equivalent surface enclosing the hero.

#### Scenario: Hero is rendered without an upper card

- **WHEN** the Login screen is displayed
- **THEN** the FindYourPet identity, headline, supporting text and form heading remain visible directly over the existing background, with no upper card surface, border, shadow or elevation grouping them

#### Scenario: Hero and form share the background

- **WHEN** a user views the Login screen in Light Theme or Dark Theme
- **THEN** the hero and authentication form appear as one continuous composition using existing theme colors and background layers

### Requirement: Preserved Authentication Presentation

The Login screen SHALL preserve the existing authentication controls and their visual hierarchy while removing only the upper card container.

#### Scenario: Authentication controls remain available

- **WHEN** the Login screen is displayed
- **THEN** Email, Contraseña, Entrar, Continuar con Google and Crear una cuenta remain available with their existing labels, actions and loading/error states

#### Scenario: Hero and form remain separated by layout

- **WHEN** the Login screen is displayed on a small or large viewport
- **THEN** spacing and typography maintain a clear separation between hero and form without introducing a replacement card or hardcoded visual values

### Requirement: Accessible Responsive Composition

The Login screen SHALL remain accessible and usable after the card removal.

#### Scenario: Keyboard is open on a small viewport

- **WHEN** a user focuses Email or Contraseña and the software keyboard opens
- **THEN** the content remains scrollable, the hero does not overlap the form, and the focused field and actions remain reachable

#### Scenario: Existing interaction semantics are used

- **WHEN** a user navigates the Login with touch, keyboard or TalkBack
- **THEN** focus order, labels, touch targets, contrast and authentication interactions remain available without new interactive wrappers
