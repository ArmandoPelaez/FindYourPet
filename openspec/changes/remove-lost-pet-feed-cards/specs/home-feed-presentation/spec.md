## MODIFIED Requirements

### Requirement: Lost-pet alert card hierarchy
The home feed SHALL present each pet post as continuous lost-pet alert content integrated with the feed surface, without an outer floating card container, while preserving a clear information hierarchy: photo, status, pet name, titleless last-seen location, user-reported information when retained by the content, post date, and actions. The presentation MUST NOT display removed breed, color, or signs summary components on the main feed.

#### Scenario: User views a pet post in the home feed
- **GIVEN** a signed-in user is on the home screen
- **WHEN** a pet post is displayed
- **THEN** the publication content is rendered on the continuous feed surface without an outer margin, rounded card shape, or outer elevation/shadow
- **THEN** the content keeps the pet photo first, followed by the pet name and a row containing only a location icon plus the post's last-seen location text
- **THEN** the content keeps the existing status, date, and action controls available according to current rules
- **THEN** the content does not show a breed chip, `Color` attribute block, or `Señas` attribute block

#### Scenario: Long information remains readable
- **GIVEN** a pet post has long retained reported information or a long last-seen-location value
- **WHEN** the continuous publication content is displayed on a compact phone width
- **THEN** text wraps or truncates intentionally without overlapping other content or pushing action controls outside the tappable area

### Requirement: Home header and bottom spacing remain usable
The redesigned home feed SHALL preserve a clear header and enough tokenized bottom space for publication content and actions to remain readable with the bottom action surface or system gesture navigation, while allowing the scrollable feed content to pass visually behind the bottom action surface.

#### Scenario: Header is displayed
- **GIVEN** the user is on the home screen
- **WHEN** the header is displayed
- **THEN** the app brand, subtitle, and notifications action remain visible without crowding the publication content

#### Scenario: Feed content scrolls behind the bottom surface
- **GIVEN** the home screen includes the bottom action surface or Android gesture navigation area
- **WHEN** the user scrolls or swipes through pet publications
- **THEN** publication content can continue visually beneath the bottom action surface instead of ending at an externally separated card boundary
- **THEN** the final information and action controls can be scrolled completely into a visible and tappable position above the obstruction
