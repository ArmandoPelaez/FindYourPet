## MODIFIED Requirements

### Requirement: Floating banner presentation
The bottom navigation SHALL appear as one full-width rectangular surface without visible internal dividers between destinations. Its separation line SHALL run continuously across the full horizontal boundary of the surface, including the area around the centered create action. The centered `Publicar` action SHALL use shared sizing tokens and a slightly smaller visual circle/icon than the current presentation while preserving an accessible touch target.

#### Scenario: Banner has continuous boundary and no internal separators
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom navigation is rendered
- **THEN** profile, plus, chat, and the other authenticated destinations appear within one continuous full-width surface
- **THEN** no visible separator lines divide individual destinations
- **THEN** the boundary/separation line is visible from the left edge to the right edge of the navigation surface instead of stopping at individual item bounds

#### Scenario: Create action uses reduced tokenized sizing
- **GIVEN** a signed-in user views the bottom navigation
- **WHEN** the centered `Publicar` action is rendered
- **THEN** its visual circle and plus icon use the shared create-action size tokens
- **THEN** those tokens produce a slightly smaller visual treatment than the current presentation while preserving the action's accessible touch target and centered alignment

#### Scenario: Banner respects system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** the home screen is displayed
- **THEN** the bottom banner is positioned above or includes the system gesture area according to the existing inset behavior
- **THEN** the continuous boundary remains visible across the complete navigation surface
