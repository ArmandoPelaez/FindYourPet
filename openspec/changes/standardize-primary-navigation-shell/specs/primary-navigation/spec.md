## MODIFIED Requirements

### Requirement: Home primary actions banner
The signed-in primary navigation bar SHALL present a persistent floating bottom surface with direct access to Home, Profile, Create Post, and Chats.

#### Scenario: Authenticated user views home
- **GIVEN** a signed-in user is on the home screen
- **WHEN** the home content is displayed
- **THEN** the bottom bar shows a home icon, a profile icon, a plus/create-post icon, and a chat icon

#### Scenario: Home action is available from primary destinations
- **GIVEN** a signed-in user is on Profile or Chats
- **WHEN** the bottom bar is displayed
- **THEN** the user can tap the home action to return to the main publications feed

#### Scenario: Create post action remains prominent
- **GIVEN** a signed-in user is on a primary destination
- **WHEN** the user looks at the floating bottom bar
- **THEN** the create-post action uses a plus icon and remains visually distinct from destination navigation actions

### Requirement: Primary action navigation
Each bottom bar action SHALL preserve the existing destination for its corresponding feature and SHALL include Home as a top-level destination.

#### Scenario: Home action
- **GIVEN** a signed-in user is on Profile or Chats
- **WHEN** the user taps the home action
- **THEN** the app navigates to the home publications feed

#### Scenario: Profile action
- **GIVEN** a signed-in user is on a primary destination
- **WHEN** the user taps the profile action
- **THEN** the app navigates to the profile screen

#### Scenario: Create post action
- **GIVEN** a signed-in user is on a primary destination
- **WHEN** the user taps the plus action
- **THEN** the app navigates to the create pet post screen

#### Scenario: Chat action
- **GIVEN** a signed-in user is on a primary destination
- **WHEN** the user taps the chat action
- **THEN** the app navigates to the private chat list screen

### Requirement: Floating banner presentation
The bottom bar SHALL appear as one floating surface without visible internal dividers between its actions.

#### Scenario: Banner has no visible separation lines
- **GIVEN** a signed-in user is on a primary destination
- **WHEN** the bottom bar is rendered
- **THEN** home, profile, plus/create, and chat actions appear within one continuous floating surface with no visible separator lines between them

#### Scenario: Banner respects system gesture area
- **GIVEN** a signed-in user uses Android gesture navigation
- **WHEN** a primary destination is displayed
- **THEN** the bottom bar is positioned above the system gesture area

## ADDED Requirements

### Requirement: Signed-in primary navigation shell
The signed-in app SHALL own the bottom primary action bar in a navigation shell at the same composition level as the `NavHost`, rather than inside an individual destination screen.

#### Scenario: Home displays shell-owned bar
- **GIVEN** a signed-in user is on the home destination
- **WHEN** the home content is displayed
- **THEN** the bottom primary action bar is visible as app-level navigation chrome
- **AND** `HomeScreen` does not own or render a separate bottom primary action bar

#### Scenario: Profile keeps primary bar
- **GIVEN** a signed-in user is on the profile destination
- **WHEN** the profile content is displayed
- **THEN** the same bottom primary action bar remains visible

#### Scenario: Chats keeps primary bar
- **GIVEN** a signed-in user is on the chats destination
- **WHEN** the chat list content is displayed
- **THEN** the same bottom primary action bar remains visible

### Requirement: Primary bar route visibility
The bottom primary action bar SHALL be visible only on primary signed-in destinations and SHALL be hidden on secondary task or detail destinations.

#### Scenario: Primary destinations show the bar
- **GIVEN** a signed-in user navigates among Home, Profile, and Chats
- **WHEN** any of those destinations is active
- **THEN** the bottom primary action bar is visible

#### Scenario: Create post hides the bar
- **GIVEN** a signed-in user opens Create Post from the center plus action
- **WHEN** the create-post destination is active
- **THEN** the bottom primary action bar is not visible

#### Scenario: Notification flow hides the bar
- **GIVEN** a signed-in user opens Notifications
- **WHEN** the notifications destination is active
- **THEN** the bottom primary action bar is not visible

#### Scenario: Detail flows hide the bar
- **GIVEN** a signed-in user opens a Sighting Alert or Chat Detail destination
- **WHEN** the detail destination is active
- **THEN** the bottom primary action bar is not visible

### Requirement: Top-level destination chrome
Profile and Chats SHALL behave as top-level destinations when reached through the primary bar.

#### Scenario: Profile has no primary back arrow
- **GIVEN** a signed-in user opens Profile from the bottom primary action bar
- **WHEN** the profile top app bar is displayed
- **THEN** it does not show an in-screen back arrow for returning to Home

#### Scenario: Chats has no primary back arrow
- **GIVEN** a signed-in user opens Chats from the bottom primary action bar
- **WHEN** the chats top app bar is displayed
- **THEN** it does not show an in-screen back arrow for returning to Home

### Requirement: Bounded primary navigation stack
Primary navigation actions SHALL use bounded navigation options so repeated taps do not create an unbounded stack of duplicate primary destinations.

#### Scenario: Repeated selected destination taps are single-top
- **GIVEN** a signed-in user is on Home, Profile, or Chats
- **WHEN** the user repeatedly taps the bar action for the currently active primary destination
- **THEN** the app does not add duplicate copies of that destination to the navigation stack

#### Scenario: Switching primary destinations remains bounded
- **GIVEN** a signed-in user repeatedly switches among Home, Profile, and Chats using the bottom primary action bar
- **WHEN** those taps occur in any order
- **THEN** primary navigation uses a `popUpTo` policy anchored to the navigation graph start destination or equivalent top-level root
- **AND** the back stack does not grow as an endless sequence of repeated primary destinations

#### Scenario: Primary destination state can be restored
- **GIVEN** a signed-in user switches away from a primary destination
- **WHEN** the user returns to that primary destination through the bottom primary action bar
- **THEN** the app uses Navigation Compose state restoration options where supported by the route and graph

### Requirement: Secondary action stack control
Secondary routes launched from the primary bar SHALL avoid accidental duplicate destinations when triggered from an eligible primary route.

#### Scenario: Create post double tap is controlled
- **GIVEN** a signed-in user is on a primary destination with the bottom primary action bar visible
- **WHEN** the user double taps the center create-post action
- **THEN** the app does not create multiple create-post destinations for the same interaction burst

#### Scenario: Create post returns to prior primary destination
- **GIVEN** a signed-in user opens Create Post from Home, Profile, or Chats
- **WHEN** the user completes or backs out of the create-post flow
- **THEN** the app returns to the previous navigation stack state without requiring a duplicate Home, Profile, or Chats entry

### Requirement: Primary shell content spacing
The signed-in navigation shell SHALL keep primary destination content unobstructed by the bottom primary action bar.

#### Scenario: Profile bottom content remains reachable
- **GIVEN** a signed-in user is on Profile with the bottom primary action bar visible
- **WHEN** the user scrolls to the end of profile content
- **THEN** profile content and actions remain visible and tappable above the bar

#### Scenario: Chats bottom content remains reachable
- **GIVEN** a signed-in user is on Chats with the bottom primary action bar visible
- **WHEN** the user scrolls to the end of the chat list
- **THEN** chat rows and empty states remain visible and tappable above the bar
