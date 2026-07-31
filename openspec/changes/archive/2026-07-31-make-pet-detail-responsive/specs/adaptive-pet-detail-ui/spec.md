## ADDED Requirements

### Requirement: Compact pet detail layout
The pet detail screen SHALL use a single-column vertical layout when the available width is below 600dp.

#### Scenario: Phone portrait renders one column
- **GIVEN** a signed-in user opens a pet detail screen on a viewport below 600dp wide
- **WHEN** the screen content is displayed
- **THEN** the media header, pet information cards, and actions appear in one vertical reading order inside a scrollable content area

#### Scenario: Compact content remains reachable
- **GIVEN** the compact pet detail content is longer than the visible viewport
- **WHEN** the user scrolls vertically
- **THEN** all information cards and controls can be reached without being hidden by fixed top or bottom surfaces

### Requirement: Expanded pet detail layout
The pet detail screen SHALL use an adaptive two-column master-detail layout when the available width is at least 600dp and the available height can support readable columns.

#### Scenario: Tablet width renders two columns
- **GIVEN** a signed-in user opens a pet detail screen on a viewport at least 600dp wide
- **WHEN** the screen content is displayed with sufficient height
- **THEN** the media header appears in the left column and the pet information cards plus actions appear in a scrollable right column

#### Scenario: Expanded fallback uses constrained column
- **GIVEN** a signed-in user opens a pet detail screen on a wide viewport whose height is too constrained for the two-column layout
- **WHEN** the screen content is displayed
- **THEN** the screen uses a centered single-column layout with a maximum content width of 640dp

### Requirement: Fixed chrome respects safe areas
The pet detail screen SHALL keep top navigation and bottom action/navigation surfaces fixed at screen edges while respecting system safe-area insets.

#### Scenario: Top app bar avoids unsafe regions
- **GIVEN** the device has a display cutout or status bar
- **WHEN** the pet detail screen is displayed
- **THEN** the top navigation surface remains visible and does not overlap the unsafe system area

#### Scenario: Bottom actions avoid gesture area
- **GIVEN** the device uses gesture navigation or has a bottom system inset
- **WHEN** the pet detail screen is displayed
- **THEN** bottom action controls are positioned above the gesture/system area and remain tappable

### Requirement: Adaptive media header
The pet detail screen SHALL display the main pet photo with adaptive sizing, center-crop scaling, and non-overlapping status badges.

#### Scenario: Portrait image ratio
- **GIVEN** the pet detail screen is displayed in portrait orientation
- **WHEN** the main photo is rendered
- **THEN** the photo area uses a 4:3 aspect ratio unless constrained by available viewport space

#### Scenario: Landscape image height cap
- **GIVEN** the pet detail screen is displayed in landscape orientation or compact horizontal space
- **WHEN** the main photo is rendered
- **THEN** the photo area does not exceed 45% of the viewport height

#### Scenario: Photo scaling preserves shape
- **GIVEN** a pet photo is available
- **WHEN** the media header renders the photo
- **THEN** the image is center-cropped without distortion

#### Scenario: Badge overlay remains readable
- **GIVEN** the media header includes a status or metadata badge
- **WHEN** the photo is rendered at any supported size
- **THEN** the badge appears over the bottom-right area with density-independent margins and does not overlap fixed screen chrome

### Requirement: Responsive action controls
Primary pet detail actions SHALL have accessible touch height and fluid width within a bounded maximum.

#### Scenario: Compact actions fill available width
- **GIVEN** the user opens pet detail on a compact viewport
- **WHEN** primary actions are displayed
- **THEN** each action fills the available parent width within the screen margins and has a 56dp height

#### Scenario: Wide actions are capped
- **GIVEN** the user opens pet detail on a medium or expanded viewport
- **WHEN** primary actions are displayed in a container wider than 400dp
- **THEN** each individual action is no wider than 400dp and remains visually aligned with the action group

### Requirement: Flexible information cards and accessible typography
Pet detail information cards SHALL grow to fit content and typography SHALL respect user text scaling.

#### Scenario: Long pet information wraps
- **GIVEN** a pet has long notes, location text, breed text, or descriptive attributes
- **WHEN** the pet detail information cards are displayed
- **THEN** the cards expand vertically and the essential text wraps instead of being truncated

#### Scenario: Larger font setting remains readable
- **GIVEN** the user has increased the system font size
- **WHEN** the pet detail screen is displayed
- **THEN** text uses scalable units and controls/cards remain readable without incoherent overlap

### Requirement: Privacy-neutral responsive presentation
The responsive pet detail layout SHALL NOT expose additional sensitive data beyond what existing privacy rules allow.

#### Scenario: Protected contact data remains protected
- **GIVEN** contact details are not authorized for the current user
- **WHEN** the pet detail screen changes between compact and expanded layouts
- **THEN** phone, email, exact address, exact coordinates, and private messages remain hidden or masked according to existing privacy behavior

#### Scenario: Existing actions keep authorization rules
- **GIVEN** the current user is not allowed to perform an action on the pet detail screen
- **WHEN** the screen is displayed in any responsive layout
- **THEN** the action remains disabled, hidden, or denied according to the existing authorization behavior
