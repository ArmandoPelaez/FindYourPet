## 1. Dependencies And Contracts

- [x] 1.1 Review current Gradle dependencies and add only the required production dependencies for Android Photo Picker/camera capture, location and Cloudinary unsigned image upload.
- [x] 1.2 Add typed models for media references, upload state, location source and location permission state.
- [x] 1.3 Add Cloudinary media metadata contract so rules and mappers share provider/public-id fields.
- [x] 1.4 Update remote DTOs/mappers for pet posts and sightings to store uploaded media references and distinguish GPS, manual and coarse location metadata.

## 2. Media Capture And Upload

- [x] 2.1 Replace preset-photo selection in create-post UI with real gallery selection and camera capture entry points.
- [x] 2.2 Replace preset-photo selection in sighting UI with real gallery selection and camera capture entry points.
- [x] 2.3 Implement scoped camera output URI handling and cleanup for cancelled or failed captures.
- [x] 2.4 Implement Cloudinary unsigned upload for post photos and sighting photos with success and retryable failure states.
- [x] 2.5 Block production submission when media is missing, unsupported, too large or still uploading.
- [x] 2.6 Add tests for media validation, upload state transitions and Firestore media reference mapping.

## 3. Location Capture

- [x] 3.1 Add location permission request flow launched only from explicit user actions in post/sighting screens.
- [x] 3.2 Implement location provider abstraction for granted, denied, permanently denied, unavailable and successful GPS capture states.
- [x] 3.3 Store precise GPS coordinates only in authorized backend fields and render public-safe coarse labels on feed/detail surfaces.
- [x] 3.4 Support approved manual/coarse fallback input when device location is unavailable or denied.
- [x] 3.5 Add tests for location validation, source classification and mapper behavior.

## 4. In-App Alert Flow

- [x] 4.1 Ensure valid sightings create a backend in-app notification for the post owner.
- [x] 4.2 Ensure valid sightings create or reuse the owner/reporter chat path with a privacy-safe system message.
- [x] 4.3 Ensure notification records and chat previews include only generic text and target ids, never contact data, exact coordinates, full notes, photo URLs or private messages.
- [x] 4.4 Update notification and chat entry points so the owner can open the relevant authenticated target after returning to the app.
- [x] 4.5 Add tests for in-app alert privacy and owner-only notification visibility.

## 5. Form Validation And Demo Isolation

- [x] 5.1 Add create-post validation for required fields, signed-in owner identity, real media, allowed location state and upload completion.
- [x] 5.2 Add sighting validation for signed-in reporter identity, target post ownership, location consent/fallback state, optional media upload completion and notes constraints.
- [x] 5.3 Remove `seedInitialDataIfNeeded` from authenticated production startup and repository/ViewModel flows.
- [x] 5.4 Gate any remaining demo seed behavior behind explicit non-production/demo mode and ensure demo ids cannot grant production ownership.
- [x] 5.5 Update empty, loading and error states so the app works with no backend posts, sightings, chats or notifications.

## 6. Backend Rules And Privacy

- [x] 6.1 Update Firestore rules for media references and precise location fields.
- [x] 6.2 Keep Firebase Storage out of scope until Blaze and validate Cloudinary media references through Firestore rules.
- [x] 6.3 Add or update static/rules validation tests for unauthorized media reads, coordinate updates and no sensitive notification preview fields.
- [x] 6.4 Review Android manifest, backup rules and data extraction rules for the new sensitive permissions and local media/location cache behavior.

## 7. Validation

- [x] 7.1 Run `./gradlew.bat testDebugUnitTest` and fix any unit test failures.
- [x] 7.2 Run `./gradlew.bat assembleDebug` and fix build failures.
- [x] 7.3 Validate Firestore rules and Cloudinary unsigned upload behavior in a documented non-production configuration.
- [x] 7.4 Manually validate post creation with camera photo, gallery photo, denied media access and upload failure.
- [x] 7.5 Manually validate sighting submission with granted GPS, denied GPS, unavailable GPS/manual fallback and optional photo upload.
- [x] 7.6 Manually validate in-app notification receipt, tap-to-target behavior and privacy-safe notification text.
- [x] 7.7 Manually validate a signed-in user with empty backend data sees empty states and no demo records from `seedInitialDataIfNeeded`.
- [x] 7.8 Document validation evidence and any remaining limitations before applying/archive readiness.
