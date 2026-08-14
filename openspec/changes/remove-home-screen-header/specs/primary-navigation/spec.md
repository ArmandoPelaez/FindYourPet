## MODIFIED Requirements

### Requirement: Header action relocation
The home screen SHALL not render a top app bar or duplicate header actions. Existing primary navigation actions, including notifications/alerts, SHALL remain available through the existing Bottom Navigation without changing its destinations, order, or behavior.

#### Scenario: Home has no top app bar
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the home feed is displayed
- **THEN** no top app bar, branding header, title, or subtitle is rendered above the feed

#### Scenario: Existing navigation remains available
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user uses the existing Bottom Navigation
- **THEN** its current destinations and callbacks remain available and unchanged
- **THEN** the user can reach the existing notifications/alerts destination without requiring the removed header
