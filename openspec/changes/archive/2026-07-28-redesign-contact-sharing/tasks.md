## 1. Data Model And Persistence

- [x] 1.1 Remove post-level public contact reveal usage from `PetPostEntity`, DAO methods, seed data, remote mappers and any post create/update payloads.
- [x] 1.2 Add a Room migration that drops or ignores legacy `isContactRevealedToAll` values and defaults all migrated public contact state to hidden.
- [x] 1.3 Add chat-scoped contact grant local/remote models with `chatId`, `postId`, `ownerId`, `reporterId`, active state, share/revoke timestamps and approved contact fields.
- [x] 1.4 Ensure shared pet post remote writes no longer include owner phone, owner email, owner address or public reveal fields.
- [x] 1.5 Ensure revoked or missing contact grants clear cached phone/email values before UI rendering.

## 2. Backend Rules And Repository

- [x] 2.1 Update `firestore.rules` to deny public post contact fields and `isContactRevealedToAll` on post create/update.
- [x] 2.2 Add Firestore rules for chat contact grant create/read/update/delete with owner-only writes and participant-only reads.
- [x] 2.3 Update repository share/revoke operations to write chat-scoped grants, generic system chat events and generic notification records in one consistent operation.
- [x] 2.4 Ensure reporter and non-participant callers cannot create, update or re-enable contact grants.
- [x] 2.5 Ensure notification and local notification helper calls never receive phone, email, address, precise coordinates or full contact values.

## 3. UI And ViewModel

- [x] 3.1 Remove or disable public reveal controls from pet detail/public pet-card flows.
- [x] 3.2 Update public contact components to always hide direct owner contact for non-owners and route users toward chat.
- [x] 3.3 Update chat detail to show contact only when the current chat has an active grant for the signed-in participant.
- [x] 3.4 Update chat owner controls to share and revoke contact for only the active chat.
- [x] 3.5 Update chat list, notification target handling and revoked-state copy so they do not imply global/public contact visibility.

## 4. Tests

- [x] 4.1 Update mapper tests to assert shared post documents omit public reveal and direct contact fields.
- [x] 4.2 Add repository/ViewModel tests for owner share, owner revoke, reporter denied and revoked grant hidden behavior.
- [x] 4.3 Update Firestore rules static tests for denied public contact writes, owner-only grant writes and participant-only grant reads.
- [x] 4.4 Update Compose tests for public hidden contact, chat authorized contact and revoked contact states.
- [x] 4.5 Add notification tests asserting contact share/revoke previews and push/local notification text contain no phone or email values.

## 5. Validation

- [x] 5.1 Run unit tests with `.\gradlew.bat testDebugUnitTest`.
- [x] 5.2 Run Android debug build with `.\gradlew.bat assembleDebug`.
- [x] 5.3 Manually validate owner shares contact in chat, reporter sees it only in that chat, and pet detail remains hidden.
- [x] 5.4 Manually validate owner revokes contact and reporter no longer sees phone/email after returning through chat, pet detail and notification entry points.
- [x] 5.5 Manually validate notification and push-preview copy for sighting, chat, contact share and revoke flows contains no direct contact values.
