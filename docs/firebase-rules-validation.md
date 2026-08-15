# Firebase Rules Validation

Validate `firestore.rules` with the Firebase Emulator Suite before using real user data for backend-backed posts, sightings, moderation or notifications.

Required cases:

- `users/{uid}` allows create/read/update/delete only when `request.auth.uid == uid`.
- `users/{uid}` denies another authenticated user.
- `users/{uid}/notifications/{notificationId}` allows read/update/delete only for the recipient user.
- `users/{uid}/notifications/{notificationId}` allows new create only for type `ALERT`, when `recipientId == uid`, and the notification starts unread; historical `CHAT` notifications are not routed or rewritten.
- `users/{uid}/notifications/{notificationId}` denies `CONTACT_SHARED` and any notification payload containing direct contact fields or precise coordinates.
- `petPosts/{postId}` allows read only to authenticated users.
- `petPosts/{postId}` allows create only when `ownerId == request.auth.uid`, `id == postId`, and status is valid.
- `petPosts/{postId}` denies owner reassignment on update.
- `petPosts/{postId}` denies non-owner update/delete.
- `sightings/{sightingId}` allows create only when `reporterId == request.auth.uid` and `ownerId` matches the referenced backend post.
- `sightings/{sightingId}` allows read only for `ownerId` or `reporterId`.
- `sightings/{sightingId}` denies all update/delete attempts.
- Atomic sighting submission creates only the sighting and owner notification; it never creates Chat documents.
- `chatSessions/{chatId}` historical direct reads remain participant-only, while create/update/delete are denied.
- `chatSessions/{chatId}/messages/{messageId}` historical reads remain participant-only; create/update/delete are denied.
- `chatSessions/{chatId}/contactGrants/{grantId}` denies all reads and writes.
- Unknown collections deny all reads and writes.
- Unauthenticated requests deny all production reads and writes.

Suggested command after installing Firebase CLI and emulator dependencies:

```powershell
firebase emulators:exec --only firestore "npm test"
```

This repository does not yet include a checked-in Firebase rules test harness. If emulator tests are not wired locally, validate the cases above manually in a non-production Firebase project and record the results in the OpenSpec change before archiving.
