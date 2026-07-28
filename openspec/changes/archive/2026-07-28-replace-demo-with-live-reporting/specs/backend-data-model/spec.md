## ADDED Requirements

### Requirement: Production Media References
The backend model SHALL store production media references as Cloudinary metadata associated with the owning post or sighting and SHALL classify photo data as sensitive.

#### Scenario: Post stores uploaded media reference
- **GIVEN** a signed-in owner uploads a pet photo
- **WHEN** the pet post document is created
- **THEN** the document references the uploaded media by Cloudinary secure URL, provider and public ID instead of a preset asset URI

#### Scenario: Sighting stores uploaded media reference
- **GIVEN** a reporter uploads sighting photo evidence
- **WHEN** the sighting document is created
- **THEN** the document references the uploaded media and links it to the sighting, post owner and reporter identities

### Requirement: Production Location Metadata
The backend model SHALL distinguish GPS-captured coordinates, manual/coarse location labels and public-safe location display fields.

#### Scenario: GPS sighting is stored
- **GIVEN** a reporter consents to GPS capture
- **WHEN** the sighting is persisted
- **THEN** the backend document records precise coordinates as sensitive fields and records the source as device-captured

#### Scenario: Coarse location is stored
- **GIVEN** a user provides an approved manual or approximate location
- **WHEN** the post or sighting is persisted
- **THEN** the backend document marks the location source separately from precise GPS coordinates
