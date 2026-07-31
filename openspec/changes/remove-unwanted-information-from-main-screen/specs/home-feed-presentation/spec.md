## MODIFIED Requirements

### Requirement: Status and identity presentation
The home alert card SHALL make the pet status and identity prominent without hiding the pet photo or duplicating unrelated controls. When breed is available, the home alert card SHALL show it as the compact purple identity chip and MUST NOT remove that chip as part of the species and duplicate-attribute cleanup.

#### Scenario: Status is available
- **GIVEN** a pet post has a lost, sighted, or reunited status
- **WHEN** the image area is displayed
- **THEN** a readable status pill is shown over or near the image area without covering the pet's main visual subject

#### Scenario: Identity is displayed
- **GIVEN** a pet post has a name and breed
- **WHEN** the information area is displayed
- **THEN** the pet name is the most prominent text and the breed is presented as a compact purple identity chip

### Requirement: Attribute sections use available post data
The home alert card SHALL present remaining available pet attributes in compact sections and MUST NOT invent values for attributes that are not present in the current post data. The home alert card MUST NOT display `Especie` as an attribute and MUST NOT display breed again as a separate `Raza` attribute block.

#### Scenario: Existing displayable attributes are available
- **GIVEN** a pet post has species, color, breed, and characteristics
- **WHEN** the attribute area is displayed
- **THEN** color and characteristics remain visible in compact, scannable sections
- **THEN** no attribute block labeled `Especie` is displayed
- **THEN** no attribute block labeled `Raza` is displayed
- **THEN** the breed remains visible only through the compact purple identity chip when the breed value is not blank

#### Scenario: Optional attributes are absent
- **GIVEN** the current post data does not include an attribute shown in the reference mockup, such as age or gender
- **WHEN** the card is displayed
- **THEN** the UI omits that attribute or uses another available post field instead of showing placeholder, fake, or hardcoded values

#### Scenario: Species references are removed from affected home feed text
- **GIVEN** home feed UI text, home feed share text, tests, specs, and docs affected by this change are reviewed
- **WHEN** they describe visible home feed card content
- **THEN** they do not include the user-facing `Especie` label or present a species value as part of the home feed card

### Requirement: In-card action controls
The home alert card SHALL keep the sighting action as the primary in-card control and SHALL present sharing as a secondary control only with privacy-safe content that does not reintroduce removed species presentation.

#### Scenario: User can report a sighting
- **GIVEN** a post is not reunited and the current user is allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the "Lo he visto" action is visually primary and navigates to the existing sighting alert flow

#### Scenario: User cannot report a sighting
- **GIVEN** the post is reunited or the current user is not allowed to report a sighting
- **WHEN** the action area is displayed
- **THEN** the primary sighting action is not shown as an available action

#### Scenario: User shares a post
- **GIVEN** the secondary share control is available
- **WHEN** the user taps it
- **THEN** the app opens the platform share flow with a summary that excludes owner phone, owner email, exact coordinates, private messages, hidden contact data, and the `Especie` label or species-only line
