## ADDED Requirements

### Requirement: Integrated Full-Viewport Login Composition

The login screen SHALL use the available viewport as its primary surface and SHALL NOT wrap the complete authentication flow in a single outer card.

#### Scenario: Login screen renders without an outer card
- **WHEN** an unauthenticated user opens the login screen
- **THEN** the identity, proximity visual, main message and authentication content render as one vertical composition over the screen surface without a card enclosing the complete flow

#### Scenario: Login screen respects system and keyboard insets
- **WHEN** the login screen is displayed with system bars or the software keyboard visible
- **THEN** the content remains reachable and is not obscured by those insets

### Requirement: Complete Authentication Content Remains Available

The redesigned presentation SHALL keep the existing authentication controls and user-visible states available without changing their behavior.

#### Scenario: Email login remains usable
- **WHEN** a user enters email and password on the redesigned login screen
- **THEN** the existing email login action remains visible, enabled by the same state rules and connected to the existing authentication flow

#### Scenario: Account creation remains discoverable
- **WHEN** a user views the login screen
- **THEN** the user can switch to account creation and the signup form remains usable within the same vertical composition

#### Scenario: Alternative authentication remains available
- **WHEN** a user views the authentication options
- **THEN** Google Sign-In remains available and the existing cancellation, configuration and error messages remain displayable

### Requirement: Design System and Theme Compatibility

The login screen SHALL use the existing FindYourPet Design System and SHALL render correctly in both Light Theme and Dark Theme.

#### Scenario: Light Theme preserves readable hierarchy
- **WHEN** the login screen is rendered in Light Theme
- **THEN** text, fields, dividers, actions and error messages use theme-aware colors and the existing typography and spacing tokens with readable contrast

#### Scenario: Dark Theme preserves readable hierarchy
- **WHEN** the login screen is rendered in Dark Theme
- **THEN** text, fields, dividers, actions and error messages use theme-aware colors and the existing typography and spacing tokens with readable contrast

#### Scenario: Visual values remain tokenized
- **WHEN** the redesigned screen is reviewed or built
- **THEN** it contains no new hardcoded colors, arbitrary text sizes, repeated spacing values or experimental Material 3 APIs

