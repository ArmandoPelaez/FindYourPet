## ADDED Requirements

### Requirement: Sighting Alert Messages Render In Chat
The app SHALL render `sighting_alert` messages as first-class chat timeline items for sighting-derived conversations.

#### Scenario: Owner opens sighting chat
- **GIVEN** user B submitted a valid sighting for user A's lost-pet post
- **WHEN** user A opens the resulting chat
- **THEN** the timeline displays the sighting alert sent by user B with the authorized photo, location and details
- **AND** the chat input is available for user A to continue the conversation

#### Scenario: Reporter opens sighting chat
- **GIVEN** user B submitted a valid sighting for user A's lost-pet post
- **WHEN** user B opens the resulting chat
- **THEN** the timeline displays the sighting alert as B's submitted chat item
- **AND** the chat input is available for user B to continue the conversation

#### Scenario: Sighting has no photo
- **GIVEN** user B submits a valid sighting without photo evidence
- **WHEN** either participant opens the chat
- **THEN** the sighting alert renders location and details without reserving empty or broken photo UI

### Requirement: Sighting Chat Excludes Generic System Notices
The app SHALL NOT display the generic internal-chat notice or generic sighting system-message cards in the sighting send/receive chat flow.

#### Scenario: Owner opens new sighting chat
- **GIVEN** user A receives a sighting-derived chat from user B
- **WHEN** user A opens the chat
- **THEN** the chat does not display the "Chat interno" notice block
- **AND** the chat does not display a "MENSAJE DEL SISTEMA" card for the new sighting
- **AND** the chat does not display "Nuevo avistamiento reportado. Abre el detalle para revisar la informacion autorizada."

#### Scenario: Reporter sends sighting alert
- **GIVEN** user B completes the sighting alert form
- **WHEN** the app navigates B to the resulting chat or chat preview
- **THEN** the flow does not show the removed internal-chat notice or generic system-message text

#### Scenario: Legacy generic message exists
- **GIVEN** a chat contains a legacy generic sighting system message
- **WHEN** the chat timeline renders current UI
- **THEN** the app suppresses the removed text or maps it to a safe fallback that does not repeat the removed copy

### Requirement: Conversation Continues After Sighting Alert
The app SHALL allow participant-only text messaging after the sighting alert item is created.

#### Scenario: Owner replies after alert
- **GIVEN** user A is viewing the chat that started from user B's sighting alert
- **WHEN** user A sends a valid chat message
- **THEN** the message is appended to the same conversation after the sighting alert

#### Scenario: Reporter replies after alert
- **GIVEN** user B is viewing the chat that started from B's sighting alert
- **WHEN** user B sends a valid chat message
- **THEN** the message is appended to the same conversation after the sighting alert
