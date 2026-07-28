# Firebase Rules Validation

Validate `firestore.rules` with the Firebase Emulator Suite before using real user data for backend-backed posts, sightings, chats or notifications.

Required cases:

- `users/{uid}` allows create/read/update/delete only when `request.auth.uid == uid`.
- `users/{uid}` denies another authenticated user.
- `users/{uid}/notifications/{notificationId}` allows read/update/delete only for the recipient user.
- `users/{uid}/notifications/{notificationId}` allows create only when `recipientId == uid`, type is one of `ALERT`, `CHAT`, `CONTACT_SHARED`, and the notification starts unread.
- `petPosts/{postId}` allows read only to authenticated users.
- `petPosts/{postId}` allows create only when `ownerId == request.auth.uid`, `id == postId`, and status is valid.
- `petPosts/{postId}` denies owner reassignment on update.
- `petPosts/{postId}` denies non-owner update/delete.
- `sightings/{sightingId}` allows create only when `reporterId == request.auth.uid` and `ownerId` matches the referenced backend post.
- `sightings/{sightingId}` allows read only for `ownerId` or `reporterId`.
- `sightings/{sightingId}` denies all update/delete attempts.
- Atomic sighting submission allows one batch to create the sighting, chat session, initial message and owner notification only when the post owner and reporter are the resulting chat participants.
- `chatSessions/{chatId}` direct reads allow only users represented by `ownerId`, `reporterId` or `participantIds`.
- `chatSessions` list queries filtered by `participantIds array-contains request.auth.uid` return only the signed-in user's sessions, including an empty result for users with no chats.
- `chatSessions/{chatId}` requires `participantIds` to contain exactly `ownerId` and `reporterId`.
- `chatSessions/{chatId}` denies owner, reporter or participant reassignment.
- `chatSessions/{chatId}` allows contact-sharing changes only for `ownerId`.
- `chatSessions/{chatId}/messages/{messageId}` allows create only when `senderId == request.auth.uid` and the sender is a session participant.
- `chatSessions/{chatId}/messages/{messageId}` denies update/delete.
- Unknown collections deny all reads and writes.
- Unauthenticated requests deny all production reads and writes.

Suggested command after installing Firebase CLI and emulator dependencies:

```powershell
firebase emulators:exec --only firestore "npm test"
```

This repository does not yet include a checked-in Firebase rules test harness. If emulator tests are not wired locally, validate the cases above manually in a non-production Firebase project and record the results in the OpenSpec change before archiving.
