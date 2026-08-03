## ADDED Requirements

### Requirement: Chat Flow Omits Internal Privacy Warning Blocks
The app SHALL omit the internal privacy warning block from the sighting chat send/receive flow while continuing to enforce privacy through data minimization and backend authorization.

#### Scenario: Owner receives sighting chat
- **GIVEN** user B reports a sighting for user A's post
- **WHEN** user A opens the resulting chat
- **THEN** the chat does not display the "Chat interno" warning block
- **AND** the chat still hides app-managed phone, email, contact address and external-contact actions

#### Scenario: Reporter opens sent sighting chat
- **GIVEN** user B submitted a sighting alert for user A's post
- **WHEN** user B opens the resulting chat
- **THEN** the chat does not display the internal privacy warning block
- **AND** the chat does not expose owner contact data outside participant messages

### Requirement: App-Generated Sighting Chat Content Avoids Personal Contact Data
The app SHALL NOT inject phone, email, contact address or app-managed contact-sharing state into sighting alert chat items, chat previews, notification previews or routing payloads.

#### Scenario: Sighting alert is rendered
- **GIVEN** a sighting alert appears inside an authorized chat
- **WHEN** the app renders the alert
- **THEN** the app shows the submitted sighting evidence and does not add owner or reporter contact fields

#### Scenario: Chat preview is rendered
- **GIVEN** a chat list item references a sighting alert
- **WHEN** the preview text is displayed
- **THEN** it does not include phone, email, contact address, exact coordinates or full private notes
