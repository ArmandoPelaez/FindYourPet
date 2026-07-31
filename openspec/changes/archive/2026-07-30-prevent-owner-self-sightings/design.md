## Context

FindYourPet currently treats ownership correctly for owner-only post management, but the sighting flow does not apply the same identity relationship before showing or accepting the "Lo he visto" action. A signed-in user can therefore attempt to report a sighting for a pet post they own, which is invalid for the product model.

The intended model is contextual rather than role-based. A user is the owner for posts they created, and a potential reporter for posts created by other users. The same account may publish lost pets and report sightings for other users' pets, but it must never be both `ownerId` and `reporterId` for the same sighting-derived flow.

## Goals / Non-Goals

**Goals:**

- Define a single contextual eligibility rule for sighting reporting: `currentUser.uid != post.ownerId`.
- Apply the rule in UI affordances, direct form access, ViewModel validation, repository guards, and Firestore rules.
- Prevent invalid self-report fan-out records: sighting, chat session, initial chat message, and owner notification.
- Preserve the existing ability for every authenticated user to publish lost-pet posts and report sightings for other users' posts.
- Keep the main discovery feed focused on other users' posts, while the profile remains the owner-facing inventory for a user's own publications.
- Add tests and manual validation for the User A/User B matrix.

**Non-Goals:**

- Introduce global account roles such as "owner" or "reporter".
- Change post creation permissions or owner-only post management behavior.
- Change contact sharing, notification content, media upload, or location capture behavior beyond blocking self-report fan-out.
- Migrate existing historical records unless invalid self-sighting production data is discovered and explicitly scheduled for cleanup.

## Decisions

1. Use contextual permission, not global roles.

   The app should add an ownership-policy helper such as `canReportSighting(currentUid, ownerId)` that returns true only when both ids are non-blank and different. This keeps the model consistent with users who can own one post and report another post in the same session.

   Alternative considered: a user-level role selector. This was rejected because it would incorrectly prevent valid mixed behavior, such as User B publishing their own lost pet while also reporting User A's pet.

2. Enforce the rule at multiple layers.

   UI should hide or disable the sighting CTA for owned posts, direct navigation should block the sighting form for owned posts, ViewModel validation should return a clear error, the repository should `require` distinct owner/reporter ids before creating records, and Firestore rules should deny production writes with equal `ownerId` and `reporterId`.

   Alternative considered: UI-only hiding. This was rejected because deep links, stale state, offline paths, or modified clients could bypass it.

3. Deny fan-out before any dependent records are created.

   The repository and backend rules should treat self-sighting as invalid before creating a sighting, chat session, message, or notification. This avoids self-chats, self-notifications, and inconsistent participant lists.

   Alternative considered: accept the sighting but suppress notification/chat. This was rejected because the underlying sighting would still be false product evidence.

4. Keep discovery surfaces distinct from owner surfaces.

   The Home feed should behave as a discovery surface: a signed-in user sees other users' pet posts, not their own. A user's own publications should remain available in Profile as a compact owner-facing list with status management.

   Alternative considered: show owned posts in the feed with the sighting action hidden. This was rejected because the desired product model is closer to discovery apps where the main feed shows other people's entries and the profile holds the user's own entries.

## Risks / Trade-offs

- Existing invalid local or backend records may already contain `ownerId == reporterId` -> Mitigate by blocking new writes first and separately auditing existing data if needed.
- Hiding the CTA could make owners wonder how to update their own pet's location -> Mitigate with owner-specific management actions in a later change if needed, distinct from third-party sightings.
- Firestore rule changes could reject current client writes if the client still produces duplicate participant ids -> Mitigate by implementing app guards and rule tests before deploying rules.
- Offline/local fallback may still create records if only remote rules are changed -> Mitigate with ViewModel and repository checks shared by remote and local paths.

## Migration Plan

1. Add policy and validation checks in app code.
2. Update Compose surfaces and route guards to respect sighting eligibility.
3. Update repository fan-out guard before local or remote persistence.
4. Update Firestore rules for sightings and chat session creation.
5. Add unit/static tests and manual validation covering User A/User B matrix.
6. Deploy app and rules together after validation.

Rollback: revert the app guards, validator/repository checks, and Firestore rule additions if the change causes a production blocker. Existing valid sightings and chats remain compatible because owner and reporter are already distinct in the intended flow.

## Open Questions

- Should owners see a disabled CTA with explanatory text, or should the CTA be hidden entirely on owned posts?
- Should historical self-sighting records be audited and removed if they exist in production data?
