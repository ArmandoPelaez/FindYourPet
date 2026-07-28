## ADDED Requirements

### Requirement: Firebase Auth Session State
The app SHALL use Firebase Authentication as the source of truth for signed-in user identity in production auth flows.

#### Scenario: App starts with no authenticated user
- **WHEN** Firebase Auth has no current user
- **THEN** owner-only screens and actions are unavailable until the user signs in

#### Scenario: App starts with authenticated user
- **WHEN** Firebase Auth has a current user
- **THEN** the app exposes that user's Firebase `uid` as the active production identity

### Requirement: Email Password Authentication
The app SHALL allow users to create an account and sign in with email/password credentials.

#### Scenario: User signs up with valid email and password
- **WHEN** a user submits valid email/password sign-up credentials
- **THEN** Firebase Auth creates the account and the app enters the authenticated state for that Firebase `uid`

#### Scenario: User signs in with valid email and password
- **WHEN** a user submits valid email/password sign-in credentials
- **THEN** Firebase Auth authenticates the user and the app enters the authenticated state for that Firebase `uid`

#### Scenario: Email password authentication fails
- **WHEN** Firebase Auth rejects submitted email/password credentials
- **THEN** the app keeps the user unauthenticated and shows a recoverable error state without exposing sensitive data

### Requirement: Google Sign-In Authentication
The app SHALL allow users to authenticate with Google Sign-In through Firebase Authentication.

#### Scenario: User signs in with Google successfully
- **WHEN** Google Sign-In returns a valid credential accepted by Firebase Auth
- **THEN** the app enters the authenticated state for the resulting Firebase `uid`

#### Scenario: Google sign-in is cancelled or fails
- **WHEN** Google Sign-In is cancelled or Firebase Auth rejects the credential
- **THEN** the app keeps the user unauthenticated and shows a recoverable error state

### Requirement: Logout
The app SHALL allow authenticated users to sign out.

#### Scenario: User logs out
- **WHEN** an authenticated user triggers logout
- **THEN** Firebase Auth signs out, local auth state is cleared, and owner-only actions become unavailable

### Requirement: No SMS Authentication
The app SHALL NOT expose SMS authentication in this stage.

#### Scenario: User views authentication options
- **WHEN** the authentication UI is displayed
- **THEN** email/password and Google Sign-In are available and SMS authentication is absent
