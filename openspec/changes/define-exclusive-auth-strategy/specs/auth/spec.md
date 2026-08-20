## MODIFIED Requirements

### Requirement: Email Password Authentication
The app SHALL allow users to create an account and sign in with email/password credentials only when that method is compatible with the account's original provider.

#### Scenario: User signs up with a new valid email and password
- **WHEN** no existing Firebase account matches the email and the user submits valid email/password sign-up credentials
- **THEN** Firebase Auth creates one account with the password provider and the app enters the authenticated state for that Firebase `uid`

#### Scenario: User attempts email/password sign-up for a Google account
- **WHEN** the submitted email is already associated with Google and the user attempts to create an account with email/password
- **THEN** the app does not create a second account, does not add a password, keeps the user unauthenticated, and directs the user to Google Sign-In when Firebase exposes that provider

#### Scenario: User signs in with valid email and password
- **WHEN** the submitted credentials match an existing Email/Password account
- **THEN** Firebase Auth authenticates that existing account and the app enters the authenticated state for the unchanged Firebase `uid`

#### Scenario: Email password authentication fails
- **WHEN** Firebase Auth rejects submitted email/password credentials or the provider cannot be determined safely
- **THEN** the app keeps the user unauthenticated and shows a recoverable functional message without exposing sensitive data or raw Firebase errors

### Requirement: Google Sign-In Authentication
The app SHALL allow users to authenticate with Google Sign-In through Firebase Authentication only when Google is the account's original provider.

#### Scenario: New user signs in with Google successfully
- **WHEN** Google Sign-In returns a valid credential and no Firebase account exists for its email
- **THEN** Firebase Auth creates one Google account and the app enters the authenticated state for the resulting Firebase `uid`

#### Scenario: Existing Google user signs in successfully
- **WHEN** Google Sign-In returns a valid credential for an existing Google account
- **THEN** Firebase Auth authenticates that existing account and the app enters the authenticated state for the unchanged Firebase `uid`

#### Scenario: Google credential conflicts with an Email/Password account
- **WHEN** Firebase reports `account-exists-with-different-credential` for a Google credential whose email belongs to an Email/Password account
- **THEN** the app discards the Google credential, keeps the user unauthenticated, instructs the user to sign in with Email/Password, and does not call account linking

#### Scenario: Google sign-in is cancelled or fails
- **WHEN** Google Sign-In is cancelled, the credential is invalid, or Firebase Auth rejects it for a reason other than a known provider conflict
- **THEN** the app keeps the user unauthenticated and shows a recoverable functional error without exposing raw Firebase details

## ADDED Requirements

### Requirement: Authentication Provider Exclusivity
Each FindYourPet account SHALL retain exactly one authentication provider, determined by the method used when the account was originally created.

#### Scenario: Email/Password account attempts Google
- **WHEN** a user attempts Google Sign-In with an email already owned by an Email/Password account
- **THEN** the app rejects the alternate method, preserves the existing UID and data, and does not link, convert, or replace providers

#### Scenario: Google account attempts Email/Password
- **WHEN** a user attempts Email/Password sign-up or sign-in with an email already owned by a Google account
- **THEN** the app rejects the alternate method, does not create a duplicate account or password, preserves the existing UID and data, and directs the user to Google when the provider is known

#### Scenario: Provider lookup is inconclusive
- **WHEN** Firebase returns no provider information, including because Email Enumeration Protection is enabled
- **THEN** the app does not infer that an account or provider exists, performs no linking or migration, and returns the recoverable message: “No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.”

#### Scenario: Authentication conflict leaves the session unchanged
- **WHEN** an authentication attempt conflicts with the account's original provider
- **THEN** the app remains unauthenticated unless the user completes a successful sign-in with the original provider, and no Firestore identity or authorization scope changes

### Requirement: Authentication Conflict Messaging
The app SHALL translate provider conflicts and authentication failures into functional, localizable messages before presenting them in the Login UI.

#### Scenario: Known Email/Password provider conflicts with Google
- **WHEN** Firebase identifies the original provider as Email/Password during a Google attempt
- **THEN** the user sees a message equivalent to “Esta cuenta fue creada con correo y contraseña. Iniciá sesión utilizando tu contraseña.”

#### Scenario: Known Google provider conflicts with Email/Password
- **WHEN** Firebase identifies the original provider as Google during an Email/Password attempt
- **THEN** the user sees a message equivalent to “Esta cuenta fue creada utilizando Google. Iniciá sesión con Google.”

#### Scenario: Login fallback clears credentials without hiding the message
- **WHEN** an Email/Password attempt fails in Login mode (`isSignUp == false`) and the fallback oriented to Google is shown
- **THEN** the app clears the Email and Contraseña fields, preserves the fallback message, and does not alter sign-up behavior, provider exclusivity, Firebase configuration, or account-linking policy

#### Scenario: Technical Firebase error is returned
- **WHEN** Firebase returns an authentication error that is not safe or specific enough to expose
- **THEN** the user sees the recoverable message “No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.” and never sees the raw exception, Firebase error code, credential, or sensitive account data
