## ADDED Requirements

### Requirement: Consistent Sighting Alert Form Presentation

The sighting alert screen SHALL use the established create-post visual language for hierarchy, section spacing, field shapes, media surfaces, feedback states and primary action presentation while preserving its alert-specific functionality and adaptive layout.

#### Scenario: Non-owner views the aligned alert form

- **GIVEN** a signed-in user who is not the owner opens an eligible sighting alert form
- **WHEN** the form is rendered
- **THEN** its visible sections, spacing rhythm, Material 3 field treatment and photo surface are consistent with the create-post screen
- **AND** the form retains alert-specific location, optional photo and notes controls

#### Scenario: Alert keeps its adaptive layout

- **GIVEN** the alert form is rendered on a compact phone or an expanded supported window
- **WHEN** the available width or height changes
- **THEN** the screen preserves its existing adaptive layout, scrolling and bottom action accessibility
- **AND** the visual tokens and component hierarchy remain consistent across those layouts

#### Scenario: Alert action keeps its semantics

- **GIVEN** the alert form has a valid location state and the existing submission rules allow sending
- **WHEN** the user views the primary action
- **THEN** the action remains an alert submission action with its existing enabled, loading, success and error states
- **AND** visual alignment does not change the validation, idempotency or backend fan-out behavior

#### Scenario: Alert preserves location and media behavior

- **GIVEN** the reporter enters a manual location, captures device location or attaches optional real photo evidence
- **WHEN** the form updates its state
- **THEN** the existing location source, media source and permission behavior remain unchanged
- **AND** the controls use Design System tokens without introducing a new permission, data source or hardcoded visual value

#### Scenario: Sighting photo selection matches create-post flow

- **GIVEN** the reporter opens the optional photo surface in the sighting form
- **WHEN** the reporter taps the surface
- **THEN** the app presents the same gallery/camera selection interaction used by the create-post form
- **AND** the empty surface does not render inline `Galeria` or `Camara` action buttons
- **AND** selecting either option continues through the existing sighting media and permission callbacks

#### Scenario: Aligned form supports both themes

- **GIVEN** the application is rendered in Light Theme or Dark Theme
- **WHEN** the create-post and sighting alert screens are displayed
- **THEN** both screens preserve readable contrast, existing semantic colors and consistent surfaces using theme tokens
