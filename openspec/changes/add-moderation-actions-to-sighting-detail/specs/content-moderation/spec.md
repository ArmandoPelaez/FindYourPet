## ADDED Requirements

### Requirement: Owner Can Open Sighting Moderation Actions

The app SHALL show a contextual moderation menu in Sighting Detail only when the authenticated user is the sighting's post owner, and SHALL keep moderation actions independent of Chat.

#### Scenario: Owner opens the moderation menu

- **GIVEN** user B owns the post for a sighting with a valid `sightingId` and a non-blank reporter id for user A
- **WHEN** user B opens the contextual menu in Sighting Detail
- **THEN** the app shows `Reportar contenido` and `Bloquear usuario` when the corresponding action is valid
- **AND** neither action uses `chatId`, `ChatSessionEntity`, `ChatMessageEntity` or a conversation to identify user A

#### Scenario: Non-owner cannot moderate a sighting

- **GIVEN** user A is the reporter or another authenticated user is not the post owner
- **WHEN** that user opens Sighting Detail
- **THEN** the app does not expose owner-only moderation actions
- **AND** the backend would deny any direct moderation write from that user

#### Scenario: Block action is hidden without an identifiable reporter

- **GIVEN** a sighting has no non-blank reporter id or the owner already has an active block for that reporter
- **WHEN** the owner opens the moderation menu
- **THEN** `Bloquear usuario` is not shown
- **AND** no `Desbloquear usuario` action is introduced

### Requirement: Owner Can Report Sighting Content

The app SHALL let the authorized post owner select a report reason, cancel without changes, and persist an independent moderation report associated with the sighting.

#### Scenario: Owner submits a content report

- **GIVEN** user B owns a sighting identified by `sighting_123`
- **WHEN** user B selects `Reportar contenido`, chooses a reason and confirms
- **THEN** the system persists a report containing `sightingId = sighting_123`, `reportingUserId = user B`, the selected reason, creation time and initial pending status
- **AND** it records `reportedUserId` as the sighting reporter when that identity is available
- **AND** the app shows success feedback

#### Scenario: Owner cancels a content report

- **GIVEN** the report reason selector is open
- **WHEN** the owner cancels or dismisses it without confirmation
- **THEN** no moderation report is created or changed

#### Scenario: Repeated report taps are idempotent

- **GIVEN** the owner confirms the same sighting and reason more than once because of repeated taps or a retry
- **WHEN** the writes are processed
- **THEN** the system does not create duplicate equivalent reports
- **AND** the original sighting remains available

#### Scenario: Report failure is controlled

- **GIVEN** the report write fails
- **WHEN** the app receives the failure
- **THEN** the app does not crash
- **AND** it keeps the sighting unchanged, records diagnostic information and allows a retry
- **AND** it does not display raw Firebase or Firestore exception text to the user

### Requirement: Owner Can Block A Sighting Reporter

The app SHALL require confirmation before persisting a unique block from the post owner to the sighting reporter, and SHALL preserve historical sightings after the block.

#### Scenario: Owner confirms a block

- **GIVEN** user B owns the post and user A is the reporter of the selected sighting
- **WHEN** user B selects `Bloquear usuario` and confirms
- **THEN** the system persists a unique relation with `blockerUserId = user B`, `blockedUserId = user A` and creation time
- **AND** the relation records the originating sighting for authorization/audit when supported
- **AND** the app shows success feedback

#### Scenario: Owner cancels a block

- **GIVEN** the block confirmation dialog is open
- **WHEN** the owner cancels or dismisses it
- **THEN** no block relation is created or changed

#### Scenario: Duplicate block is avoided

- **GIVEN** the owner confirms the same owner/reporter block more than once
- **WHEN** the writes are processed
- **THEN** only one effective block relation exists
- **AND** existing historical sightings remain available

#### Scenario: Block failure is controlled

- **GIVEN** the block write fails
- **WHEN** the app receives the failure
- **THEN** the app does not crash
- **AND** it keeps the previous moderation state, records diagnostics and allows a retry
- **AND** it does not expose raw backend exception text

### Requirement: Blocked Reporters Cannot Create New Sightings

The system SHALL reject a new sighting before persistence or fan-out when the post owner has blocked the authenticated reporter for that owner.

#### Scenario: Blocked reporter submits a new sighting

- **GIVEN** user B has an active block for user A
- **AND** user A attempts to report a sighting for a post owned by user B
- **WHEN** the sighting submission is validated
- **THEN** the operation is rejected with controlled user feedback
- **AND** no `SightingAlertEntity`, notification, chat session, chat message or related Chat record is created

#### Scenario: Block does not affect another owner

- **GIVEN** user B blocked user A
- **AND** user A reports a sighting for a post owned by user C
- **WHEN** the sighting submission is validated
- **THEN** the block between B and A does not reject the report for C

#### Scenario: Historical sighting remains visible

- **GIVEN** user B blocks user A after an earlier sighting exists
- **WHEN** user B opens Activity or the earlier Sighting Detail
- **THEN** the historical sighting remains available according to existing authorization rules

### Requirement: Moderation UI Uses Existing Design Rules

The moderation menu, dialogs, selectors, feedback and operation states SHALL use stable Material 3 components, existing design tokens, accessible touch targets and both Light and Dark Theme.

#### Scenario: Moderation operation is loading

- **GIVEN** a report or block operation is being submitted
- **WHEN** the operation is in progress
- **THEN** the relevant confirm action is disabled against repeated submission
- **AND** the existing detail content remains stable

#### Scenario: Moderation feedback is readable in both themes

- **GIVEN** the detail screen is shown in Light Theme or Dark Theme
- **WHEN** the owner opens, confirms or cancels a moderation action
- **THEN** menu, dialog, error and success feedback remain readable and accessible
- **AND** no new hardcoded visual value replaces an existing design token
