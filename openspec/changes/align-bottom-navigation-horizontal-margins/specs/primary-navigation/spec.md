## MODIFIED Requirements

### Requirement: Floating banner presentation
The bottom banner SHALL appear as one centered floating surface with symmetric horizontal margins aligned to the authenticated content container, without visible internal dividers between its navigation actions.

#### Scenario: Banner margins align with the content container
- **GIVEN** a signed-in user views the app on a supported phone or tablet width
- **WHEN** the bottom banner is rendered
- **THEN** its left and right outer edges use the same responsive horizontal inset rule as the main content container, subject to the shared maximum width token

#### Scenario: Banner remains centered and responsive
- **GIVEN** the available window width changes across compact, medium, large, or tablet layouts
- **WHEN** the bottom banner is measured
- **THEN** the surface remains centered, preserves symmetric margins, and does not use hardcoded per-screen dimensions

#### Scenario: Banner has no visible separation lines
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom banner is rendered
- **THEN** profile, plus, and chat actions appear within one continuous floating surface with no visible separator lines between them

#### Scenario: Banner respects system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** the home screen is displayed
- **THEN** the bottom banner is positioned above the system gesture area and remains fully tappable

#### Scenario: Reportar remains centered and accessible
- **GIVEN** the authenticated navigation includes the central `Reportar` action
- **WHEN** the bottom banner is rendered in Light Theme or Dark Theme
- **THEN** `Reportar` remains centered, retains its existing icon, label, content description, touch target, and vertical emphasis
