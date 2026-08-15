## ADDED Requirements

### Requirement: Sighting Notification Routes To Detail
The app SHALL route a notification representing a sighting to the existing sighting detail destination using its non-blank `sightingId` as the route identifier.

#### Scenario: Owner opens a valid sighting notification
- **GIVEN** user A has a notification representing a sighting with `sightingId = sighting_123`
- **WHEN** user A selects that notification
- **THEN** the app marks the notification as read and navigates to `sighting/sighting_123`
- **AND** the app does not navigate to `ChatScreen`

#### Scenario: Sighting notification ignores legacy chat identifiers
- **GIVEN** a sighting notification contains a valid `sightingId` and also contains `chatId` or a legacy `targetId`
- **WHEN** the notification is selected
- **THEN** the app uses `sightingId` as the destination identifier
- **AND** the app does not use `chatId` or legacy `targetId` to determine the destination

### Requirement: Invalid Sighting Notification Is Safe
The app SHALL handle a sighting notification with a missing or blank `sightingId` without crashing or opening Chat.

#### Scenario: Sighting notification has no identifier
- **GIVEN** a sighting notification has no non-blank `sightingId`
- **WHEN** the notification is selected
- **THEN** the app marks the notification as read according to the existing behavior
- **AND** the app does not navigate
- **AND** the app does not open `ChatScreen`
- **AND** the app records diagnostic information containing the notification identity or type

### Requirement: Other Notification Destinations Remain Unchanged
The app SHALL preserve the existing destination behavior for notifications that do not represent sightings.

#### Scenario: User opens a chat notification
- **GIVEN** user A has a non-sighting chat notification with a valid chat identifier
- **WHEN** user A selects that notification
- **THEN** the app marks the notification as read and navigates to the existing chat detail destination

## MODIFIED Requirements

None.
