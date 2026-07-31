## ADDED Requirements

### Requirement: Lost-pet alert card hierarchy
The home feed SHALL present each pet post as a single lost-pet alert card with a clear information hierarchy: photo, status, pet identity, attributes, reported information, lost location, date, and actions.

#### Scenario: User views a pet post in the home feed
- **GIVEN** a signed-in user is on the home screen
- **WHEN** a pet post is displayed
- **THEN** the card shows the pet photo first, followed by the pet name, breed when available, key attributes, reported information, lost location, post date, and action controls

#### Scenario: Long information remains readable
- **GIVEN** a pet post has long reported information or a long lost-location value
- **WHEN** the card is displayed on a compact phone width
- **THEN** text wraps or truncates intentionally without overlapping other content or pushing action controls outside the tappable area

### Requirement: Existing pet image source is preserved
The home feed SHALL keep using the current pet image source and loading behavior for each post, and MUST NOT use the provided reference dog image as an application asset or replacement image.

#### Scenario: Post has an existing image URI
- **GIVEN** a pet post has a `photoUri`
- **WHEN** the home alert card renders the image area
- **THEN** the image is loaded from that post's existing `photoUri` using the current image-loading path

#### Scenario: Reference image is only design guidance
- **GIVEN** the implementation uses the provided mockup as visual inspiration
- **WHEN** app assets and UI image references are reviewed
- **THEN** the example dog image is not bundled, copied, referenced, or used as fallback content

### Requirement: Status and identity presentation
The home alert card SHALL make the pet status and identity prominent without hiding the pet photo or duplicating unrelated controls.

#### Scenario: Status is available
- **GIVEN** a pet post has a lost, sighted, or reunited status
- **WHEN** the image area is displayed
- **THEN** a readable status pill is shown over or near the image area without covering the pet's main visual subject

#### Scenario: Identity is displayed
- **GIVEN** a pet post has a name and breed
- **WHEN** the information area is displayed
- **THEN** the pet name is the most prominent text and the breed is presented as supporting identity text or a compact chip

### Requirement: Attribute sections use available post data
The home alert card SHALL present available pet attributes in compact sections and MUST NOT invent values for attributes that are not present in the current post data.

#### Scenario: Existing attributes are available
- **GIVEN** a pet post has species, color, breed, and characteristics
- **WHEN** the attribute area is displayed
- **THEN** those values are visible in compact, scannable sections

#### Scenario: Optional attributes are absent
- **GIVEN** the current post data does not include an attribute shown in the reference mockup, such as age or gender
- **WHEN** the card is displayed
- **THEN** the UI omits that attribute or uses another available post field instead of showing placeholder, fake, or hardcoded values

### Requirement: In-card action controls
The home alert card SHALL keep the sighting action as the primary in-card control and SHALL present sharing as a secondary control only with privacy-safe content.

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
- **THEN** the app opens the platform share flow with a summary that excludes owner phone, owner email, exact coordinates, private messages, and hidden contact data

### Requirement: Home header and bottom spacing remain usable
The redesigned home feed SHALL preserve a clear header and enough bottom spacing so alert cards and actions remain readable with the bottom action surface or system gesture navigation.

#### Scenario: Header is displayed
- **GIVEN** the user is on the home screen
- **WHEN** the header is displayed
- **THEN** the app brand, subtitle, and notifications action remain visible without crowding the alert card content

#### Scenario: Bottom controls are present
- **GIVEN** the home screen includes a floating bottom action surface or Android gesture navigation area
- **WHEN** the user scrolls or swipes through pet cards
- **THEN** card content and in-card actions remain visible and tappable above the bottom obstruction
