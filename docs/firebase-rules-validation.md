# Firebase Rules Validation

Validate `firestore.rules` with the Firebase Emulator Suite before closing `add-user-authentication`.

Required cases:

- `users/{uid}` allows create/read/update/delete only when `request.auth.uid == uid`.
- `users/{uid}` denies another authenticated user.
- `petPosts/{postId}` allows create only when `ownerId == request.auth.uid`.
- `petPosts/{postId}` denies owner reassignment on update.
- `petPosts/{postId}` denies non-owner update/delete.
- `sightings/{sightingId}` allows reporter create with authenticated `reporterId`.
- `chatSessions/{chatId}` allows reads only for `ownerId` or `reporterId`.
- `chatSessions/{chatId}` denies participant reassignment.
- `chatSessions/{chatId}` allows contact-sharing changes only for `ownerId`.
- `chatSessions/{chatId}/messages/{messageId}` allows create only when `senderId == request.auth.uid` and the sender is a session participant.

Suggested command after installing Firebase CLI and emulator dependencies:

```powershell
firebase emulators:exec --only firestore "npm test"
```

If emulator tests are not yet wired in the repo, manually validate these cases in a non-production Firebase project before using real user data.
