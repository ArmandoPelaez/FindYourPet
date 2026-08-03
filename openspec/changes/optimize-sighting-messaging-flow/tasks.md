## 1. Data Contract And Fan-Out

- [ ] 1.1 Extend the chat message contract in `ChatMessageEntity`, `RemoteDocuments.kt` and `RemoteMappers.kt` to support `type = sighting_alert`, `sightingId`, optional sighting photo metadata, authorized location display and sighting details.
- [ ] 1.2 Add any required Room schema/version migration or safe local fallback so existing text messages and legacy system messages continue to load.
- [ ] 1.3 Update `PetRepository.reportSighting` so the sighting fan-out creates or reuses the A/B chat session and writes a `sighting_alert` message instead of the current generic system message text.
- [ ] 1.4 Update chat session previews/last-message fields so sighting chats show a minimized preview such as a new sighting message, without full notes, exact coordinates, phone, email or address.
- [ ] 1.5 Update remote write batching so sighting, chat session, `sighting_alert` message and owner notification are committed atomically for the valid path and not created for invalid/self-sighting paths.

## 2. Backend Rules And Notifications

- [ ] 2.1 Update `firestore.rules` to validate `sighting_alert` message writes against authenticated `senderId`, matching `chatId`, `sightingId`, `postId`, `ownerId`, `reporterId` and participant membership.
- [ ] 2.2 Deny `sighting_alert` payloads that include phone, email, contact address, public reveal flags, contact-grant fields or other app-managed personal contact data.
- [ ] 2.3 Ensure linked sighting alert reads remain participant-only for owner/reporter and denied to unrelated users.
- [ ] 2.4 Update notification creation/helpers so A receives a new-message/new-sighting notification linked to `chatId`, `sightingId` and `postId`, with minimized preview/push text.
- [ ] 2.5 Ensure tapping the notification target navigates to `ChatDetailScreen` for the active A/B conversation and preserves notification read-state behavior.

## 3. Chat UI

- [ ] 3.1 Remove the "Chat interno" notice block from `ChatDetailScreen` for the sighting chat send/receive flow.
- [ ] 3.2 Remove or suppress the generic "MENSAJE DEL SISTEMA" rendering for new sighting alerts and the text "Nuevo avistamiento reportado. Abre el detalle para revisar la informacion autorizada."
- [ ] 3.3 Add a `sighting_alert` timeline item in `ChatMessageItem`/chat components that displays the submitted photo when present, authorized location display, details, timestamp and pet/post context.
- [ ] 3.4 Handle the no-photo alert state without blank image placeholders or layout gaps.
- [ ] 3.5 Keep the normal chat input enabled after the sighting alert so both A and B can append participant-only text messages in the same conversation.
- [ ] 3.6 Update `ChatListScreen` previews for sighting-alert chats to stay useful but minimized.

## 4. Tests

- [ ] 4.1 Update or add mapper/data contract tests in `RemoteMappersTest` and `ProductionDataPathContractsTest` for `sighting_alert` fields, defaults and legacy fallback.
- [ ] 4.2 Update `PetViewModelStateTest` or repository-level tests to assert reporting a valid sighting selects/creates the active chat and creates a `sighting_alert` instead of a generic system message.
- [ ] 4.3 Update `FirestoreRulesStaticTest` with assertions for sighting-alert validation, denied spoofed sender, denied mismatched ids and denied prohibited contact fields.
- [ ] 4.4 Add Compose/static chat UI tests asserting the removed "Chat interno", "MENSAJE DEL SISTEMA" and generic sighting text do not render in new sighting chat flow.
- [ ] 4.5 Add chat UI tests for sighting alert rendering with photo, without photo, with location/details, and with the message input still enabled.
- [ ] 4.6 Add notification/routing tests or static assertions for minimized notification text and navigation to the chat detail route.

## 5. Validation

- [ ] 5.1 Run `openspec status --change "optimize-sighting-messaging-flow"` and confirm all planning artifacts are complete.
- [ ] 5.2 Run `./gradlew test` or `.\gradlew.bat test` depending on shell.
- [ ] 5.3 Run `./gradlew assembleDebug` or `.\gradlew.bat assembleDebug` depending on shell.
- [ ] 5.4 Manually validate B reports a sighting with photo/location/details, A receives the notification, A opens the active chat with B, the alert data is visible, and both users can continue messaging.
- [ ] 5.5 Manually validate the removed red-X content never appears in the sighting send/receive chat path, chat previews or notification target.
- [ ] 5.6 Manually validate notification previews and push/local text do not expose full notes, photo, exact coordinates, phone, email or address.
