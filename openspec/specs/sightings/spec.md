# sightings Specification

## Purpose
Define backend sighting creation, visibility, immutability and chat fan-out.
## Requirements
### Requirement: Backend Sighting Creation
The system SHALL persist sighting reports in the backend with `postId`, `ownerId`, `reporterId`, location data, notes, optional photo and timestamp.

#### Scenario: Reporter submits a sighting
- **GIVEN** user B is signed in and views user A's lost pet post
- **WHEN** user B submits a sighting report
- **THEN** the backend creates a sighting whose `reporterId` is user B and whose `ownerId` is derived from the referenced post

### Requirement: Sighting Delivery To Owner
The system SHALL make each sighting visible to the post owner and the reporter, and SHALL deny it to unrelated users.

#### Scenario: Owner receives sighting
- **GIVEN** user B submits a sighting for user A's post
- **WHEN** user A opens the post's sightings or notification target
- **THEN** user A can read the sighting details

#### Scenario: Unrelated user cannot read sighting
- **GIVEN** user C is neither the owner nor reporter of a sighting
- **WHEN** user C attempts to read the sighting
- **THEN** the backend denies access

### Requirement: Sightings Are Append-Only
The system SHALL prevent clients from updating or deleting production sighting reports after creation.

#### Scenario: Reporter attempts to edit a submitted sighting
- **GIVEN** user B has submitted a sighting
- **WHEN** user B attempts to update its notes or coordinates
- **THEN** the backend denies the update

### Requirement: Sighting Creates Conversation Path
The system SHALL create or reuse a chat session between the post owner and reporter when a sighting is accepted by the backend.

#### Scenario: Sighting starts chat
- **GIVEN** user B submits the first sighting for user A's post
- **WHEN** the backend write succeeds
- **THEN** the app opens or offers a chat session containing user A and user B as participants

#### Scenario: Sighting fan-out is atomic
- **GIVEN** user B submits a sighting for user A's post
- **WHEN** the backend accepts the sighting write
- **THEN** the sighting, chat session, initial chat message and owner notification are committed together

### Requirement: Real Sighting Evidence
The app SHALL allow signed-in reporters to submit sightings with consented real location data and optional real photo evidence instead of preset demo media or simulated GPS values.

#### Scenario: Reporter submits sighting with GPS and photo
- **GIVEN** user B is signed in and views user A's lost pet post
- **WHEN** user B submits a valid sighting with granted location and uploaded photo evidence
- **THEN** the backend creates a sighting linked to user A's post with user B as reporter

#### Scenario: Simulated sighting values are rejected
- **GIVEN** the sighting form contains a preset photo URI or simulated coordinate source
- **WHEN** the reporter submits the production sighting
- **THEN** the app blocks the write and asks for valid real input or approved fallback input

### Requirement: Sighting Validation Precedes Fan-Out
The app SHALL validate sighting fields, media upload result, location consent/fallback state and authenticated user identity before creating chat, notification or backend fan-out records.

#### Scenario: Invalid sighting does not notify owner
- **GIVEN** a reporter submits a sighting with missing required location information
- **WHEN** validation fails
- **THEN** the app creates no sighting, chat message or owner notification

