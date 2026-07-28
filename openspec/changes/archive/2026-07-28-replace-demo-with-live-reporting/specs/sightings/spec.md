## ADDED Requirements

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
