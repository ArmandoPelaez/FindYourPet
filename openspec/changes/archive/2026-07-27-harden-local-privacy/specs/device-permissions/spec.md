## ADDED Requirements

### Requirement: Manifest Declares Only Implemented Permissions
The Android manifest SHALL declare only permissions required by real implemented flows in the current app stage.

#### Scenario: Current stage only needs internet
- **GIVEN** camera, gallery, GPS, push notification, and backend flows are not implemented as real production features
- **WHEN** the Android manifest is inspected
- **THEN** the manifest declares `android.permission.INTERNET` and no camera, location, contacts, storage, microphone, SMS, phone, or notification permissions

#### Scenario: Future sensitive permission requires a feature flow
- **GIVEN** a future change adds camera, media, location, notification, or other sensitive Android permission
- **WHEN** the change is proposed
- **THEN** the proposal and tasks include the user-visible flow, runtime permission handling, denial behavior, and validation

### Requirement: Simulated Features Do Not Request Runtime Permissions
The app SHALL NOT request runtime permissions for simulated photo, location, notification, chat, or contact flows.

#### Scenario: Simulated location uses manual/local input
- **GIVEN** the sighting flow still uses typed or seeded location values
- **WHEN** the user opens the sighting UI
- **THEN** the app does not request fine or coarse location permission

#### Scenario: Preset photo flow avoids camera permission
- **GIVEN** photo upload still uses preset/demo assets
- **WHEN** the user creates or reports a pet post
- **THEN** the app does not request camera or media permissions

#### Scenario: Permission denial states are required before permission use
- **GIVEN** a future feature needs a runtime permission
- **WHEN** that feature is implemented
- **THEN** the UI handles granted, denied, permanently denied, and unavailable states before the permission is considered production-ready
