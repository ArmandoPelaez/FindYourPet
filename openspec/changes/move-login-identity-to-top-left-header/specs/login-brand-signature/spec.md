## ADDED Requirements

### Requirement: Compact Top-Left Brand Signature

The Login screen SHALL present the existing FindYourPet brand mark and name as a compact horizontal signature aligned to the top-left of the content area.

#### Scenario: User opens the Login screen

- **WHEN** the Login screen is displayed
- **THEN** the existing brand mark and the text `FindYourPet` appear side by side near the top-left of the content area
- **AND** the previous large centered circular avatar composition is absent

#### Scenario: Brand signature is rendered over the Login background

- **WHEN** the brand signature is displayed
- **THEN** it is rendered directly over the existing continuous background without a card, surface, border, shadow or equivalent container

### Requirement: Brand Hierarchy

The Login screen SHALL give the headline greater visual prominence than the brand signature while preserving the existing contextual header content.

#### Scenario: User compares brand and headline

- **WHEN** the Login screen shows the brand signature, headline and supporting text
- **THEN** the brand signature uses an existing lower-emphasis typography style and the headline remains the dominant visual element
- **AND** the headline and supporting text retain their existing content and centered hierarchy

#### Scenario: Brand uses existing resources and tokens

- **WHEN** the implementation is inspected
- **THEN** it reuses the existing transparent brand resource and Design System tokens for size, spacing, typography and theme colors
- **AND** it introduces no new logo asset, arbitrary `dp`/`sp`, direct screen color, shape, elevation or opacity

### Requirement: Responsive Accessible Login Identity

The Login brand signature SHALL remain visible, legible and non-interactive across supported themes and screen sizes without changing authentication behavior.

#### Scenario: Small viewport or keyboard is visible

- **WHEN** the Login is displayed on a small viewport or the keyboard opens for a form field
- **THEN** the signature remains aligned to the top-left, does not overlap the hero or form, and the scrollable content remains usable

#### Scenario: User navigates the authentication form

- **WHEN** a user navigates the Login with touch, keyboard or TalkBack
- **THEN** the brand mark is decorative and non-focusable, and the existing focus order, labels, touch targets and authentication actions remain unchanged
