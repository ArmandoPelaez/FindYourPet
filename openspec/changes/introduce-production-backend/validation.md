# Validation

## Automated

- `.\gradlew.bat testDebugUnitTest`: passed.
- `.\gradlew.bat assembleDebug`: passed.
- Static tests now cover backend mappers, ownership policy guardrails and important `firestore.rules` constraints.
- After the atomic sighting rules fix, `.\gradlew.bat testDebugUnitTest` and `.\gradlew.bat assembleDebug` passed again.

## Firestore Rules

Firebase Emulator Suite is not wired in this repository and the local `firebase` CLI is not installed, so emulator execution was not run in this session.

Manual fallback is documented in `docs/firebase-rules-validation.md` and must be run against a non-production Firebase project before real user data is used.

## Manual Product Flow

In progress against non-production Firebase project `findyourpet-db301`:

- User A created a pet post and User B could see it.
- User B sighting submission initially failed with `PERMISSION_DENIED: Missing or insufficient permissions`.
- Root cause: message rules used `get()` to read `chatSessions/{chatId}` while the sighting flow created the chat session and first message in the same Firestore batch.
- Follow-up: rules were updated to validate the resulting chat session with `getAfter()`; Firestore rules must be republished and the manual flow must be rerun.
- After republishing, User B submitted a sighting, the chat session and initial message were created, and User A received the notification.
- User A and User B exchanged chat messages successfully.
- User C chat access produced `PERMISSION_DENIED`, confirming private chat data was denied, but the chat list query should return an empty result instead of a raw backend error.
- Follow-up: chat participant rules now separate direct `get` reads from `list` queries so `participantIds array-contains request.auth.uid` can return an empty chat list without allowing direct access to A/B chat documents. Firestore rules must be republished and User C chat-list access must be rerun.
- Pending: User C cannot read or modify User A/User B sightings, direct chat documents, messages or notifications while their own chat list renders empty.
