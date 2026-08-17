## MODIFIED Requirements

### Requirement: Email Password Authentication
The app SHALL allow users to create an account and sign in with email/password credentials. While an email/password operation is pending, the Login SHALL provide loading feedback, disable competing authentication actions and editable fields, and ignore additional submit attempts.

#### Scenario: User signs up with valid email and password
- **WHEN** a user submits valid email/password sign-up credentials
- **THEN** Firebase Auth creates the account and the app enters the authenticated state for that Firebase `uid`

#### Scenario: User signs in with valid email and password
- **WHEN** a user submits valid email/password sign-in credentials
- **THEN** Firebase Auth authenticates the user and the app enters the authenticated state for that Firebase `uid`

#### Scenario: Email password authentication fails
- **WHEN** Firebase Auth rejects submitted email/password credentials
- **THEN** the app keeps the user unauthenticated, shows a recoverable error state without exposing sensitive data, and re-enables the form for another attempt

#### Scenario: Email password authentication is pending
- **WHEN** an email/password operation is in progress
- **THEN** the primary action shows loading feedback, editable authentication fields and competing authentication actions are disabled, and additional submit attempts do not start another operation

### Requirement: Google Sign-In Authentication
The app SHALL allow users to authenticate with Google Sign-In through Firebase Authentication. While Google authentication is pending, the Login SHALL provide loading feedback, disable competing authentication actions and editable fields, and ignore additional authentication attempts.

#### Scenario: User signs in with Google successfully
- **WHEN** Google Sign-In returns a valid credential accepted by Firebase Auth
- **THEN** the app enters the authenticated state for the resulting Firebase `uid`

#### Scenario: Google sign-in is cancelled or fails
- **WHEN** Google Sign-In is cancelled or Firebase Auth rejects the credential
- **THEN** the app keeps the user unauthenticated, shows a recoverable error state, and re-enables authentication actions

#### Scenario: Google authentication is pending
- **WHEN** Google credential acquisition or the resulting Firebase authentication operation is in progress
- **THEN** the Google action shows loading feedback, competing authentication actions and editable fields are disabled, and additional Google attempts do not start another operation

## ADDED Requirements

### Requirement: Login Visual State Feedback
The Login SHALL provide finite, non-blocking, theme-aware visual feedback for focus, password visibility, authentication loading, recoverable errors, and successful authentication state changes, using stable APIs and existing design-system tokens.

#### Scenario: An input receives focus
- **WHEN** the email or password input gains or loses focus
- **THEN** its existing token-backed visual state changes smoothly or resolves immediately when reduced motion is active, without changing its size or interaction target

#### Scenario: Password visibility changes
- **WHEN** the user activates the password visibility control
- **THEN** the visibility affordance and password presentation transition to the selected state without losing the field value, focus, label, or password semantics

#### Scenario: Authentication loading begins
- **WHEN** email/password or Google authentication begins
- **THEN** the corresponding action communicates progress, all competing authentication actions become unavailable, and no infinite decorative animation starts

#### Scenario: Authentication error is returned
- **WHEN** an authentication operation returns a recoverable error
- **THEN** the Login presents the existing non-sensitive error message with a finite appearance transition and leaves the user able to retry

#### Scenario: Authentication succeeds
- **WHEN** the observed authentication state changes to signed in
- **THEN** the Login may provide brief non-blocking confirmation, but it does not delay, replace, or block the existing navigation to the authenticated experience

#### Scenario: Reduced motion is active
- **WHEN** the platform indicates that reduced motion should be respected
- **THEN** the Login renders the same final visual states without animated interpolation or other motion that is not required for understanding the interaction
