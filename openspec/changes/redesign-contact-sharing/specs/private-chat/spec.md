## ADDED Requirements

### Requirement: Contact Share Events Are Auditable
The system SHALL record contact share and revoke actions as generic chat system events without embedding direct contact values in message text.

#### Scenario: Owner shares contact
- **GIVEN** the owner enables contact sharing in a chat
- **WHEN** the system records the chat event
- **THEN** the event identifies that contact availability changed without including phone, email, address or precise coordinates

#### Scenario: Owner revokes contact
- **GIVEN** the owner revokes contact sharing in a chat
- **WHEN** the system records the chat event
- **THEN** the event identifies that contact is no longer available without including prior contact values

## MODIFIED Requirements

### Requirement: Contact Sharing Is Owner-Controlled
The system SHALL allow only the post owner participant to create, update or revoke contact-sharing state for a specific chat session.

#### Scenario: Reporter attempts to reveal owner contact
- **GIVEN** user B is the reporter in a chat session
- **WHEN** user B attempts to enable owner contact sharing
- **THEN** the backend denies the update

#### Scenario: Owner shares contact in one chat
- **GIVEN** user A is the owner participant in chat A
- **WHEN** user A enables contact sharing for chat A
- **THEN** the system creates or activates contact sharing only for chat A

#### Scenario: Owner revokes contact in one chat
- **GIVEN** user A previously shared contact in chat A
- **WHEN** user A revokes contact sharing for chat A
- **THEN** the system deactivates contact sharing for chat A and does not change unrelated chats

#### Scenario: Contact shared in another chat is unavailable
- **GIVEN** user A shared contact in chat A
- **WHEN** a participant opens chat B without an active contact grant
- **THEN** the chat UI hides user A's direct contact data
