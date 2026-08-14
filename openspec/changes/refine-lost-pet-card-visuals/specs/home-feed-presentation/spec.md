## MODIFIED Requirements

### Requirement: Lost-pet alert card hierarchy
The home feed SHALL present each pet post as a single lost-pet alert card with a clear information hierarchy: a contained rounded photo frame, status, pet name, titleless last-seen location, user-reported information when retained by the card, post date, and actions. The photo frame MUST use the shared `AppSpacing.cardImageAspectRatio` and a shared `AppShapes` shape, and MUST NOT display removed breed, color, or signs summary components on the main feed.

#### Scenario: User views a pet post in the home feed
- **GIVEN** a signed-in user is on the home screen
- **WHEN** a pet post is displayed
- **THEN** the card shows the pet photo first inside a contained frame with the shared aspect ratio and rounded shape
- **THEN** the card shows the pet name and a row containing only a location icon plus the post's last-seen location text
- **THEN** the card keeps the existing status, date, and action controls available according to current rules
- **THEN** the card does not show a breed chip, `Color` attribute block, or `Señas` attribute block

#### Scenario: Existing photo source remains unchanged
- **GIVEN** a pet post has a `photoUri`
- **WHEN** the home alert card renders the image area
- **THEN** the image is loaded from that post's existing `photoUri` using the current image-loading path and cropped into the shared frame
- **THEN** the reference image is not bundled, copied, referenced, or used as fallback content

#### Scenario: Long information remains readable
- **GIVEN** a pet post has long retained reported information or a long last-seen-location value
- **WHEN** the card is displayed on a compact phone width or with a larger font scale
- **THEN** text wraps or truncates intentionally without overlapping the photo, status label, identity content, or action controls

### Requirement: Status and identity presentation
The home alert card SHALL make the pet status and identity prominent without hiding the pet photo or duplicating unrelated controls. The status label SHALL be overlaid at the top-left of the rounded photo frame using `PetStatusColors`. The pet identity area SHALL show the pet name as the primary text and SHALL place the titleless last-seen location directly below it.

#### Scenario: Status is available
- **GIVEN** a pet post has a lost, sighted, reunited, or unknown status
- **WHEN** the image area is displayed
- **THEN** a readable status pill is shown over the top-left of the image frame using the semantic status colors
- **THEN** the status pill does not cover the pet's main visual subject more than the existing tokenized overlay allows

#### Scenario: Identity is displayed
- **GIVEN** a pet post has a name and last-seen location
- **WHEN** the information area is displayed
- **THEN** the pet name is the most prominent text
- **THEN** the last-seen location appears directly below the pet name as an icon plus location text only
- **THEN** no breed chip or other classification chip is displayed beside the pet name

### Requirement: In-card action controls
The home alert card SHALL keep the sighting action as the primary in-card control and SHALL present sharing as a secondary control only with privacy-safe content that does not reintroduce removed breed, color, or signs presentation. When available, the sighting action SHALL use the label `He visto a esta mascota`, an actionable compact chip container, and the theme's orange/primary container color with readable contrasting content.

#### Scenario: User can report a sighting
- **GIVEN** a post is not reunited and the current user is allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the primary action shows `He visto a esta mascota`
- **THEN** the action remains compact and its icon/content use theme tokens with sufficient contrast
- **THEN** tapping it preserves the existing sighting alert flow and eligibility rules

#### Scenario: Sighting action semantics are updated
- **GIVEN** the sighting action is visible for a post
- **WHEN** accessibility semantics or UI text are queried
- **THEN** the visible label and content description identify the action as `He visto a esta mascota`
- **THEN** no user-facing `Lo vi` label remains for this action

#### Scenario: User cannot report a sighting
- **GIVEN** the post is reunited or the current user is not allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the sighting action is not shown as an available action

#### Scenario: User shares a post
- **GIVEN** the secondary share control is available
- **WHEN** the user taps it
- **THEN** the app opens the platform share flow with a summary that excludes owner phone, owner email, exact coordinates, private messages, hidden contact data, and the removed breed, color, or signs presentation lines
