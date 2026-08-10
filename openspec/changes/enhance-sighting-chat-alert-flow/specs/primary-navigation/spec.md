## ADDED Requirements

### Requirement: Sighting Confirmation Returns To Home
The app SHALL return to the Home destination after successful sighting confirmation and SHALL preserve the confirmation route when submission fails.

#### Scenario: Confirmed sighting returns Home
- **GIVEN** the current authenticated user confirms a valid sighting
- **WHEN** the repository reports success after persisting the fan-out
- **THEN** navigation clears the sighting form flow and displays Home

#### Scenario: Failed confirmation does not navigate
- **GIVEN** the current user is confirming a sighting
- **WHEN** validation, media upload or persistence returns an error
- **THEN** the app stays on the confirmation flow, presents the error and allows retry

#### Scenario: Return event is consumed once
- **GIVEN** the submission has already produced a success navigation event
- **WHEN** the screen recomposes or the user returns from Home
- **THEN** the app does not navigate repeatedly or recreate the sighting

