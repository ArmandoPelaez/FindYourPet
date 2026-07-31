## 1. Policy And Validation

- [x] 1.1 Add a contextual ownership helper that allows sighting reports only when `currentUid` and `ownerId` are non-blank and different.
- [x] 1.2 Update sighting validation to reject `reporterId == ownerId` with a clear user-facing error.
- [x] 1.3 Add a repository guard before local or remote sighting fan-out so self-reports create no sighting, chat session, message, or notification.

## 2. UI And Navigation

- [x] 2.1 Update post detail UI so owned non-reunited posts do not expose the "Lo he visto" sighting action.
- [x] 2.2 Update feed card/home UI so owned non-reunited posts do not expose the "Lo he visto" sighting action.
- [x] 2.3 Guard direct access to the sighting form route for owned posts and show a blocked self-report state or navigate back safely.
- [x] 2.4 Preserve owner-only post management controls and non-owner sighting actions for valid posts.

## 3. Backend Rules

- [x] 3.1 Update `firestore.rules` for `sightings` create to require `request.resource.data.reporterId != request.resource.data.ownerId`.
- [x] 3.2 Update `firestore.rules` for `chatSessions` create to require distinct `ownerId` and `reporterId`.
- [x] 3.3 Ensure the atomic sighting batch is denied when a client attempts self-sighting fan-out.

## 4. Tests

- [x] 4.1 Add unit tests for ownership policy: owner can manage own post, non-owner can report, owner cannot report own post.
- [x] 4.2 Add validator tests covering valid cross-user sightings and invalid self-sightings.
- [x] 4.3 Add ViewModel or repository tests confirming self-sightings return an error and do not complete with a chat id.
- [x] 4.4 Add static or rules-focused tests asserting Firestore rules include distinct owner/reporter checks for sightings and chat sessions.
- [x] 4.5 Add UI tests or focused static tests covering the hidden/disabled "Lo he visto" action for owned posts.

## 5. Validation

- [x] 5.1 Run unit tests for affected app logic.
- [x] 5.2 Run Android debug build.
- [x] 5.3 Manually validate User A can publish a lost pet and User B can report a sighting for User A's post.
- [x] 5.4 Manually validate User B can publish a lost pet and User A can report a sighting for User B's post.
- [x] 5.5 Manually validate neither User A nor User B can report a sighting for their own post from feed, detail, or direct sighting route.
- [x] 5.6 Confirm no private chat, owner notification, or sighting record is created for blocked self-report attempts.

## 6. Discovery Feed And Profile Ownership

- [x] 6.1 Add an ownership helper for discovery-feed visibility that hides a signed-in user's own posts.
- [x] 6.2 Apply discovery-feed visibility in the Home feed state without removing owned posts from `allPosts`.
- [x] 6.3 Keep the profile's own-publications list sourced from `allPosts` and reduce each item to pet name and status.
- [x] 6.4 Add tests or guardrails covering feed exclusion and profile retention of owned posts.
