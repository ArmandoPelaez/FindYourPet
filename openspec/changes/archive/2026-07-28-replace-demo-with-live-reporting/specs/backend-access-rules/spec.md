## ADDED Requirements

### Requirement: Media References Are Validated
The backend SHALL accept production media references only when they are Cloudinary image references written through an authorized post or sighting flow.

#### Scenario: Owner creates post with Cloudinary photo
- **GIVEN** user A is signed in
- **WHEN** user A creates a pet post with uploaded media metadata
- **THEN** Firestore rules allow the write only when owner identity matches user A and the media provider, public ID, content type and Cloudinary URL are valid

#### Scenario: Sighting without optional photo
- **GIVEN** user B reports a sighting without photo evidence
- **WHEN** user B creates the sighting
- **THEN** Firestore rules allow empty media metadata only for the optional sighting photo path

### Requirement: Precise Location Writes Are Validated
The backend SHALL accept precise location fields only from authorized create/update paths and SHALL deny unauthorized changes to those fields.

#### Scenario: Reporter creates sighting coordinates
- **GIVEN** user B is signed in and reporting user A's post
- **WHEN** user B creates a sighting with precise coordinates
- **THEN** backend rules allow the create only when `reporterId` matches user B and `ownerId` matches the referenced post owner

#### Scenario: User attempts coordinate reassignment
- **GIVEN** a production sighting exists
- **WHEN** any client attempts to update its precise coordinates after creation
- **THEN** backend rules deny the update
