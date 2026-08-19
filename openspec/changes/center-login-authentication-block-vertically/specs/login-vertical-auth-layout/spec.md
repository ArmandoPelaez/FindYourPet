## ADDED Requirements

### Requirement: Identity header remains fixed while hero moves independently

The Login screen SHALL keep the IdentityHeader (`FindYourPet` mark/name) at its current vertical coordinate and position the Hero region (headline plus supporting text) independently downward until it reaches the marked composition in the reference.

#### Scenario: Header remains fixed while Hero follows its target

- **WHEN** the Login screen is rendered with the SCRUM-43 composition
- **THEN** the IdentityHeader keeps its current vertical coordinate
- **AND** the Hero headline and supporting text reach their marked lower composition
- **AND** changing the Hero position does not move the IdentityHeader implicitly

#### Scenario: Hero content remains unchanged

- **WHEN** the Hero is repositioned
- **THEN** the headline and supporting text retain their existing order, typography, text, alignment, and internal spacing
- **AND** no new Hero controls or text are introduced

### Requirement: Hero and authentication label follow reference text alignment

The Login screen SHALL use the reference only to align and format the Hero headline/supporting text and to align the visible authentication label with the form fields, without copying reference-only assets or changing region coordinates.

#### Scenario: Hero text is aligned and formatted

- **WHEN** the Login screen is rendered
- **THEN** the Hero headline and supporting text use existing typography tokens corresponding to the reference hierarchy
- **AND** both texts are start-aligned within the Hero content width
- **AND** their order, wording, and internal spacing remain unchanged

#### Scenario: Authentication label is aligned

- **WHEN** the login form is rendered
- **THEN** the `Iniciar sesiÃ³n` label is start-aligned with the authentication fields
- **AND** its vertical coordinate, typography token, and the coordinates/spacing of the controls remain unchanged
- **AND** the button content and authentication behavior are not changed by this alignment

### Requirement: Authentication block remains fixed

The Login screen SHALL preserve the current vertical coordinate, distribution, spacing, controls, and behavior of the AuthenticationBlock: `Iniciar sesión`, Email, Contraseña, Entrar, the divider, Continuar con Google, and Crear una cuenta.

#### Scenario: Authentication position is unchanged

- **WHEN** IdentityHeader or Hero is repositioned
- **THEN** the AuthenticationBlock remains at its current normal-height vertical coordinate
- **AND** its internal order, spacing, widths, styles, callbacks, validation, loading, errors, focus, semantics, and navigation remain unchanged

#### Scenario: Short viewport or IME

- **WHEN** the available viewport is reduced or the keyboard opens
- **THEN** the existing `verticalScroll()` and `imePadding()` behavior remains available
- **AND** the AuthenticationBlock remains reachable without changing its normal-height position

### Requirement: No shared-parent displacement

The IdentityHeader and Hero adjustment SHALL NOT use padding, spacer, or offset on a shared parent when that value changes the AuthenticationBlock position or couples the three regions.

#### Scenario: Layout boundaries are independent

- **WHEN** the implementation is inspected or rendered
- **THEN** IdentityHeader, Hero, and AuthenticationBlock have independent positioning boundaries
- **AND** only the Hero boundary receives the requested downward displacement
- **AND** no shared-parent displacement moves AuthenticationBlock as a side effect
- **AND** no device-specific hardcoded vertical value is introduced

#### Scenario: Existing identity and behavior are preserved

- **WHEN** the revised Login screen is rendered in Light Theme or Dark Theme and the user interacts with its controls
- **THEN** the existing background, identity, colors, typography, shapes, widths, authentication methods, and behavior remain unchanged
- **AND** no reference-only controls or texts are introduced
