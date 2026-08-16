## ADDED Requirements

### Requirement: Authentication Action Hierarchy

The Login screen SHALL present authentication actions with one clear primary action, one secondary action, and one tertiary action while preserving the existing authentication callbacks.

#### Scenario: Login mode presents the primary action

- **WHEN** the Login screen is displayed in sign-in mode
- **THEN** `Entrar` is the only filled primary action and it invokes the existing email/password sign-in flow

#### Scenario: Google is presented as a secondary action

- **WHEN** the Login screen is displayed in sign-in mode
- **THEN** `Continuar con Google` is presented as a secondary outlined or neutral action and it invokes the existing Google credential flow

#### Scenario: Account creation is presented as a tertiary action

- **WHEN** the Login screen is displayed in sign-in mode
- **THEN** `Crear una cuenta` is presented as a text/link action and toggles the existing sign-up mode without invoking authentication

#### Scenario: Sign-up mode preserves the same hierarchy

- **WHEN** the screen is in sign-up mode
- **THEN** the account creation submit action remains the only primary action, Google remains secondary, and the switch back to sign-in remains tertiary

### Requirement: Authentication Action Feedback And Locking

The Login screen SHALL show recoverable feedback for authentication failures and SHALL prevent simultaneous authentication submissions.

#### Scenario: Email authentication is loading

- **WHEN** the email/password authentication state is Loading
- **THEN** Entrar or the sign-up submit action, Google, and the mode-switch action are disabled and a visible loading indication is presented

#### Scenario: Google authentication is loading

- **WHEN** a Google credential request is in progress
- **THEN** the Google action is disabled, the other authentication actions cannot submit concurrently, and a visible loading indication is presented

#### Scenario: Authentication fails or is cancelled

- **WHEN** email/password or Google authentication fails or Google sign-in is cancelled
- **THEN** the screen remains unauthenticated, shows a recoverable error message, and re-enables the actions for retry

#### Scenario: Authentication succeeds

- **WHEN** an existing email/password or Google authentication callback succeeds
- **THEN** the screen preserves the existing authenticated-state transition and does not issue a second submit for the same user action
