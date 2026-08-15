## 1. Sighting data access

- [x] 1.1 Add repository APIs and `BackendSyncState` flow to read one `sightings/{sightingId}` document directly by `sightingId`.
- [x] 1.2 Add the local/cache fallback needed by the direct sighting read without changing Room schema or making Room authoritative for production authorization.
- [x] 1.3 Preserve owner/reporter Firestore authorization behavior and map missing, denied and malformed documents to the existing error state conventions.
- [x] 1.4 Add repository/mapping tests proving `SightingAlertEntity.notes`, location, timestamp, optional photo metadata and `postId` survive the direct read.

## 2. ViewModel and navigation contract

- [x] 2.1 Add a dedicated sighting-detail state keyed by `sightingId`, independent of `activeChatId`, `activeChatSession` and `activeChatMessages`.
- [x] 2.2 Resolve the associated pet context from `sighting.postId` using the existing post read path, while allowing the detail to render if optional post context is unavailable.
- [x] 2.3 Add an internal detail route/screen entry point that receives `sightingId` without changing the current notification click behavior.
- [x] 2.4 Add ViewModel/navigation tests proving the detail identifier is a `sightingId` and no Chat selection or Chat creation occurs.

## 3. Read-only Compose screen

- [x] 3.1 Create the read-only sighting detail screen with loading, success, error and optional-data states.
- [x] 3.2 Render the associated pet context, sighting location label, date/time, non-empty notes and optional sighting photo from the correct source fields.
- [x] 3.3 Omit the empty comment section when `notes` is blank and show a bounded missing-context state when the pet post cannot be loaded.
- [x] 3.4 Add `Ver ubicación` only when authorized location data is usable, reusing the existing map mechanism in read-only mode without new runtime permissions.
- [x] 3.5 Ensure the screen has no message input, send/reply action, Chat bubbles, message history, Chat title or Chat creation behavior.
- [x] 3.6 Use Material 3 stable APIs, existing components/tokens and Light/Dark Theme support; do not introduce hardcoded visual values or experimental APIs.
- [x] 3.7 Add Compose/screenshot or static UI tests for success, loading, error, optional notes/photo, location action, no-Chat content and both themes where supported.

## 4. Privacy and regression validation

- [x] 4.1 Verify the detail reads only the authorized sighting and does not add sensitive sighting content to notifications, public post surfaces or Chat documents.
- [x] 4.2 Add regression assertions that the detail never reads `ChatMessageEntity.generalDetails` and does not depend on `ChatSessionEntity`.
- [x] 4.3 Preserve existing Chat, sighting submission, notification, location permission and legacy route behavior outside this screen.

## 5. Verification and handoff

- [x] 5.1 Run `openspec validate "create-sighting-detail-screen" --strict`.
- [x] 5.2 Run `openspec instructions apply --change "create-sighting-detail-screen" --json` and confirm every implementation task is complete.
- [x] 5.3 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 5.4 Run `.\gradlew.bat assembleDebug`.
- [x] 5.5 Run `git diff --check` and review the diff against SCRUM-21, confirming no notification navigation or Chat migration was included.
- [x] 5.6 Manually verify an authorized owner/reporter can open a sighting detail fixture, see the correct notes/photo/location/date, observe loading/error states, and cannot interact as Chat.
- [x] 5.7 Manually verify an unrelated authenticated user receives an authorization/error state and that Light Theme, Dark Theme and supported phone/tablet sizes remain legible.
