## MODIFIED Requirements

### Requirement: In-card action controls
The home alert card SHALL keep the sighting action as the primary inline control according to the existing eligibility rules. The control SHALL appear to the right of the pet name, use the label `La vi`, start with a closed-eye icon, open the eye when pressed, and invoke the existing sighting alert flow. The previous sticky `¡Lo he visto!` action SHALL NOT be rendered. Sharing behavior, when present, SHALL remain privacy-safe and SHALL NOT reintroduce removed breed, color, or signs presentation.

#### Scenario: User can report a sighting
- **GIVEN** a post is not reunited and the current user is allowed to report a sighting
- **WHEN** the post is displayed and its action area is available
- **THEN** the `La vi` action is aligned to the right of the pet name
- **AND** its eye icon is closed before interaction
- **AND** tapping it opens the eye and navigates through the existing sighting alert flow

#### Scenario: User changes the visible post
- **GIVEN** the home feed displays multiple posts through the existing pager
- **WHEN** the user changes the visible post
- **THEN** the inline sighting action updates to the eligibility and callback of the visible post

#### Scenario: User cannot report a sighting
- **GIVEN** the post is reunited or the current user is not allowed to report a sighting
- **WHEN** the post is displayed
- **THEN** the inline sighting action is not shown as an available action

#### Scenario: User shares a post
- **GIVEN** the secondary share control is available
- **WHEN** the user taps it
- **THEN** the app opens the platform share flow with a summary that excludes owner phone, owner email, exact coordinates, private messages, hidden contact data, and the removed breed, color, or signs presentation lines

### Requirement: Home header and bottom spacing remain usable
The redesigned home feed SHALL preserve a clear header and enough bottom spacing for the primary navigation surface so alert cards and inline actions remain readable with the bottom action surface or system gesture navigation.

#### Scenario: Header is displayed
- **GIVEN** the user is on the home screen
- **WHEN** the header is displayed
- **THEN** the app brand, subtitle, and notifications action remain visible without crowding the alert card content

#### Scenario: Bottom controls are present
- **GIVEN** the home screen includes an eligible inline sighting action and the primary navigation surface
- **WHEN** the user scrolls or swipes through pet cards
- **THEN** card content remains reachable
- **AND** the final content is not hidden behind the navigation surface
- **AND** the inline action remains aligned with the pet name without overlapping it

### Requirement: Reference post metadata presentation
The home alert card SHALL place the status label at the top-left of the photo, use the status color tokens with the reference emphasis, render the photo using the shared card image aspect-ratio token, and show the last-seen metadata below the location.

#### Scenario: Status and photo presentation
- **GIVEN** a pet post has a known status and a photo URI
- **WHEN** the home alert card is displayed
- **THEN** the status label appears in the top-left photo overlay
- **AND** the label uses the semantic status color tokens
- **AND** the photo occupies the shared reference aspect ratio without stretching

#### Scenario: Last-seen metadata presentation
- **GIVEN** a pet post has a last-seen location and date
- **WHEN** the identity section is displayed
- **THEN** the location appears below the pet name
- **AND** a row below the location shows a calendar icon, `Última vez visto`, and the formatted date beside it
