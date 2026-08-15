## 1. Sighting submission contract

- [x] 1.1 Refactor `PetRepository.submitSightingAlert` remote fan-out to build and write only the sighting document and owner notification, without constructing or writing `ChatSessionEntity` or `ChatMessageEntity`.
- [x] 1.2 Refactor the local Room fallback of `PetRepository.submitSightingAlert` to insert only the sighting and notification inside its transaction, preserving the existing validation and media/location metadata.
- [x] 1.3 Preserve stable `sightingId`, `idempotencyKey`, `SightingAlertEntity.notes`, authentication, ownership, reporter, location and permission validation across both repository paths.
- [x] 1.4 Set new sighting notifications to `targetId = sightingId`, retain `sightingId` and `postId`, omit `chatId`, and keep mapper compatibility for legacy Chat notifications.

## 2. ViewModel and compatibility behavior

- [x] 2.1 Update the repository/ViewModel success contract to return the new `sightingId` and stop assigning it to `activeChatId`.
- [x] 2.2 Preserve the existing form submit state, success callback and return-to-Home behavior without changing the sighting form or Alertas UI.
- [x] 2.3 Verify that normal Chat sending, Chat reads, legacy Chat models, Room tables and historical documents remain unchanged.

## 3. Firestore authorization and backend contracts

- [x] 3.1 Update `firestore.rules` to authorize a new sighting notification from the matching sighting/post/owner/reporter and require `targetId == sightingId`, without requiring a Chat document.
- [x] 3.2 Preserve participant-only validation for explicit legacy `sighting_alert` messages and ordinary Chat messages.
- [x] 3.3 Ensure invalid, self-owned, mismatched-owner, mismatched-post and mismatched-sighting notification writes remain denied.

## 4. Automated tests

- [x] 4.1 Update repository/ViewModel contract tests to verify a successful sighting does not create a Chat session or Chat message and does not set an active Chat id.
- [x] 4.2 Add or update mapper tests for sighting-targeted notifications with no `chatId`, while retaining coverage for legacy Chat notification mapping.
- [x] 4.3 Update `FirestoreRulesStaticTest` and related static contracts for sighting notification references, target selection and absence of a new Chat dependency.
- [x] 4.4 Add regression assertions that submitted notes remain on `SightingAlertEntity` and are not copied to `ChatMessageEntity.generalDetails`.

## 5. Validation and handoff

- [x] 5.1 Run `openspec validate "decouple-sightings-from-chat" --strict` and confirm all required artifacts are complete.
- [x] 5.2 Run `openspec instructions apply --change "decouple-sightings-from-chat" --json` and confirm every implementation task is complete or explicitly justified.
- [x] 5.3 Run `.\gradlew.bat testDebugUnitTest` and record the result.
- [x] 5.4 Run `.\gradlew.bat assembleDebug` and record the result.
- [x] 5.5 Review the diff against SCRUM-20 to confirm no form/UI redesign, Chat deletion, data migration or unrelated business-logic changes were introduced.
- [x] 5.6 Document manual verification for a valid sighting, invalid/self-report rejection, notification references, no new Chat writes and preservation of existing Chat behavior.

### Manual verification checklist

The following requires a configured Firebase project and an authenticated device/emulator; it was documented for the verifier and not executed by this implementer:

- Submit a valid sighting for another user's post and confirm exactly one sighting and one owner notification are created.
- Confirm the notification contains matching `sightingId`/`postId`, uses `targetId == sightingId`, and omits `chatId`.
- Confirm no `chatSessions/{chatId}` or `messages/{messageId}` documents are created and `activeChatId` remains unchanged.
- Submit a self-owned or otherwise invalid sighting and confirm the write and notification are rejected.
- Send/read an ordinary Chat message and confirm the existing participant-only Chat behavior remains available.

### Verification repair

The Firestore notification contract now has explicit, separate branches for new sighting-targeted `ALERT` notifications and legacy Chat-routed `ALERT` notifications. `FirestoreRulesStaticTest` asserts both branches.
