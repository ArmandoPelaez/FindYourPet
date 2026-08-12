## MODIFIED Requirements

### Requirement: Floating banner presentation
The bottom banner SHALL appear as one floating surface without visible internal dividers between the three actions and SHALL use a subtle non-opaque treatment through a dedicated design-system opacity token, while preserving the existing surface color, icon tint, shape, elevation, spacing, and action layout.

#### Scenario: Banner has no visible separation lines
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom banner is rendered
- **THEN** profile, plus, and chat actions appear within one continuous floating surface with no visible separator lines between them

#### Scenario: Banner uses subtle transparency
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom banner is rendered over scrolling content
- **THEN** the banner surface is not fully opaque and the content behind it contributes to a sense of continuity
- **AND** the banner keeps the existing surface color and icon tint

#### Scenario: Transparency is isolated to the bottom banner
- **GIVEN** the app renders another component that uses the existing shared banner opacity token
- **WHEN** the transparent bottom banner treatment is applied
- **THEN** the other component's opacity remains unchanged

#### Scenario: Banner remains legible in both themes
- **GIVEN** the app is rendered in Light Theme or Dark Theme
- **WHEN** the bottom banner is displayed
- **THEN** its icons remain visible with their existing tint and the surface remains visually distinct from the content behind it

#### Scenario: Banner respects system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** the home screen is displayed
- **THEN** the bottom banner is positioned above the system gesture area
