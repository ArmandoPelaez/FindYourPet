## Purpose

Define the home screen primary navigation actions and placement.

## Requirements

### Requirement: Home primary actions banner
The home screen SHALL present a floating bottom banner containing exactly three primary actions: profile on the left, create post in the center, and chat on the right.

#### Scenario: Authenticated user views home
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the home content is displayed
- **THEN** the bottom banner shows a profile icon on the left, a plus icon in the center, and a chat icon on the right

#### Scenario: Create post action is centered
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user looks at the floating bottom banner
- **THEN** the create-post action is visually centered between profile and chat and uses a plus icon

### Requirement: Primary action navigation
Each bottom banner action SHALL preserve the existing destination for its corresponding feature.

#### Scenario: Profile action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the left profile action
- **THEN** the app navigates to the profile screen

#### Scenario: Create post action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the centered plus action
- **THEN** the app navigates to the create pet post screen

#### Scenario: Chat action
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the right chat action
- **THEN** the app navigates to the private chat list screen

### Requirement: Header action relocation
The home top app bar SHALL keep branding and notifications, and SHALL NOT duplicate profile, create-post, or chat actions that are present in the bottom banner.

#### Scenario: Header is simplified
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the top app bar is displayed
- **THEN** it shows the app branding and notifications action without profile, create-post, or chat actions

#### Scenario: Notifications remain available
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the user taps the notification action in the top app bar
- **THEN** the app navigates to the notifications screen

### Requirement: Floating banner presentation
The bottom banner SHALL appear as one floating surface without visible internal dividers between the three actions.

#### Scenario: Banner has no visible separation lines
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the bottom banner is rendered
- **THEN** profile, plus, and chat actions appear within one continuous floating surface with no visible separator lines between them

#### Scenario: Banner respects system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** the home screen is displayed
- **THEN** the bottom banner is positioned above the system gesture area

### Requirement: Home content remains unobstructed
The home screen SHALL reserve enough bottom spacing so feed cards, empty states, and actionable content are not covered by the floating banner.

#### Scenario: Empty state remains readable
- **GIVEN** the home feed has no pet posts
- **WHEN** the empty state is displayed with the bottom banner
- **THEN** the empty state text and card remain readable and are not covered by the banner

#### Scenario: Feed content remains tappable
- **GIVEN** the home feed contains pet posts
- **WHEN** the user scrolls or swipes through the feed
- **THEN** card content and in-card actions remain visible and tappable above the banner
