## 1. Owner-scoped sighting data

- [x] 1.1 Add a Room DAO query for received sightings filtered by the authenticated owner's `ownerId` and ordered by `timestamp DESC`.
- [x] 1.2 Add repository state/data access for owner-scoped sightings in both Firestore and local/cache paths without changing sighting creation or alert generation.
- [x] 1.3 Expose owner-scoped received sightings from `PetViewModel` using the authenticated `currentUser`, including loading, cache, empty, and error state propagation.
- [x] 1.4 Confirm the query and state flow do not read `ChatSessionEntity`, `ChatMessageEntity`, `lastMessage`, or `chatId`.

## 2. Activity screen

- [x] 2.1 Create or adapt `ActivityScreen` as a read-only Compose list backed by the owner-scoped sighting state.
- [x] 2.2 Render each item with available pet name, avistamiento indicator, location, timestamp, optional sighting/pet image, and stable `sightingId` identity.
- [x] 2.3 Exclude conversation names, Chat previews, `lastMessage`, online/typing indicators, reply/send controls, message input, and `chatId` from Activity.
- [x] 2.4 Implement loading, success, EmptyState, and controlled error states using existing `SyncStatusBanner`, Design System tokens, Light Theme, Dark Theme, and accessible touch targets.
- [x] 2.5 Add the Activity route to `MainActivity` while preserving legacy Chat routes and keeping Activity-item to detail navigation out of scope.

## 3. Primary navigation replacement

- [x] 3.1 Replace the fourth `BottomPrimaryActionBanner` destination API, label, content description, icon, and enum state from `Mensajes`/Chat to `Actividad`.
- [x] 3.2 Route the Activity destination to `ActivityScreen` and update selected-state handling without changing Inicio, Perfil, Reportar, or Alertas behavior.
- [x] 3.3 Preserve the existing responsive floating surface, spacing, shapes, theme colors, unread alert badge, and safe-area behavior through existing tokens.
- [x] 3.4 Keep ChatListScreen, ChatDetailScreen, Chat routes, and their non-primary callers available for compatibility without exposing Chat as the Activity source.

## 4. Automated tests

- [x] 4.1 Add or update DAO/repository/ViewModel contract tests for owner scoping, newest-first ordering, empty data, and error propagation.
- [x] 4.2 Add Compose or static tests for Activity metadata, `sightingId` preservation, optional image handling, empty/loading/error states, and absence of messaging UI/content.
- [x] 4.3 Update primary-navigation tests for the five-destination order, `Actividad` label/icon/action, selected state, and preservation of the other destinations.
- [x] 4.4 Add privacy-scope assertions that Activity does not depend on Chat entities, `lastMessage`, contact fields, or `chatId`.

## 5. Final validation

- [x] 5.1 Run `openspec validate "replace-messages-with-activity" --strict` and review the diff against SCRUM-23 and `docs/design-system.md`.
- [x] 5.2 Run `openspec instructions apply --change "replace-messages-with-activity" --json` and confirm all implementation tasks are complete.
- [x] 5.3 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 5.4 Run `.\gradlew.bat assembleDebug`.
- [x] 5.5 Manually validate authenticated navigation shows `Actividad`, received sightings appear newest-first with the expected metadata, no Chat content is loaded, and Inicio/Perfil/Reportar/Alertas remain functional.
- [x] 5.6 Manually validate Activity loading, empty, error, Light Theme, Dark Theme, safe-area, and accessibility behavior with an authorized test account and sighting fixtures.
