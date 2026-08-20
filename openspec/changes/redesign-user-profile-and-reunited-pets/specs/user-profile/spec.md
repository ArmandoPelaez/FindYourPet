## ADDED Requirements

### Requirement: Reunited Publication Cleanup
When the authenticated owner confirms marking a `PERDIDO` publication as `REUNIDO`, the system SHALL physically remove all activity and alert records related to that publication from the backend and local cache. The cleanup SHALL include all sightings submitted by other users for the publication and the owner's related notifications/alerts.

#### Scenario: Owner marks publication as reunited and cleanup completes
- **GIVEN** the authenticated owner confirms marking their `PERDIDO` publication as `REUNIDO`
- **WHEN** the reunification operation completes successfully
- **THEN** the publication is `REUNIDO`, all sightings for its `postId` are deleted from backend and local storage, and all related owner notifications/alerts are deleted from backend and local storage

#### Scenario: Activity and alerts no longer show deleted records
- **GIVEN** a publication was marked `REUNIDO` and its cleanup completed
- **WHEN** the owner opens Actividad or Alertas
- **THEN** no activity item or notification related to that publication is displayed

#### Scenario: Non-owner cannot trigger cleanup
- **GIVEN** an authenticated user is not the owner of a publication
- **WHEN** that user attempts to mark the publication as `REUNIDO` or trigger its cleanup
- **THEN** the backend denies the operation and related sightings and notifications remain unchanged

#### Scenario: Cleanup failure is reported
- **GIVEN** the owner confirms the reunification but a required backend or local cleanup operation fails
- **WHEN** the operation reports its result
- **THEN** the app shows an error state and does not present the cleanup as successfully completed

### Requirement: Headerless Compact Profile Layout
The authenticated profile screen SHALL render without an AppBar or a `Perfil` title. The first content element SHALL be a compact user card containing the avatar, username and `Colaborador` role. The card SHALL use the same shared semantic surface token as the existing bottom navigation surface to create the requested contrast in both themes.

#### Scenario: Profile opens without a header
- **WHEN** an authenticated user opens the profile destination
- **THEN** no AppBar, TopAppBar or `Perfil` title is rendered and the compact user card is the first content element

#### Scenario: Profile card uses navigation contrast token
- **WHEN** the compact user card is rendered in Light Theme or Dark Theme
- **THEN** its container uses the shared token/function that defines the bottom navigation surface, without a new hardcoded color or opacity

### Requirement: Compact Pet Publication Visual Hierarchy
Owned pet publications SHALL use compact visual cards. Each card SHALL present its status through the existing status-chip component/tokens, and `Marcar reunida` SHALL be a secondary compact action rather than a large primary call-to-action.

#### Scenario: Lost publication uses compact secondary action
- **GIVEN** the authenticated user owns a publication with status `PERDIDO`
- **WHEN** the publication card is rendered
- **THEN** the status is shown as a chip and `Marcar reunida` is rendered as a compact secondary action

#### Scenario: Reunited publication uses status chip only
- **GIVEN** the authenticated user owns a publication with status `REUNIDO`
- **WHEN** the publication card is rendered
- **THEN** the status is shown as a chip and no reactivation action is rendered

### Requirement: Simplified Profile Presentation
The authenticated profile screen SHALL present the current user's avatar, username and collaborator role while omitting the email, community card and header logout action. The screen SHALL keep the existing bottom navigation unchanged and SHALL expose `Cerrar sesión` as a text action at the bottom of the profile.

#### Scenario: User views the profile header
- **WHEN** an authenticated user opens their profile
- **THEN** the screen shows the avatar, username and `Colaborador` role without showing the email, `Comunidad colaborativa` card or a header logout icon

#### Scenario: User signs out from the profile footer
- **WHEN** the authenticated user taps `Cerrar sesión` at the bottom of the profile
- **THEN** the existing sign-out logic runs and the user is no longer presented as authenticated

#### Scenario: Bottom navigation remains stable
- **WHEN** the profile redesign is rendered
- **THEN** the existing bottom navigation items, destinations and behavior remain unchanged

### Requirement: Owner Publication Management Presentation
The profile screen SHALL show the authenticated user's own publications, including `REUNIDO` publications, as compact cards without pet photos. A `PERDIDO` card SHALL offer `Marcar reunida`; a `REUNIDO` card SHALL show status only and SHALL NOT offer reactivation.

#### Scenario: Owner views lost publication
- **GIVEN** the authenticated user owns a publication with status `PERDIDO`
- **WHEN** the user's publications are displayed
- **THEN** the compact card shows the publication identity and offers `Marcar reunida`

#### Scenario: Owner views reunited publication
- **GIVEN** the authenticated user owns a publication with status `REUNIDO`
- **WHEN** the user's publications are displayed
- **THEN** the compact card remains visible, shows `REUNIDO` and has no reactivation action

#### Scenario: Other user's publication is not shown as an owned publication
- **GIVEN** a publication belongs to another authenticated user
- **WHEN** the profile loads the current user's publications
- **THEN** that publication is not included in the owned-publications list
