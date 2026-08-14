## MODIFIED Requirements

### Requirement: Home header and bottom spacing remain usable
The home feed SHALL omit the previous visual header and SHALL preserve enough safe-area and bottom spacing for alert cards and actions to remain readable with the bottom action surface or system gesture navigation.

#### Scenario: Header is removed
- **GIVEN** the user is on the home screen
- **WHEN** the home feed is displayed
- **THEN** the previous logo, title, subtitle, and header container are not rendered
- **THEN** the first feed content begins after the system safe area with the spacing defined by the existing Design System tokens

#### Scenario: Status Bar remains integrated
- **GIVEN** the user is on the home screen
- **WHEN** the system Status Bar is visible
- **THEN** it remains visible and uses the Home surface/theme treatment without introducing a separate header background

#### Scenario: Bottom controls are present
- **GIVEN** the home screen includes a floating bottom action surface or Android gesture navigation area
- **WHEN** the user scrolls or swipes through pet cards
- **THEN** card content and in-card actions remain visible and tappable above the bottom obstruction
