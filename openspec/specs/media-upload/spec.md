# media-upload Specification

## Purpose
TBD - created by archiving change replace-demo-with-live-reporting. Update Purpose after archive.
## Requirements
### Requirement: Real Media Selection For Pet Posts
The app SHALL allow a signed-in user to attach a real photo to a production pet post from camera capture or gallery selection instead of choosing a preset demo asset.

#### Scenario: Owner selects a gallery photo
- **GIVEN** a signed-in user is creating a pet post
- **WHEN** the user selects an image through the supported gallery picker
- **THEN** the create-post form stores a pending real media reference and does not use a preset demo URI

#### Scenario: Owner captures a camera photo
- **GIVEN** a signed-in user grants the required camera flow access
- **WHEN** the user captures a pet photo
- **THEN** the captured image is attached to the create-post form through a scoped app-owned URI

### Requirement: Cloudinary Media Upload
The system SHALL upload production pet and sighting photos to Cloudinary using an unsigned upload preset and reference the uploaded media from backend documents.

#### Scenario: Post photo upload succeeds
- **GIVEN** a signed-in owner attaches a real post photo
- **WHEN** the user submits a valid pet post
- **THEN** the app uploads the photo before creating the production post document and stores the Cloudinary secure URL, provider and public ID in Firestore

#### Scenario: Cloudinary secret is never shipped
- **GIVEN** the app uploads through the unsigned preset
- **WHEN** the Android application is built
- **THEN** the app contains the Cloudinary cloud name and upload preset only, never the Cloudinary API secret

### Requirement: Media Upload Failure Is Recoverable
The app SHALL keep the user on the form with a clear error state when media capture, selection or upload fails.

#### Scenario: Upload fails before post creation
- **GIVEN** a user submits a post with an attached photo
- **WHEN** the photo upload fails
- **THEN** the app does not create the post document and shows a retryable upload error

#### Scenario: Unsupported file is selected
- **GIVEN** a user selects a file that is not an allowed image type or exceeds configured size limits
- **WHEN** the app validates the selected media
- **THEN** the app rejects the media before upload and keeps the form editable

