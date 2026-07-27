## 1. Backend Model And Mapping

- [x] 1.1 Define Firestore collection names, field constants and document id strategy for users, pet posts, sightings, chat sessions, messages and user notifications.
- [x] 1.2 Add remote DTO/model classes for pet posts, sightings, chat sessions, chat messages and notifications.
- [x] 1.3 Add mapper functions between Firestore DTOs, domain/UI models and existing Room entities.
- [x] 1.4 Replace use of demo ownership ids in production paths with Firebase Auth `uid`.
- [x] 1.5 Add unit tests for mapper defaults, required identity fields, timestamp handling and sensitive-field boundaries.

## 2. Shared Pet Posts

- [x] 2.1 Add backend-backed post observation for feed, post detail and owner posts using Firestore listeners.
- [x] 2.2 Route post creation through Firestore with `ownerId` set from the signed-in user.
- [x] 2.3 Route post status, visibility and delete/archive actions through owner-only backend writes.
- [x] 2.4 Update `PetViewModel` state so feed/detail screens can render loading, empty, error, cache and pending-write states.
- [x] 2.5 Keep Room writes as cache updates from remote snapshots, not as the production source of truth.

## 3. Sightings Flow

- [x] 3.1 Create backend sighting writes that derive `ownerId` from the referenced backend post.
- [x] 3.2 Add owner/reporter-only sighting reads for post detail and notification targets.
- [x] 3.3 Prevent client-side edit/delete actions for submitted sightings.
- [x] 3.4 Create or reuse the owner/reporter chat session after a sighting write succeeds.
- [x] 3.5 Add tests for sighting ownership routing, reporter identity and unauthorized access handling.

## 4. Private Chat

- [x] 4.1 Add backend chat session observation filtered to sessions where the signed-in user is owner or reporter.
- [x] 4.2 Add backend message listeners for selected chat sessions.
- [x] 4.3 Route message sending through Firestore and require `senderId` to match the signed-in user.
- [x] 4.4 Replace user-facing "chat local" copy in production chat flows.
- [x] 4.5 Add tests for participant checks, sender mismatch handling, immutable messages and contact-sharing ownership.

## 5. Notifications

- [x] 5.1 Add per-user backend notification records under `users/{uid}/notifications/{notificationId}`.
- [x] 5.2 Create notification records for sighting, chat and contact-sharing events with minimized preview content.
- [x] 5.3 Replace global Room notification reads with the signed-in user's backend notification inbox.
- [x] 5.4 Route mark-as-read through recipient-only backend updates and mirror the result to local cache if enabled.
- [x] 5.5 Keep Android system notifications generic and free of message body, contact data and exact location.

## 6. Room Cache And Session Boundaries

- [x] 6.1 Isolate demo seed data so authenticated production screens do not inject local demo posts, chats or notifications.
- [x] 6.2 Clear or partition cached private chats, sightings and notifications when the authenticated user changes.
- [x] 6.3 Add repository behavior for Firebase unavailable mode that is explicitly demo/local and not presented as production backend.
- [x] 6.4 Add tests that Room cache cannot grant owner or chat participant privileges.

## 7. Firestore Rules And Documentation

- [x] 7.1 Update `firestore.rules` for `users`, `petPosts`, `sightings`, `chatSessions`, `chatSessions/{chatId}/messages` and `users/{uid}/notifications`.
- [x] 7.2 Validate create payloads for required owner, reporter, sender, recipient and participant fields.
- [x] 7.3 Prevent owner, participant and recipient reassignment on update.
- [x] 7.4 Deny all unknown collections and all unauthenticated production access.
- [x] 7.5 Update `docs/firebase-rules-validation.md` with backend model cases and exact validation commands or manual fallback.

## 8. UI States And Privacy Review

- [x] 8.1 Update feed, detail, sighting, chat and notification screens to render loading, empty, error, cache and pending-write indicators.
- [x] 8.2 Ensure sensitive contact, exact location, message and sighting fields are not exposed outside authorized views.
- [x] 8.3 Review app copy so no screen claims realtime delivery, push delivery, encryption or privacy guarantees beyond implemented behavior.
- [ ] 8.4 Manually validate two real signed-in users: shared post visibility, sighting delivery, chat exchange and denied third-user access.
  Note: rerun after publishing the `getAfter()` rules update that fixes initial sighting chat-message validation in the atomic batch.

## 9. Validation

- [x] 9.1 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 9.2 Run `.\gradlew.bat assembleDebug`.
- [x] 9.3 Run Firestore rules validation with Firebase Emulator Suite, or document non-production Firebase manual validation if emulator tests are not wired.
- [x] 9.4 Document final validation results, known limitations and rollback path before closing the change.
