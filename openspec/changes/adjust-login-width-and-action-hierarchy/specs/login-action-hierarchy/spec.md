## ADDED Requirements

### Requirement: Login content uses one contained column

The Login screen SHALL render the headline, supporting text, form heading, Email, Contraseña, Entrar, Continuar con Google, and Crear una cuenta within one shared horizontal content column using existing Design System width and spacing tokens.

#### Scenario: Wide screen content remains contained

- **WHEN** the Login screen is rendered on a screen wider than the form content area
- **THEN** the main content does not expand indefinitely
- **AND** headline, supporting text, fields, and actions share the same horizontal alignment and content width

#### Scenario: Small screen content remains usable

- **WHEN** the Login screen is rendered on a small device
- **THEN** the shared column preserves tokenized lateral margins without horizontal overflow
- **AND** Email, Contraseña, and all authentication actions remain reachable

#### Scenario: No arbitrary visual dimensions are introduced

- **WHEN** the content column and its children are laid out
- **THEN** they use existing `AppSpacing`, theme, and reusable component tokens
- **AND** no hardcoded width, `maxWidth`, margin, padding, color, opacity, size, shape, elevation, or typography value is introduced

### Requirement: Login vertical rhythm follows the approved reference intent

The Login screen SHALL use the provided reference only as guidance for vertical distribution: the hero SHALL sit higher and remain compact, the spaces between hero, form heading, fields, and actions SHALL be reduced coherently, the existing controls SHALL read as a centered group, and excessive bottom space SHALL be avoided.

#### Scenario: Hero and form use compact vertical rhythm

- **WHEN** the Login screen is rendered with its existing identity, hero, form, and actions
- **THEN** the hero begins higher in the scrollable content and does not consume unnecessary vertical space
- **AND** the form heading, fields, and actions are grouped with the shorter tokenized rhythm defined by the existing Design System

#### Scenario: Existing controls remain centered without new content

- **WHEN** the vertical composition is adjusted using the reference intent
- **THEN** the existing fields and authentication actions remain visually grouped in the center of the main column
- **AND** the implementation does not add `Recordarme`, password recovery, explanatory copy, or any other control/text present only in the reference image

#### Scenario: Compact rhythm remains responsive

- **WHEN** the screen is small or the keyboard opens
- **THEN** the compact spacing does not cause overlap or inaccessible fields/actions
- **AND** `verticalScroll` and `imePadding()` remain available to reach the complete form

### Requirement: Authentication actions expose clear visual hierarchy

The Login screen SHALL present Entrar as the primary action, Continuar con Google as a secondary action, and Crear una cuenta as a tertiary action using existing stable Design System components.

#### Scenario: Entrar is the primary CTA

- **WHEN** the Login actions are displayed
- **THEN** Entrar uses the existing primary action style and is the most visually prominent action
- **AND** its existing callback, loading state, enabled state, and content description remain unchanged

#### Scenario: Google is secondary and retains official branding

- **WHEN** Continuar con Google is displayed
- **THEN** it uses an existing secondary action style with less visual prominence than Entrar
- **AND** it retains the official Google branding asset without modification
- **AND** its existing callback and loading/error behavior remain unchanged

#### Scenario: Create account is tertiary

- **WHEN** Crear una cuenta is displayed in Login mode
- **THEN** it is presented as a tertiary text/action treatment within the lower form content
- **AND** it does not compete visually with Entrar or appear as a second primary CTA
- **AND** its existing navigation behavior remains unchanged

### Requirement: Login presentation remains accessible and responsive

The visual adjustment SHALL preserve touch targets, contrast, labels, semantics, focus order, keyboard behavior, scroll behavior, and Light/Dark Theme support.

#### Scenario: Keyboard and focus remain usable

- **WHEN** Email or Contraseña receives focus and the keyboard opens
- **THEN** the existing scroll and IME behavior keeps the focused field and actions usable
- **AND** the order of focus and field semantics remains unchanged

#### Scenario: Theme and responsive behavior remain supported

- **WHEN** the Login is rendered in Light Theme, Dark Theme, or different device widths
- **THEN** all content remains legible, aligned, and interactive without overflow or clipping
