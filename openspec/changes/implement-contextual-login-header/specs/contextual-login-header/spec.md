## ADDED Requirements

### Requirement: Contextual Login Header Hierarchy
The Login screen SHALL present a contextual header with a dominant headline, a secondary supporting text, and the `FindYourPet` identity shown with less emphasis than the headline.

#### Scenario: User opens the Login screen
- **WHEN** the authentication screen is displayed in sign-in or sign-up mode
- **THEN** the header shows the headline `Conectá con avisos cerca tuyo.`
- **AND** the header shows the supporting text `Reportá, buscá y ayudá a reencontrar mascotas.`
- **AND** the `FindYourPet` identity is visible with lower visual emphasis than the headline

### Requirement: Design System Typography and Color Usage
The contextual header SHALL use the existing `AppTypography` styles and theme color tokens, without declaring arbitrary typography sizes or direct screen colors.

#### Scenario: Header implementation is reviewed
- **WHEN** the contextual header source is inspected
- **THEN** its text styles resolve through `AppTypography` or the existing Material theme typography backed by `AppTypography`
- **AND** its colors resolve through `MaterialTheme.colorScheme` or existing design tokens
- **AND** no new hardcoded `sp`, `Color(...)`, padding, or shape values are introduced in the screen

### Requirement: Responsive and Theme-Aware Header Legibility
The contextual header SHALL remain readable and visually ordered over the existing Login background in both supported themes and on small screens.

#### Scenario: Header is displayed on a small Light Theme screen
- **WHEN** the available width is small and the Light Theme is active
- **THEN** the headline and supporting text wrap within the existing responsive content bounds without horizontal clipping
- **AND** the headline remains visually dominant over the supporting text and identity

#### Scenario: Header is displayed on a small Dark Theme screen
- **WHEN** the available width is small and the Dark Theme is active
- **THEN** the headline, supporting text, and identity remain legible over the existing background
- **AND** the contextual header does not require a theme-specific hardcoded color

### Requirement: Authentication Flow Preservation
The contextual header SHALL not remove or block the existing authentication controls and state transitions.

#### Scenario: User switches between sign-in and sign-up
- **WHEN** the user toggles between sign-in and sign-up modes
- **THEN** the contextual header remains available
- **AND** the corresponding existing form fields and authentication actions remain available

#### Scenario: User submits an authentication action
- **WHEN** the user submits email/password or Google Sign-In from the Login screen
- **THEN** the existing authentication callback and error-state behavior remain unchanged
