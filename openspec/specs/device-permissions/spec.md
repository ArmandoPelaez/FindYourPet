# device-permissions Specification

## Purpose
Define the Android permission surface allowed by the current implemented app flows.
## Requirements
### Requirement: Manifest Declares Only Implemented Permissions
The Android manifest SHALL declare only permissions required by real implemented flows in the current app stage, including camera, media/photo access and location permissions only when the corresponding production flow is implemented.

#### Scenario: Current stage declares real product permissions
- **GIVEN** camera, gallery and location flows are implemented as real production features
- **WHEN** the Android manifest is inspected
- **THEN** the manifest declares only the permissions required by those implemented flows plus `android.permission.INTERNET`

#### Scenario: Future sensitive permission requires a feature flow
- **GIVEN** a future change adds camera, media, location, notification, or other sensitive Android permission
- **WHEN** the change is proposed
- **THEN** the proposal and tasks include the user-visible flow, runtime permission handling, denial behavior, and validation

### Requirement: Simulated Features Do Not Request Runtime Permissions
The app SHALL NOT request runtime permissions for simulated photo, location, chat, or contact flows, and SHALL request runtime permissions only from real user-initiated production flows.

#### Scenario: Real location uses runtime permission
- **GIVEN** the sighting flow captures GPS coordinates from the device
- **WHEN** the user asks to use current location
- **THEN** the app requests the required location permission and handles granted, denied, permanently denied and unavailable states

#### Scenario: Real media input uses scoped access
- **GIVEN** the user creates or reports a pet post with real media
- **WHEN** camera or gallery access is required
- **THEN** the app requests only the permission or picker access required for the selected action

#### Scenario: Permission denial states are required before permission use
- **GIVEN** a feature needs a runtime permission
- **WHEN** that feature is implemented
- **THEN** the UI handles granted, denied, permanently denied, and unavailable states before the permission is considered production-ready

