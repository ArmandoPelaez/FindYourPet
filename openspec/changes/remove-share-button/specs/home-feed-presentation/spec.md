## MODIFIED Requirements

### Requirement: In-card action controls

The home alert card SHALL keep the sighting action as the primary in-card control and SHALL NOT expose a Share control or platform-sharing behavior.

#### Scenario: User can report a sighting

- **GIVEN** a post is not reunited and the current user is allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the "Lo he visto" action is visually primary and navigates to the existing sighting alert flow
- **THEN** no Share button, Share label, or Share content description is displayed

#### Scenario: User cannot report a sighting

- **GIVEN** the post is reunited or the current user is not allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the primary sighting action is not shown as an available action
- **THEN** no Share button, Share label, or Share content description is displayed

#### Scenario: Share functionality is removed

- **GIVEN** a user views any lost-pet post in the home feed
- **WHEN** the user inspects or interacts with the card actions
- **THEN** the app does not expose a Share control
- **THEN** the app does not construct or launch an Android platform share intent for the post
- **THEN** the remaining card actions continue to behave according to their existing rules
