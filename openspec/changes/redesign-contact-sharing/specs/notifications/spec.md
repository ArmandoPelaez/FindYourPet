## MODIFIED Requirements

### Requirement: Notification Content Is Minimised
The system SHALL avoid storing or displaying full sensitive message, location or contact data in notification preview fields, local notification text, and push notification payloads.

#### Scenario: Chat notification is created
- **GIVEN** a participant sends a private chat message
- **WHEN** the recipient notification is created
- **THEN** the notification preview uses generic text and links to the chat instead of exposing full message body or contact fields

#### Scenario: Contact share notification is created
- **GIVEN** an owner shares contact inside a chat
- **WHEN** the recipient notification or push payload is created
- **THEN** it uses generic contact availability text and links to the chat without phone, email, address or precise coordinates

#### Scenario: Contact revoke notification is created
- **GIVEN** an owner revokes contact sharing inside a chat
- **WHEN** the recipient notification or push payload is created
- **THEN** it uses generic contact availability text and does not include previously shared contact values
