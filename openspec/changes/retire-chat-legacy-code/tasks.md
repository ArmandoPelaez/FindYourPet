## 1. Inventory and active navigation

- [x] 1.1 Build a complete repository inventory of Chat references and classify each occurrence as executable code to remove, historical compatibility to retain, or test/documentation to update; record unresolved references in the orchestration log.
- [x] 1.2 Remove Chat imports, `ROUTE_CHATS`, `ROUTE_CHAT_DETAIL`, Chat composables, `chatId` route builders and Chat callbacks from `MainActivity`, preserving Alertas/Actividad/Detalle routes by `sightingId`.
- [x] 1.3 Remove `ChatListScreen` and `ChatDetailScreen` from the active source set, including the composer, send action, reply affordances and Chat-only resources/previews that have no other consumer.
- [x] 1.4 Update the authenticated navigation contract and related project copy so the five destinations remain Inicio, Perfil, Reportar, Actividad and Alertas, with no Mensajes/Chat promise or contact-through-Chat text.

## 2. Domain, repository and remote model cleanup

- [x] 2.1 Remove Chat state flows, selection methods and `sendChatMessage` from `PetViewModel`; preserve sighting submit loading/success/error behavior without storing a result as `activeChatId`.
- [x] 2.2 Remove Chat reads, writes, listeners, seed fixtures and notification fan-out helpers from `PetRepository`; verify valid sighting submission still writes only the sighting and required notification.
- [x] 2.3 Remove unused `ChatSessionEntity`, `ChatMessageEntity`, Chat-only mappers, remote document classes and collection/id helpers after confirming no non-Chat consumer remains.
- [x] 2.4 Preserve only explicitly justified nullable legacy notification fields needed for historical decoding, and ensure no active code uses `chatId` for routing, authorization or new writes.

## 3. Room migration and local data safety

- [x] 3.1 Remove Chat entities and DAO queries/inserts/clears from the Room schema while preserving posts, sightings, notifications, content reports and user blocks.
- [x] 3.2 Add and register a directed Room migration from version 9 to 10 that drops only `chat_sessions` and `chat_messages`, without destructive fallback migration.
- [x] 3.3 Add migration/contract coverage proving non-Chat tables survive the 9-to-10 upgrade and that the database schema no longer exposes Chat tables to the app.

## 4. Firestore and notification contracts

- [x] 4.1 Update Firestore rules so new client creates, updates and deletes for Chat sessions, messages and Chat-scoped contact records are denied, while historical documents are not deleted and retained read authorization is explicit.
- [x] 4.2 Remove active Chat notification routing and keep new sighting notifications addressed by `sightingId`, with controlled handling for historical Chat notifications and no crash.
- [x] 4.3 Update backend/static rule tests to cover denied new Chat writes, preserved non-Chat authorization, historical retention assumptions and the absence of Chat fan-out for new sightings.

## 5. Tests, documentation and regression protection

- [x] 5.1 Delete or adapt tests and fixtures that exist only for Chat rendering, sending, conversations or Chat-only contact sharing; do not remove Alertas, Actividad, Detalle, sighting or moderation coverage.
- [x] 5.2 Add a focused Chat-retirement contract test that checks no active Chat screen/route/send/read/listener symbols remain, no `chatId` routing remains, and the five primary destinations are preserved.
- [x] 5.3 Update local-storage/privacy/design documentation and OpenSpec-facing contracts so they describe Chat as retired, historical remote retention honestly, and the existing Material 3/token rules remain unchanged.
- [x] 5.4 Run a global reference audit for `ChatScreen`, `ChatViewModel`, `ChatRepository`, Chat entities, `sendMessage`, `chatId`, `chatSessions` and `messages`; justify every remaining occurrence in code comments, rules, historical compatibility or tests.

## 6. Verification and handoff

- [x] 6.1 Run `openspec validate "retire-chat-legacy-code" --strict` and `openspec instructions apply --change "retire-chat-legacy-code" --json`, recording all task results and remaining references in the orchestration log.
- [x] 6.2 Run focused regression tests for navigation, sightings, notifications, moderation, Room migration and Chat retirement, then run `./gradlew.bat testDebugUnitTest` and record failures separately when pre-existing.
- [x] 6.3 Run `./gradlew.bat assembleDebug` and `git diff --check`, confirming the diff stays within SCRUM-26 and does not modify unrelated parallel changes.
- [x] 6.4 Perform manual/emulator verification that no Chat/Mensajes destination, conversation screen, composer or send action is reachable, and that Alertas/Actividad open Sighting Detail by `sightingId` without crashes.
- [x] 6.5 Review the final diff against the proposal and specs, mark only evidenced tasks complete, and provide the implementer handoff with any unavailable backend/emulator evidence explicitly listed.
