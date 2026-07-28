## Context

FindYourPet is currently an Android native prototype using Kotlin, Jetpack Compose, Room, and local seeded data. The app has an in-memory `currentUser`, seeded owner ids such as `owner_1`, and client-side checks that grant owner actions based on hardcoded strings. That is acceptable for a demo, but it cannot protect real owner contact data, posts, sightings, or chats.

This change moves identity and owner checks to Firebase Authentication and Cloud Firestore on the Spark plan. Firebase Auth provides the real user identity, Firestore stores authenticated user-owned documents, and Firestore Security Rules enforce access with `request.auth.uid`. Room must stop being the source of truth for authenticated production data; it can remain only as an offline cache or demo seed store when clearly separated from authenticated records.

## Goals / Non-Goals

**Goals:**

- Add Firebase Authentication for email/password and Google Sign-In.
- Store a real profile document at `users/{uid}` for every authenticated user.
- Make Firebase `uid` the only production identity used for ownership checks.
- Move authenticated production records needed by this stage to Firestore-backed repositories.
- Enforce owner-only reads/writes with Firestore Security Rules.
- Remove owner permissions based on `user_1`, `owner_1`, prefixes such as `owner`, or other hardcoded strings.
- Define Room as a cache/demo store rather than an authority for production ownership.
- Keep personal contact details hidden unless contact-sharing rules and authenticated identity allow access.

**Non-Goals:**

- SMS authentication.
- Paid Firebase features beyond the Spark plan.
- Cloud Functions, custom backend servers, or admin-only moderation workflows.
- Real camera/gallery upload, GPS capture, push notifications, or payment features.
- Full production chat redesign beyond authenticated ownership and access boundaries.
- Claiming end-to-end encryption or stronger privacy guarantees than Firebase Auth, Firestore transport, and Firestore rules provide.

## Decisions

1. Use Firebase Authentication plus Cloud Firestore.
   - Rationale: The repo already anticipates Firebase dependencies, and Firebase provides Android-first Auth, Google Sign-In integration, offline-capable Firestore clients, and server-evaluated Security Rules without adding a custom backend.
   - Alternatives considered: Supabase Auth/Postgres, which is viable but adds a different backend stack; custom backend auth, which adds server maintenance before the MVP needs it.

2. Use Firebase `uid` as the sole production owner identifier.
   - Rationale: `uid` is stable across email/password and Google Sign-In providers for the Firebase account and is available in Security Rules as `request.auth.uid`.
   - Alternatives considered: email address or local profile id. Emails can change and local ids cannot be trusted by backend rules.

3. Store profiles at `users/{uid}`.
   - Rationale: This matches the rules pattern where the document id must equal `request.auth.uid`, making read/update/delete checks simple and auditable.
   - Alternatives considered: generated profile document ids with a `uid` field, which require more rule checks and extra queries.

4. Store owned production documents with immutable owner fields.
   - Rationale: Pet posts and related private records need `ownerId` or participant ids that Security Rules can verify. Create rules require `request.resource.data.ownerId == request.auth.uid`; update rules require both existing and new `ownerId` to match `request.auth.uid`.
   - Alternatives considered: trusting the Android client to hide edit buttons. That improves UX but does not protect data.

5. Treat Firestore as the source of truth for authenticated production data.
   - Rationale: Ownership, access, and synchronization must be enforced consistently across devices. Firestore provides server-side rule enforcement; Room does not.
   - Room role: Room may remain for local demo seed data and for explicitly marked cache tables that mirror Firestore documents for the signed-in user. Cached records must never grant permissions when Firestore/Auth state disagrees.

6. Keep Spark-plan constraints visible.
   - Rationale: Spark is enough for Auth, Firestore, and local emulator/rules testing, but quotas and product limits must be respected. No design decision should require billing-only Firebase services in this stage.

## Firestore Model And Rules

Initial production collections:

- `users/{uid}`: profile fields for the authenticated user.
- `petPosts/{postId}`: public-safe post fields plus `ownerId`.
- `sightings/{sightingId}`: sighting data with `reporterId`, `ownerId`, and `postId`.
- `chatSessions/{chatId}`: private session metadata with `ownerId` and `reporterId`.
- `chatSessions/{chatId}/messages/{messageId}`: private messages where sender must be one of the session participants.

Rules principles:

- Deny all by default.
- `users/{uid}` can be read and updated only by the same authenticated `uid`; creation requires `request.auth.uid == uid`.
- Pet posts can be read by authenticated users, but create/update/delete is limited to the owner and cannot change `ownerId`.
- Sightings and chats are readable only by participants. Writes are limited to the authenticated reporter/owner according to the action.
- Contact-sharing flags and contact detail fields are owner-controlled and cannot be modified by non-owners.
- Rules must be validated with emulator tests or documented manual rules validation before closing the change.

Example rule shape:

```firestore
service cloud.firestore {
  match /databases/{database}/documents {
    function signedIn() {
      return request.auth != null;
    }

    function isUser(uid) {
      return signedIn() && request.auth.uid == uid;
    }

    match /users/{uid} {
      allow create: if isUser(uid);
      allow read, update, delete: if isUser(uid);
    }

    match /petPosts/{postId} {
      allow read: if signedIn();
      allow create: if signedIn()
        && request.resource.data.ownerId == request.auth.uid;
      allow update: if signedIn()
        && resource.data.ownerId == request.auth.uid
        && request.resource.data.ownerId == resource.data.ownerId;
      allow delete: if signedIn()
        && resource.data.ownerId == request.auth.uid;
    }
  }
}
```

## Migration Plan

1. Add Firebase project configuration placeholders and document required local `google-services.json` setup without committing secrets.
2. Add Firebase Auth, Firestore, Google Services, Google Sign-In/Credential Manager, and coroutine Task interop dependencies.
3. Add auth state, email/password auth, Google Sign-In, logout, and profile creation/loading services.
4. Add Firestore repositories for authenticated profiles and owned pet-post data, then wire owner actions to authenticated `uid`.
5. Keep existing Room seed data as demo-only fallback for unauthenticated development until the authenticated Firestore flow is available.
6. Replace hardcoded owner checks in UI/ViewModel/repository code with `uid` comparisons and repository authorization outcomes.
7. Add Firestore rules and tests/validation fixtures.
8. Run Gradle build/tests and document manual validation for login/logout, profile, post ownership, denied non-owner edits, and data cache behavior.

Rollback strategy:

- Revert Gradle dependency/plugin changes, Firebase app configuration, Auth/Firestore repositories, and auth-gated UI.
- Remove deployed Firestore rules or restore the previous Firebase project rules if they were changed.
- Keep the local demo Room flow only if rollback is explicitly needed for development; do not ship it as production authorization.

## Risks / Trade-offs

- Firebase project configuration is external to the repo -> Keep `google-services.json` out of version control and provide setup docs/checks.
- Google Sign-In requires console configuration and SHA fingerprints -> Document debug/release SHA requirements and expose clear error states.
- Firestore offline cache can show stale data -> Treat cache as display-only when auth is missing and refresh owner-sensitive actions against authenticated repositories.
- Rules can diverge from client assumptions -> Add emulator/rules validation and source tests rejecting hardcoded owner strings.
- Spark quotas can be exceeded during testing -> Keep reads scoped, avoid broad polling, and prefer targeted listeners.
- Migrating seeded Room records may create fake ownership -> Require explicit signed-in ownership assignment before a demo record becomes a production Firestore record.

## Open Questions

- Which Firebase project id and Android app registration will be used for local development and future release builds?
- Should demo seed posts be importable by a signed-in developer account, or should they remain read-only sample data until a later backend seeding change?
- Which profile fields are mandatory at sign-up beyond display name and email: phone, address, or contact preferences?
