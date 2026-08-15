change: replace-messages-with-activity
issue: SCRUM-23
title: Reemplazar sección Mensajes por Actividad y listar avistamientos recibidos
status: INTEGRATED
phase: INTEGRATED
base_branch: main
base_commit: e7303bc1d2209d340b0e23c6fed43f455158cbb8
remote_base_commit: e7303bc1d2209d340b0e23c6fed43f455158cbb8
branch: ops/replace-messages-with-activity
integration_status: MERGED
integrated_commit: 9b93782f574fd6fed1866bba6accd20b0bdcf0e6
integration_evidence: PR #43 merged into main; manual tests confirmed by user.

## Preflight and synchronization

- `git status --short --branch`: `## main...origin/main`
- `git status --porcelain=v1`: empty.
- `git switch main`: passed.
- `git fetch origin --prune`: passed.
- `git pull --ff-only origin main`: passed; already up to date.
- `git rev-parse main`: `e7303bc1d2209d340b0e23c6fed43f455158cbb8`
- `git rev-parse origin/main`: `e7303bc1d2209d340b0e23c6fed43f455158cbb8`
- Main remained clean and synchronized.

## Parallel-work authorization

The user explicitly authorized working in parallel and obviating the state of pending branches for SCRUM-23.

## Jira Scrum recibido

- Issue: `SCRUM-23`
- Summary: replace `Mensajes` with `Actividad` and list received sightings.
- Status: `To Do`.
- Priority: `Medium`.
- Sprint: `SCRUM Sprint 1`.
- Scope: replace the authenticated bottom-navigation destination label and screen, list sightings received for the signed-in user's posts from `SightingAlertEntity`/`sightings/{sightingId}`, order newest first, preserve `sightingId`, and provide loading, success, empty, and controlled error states.
- Exclusions: no change to sighting creation, alert generation, alert-to-detail navigation, sighting detail screen, chat cleanup, chat responses, reporting content, blocking users, or historical chat migration.
- Privacy/design constraints: Activity must not use Chat entities, messages, previews, `lastMessage`, or `chatId`; UI must use existing Design System tokens, stable Material 3, Light/Dark themes, and accessible touch targets.

## Repository contrast

- Existing navigation shell exposes five destinations and currently labels the chat destination `Mensajes`.
- Existing `PetViewModel` exposes sightings for a selected post and sighting detail, but not an owner-scoped received-sightings list.
- Existing `PetRepository`/DAO support post-scoped sightings and require an owner-scoped query for Activity.
- Existing `SightingAlertEntity` contains `ownerId`, `postId`, `reporterName`, `photoUri`, `locationName`, and `timestamp` needed by the list.
- `docs/design-system.md` was read before planning visual changes.

delegation_status: SPAWNED
handoff_mode: SUBAGENT
agent_id: 01a005f9-cd2b-7d22-bd99-aa9335a2b0b5
agent_role: findyourpet-implementer
delegation_error:

## Implementer report

- Result: `READY_FOR_VERIFICATION`.
- Progress: `21/23` tasks.
- Implemented: owner-scoped newest-first Activity inbox, loading/empty/error/success states, sighting metadata and optional images, stable `sightingId`, primary navigation replacement, Chat legacy preservation, static and Compose tests.
- Automated results from implementer: `openspec validate ... --strict` passed; `.\gradlew.bat testDebugUnitTest` passed with 160 tests; `.\gradlew.bat assembleDebug` passed; `git diff --check` passed.
- Pending: manual authenticated fixture validation across themes, safe-area/accessibility, and complete device navigation.

## Verification evidence

- `openspec instructions apply --change "replace-messages-with-activity" --json`: `21/23`; only manual tasks 5.5 and 5.6 remain.
- `openspec validate "replace-messages-with-activity" --strict`: passed.
- Orchestrator rerun `.\gradlew.bat testDebugUnitTest`: passed.
- Orchestrator targeted `.\gradlew.bat testDebugUnitTest --tests com.findyourpet.app.ActivityContractStaticTest`: passed.
- Orchestrator rerun `.\gradlew.bat assembleDebug`: passed.
- `git diff --check`: passed.
- Diff review: changes are limited to owner-scoped sighting access, Activity Compose UI, primary navigation replacement, related tests, and OpenSpec/orchestration artifacts. Chat legacy code/routes remain present.

## Manual validation and closure

- The user confirmed that the manual tests were completed after the Firestore composite index for `sightings(ownerId ASC, timestamp DESC)` finished compiling.
- Manual tasks 5.5 and 5.6 are therefore considered completed based on the user's confirmation.
- No credentials or production data were recorded in this bitácora.

## Integration

- PR #43 (`ops/replace-messages-with-activity`) was merged into `main`.
- `origin/main` is at `9b93782f574fd6fed1866bba6accd20b0bdcf0e6`.
- The implementation commit `c35918a200000b0e2adb7df6e6fc7ce0843e8548` is an ancestor of the integrated commit.

## Branch creation

- `git switch -c ops/replace-messages-with-activity main`: passed.
- Branch `HEAD`: `e7303bc1d2209d340b0e23c6fed43f455158cbb8`, matching the synchronized base.

## OpenSpec artifacts

- `proposal.md`: generated from SCRUM-23.
- `design.md`: generated after reading `docs/design-system.md` and reviewing navigation, repository, DAO, ViewModel, entity, and Chat code.
- `specs/activity/spec.md`: new owner-scoped Activity capability with metadata, ordering, privacy, and UI states.
- `specs/primary-navigation/spec.md`: modified five-destination navigation contract replacing Mensajes with Actividad.
- `tasks.md`: data, UI, navigation, tests, and manual validation checklist.
- `openspec validate "replace-messages-with-activity" --strict`: passed.
