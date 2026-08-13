## ADDED Requirements

### Requirement: Authenticated primary navigation is always sticky
The authenticated application shell SHALL render the primary navigation banner as a fixed bottom surface on every authenticated destination, independent of the current route.

#### Scenario: User navigates to any authenticated destination
- **GIVEN** a signed-in user opens Home, Profile, Chats, Create Post, Notifications, Sighting Alert, or Chat Detail
- **WHEN** the destination content is displayed
- **THEN** the primary navigation banner remains fixed to the viewport bottom above the system gesture area
- **AND** the banner actions keep their existing destinations and callbacks

#### Scenario: User scrolls content behind the navigation
- **GIVEN** an authenticated destination contains scrollable content
- **WHEN** the user scrolls from the first item to the last item
- **THEN** the primary navigation banner does not move with the content
- **AND** the content remains reachable without being permanently hidden behind the banner

## MODIFIED Requirements

### Requirement: Home content remains unobstructed
The authenticated application content SHALL reserve enough bottom spacing for the fixed primary navigation banner so feed cards, empty states, forms, chats, notifications, and actionable content are not covered by the banner or system gesture area.

#### Scenario: Empty state remains readable
- **GIVEN** the home feed has no pet posts
- **WHEN** the empty state is displayed with the fixed bottom banner
- **THEN** the empty state text and card remain readable and are not covered by the banner

#### Scenario: Feed content remains tappable
- **GIVEN** the home feed contains pet posts
- **WHEN** the user scrolls or swipes through the feed
- **THEN** card content and in-card actions remain visible and tappable above the banner

#### Scenario: Secondary destination remains usable
- **GIVEN** an authenticated secondary destination contains scrollable or actionable content
- **WHEN** the user reaches its final content or action
- **THEN** that content or action remains fully visible and tappable above the fixed banner

### Requirement: Primary navigation matches the reference order and actions
The authenticated primary navigation SHALL present five labeled destinations in this order: Inicio, Perfil, Publicar, Mensajes, and Alertas.

#### Scenario: User sees the primary navigation
- **GIVEN** a signed-in user views any authenticated destination
- **THEN** the bottom navigation shows the five items in the specified order
- **AND** the current destination uses the active primary color while inactive items use the theme surface-variant content color
- **AND** Publicar uses the centered filled circular action treatment

#### Scenario: User opens alerts from the primary navigation
- **GIVEN** the user taps Alertas in the bottom navigation
- **THEN** the app opens the existing Notifications destination
- **AND** the unread notification badge remains available on that item
- **AND** Home does not render a duplicate alert icon in its top app bar

### Requirement: Primary navigation uses a full-width bank treatment
The primary navigation SHALL occupy the full available width without a floating card container, and the Publicar action SHALL be emphasized by a small circular well that rises above the bar.

#### Scenario: User views the bottom bank
- **GIVEN** the authenticated shell is displayed
- **THEN** the navigation background spans the full width of the viewport
- **AND** it does not use the previous horizontal card inset, rounded corners, or floating-card treatment
- **AND** the Publicar circle has a subtle circular surface behind it and remains proportionally undistorted
- **AND** the system gesture area remains covered by the same navigation surface

### Requirement: Compact navigation aligns the create label
The primary navigation SHALL use a compact tokenized height, keep `Publicar` text on the same label baseline as the other destinations, and render its circular action over a dark elevated extension of the bar.

#### Scenario: User views the compact navigation
- **GIVEN** the authenticated shell is displayed
- **THEN** the navigation uses the compact height token and adapts all icon slots to that height
- **AND** `Publicar` is the same visual label level as Inicio, Perfil, Mensajes, and Alertas
- **AND** the orange plus button remains undistorted above a dark shadowed circular extension
