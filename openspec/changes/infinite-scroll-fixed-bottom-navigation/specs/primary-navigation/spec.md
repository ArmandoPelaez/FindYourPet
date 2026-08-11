## ADDED Requirements

### Requirement: Fixed bottom navigation overlays scrolling content
The signed-in app SHALL keep the existing bottom navigation surface fixed relative to the viewport while the active primary destination content scrolls behind it. The surface SHALL use the existing partially transparent design-system treatment without changing its actions, colors, shapes, typography, or navigation behavior.

#### Scenario: Primary content scrolls behind the navigation surface
- **GIVEN** a signed-in user is on Home, Profile, or Chats
- **WHEN** the user scrolls the destination content toward the beginning or end
- **THEN** the bottom navigation surface remains fixed at the bottom of the viewport
- **AND** content moving behind the surface remains partially visible through its existing transparency

#### Scenario: Navigation surface remains usable in both themes
- **GIVEN** a signed-in user uses Light Theme or Dark Theme
- **WHEN** scrollable content passes behind the bottom navigation surface
- **THEN** the existing theme colors and contrast of the navigation actions remain usable
- **AND** no new hardcoded color or visual style is introduced

#### Scenario: Navigation surface respects the system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** a primary destination is displayed and scrolled
- **THEN** the fixed navigation surface remains above the system gesture area
- **AND** its actions remain tappable
