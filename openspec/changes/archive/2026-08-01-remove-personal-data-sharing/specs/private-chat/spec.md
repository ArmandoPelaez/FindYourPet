## ADDED Requirements

### Requirement: Chat Is The Only App-Mediated Contact Path
The app SHALL provide in-app private chat as the only app-mediated contact path between a pet owner and a reporter.

#### Scenario: Reporter contacts owner
- **GIVEN** a reporter submits or opens a sighting-derived conversation
- **WHEN** the reporter wants to communicate with the owner
- **THEN** the app provides the private chat and does not provide phone, email, address, or external-contact actions

#### Scenario: Owner contacts reporter
- **GIVEN** an owner opens a private chat with a reporter
- **WHEN** the owner wants to follow up
- **THEN** the app provides the private chat and does not provide an app-managed personal-data sharing control

## MODIFIED Requirements

### Requirement: Contact Sharing Is Owner-Controlled
The system SHALL retire owner-controlled contact sharing. No chat participant SHALL be able to enable, revoke, read, or depend on an app-managed contact-sharing state for direct personal data.

#### Scenario: Reporter attempts to reveal owner contact
- **GIVEN** user B is the reporter in a chat session
- **WHEN** user B attempts to access a retired owner contact sharing path
- **THEN** the backend denies the request and the chat UI shows no contact reveal state

#### Scenario: Owner opens chat actions
- **GIVEN** user A is the owner participant in chat A
- **WHEN** user A opens the chat actions
- **THEN** the app provides messaging actions only and no contact-sharing toggle

#### Scenario: Legacy contact sharing state exists
- **GIVEN** chat A contains a legacy active contact-sharing flag
- **WHEN** either participant opens chat A
- **THEN** the system ignores that flag and does not change unrelated chats or messages

### Requirement: Contact Share Events Are Auditable
The system SHALL stop creating contact share or revoke system events because app-managed contact sharing is retired. Existing legacy events SHALL be displayed, if at all, as generic historical system messages without exposing contact values or active availability.

#### Scenario: Owner would have shared contact
- **GIVEN** the owner is in a chat
- **WHEN** the owner opens current chat controls
- **THEN** no action exists that records a contact share event

#### Scenario: Legacy share event is present
- **GIVEN** a prior chat history contains a contact share or revoke event
- **WHEN** the chat message list renders
- **THEN** the event does not expose phone, email, address, precise coordinates, or current contact availability
