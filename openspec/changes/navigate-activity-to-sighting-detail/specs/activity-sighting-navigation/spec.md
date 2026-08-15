## ADDED Requirements

### Requirement: Activity Item Opens Existing Sighting Detail

The app SHALL allow an authenticated user to select an Activity item and SHALL navigate to the existing sighting detail destination using that item's non-blank `sightingId` as the only sighting identifier.

#### Scenario: User selects a received sighting

- **GIVEN** the authenticated user's Activity list contains a sighting item with `sightingId = sighting_123`
- **WHEN** the user selects that item
- **THEN** the app navigates to `sighting/sighting_123`
- **AND** the existing `SightingDetailScreen` displays the selected sighting through its current loading and authorization flow

#### Scenario: Activity navigation is independent of Chat

- **GIVEN** an Activity item has a valid `sightingId` and Chat-related identifiers may exist elsewhere in application state
- **WHEN** the user selects the Activity item
- **THEN** the app resolves the destination from `sightingId` only
- **AND** the app does not open `ChatScreen`, create a conversation, or use `chatId`, `conversationId`, `messageId` or a Chat `targetId`

### Requirement: Invalid Activity Sighting Identifier Is Safe

The app SHALL ignore an Activity selection when its `sightingId` is null, blank or invalid for route construction, SHALL avoid a crash and SHALL record useful diagnostic information.

#### Scenario: Activity item has a blank identifier

- **GIVEN** an Activity item exposes a null, empty or whitespace-only `sightingId`
- **WHEN** the user selects that item
- **THEN** the app does not navigate
- **AND** the app does not open Chat or attempt a fallback identifier
- **AND** the app records the invalid selection using the existing diagnostic pattern

#### Scenario: Detail route receives only a normalized identifier

- **GIVEN** an Activity item has a valid identifier with surrounding whitespace
- **WHEN** the user selects that item
- **THEN** the app trims the identifier before constructing the route
- **AND** the route contains the normalized non-blank `sightingId`

### Requirement: Back Returns To Activity Without Duplication

The app SHALL preserve the existing Activity destination in the navigation back stack when opening a sighting detail and SHALL return to that Activity instance when the user presses Back.

#### Scenario: User returns from sighting detail

- **GIVEN** the user navigated from Activity to `sighting/sighting_123`
- **WHEN** the user presses Back from the detail screen
- **THEN** the app returns to Activity
- **AND** it does not navigate unexpectedly to Home, Alerts or Chat
- **AND** it does not create a second Activity instance solely for the return

#### Scenario: Repeated selection does not duplicate the same detail

- **GIVEN** the user repeatedly selects the same Activity item before leaving its detail
- **WHEN** the navigation requests are processed
- **THEN** the app does not accumulate duplicate instances of the same sighting detail route

### Requirement: Activity Selection Respects Existing Design Rules

The app SHALL present Activity items as accessible interactive Material 3 components using existing design tokens and SHALL remain readable in Light Theme and Dark Theme.

#### Scenario: Interactive item exposes pressed and touch-target behavior

- **GIVEN** the Activity list renders a sighting item
- **WHEN** the user points to or presses the item
- **THEN** the item provides the existing Material 3 pressed/click feedback and an accessible touch target
- **AND** no new hardcoded color, typography, spacing or shape value is introduced

#### Scenario: Activity selection works in both themes

- **GIVEN** Activity is displayed in Light Theme or Dark Theme
- **WHEN** the user selects an item
- **THEN** the item remains readable and its interactive state uses the existing theme tokens
