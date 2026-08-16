## Context

SCRUM-27 is a release decision for the current Beta candidate, not a product feature. The repository already contains the Android app, Gradle wrapper, Firebase integration, Firestore rules, Room database, Compose screens, and a substantial local test suite. The gate must combine repository checks with manual or device-backed validation because installation, authentication, two-user behavior, Firestore authorization, and Light/Dark smoke behavior cannot all be proven by static tests alone.

The output is evidence and a final status, not a runtime artifact. The gate must preserve the distinction between a local validation failure (`FAIL`) and an unavailable external prerequisite (`BLOCKED`).

## Goals / Non-Goals

**Goals:**

- Establish one ordered Beta smoke workflow from build preflight through two-user E2E validation.
- Reuse existing Gradle, unit-test, Android, Firebase, Firestore, Room, and navigation surfaces instead of adding test dependencies or production code.
- Verify both positive flows and negative authorization/privacy cases, including blocked-user enforcement and absence of new Chat data.
- Record commands, environment prerequisites, observed results, and evidence sufficient for an independent Beta distribution decision.
- Keep the gate compatible with the existing release-readiness requirements while explicitly limiting its scope to Beta.

**Non-Goals:**

- Adding or changing application functionality, UI, navigation, backend rules, indexes, permissions, schemas, or dependencies.
- Replacing production-readiness, load, upgrade-matrix, or exhaustive accessibility testing.
- Deleting historical Chat data or refactoring legacy code as part of validation.
- Treating App Check as an automatic Beta blocker when the external setup is incomplete; record `VERIFIED`, `PARTIAL`, or `PENDING` instead.

## Decisions

### Use an evidence-driven gate with three final outcomes

Each check records `PASS`, `FAIL`, `BLOCKED`, or `N/A` where the checklist allows it. The final result is:

- `PASS` when all mandatory checks pass and no critical security or stability defect is known.
- `FAIL` when a reproducible defect exists in build, startup, authentication, a critical flow, security, persistence, navigation, or stability.
- `BLOCKED` when an external prerequisite prevents execution of a mandatory check, such as no device/emulator, unavailable Firebase environment, or unusable test accounts.

This keeps an untested requirement from being reported as a successful Beta and avoids classifying infrastructure absence as an application defect.

### Separate repository validation from environment validation

Repository validation runs first and includes Git preflight, OpenSpec validation, `testDebugUnitTest`, `assembleDebug`, application identity/build metadata inspection, manifest and backup review, Firestore rules/index inspection, and targeted contract tests. Environment validation then covers installation, launch, authentication, two-user workflows, backend authorization, query execution, and Light/Dark smoke behavior.

This ordering makes failures reproducible locally and identifies the exact external dependency when the gate is blocked.

### Reuse current test and production surfaces

The gate targets existing artifacts such as `gradlew.bat`, `app/build.gradle.kts`, `app/src/test`, `app/src/androidTest`, `firestore.rules`, `app/src/main/res/xml/backup_rules.xml`, `app/src/main/res/xml/data_extraction_rules.xml`, the Auth/Firestore repositories, sighting and notification screens, and Room migration tests. No new library, emulator harness, seed data, or production endpoint is introduced.

The two-user flow uses isolated test accounts and test data. It checks Firestore documents and collection writes after each sensitive operation so that the absence of `chatSessions`, chat messages, or `chatId` dependencies is verified directly rather than inferred from UI state.

### Treat security checks as negative tests

The gate must attempt unauthorized operations as well as authorized ones: unauthenticated writes, cross-user private-data access, invalid report/block ownership, private notification access, and a second sighting after a user is blocked. A test passes only when the expected authorization rejection occurs and no side effect is created.

### Preserve user-facing error boundaries

Manual and automated checks inspect failure states for controlled Spanish user messages. Raw Firebase codes such as `PERMISSION_DENIED`, `FAILED_PRECONDITION`, `UNAVAILABLE`, or `FirebaseException` may be retained in diagnostics but must not be the user-facing result.

### Keep evidence append-only and traceable

The orchestration state records the branch, base commits, commands, artifact validation, delegation, verification results, blockers, and integration status. The final Beta report maps each mandatory area to its observed result and evidence location. If a rerun is needed, append a new attempt rather than replacing the prior failure rationale.

## Risks / Trade-offs

- [No compatible device or emulator] → Mark installation, launch, visual, or E2E checks `BLOCKED` with the required device action; do not infer a pass from unit tests.
- [Firebase credentials or test accounts unavailable] → Mark backend-dependent checks `BLOCKED`; use static rules and contract checks only as partial evidence.
- [Tests exercise demo/local paths rather than production-backed paths] → Record the limitation and do not claim a full Beta `PASS` for flows that could not reach the required backend.
- [Backend data created during smoke testing] → Use dedicated test accounts and uniquely identifiable test records; document cleanup without deleting historical production data.
- [Rules or query checks vary by Firebase environment] → Record project/environment identity and query error details; do not alter rules or indexes during this validation change.
- [Existing Chat history is present] → Scope the assertion to newly created records and active navigation; historical Chat data remains explicitly out of scope.
- [A visual smoke check misses a device-specific issue] → Validate at least one compatible device/emulator in both themes and record the form factor; defer full device-matrix coverage to Production Readiness.

## Migration Plan

No application migration or deployment is required. On the change branch:

1. Complete and validate the OpenSpec artifacts.
2. Run repository checks and record their output.
3. Execute available device/backend smoke flows with dedicated test accounts.
4. Produce the Beta Smoke Gate report and set the orchestration state to `PASSED_PENDING_INTEGRATION`, `FAILED`, or `BLOCKED`.

Rollback is removal or archival of the validation artifacts and report. Since no runtime code, rules, data, or configuration is changed, no production rollback is necessary.

## Open Questions

- Which specific Beta APK variant and signing configuration is the candidate to distribute?
- Which Firebase project/environment and two test accounts are authorized for the smoke flow?
- Is a compatible emulator or physical Android device available for installation, Light/Dark, and E2E checks?
- Which existing Firestore index/query combinations are expected for the current Beta data set?
