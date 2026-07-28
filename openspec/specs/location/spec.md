# location Specification

## Purpose
TBD - created by archiving change replace-demo-with-live-reporting. Update Purpose after archive.
## Requirements
### Requirement: Consent-Based Location Capture
The app SHALL capture device location only after the user explicitly requests it in a post or sighting flow and grants the required runtime permission.

#### Scenario: User grants location for a sighting
- **GIVEN** a signed-in user is reporting a sighting
- **WHEN** the user taps the current-location action and grants location permission
- **THEN** the app fills the sighting with the captured location and records that the location was consented

#### Scenario: User denies location permission
- **GIVEN** a signed-in user is reporting a sighting
- **WHEN** the user denies location permission
- **THEN** the app does not capture GPS coordinates and shows a denial state without submitting the sighting

### Requirement: Precise Location Is Protected
The system SHALL treat latitude, longitude and precise address data as sensitive and SHALL expose them only to authorized users according to post, sighting and contact-sharing rules.

#### Scenario: Public feed hides precise coordinates
- **GIVEN** a pet post contains precise coordinates
- **WHEN** another signed-in user views the shared feed
- **THEN** the app shows only allowed coarse location information and hides exact coordinates

#### Scenario: Owner reads sighting location
- **GIVEN** a reporter submits a sighting with consented coordinates
- **WHEN** the post owner opens the sighting detail
- **THEN** the owner can view the authorized sighting location detail

### Requirement: Location Fallback Is Explicit
The app SHALL allow only product-approved fallback location input when device location is unavailable or denied, and SHALL distinguish fallback input from GPS-captured coordinates.

#### Scenario: Location service unavailable
- **GIVEN** device location is unavailable
- **WHEN** the user reports a sighting
- **THEN** the app allows an explicit manual/coarse location field if the report remains valid for production

#### Scenario: Manual location is not GPS
- **GIVEN** a user enters a manual location label
- **WHEN** the sighting is saved
- **THEN** the backend document does not mark the value as GPS-captured coordinates

