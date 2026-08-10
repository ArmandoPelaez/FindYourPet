## MODIFIED Requirements

### Requirement: Participant-Only Message Sending
The system SHALL allow only chat participants to create messages, SHALL require `senderId` to match the signed-in Firebase `uid`, and SHALL support an authorized `sighting_alert` message as the first message of a sighting-derived chat.

#### Scenario: Reporter sends alert message
- **GIVEN** user B is the reporter participant in a chat and submits a valid sighting for user A's post
- **WHEN** the sighting fan-out creates the first chat message
- **THEN** the backend creates a message with `type = sighting_alert`, `senderId` equal to user B's Firebase `uid`, and references matching the sighting and post

#### Scenario: Reporter creates first message with new chat session
- **GIVEN** user B submits the first sighting for user A's post
- **WHEN** the first `sighting_alert` message is created in the same backend batch as the chat session
- **THEN** backend rules validate the post owner/reporter membership from the resulting chat session before accepting the message

#### Scenario: Participant sends normal follow-up
- **GIVEN** a sighting alert exists in a chat
- **WHEN** user A or user B sends a normal text message
- **THEN** the message is appended to the same participant-only conversation and does not replace or mutate the alert

#### Scenario: Non-participant spoofs an alert
- **GIVEN** user C is not a participant in the A/B chat
- **WHEN** user C attempts to create a `sighting_alert` or normal message with forged participant ids
- **THEN** the backend denies the write

## ADDED Requirements

### Requirement: Sighting Alert Is Conversational Content
The chat timeline SHALL render each `sighting_alert` as a readable message card containing its authorized sighting information, with an optional photo attachment and a normal composer available for follow-up messages.

#### Scenario: Alert with photo is rendered
- **GIVEN** a participant opens a chat containing a `sighting_alert` with a valid photo reference
- **WHEN** the message timeline is displayed
- **THEN** the alert card shows the photo as an attachment/preview together with authorized location, general details, timestamp and pet/post context

#### Scenario: Alert without photo is rendered
- **GIVEN** a participant opens a chat containing a `sighting_alert` without photo evidence
- **WHEN** the message timeline is displayed
- **THEN** the alert card shows location and general details without a blank image placeholder or layout gap

#### Scenario: Chat composer remains available
- **GIVEN** a participant has viewed a sighting alert
- **WHEN** the chat screen finishes rendering
- **THEN** the normal participant-only message composer remains enabled unless the conversation itself is unavailable

#### Scenario: Legacy message remains readable
- **GIVEN** a chat contains a legacy text or system message without `sighting_alert` fields
- **WHEN** the client renders the timeline
- **THEN** it uses the legacy safe renderer and does not fail because the new fields are absent

