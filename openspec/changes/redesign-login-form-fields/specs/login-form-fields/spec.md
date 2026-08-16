## ADDED Requirements

### Requirement: Lightweight Email and Password Inputs
The Login and Registration forms SHALL present Email and Password as integrated, lightweight inputs with their existing icons, clear placeholders, shared form typography, and visual priority below the primary CTA.

#### Scenario: User views the authentication form
- **WHEN** the Login or Registration form is displayed
- **THEN** the Email input shows an email icon, a non-empty email placeholder, and the existing form typography tokens
- **AND** the Password input shows a password icon, a non-empty password placeholder, and the existing form typography tokens
- **AND** the inputs do not visually compete with the primary Entrar or Crear cuenta CTA

### Requirement: Password Visibility Control
The Password input SHALL provide an explicit accessible control to show or hide the entered password without changing the value submitted to authentication.

#### Scenario: User shows the password
- **WHEN** the user activates the password visibility control while the Password input contains text
- **THEN** the entered password becomes readable
- **AND** the password value remains unchanged
- **AND** the control exposes an action description indicating that the password can be hidden

#### Scenario: User hides the password
- **WHEN** the user activates the password visibility control while the password is readable
- **THEN** the entered password is visually transformed again
- **AND** the password value remains unchanged
- **AND** the control exposes an action description indicating that the password can be shown

### Requirement: Field State and Error Presentation
The Email and Password inputs SHALL preserve their existing validation behavior and render focused, disabled, and error states distinctly, with errors associated with the corresponding field when the source error can be attributed to that field.

#### Scenario: User focuses an enabled field
- **WHEN** the Email or Password input receives focus
- **THEN** the focused state is visually distinguishable using the stable Material 3 field state and project color tokens

#### Scenario: Field validation fails
- **WHEN** the user attempts to submit an empty or invalid Email or Password value
- **THEN** the corresponding field displays an associated error state/message
- **AND** the authentication callback is not invoked for that invalid submission

#### Scenario: Field is disabled or has an unattributed remote error
- **WHEN** a field is disabled or an authentication error cannot be attributed safely to one field
- **THEN** the disabled state remains visually distinct or the existing global error presentation is retained, respectively

### Requirement: Keyboard Support
The Email and Password inputs SHALL provide appropriate keyboard actions and preserve submit-from-keyboard behavior.

#### Scenario: User advances from Email
- **WHEN** the user presses the next action on the Email keyboard
- **THEN** focus moves to the Password input
- **AND** the email keyboard presents an email-appropriate input mode

#### Scenario: User submits from Password
- **WHEN** the user presses the done action on the Password keyboard
- **THEN** the same validated submit action as the Entrar or Crear cuenta CTA is executed
- **AND** the existing Authentication/ViewModel contract is used unchanged

### Requirement: Authentication Contract Preservation
The field redesign SHALL NOT change the existing Authentication, `PetViewModel`, Firebase, or repository contracts.

#### Scenario: Valid form submission
- **WHEN** the user submits valid Email and Password values
- **THEN** the existing sign-in or sign-up callback is invoked with the same values and mode as before

#### Scenario: Registration form remains available
- **WHEN** the user switches to Registration mode
- **THEN** the redesigned Email and Password inputs remain available with the same field behavior
- **AND** the existing display-name field and Create account action remain available
