## Why

Owners can currently enter the sighting flow for their own lost-pet posts, which lets one authenticated user act as both post owner and sighting reporter for the same pet. This breaks the product model, creates confusing chats and notifications, and weakens backend authorization semantics for production data.

This change clarifies that FindYourPet does not have fixed global roles: any signed-in user may publish lost pets and report sightings for other users' posts, but the same user MUST NOT report a sighting against a post they own.

## What Changes

- Block sighting submission when the authenticated reporter `uid` equals the referenced post `ownerId`.
- Hide a signed-in user's own posts from the main discovery feed while keeping them visible in the profile's own-publications list.
- Hide or disable the "Lo he visto" / sighting alert action on any owned post reachable outside the discovery feed while preserving owner-only management actions.
- Guard direct navigation to the sighting form so route access cannot bypass the post ownership check.
- Add validation in app business logic and repository boundaries so no sighting, chat, message, or owner notification is created for self-reports.
- Strengthen Firestore rules so production backend writes explicitly deny sightings and chat sessions where `ownerId == reporterId`.
- Add tests and manual validation for the contextual permission matrix:
  - User A can publish a lost pet.
  - User B can report a sighting for User A's pet.
  - User B can publish their own lost pet.
  - User A can report a sighting for User B's pet.
  - Neither User A nor User B can report a sighting for their own post.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `sightings`: sighting creation must require a reporter distinct from the post owner and must block self-reports before fan-out.
- `ownership-rules`: ownership policy must distinguish owner management permissions, contextual sighting reporter eligibility, and discovery-feed visibility.
- `backend-access-rules`: Firestore rules must explicitly deny self-sighting writes and derived chat/session fan-out where owner and reporter are the same user.
- `private-chat`: chat sessions created from sightings must represent two distinct participants: post owner and sighting reporter.

## Impact

- Affected app code: `PetDetailScreen.kt`, `HomeScreen.kt`, `SightingAlertScreen.kt`, `PetViewModel.kt`, `RealProductValidators.kt`, `PetRepository.kt`, `OwnershipPolicy.kt`, and related tests.
- Affected backend security: `firestore.rules` sighting and chat session create paths.
- Privacy/security impact: improves integrity of owner/reporter separation and prevents self-generated private records or notifications.
- User impact: users can still publish their own lost pets and see/manage them from Profile, while the main feed behaves like a discovery surface showing only other users' posts.
- Dependencies: no new runtime dependency expected.
- Rollback: revert the UI guards, validation checks, repository guard, tests, and Firestore rule additions to restore previous behavior if needed.
- Guardrails: continue using Firebase `uid` for ownership, do not introduce global role switching, do not rely on UI-only authorization, and do not create chat, notification, or sighting fan-out for invalid self-reports.
