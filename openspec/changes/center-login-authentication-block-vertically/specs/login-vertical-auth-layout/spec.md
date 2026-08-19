## ADDED Requirements

### Requirement: Vertically balanced login authentication block

The Login screen SHALL render the authentication block consisting of the form heading, Email, Contraseña, Entrar, the authentication divider, Continuar con Google, and Crear una cuenta as one visual unit separated from the hero by flexible, responsive vertical distribution.

#### Scenario: Standard-height screen has balanced separation

- **WHEN** the Login screen is displayed on a device with sufficient vertical space
- **THEN** the authentication block is visibly separated from the supporting text and is approximately centered within the remaining space below the hero
- **AND** the internal order and spacing of the authentication controls are preserved

#### Scenario: Short screen remains accessible

- **WHEN** the available viewport height is insufficient for the hero and authentication block
- **THEN** the screen preserves the existing vertical scroll behavior
- **AND** Email, Contraseña, Entrar, Continuar con Google, and Crear una cuenta remain reachable without clipping

#### Scenario: Keyboard is open

- **WHEN** the user focuses Email or Contraseña and the IME opens
- **THEN** the screen preserves IME padding and allows the focused field and required actions to be reached by scrolling
- **AND** the flexible vertical distribution does not make authentication controls inaccessible

#### Scenario: Existing visual identity is preserved

- **WHEN** the revised Login screen is rendered in Light Theme or Dark Theme
- **THEN** it retains the existing hero, background, text, colors, typography, shapes, widths, controls, and action hierarchy
- **AND** no new authentication method, control, or reference-only text is introduced

#### Scenario: Authentication behavior is preserved

- **WHEN** the user interacts with Email, Contraseña, Entrar, Continuar con Google, or Crear una cuenta
- **THEN** the existing callbacks, validation, loading, error, focus, semantics, and navigation behavior remain unchanged

### Requirement: No device-specific vertical offsets

The Login screen SHALL achieve the vertical balance through responsive Compose layout distribution and existing design tokens, without fixed `offset(y = ...)` values, arbitrary device-specific margins, or hardcoded spacing introduced only for this change.

#### Scenario: Layout adapts across heights

- **WHEN** the same Login screen is rendered on devices with different viewport heights
- **THEN** the separation and authentication block position adapt to the available space
- **AND** no device-specific vertical correction is required
