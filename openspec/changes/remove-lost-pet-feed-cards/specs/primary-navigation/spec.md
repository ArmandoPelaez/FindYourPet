## MODIFIED Requirements

### Requirement: Home content remains unobstructed
The home screen SHALL let the feed occupy the visual area behind the floating bottom banner while reserving enough tokenized bottom inset for feed content, empty states, and actionable content to be fully readable and tappable when scrolled to the end.

#### Scenario: Empty state remains readable
- **GIVEN** the home feed has no pet posts
- **WHEN** the empty state is displayed with the bottom banner
- **THEN** the empty state text remains readable and is not permanently covered by the banner or system gesture area

#### Scenario: Feed content remains tappable
- **GIVEN** the home feed contains pet posts
- **WHEN** the user scrolls or swipes through the feed
- **THEN** the feed content may pass visually behind the banner during scrolling
- **THEN** the final publication information and in-content actions can be brought completely above the banner and remain tappable
