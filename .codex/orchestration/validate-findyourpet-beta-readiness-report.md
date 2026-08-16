# Beta Smoke Gate Report

- Change: `validate-findyourpet-beta-readiness`
- Issue: `SCRUM-27`
- Date: `2026-08-15`
- Branch: `ops/validate-findyourpet-beta-readiness`
- Base commit: `aeb0d535851ecff17c1acd177f2e26e43a3bb2bc`
- Final status: `BLOCKED`

## Decision

The repository gate is green, but the Beta decision cannot be completed because this execution environment has no `adb` command or compatible Android device/emulator. The mandatory installation, launch, authenticated two-user, Firebase authorization/query, visual, and E2E checks therefore remain unverified. No application defect is asserted from the unavailable external checks.

## Area results

| Mandatory area | Result | Evidence / limitation |
| --- | --- | --- |
| Repository and candidate preflight | PASS | Git branch/base recorded; OpenSpec strict validation passed; debug unit tests and assemble passed; metadata and tracked-secret exposure reviewed. |
| Installation, launch, authentication | BLOCKED | `adb devices` could not run because `adb` is not available; no device identity or install result can be recorded. |
| Publishing and sighting flows | BLOCKED | Requires an installed candidate, Firebase environment, and two authorized test users. |
| Alerts, Activity, and sighting detail | BLOCKED | Static routing contracts pass, but real alert delivery, `sightingId` routing, backend loading, and Back behavior require runtime execution. |
| Reporting, blocking, and authorization | BLOCKED | Candidate rules and contracts were inspected, but real unauthenticated/cross-user operations and block enforcement require Firebase test accounts. |
| Chat absence and backend operations | PARTIAL / BLOCKED | Static Chat-retirement contracts and rules checks pass; before/after Firestore state, real queries, and active navigation were not executable. |
| Room and visual smoke | PARTIAL / BLOCKED | Room migration contract passes and no destructive fallback is present; Light/Dark device smoke was not executable. |
| Two-user E2E and final handoff | BLOCKED | Full E2E cannot run without device/emulator, Firebase access, and two test users. |

## Local evidence

- `openspec validate "validate-findyourpet-beta-readiness" --strict` => PASS.
- `\.gradlew.bat testDebugUnitTest` => BUILD SUCCESSFUL.
- `\.gradlew.bat testDebugUnitTest --rerun-tasks` => BUILD SUCCESSFUL; compilation emitted only existing deprecation warnings.
- Targeted contract tests for Room, Firestore rules, Chat retirement, sighting navigation/fan-out/detail, notification routing, moderation, release readiness, and product validators => BUILD SUCCESSFUL.
- `\.gradlew.bat assembleDebug` => BUILD SUCCESSFUL.
- APK: `app/build/outputs/apk/debug/app-debug.apk`; SHA-256 `7CCD64244D3FD501628137CD4352C5D03088C5DB23F9BB4C08AD5579729E93D`.
- Candidate metadata: `applicationId=com.findyourpet.app`, `versionCode=1`, `versionName=1.0`, variant `debug`.
- Signing: debug uses local `debugConfig`; release signing points to a local upload keystore with no resolved alias in `signingReport`, so this APK is not claimed as a distributable release candidate.
- Secret exposure review: `app/google-services.json`, `secrets.properties`, `debug.keystore`, `my-upload-key.jks`, and `local.properties` are ignored/untracked; no sensitive file was added by this change.
- Firestore static review: default deny is present, critical collections have ownership/authentication predicates, and no unrestricted global allow was found. `FirestoreRulesStaticTest` passed.
- Room static review: `RoomMigrationContractTest` passed; `MIGRATION_9_10` is explicit and `fallbackToDestructiveMigration` is absent.
- App Check: `PENDING`. No App Check implementation/configuration was found by repository search, and runtime Firebase verification was unavailable. Production-readiness debt: configure and verify App Check before relying on it for distribution protection.

## Pending checks and resume action

ALL DONE
Tasks `2.1`–`2.4`, `3.1`–`3.5`, `4.1`–`4.4`, `5.1`–`5.4`, `6.2`–`6.3`, and `7.1` remain pending or partially unverified. Resume with a compatible Android emulator/physical device exposing `adb`, the authorized Firebase project/environment, two isolated test accounts, and the selected candidate/signing configuration. Then append a new attempt rather than replacing this evidence.
