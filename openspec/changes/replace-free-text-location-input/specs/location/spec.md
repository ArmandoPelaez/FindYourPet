## MODIFIED Requirements

### Requirement: Consent-Based Location Capture
The app SHALL capture device location only after the user explicitly requests it in a post or sighting flow and grants the required runtime permission. Google Maps map selection SHALL be a user-confirmed alternative and SHALL NOT be treated as device GPS capture.

#### Scenario: User grants location for a post
- **GIVEN** a signed-in user is creating a lost-pet post
- **WHEN** the user chooses `Usar mi ubicacion actual` and grants location permission
- **THEN** the app fills the post location with captured coordinates, marks the source as device GPS and allows confirmation before publishing

#### Scenario: User denies location permission
- **GIVEN** a signed-in user is creating a post or reporting a sighting
- **WHEN** the user denies location permission
- **THEN** the app does not capture GPS coordinates and offers Google Maps or manual reference alternatives where the flow permits

### Requirement: Location Fallback Is Explicit
The app SHALL allow product-approved fallback location input when device location is unavailable or denied, and SHALL distinguish fallback input from GPS-captured coordinates. For lost-pet posts, the approved fallbacks SHALL include a confirmed Google Maps point and a manual coarse reference.

#### Scenario: Location service unavailable
- **GIVEN** device location is unavailable
- **WHEN** the user creates a post or reports a sighting
- **THEN** the app allows an explicit approved fallback location if the flow remains valid for production

#### Scenario: User selects a map point
- **GIVEN** a user opens the Google Maps location picker
- **WHEN** the user taps a point and confirms it
- **THEN** the app stores the selected latitude and longitude as a user-selected non-GPS location and shows a non-empty public-safe label

#### Scenario: Map selection attempts an automatic reference
- **GIVEN** a user taps a valid point in the Google Maps picker
- **WHEN** the app attempts reverse geocoding with Android `Geocoder`
- **THEN** the app uses the first usable address or locality as the public-safe label without changing the selected coordinates

#### Scenario: Map reference uses a safe label when automatic lookup fails
- **GIVEN** Android `Geocoder` is unavailable, fails or returns no usable address for the selected point
- **WHEN** the automatic lookup finishes
- **THEN** the app keeps the selected map coordinates, uses `Punto seleccionado en el mapa` and does not open or require manual input automatically

#### Scenario: Manual location is not GPS
- **GIVEN** a user enters a manual location label
- **WHEN** the post or sighting is saved
- **THEN** the backend document does not mark the value as GPS-captured coordinates

#### Scenario: Map provider fails
- **GIVEN** Google Maps cannot load because the network or API key is unavailable
- **WHEN** the user is selecting a post location
- **THEN** the app shows a recoverable error and preserves the manual reference alternative without saving an empty location
