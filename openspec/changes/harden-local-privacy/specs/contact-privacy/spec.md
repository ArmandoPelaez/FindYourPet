## ADDED Requirements

### Requirement: Public Pet UI Hides Direct Contact Data By Default
The app SHALL hide owner phone, email, address, and precise location from public pet cards and pet detail surfaces unless an explicit local contact-reveal state authorizes showing that data.

#### Scenario: Public details hide contact fields
- **GIVEN** a pet post has owner phone and email values
- **WHEN** the public pet detail screen is rendered before contact reveal
- **THEN** the phone and email values are not displayed

#### Scenario: Revealed contact fields are intentional
- **GIVEN** the local contact-reveal state is enabled for a supported flow
- **WHEN** the protected contact UI is rendered
- **THEN** the UI shows the contact data together with copy indicating that contact sharing was authorized

#### Scenario: Address and coordinates are not exposed as direct contact
- **GIVEN** a pet or sighting contains an address, location name, latitude, or longitude
- **WHEN** a public-facing screen renders the data
- **THEN** it does not expose precise contact/location data as a substitute for direct contact permission

### Requirement: Contact Sharing Copy Matches Actual Behavior
The app SHALL describe contact sharing as a local demo/contact-reveal behavior unless production authentication, authorization, and backend rules are implemented.

#### Scenario: Copy does not imply production authorization
- **GIVEN** production authentication and backend access rules are not implemented
- **WHEN** users read contact-sharing copy
- **THEN** the copy does not claim production authorization, verified identity, encryption, or server-enforced privacy

#### Scenario: Hidden state explains limited exposure
- **GIVEN** contact data is hidden
- **WHEN** the protected contact component is rendered
- **THEN** the visible text explains that direct contact details are not shown in the public card

### Requirement: Sensitive Contact Data Is Not Sent In Notifications
The app SHALL NOT include phone, email, address, precise coordinates, or full private-message content in local or future notification text.

#### Scenario: Sighting notification avoids precise sensitive data
- **GIVEN** a sighting includes a reporter name, location name, latitude, longitude, and notes
- **WHEN** notification text is generated
- **THEN** the notification avoids phone, email, address, exact coordinates, and full private note content

#### Scenario: Contact-sharing notification avoids direct contact values
- **GIVEN** contact sharing changes state
- **WHEN** a notification or chat preview is generated
- **THEN** it does not expose phone or email values outside the protected in-app contact surface
