## ADDED Requirements

### Requirement: Login hero and form have distinct hierarchy

The Login screen SHALL present the FindYourPet identity, approved headline, and approved supporting text as a grouped hero area, followed by a tokenized visual separation, followed by the authentication form.

#### Scenario: Hero and form are visually distinguishable

- **WHEN** the Login screen is displayed
- **THEN** the identity, `Conectá con avisos cerca tuyo.` and `Reportá, buscá y ayudá a reencontrar mascotas.` remain grouped as the hero
- **AND** `Iniciar sesión` is separated from the hero and is visually associated with Email, Contraseña, and the authentication actions

#### Scenario: Separation uses the existing design system

- **WHEN** the hero-to-form separation is rendered
- **THEN** it uses existing `AppSpacing` and existing typography/theme tokens
- **AND** it does not introduce hardcoded visual values, a new card, divider, replacement surface, elevation, or decorative component

#### Scenario: Small screen and keyboard remain usable

- **WHEN** the Login screen is displayed on a small screen or while the keyboard is open
- **THEN** the hero and form do not overlap
- **AND** Email, Contraseña, and authentication actions remain reachable through the existing scroll and IME behavior

### Requirement: Redundant Login subtitle is absent

The Login mode SHALL show `Iniciar sesión` as a self-sufficient form heading and SHALL NOT render the redundant subtitle `Accedé para seguir avisos y actualizar tus publicaciones.` or an equivalent replacement between the heading and the form fields.

#### Scenario: Login form has no redundant explanatory subtitle

- **WHEN** the screen is in Login mode
- **THEN** the form heading is `Iniciar sesión`
- **AND** no equivalent explanatory subtitle is rendered before Email
- **AND** functional field validation and error messages remain available

#### Scenario: Sign-up navigation and behavior remain unchanged

- **WHEN** the user switches to or selects Crear una cuenta
- **THEN** the existing sign-up navigation, fields, actions, and behavior remain unchanged by this presentation change

### Requirement: Authentication behavior is preserved

The presentation change SHALL preserve the existing authentication callbacks, field semantics, focus order, and theme-aware rendering.

#### Scenario: Existing authentication actions continue to work

- **WHEN** the user enters credentials or selects Entrar, Continuar con Google, or Crear una cuenta
- **THEN** the existing callbacks and navigation behavior are invoked without changes to authentication logic

#### Scenario: Accessibility and themes remain supported

- **WHEN** the Login screen is rendered in Light Theme or Dark Theme
- **THEN** labels, semantics, contrast, focus order, and field behavior remain available and legible
