## 1. Repository and candidate preflight

- [x] 1.1 Record Git status, current branch, base commit, and applicable OpenSpec validation for the Beta candidate.
- [x] 1.2 Run `\.gradlew.bat testDebugUnitTest` and record the complete result.
- [x] 1.3 Run `\.gradlew.bat assembleDebug` and record the complete result.
- [x] 1.4 Inspect `applicationId`, `versionCode`, `versionName`, candidate variant, signing configuration, and tracked-secret exposure.

## 2. Install, launch, and authentication

- [ ] 2.1 Install the candidate APK on one compatible physical device or emulator and record device identity and installation result.
- [ ] 2.2 Launch the installed candidate, reach Home, and record startup, crash, and loading behavior.
- [ ] 2.3 Validate authenticated access to the required publishing, sighting, activity, alert, reporting, and blocking flows.
- [ ] 2.4 Validate unauthenticated rejection for publishing, sighting submission, content reporting, user blocking, and private-data access.

## 3. Critical product flows

- [ ] 3.1 Execute the lost-pet publishing flow with required fields, photo, location, persistence, feed visibility, and post-publication navigation.
- [ ] 3.2 Execute the two-user sighting flow and verify one `sightingId`, correct ownership, reporter, notes, location, and timestamp.
- [ ] 3.3 Validate alert delivery, `sightingId` routing, correct detail opening, read behavior, and Back navigation.
- [ ] 3.4 Validate Activity loading, empty, error, and selected-item states using `sightingId` to open the existing detail screen.
- [ ] 3.5 Validate sighting detail content and confirm location action, photo handling, and absence of Chat UI.

## 4. Moderation and authorization

- [ ] 4.1 Create a content report, verify `PENDING` status and no duplicate, and verify cancellation creates no report.
- [ ] 4.2 Persist a user block and verify historical sightings remain available to the blocking user.
- [ ] 4.3 Attempt a new sighting from the blocked user and verify rejection with no sighting, alert, or Chat side effect.
- [ ] 4.4 Validate unauthenticated and cross-user access rejection for `users`, `petPosts`, `sightings`, `contentReports`, `userBlocks`, and `notifications`.
- [x] 4.5 Inspect candidate Firestore rules for ownership protections and absence of unrestricted global allow statements.

## 5. Chat removal and backend operations

- [ ] 5.1 Inspect Firestore and local state before and after a new sighting to confirm no new Chat session, message, subcollection, or `chatId` is created.
- [ ] 5.2 Navigate Home, Alerts, Activity, sighting detail, reporting, and blocking to confirm no active Chat destination or composer is reachable.
- [ ] 5.3 Execute the real Home, Alerts, Activity, and sighting queries and record index, permission, and data-loading outcomes.
- [ ] 5.4 Verify technical Firebase errors are logged diagnostically but replaced by controlled user-facing messages.

## 6. Persistence and visual smoke checks

- [x] 6.1 Run relevant Room migration and persistence tests and confirm no destructive migration is introduced.
- [ ] 6.2 Smoke-test Home, bottom navigation, dialogs, errors, empty states, and critical CTAs in Light Mode.
- [ ] 6.3 Repeat the critical visual smoke test in Dark Mode and record contrast, legibility, and crash results.

## 7. End-to-end decision and handoff

- [ ] 7.1 Execute the complete two-user E2E flow from sighting submission through alert, detail, activity, report, block, and rejected follow-up sighting.
- [x] 7.2 Record App Check as `VERIFIED`, `PARTIAL`, or `PENDING` and capture the related production-readiness debt when incomplete.
- [x] 7.3 Produce the Beta Smoke Gate report with a result for every mandatory area and final `PASS`, `BLOCKED`, or `FAIL` status.
- [x] 7.4 Review the diff against this OpenSpec change, validate with `openspec validate "validate-findyourpet-beta-readiness" --strict`, and record all evidence in orchestration state.
