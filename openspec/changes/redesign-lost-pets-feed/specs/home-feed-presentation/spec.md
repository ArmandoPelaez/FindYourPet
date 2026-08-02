## MODIFIED Requirements

### Requirement: Lost-pet alert card hierarchy
The home feed SHALL present each pet post as a single lost-pet alert card with a clear information hierarchy: photo, status, pet name, titleless last-seen location, user-reported information when retained by the card, post date, and actions. The card MUST NOT display removed breed, color, or signs summary components on the main feed.

#### Scenario: User views a pet post in the home feed
- **GIVEN** a signed-in user is on the home screen
- **WHEN** a pet post is displayed
- **THEN** the card shows the pet photo first, followed by the pet name and a row containing only a location icon plus the post's last-seen location text
- **THEN** the card keeps the existing status, date, and action controls available according to current rules
- **THEN** the card does not show a breed chip, `Color` attribute block, or `Señas` attribute block

#### Scenario: Long information remains readable
- **GIVEN** a pet post has long retained reported information or a long last-seen-location value
- **WHEN** the card is displayed on a compact phone width
- **THEN** text wraps or truncates intentionally without overlapping other content or pushing action controls outside the tappable area

### Requirement: Status and identity presentation
The home alert card SHALL make the pet status and identity prominent without hiding the pet photo or duplicating unrelated controls. The pet identity area SHALL show the pet name as the primary text and SHALL place the titleless last-seen location directly below it.

#### Scenario: Status is available
- **GIVEN** a pet post has a lost, sighted, or reunited status
- **WHEN** the image area is displayed
- **THEN** a readable status pill is shown over or near the image area without covering the pet's main visual subject

#### Scenario: Identity is displayed
- **GIVEN** a pet post has a name and last-seen location
- **WHEN** the information area is displayed
- **THEN** the pet name is the most prominent text
- **THEN** the last-seen location appears directly below the pet name as an icon plus location text only
- **THEN** no breed chip or other classification chip is displayed beside the pet name

### Requirement: Attribute sections use available post data
The home alert card SHALL only present post facts approved for the redesigned home feed and MUST NOT invent values for attributes that are not present in the current post data. The home alert card MUST NOT display breed, color, or signs as standalone summary components in the main feed.

#### Scenario: Existing removed attributes are available
- **GIVEN** a pet post has species, color, breed, and characteristics values
- **WHEN** the home feed card is displayed
- **THEN** no compact chip or attribute block displays the breed value
- **THEN** no attribute block labeled `Color` is displayed
- **THEN** no attribute block labeled `Señas` is displayed
- **THEN** no replacement placeholder, generated classification, or hardcoded value is displayed for those removed elements

#### Scenario: Optional attributes are absent
- **GIVEN** the current post data does not include an attribute shown in a mockup or older card design
- **WHEN** the card is displayed
- **THEN** the UI omits that attribute instead of showing placeholder, fake, default, or hardcoded values

#### Scenario: Deleted presentation references are removed from affected home feed text
- **GIVEN** home feed UI text, home feed share text, tests, specs, and docs affected by this change are reviewed
- **WHEN** they describe visible home feed card content
- **THEN** they do not present the removed breed chip, `Color` attribute block, or `Señas` attribute block as part of the home feed card

### Requirement: In-card action controls
The home alert card SHALL keep the sighting action as the primary in-card control and SHALL present sharing as a secondary control only with privacy-safe content that does not reintroduce removed breed, color, or signs presentation.

#### Scenario: User can report a sighting
- **GIVEN** a post is not reunited and the current user is allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the "Lo he visto" action is visually primary and navigates to the existing sighting alert flow

#### Scenario: User cannot report a sighting
- **GIVEN** the post is reunited or the current user is not allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the primary sighting action is not shown as an available action

#### Scenario: User shares a post
- **GIVEN** the secondary share control is available
- **WHEN** the user taps it
- **THEN** the app opens the platform share flow with a summary that excludes owner phone, owner email, exact coordinates, private messages, hidden contact data, and the removed breed, color, or signs presentation lines
