## Why

FindYourPet needs an evidence-based gate before the current candidate is distributed to Beta testers. The existing release-readiness contract covers production-oriented build and privacy concerns, but it does not define the narrower functional, security, and two-user smoke validation required to decide whether this Beta is `PASS`, `BLOCKED`, or `FAIL`.

## What Changes

- Define a Beta Smoke Gate covering the candidate build, relevant automated tests, installation and launch, authentication, publishing, sightings, alerts, activity, sighting detail, content reporting, user blocking, navigation, error handling, Room, and Light/Dark smoke checks.
- Define explicit checks that alerts and activity use `sightingId`, critical Firestore queries work, and no new Chat sessions, messages, or Chat navigation are created.
- Define a minimum review of Firestore Security Rules and ownership protections for Beta-critical collections.
- Define a two-user end-to-end smoke flow and the evidence required for each result.
- Define the final Beta report and the conditions for `PASS`, `BLOCKED`, and `FAIL`.
- Make no product, UI, backend, permission, or data-model changes; this change establishes validation criteria and executable evidence only.

## Capabilities

### New Capabilities

- `beta-smoke-gate`: Defines the Beta validation gate, required smoke flows, security checks, evidence, and final status.

### Modified Capabilities

- None. Existing `release-readiness` requirements remain unchanged; this Beta gate is a narrower pre-distribution validation contract.

## Impact

- Affected artifacts: OpenSpec specifications, validation task checklist, orchestration state, and the final Beta smoke report.
- Affected systems for validation: Android Gradle build and tests, installable Android candidate, Firebase Authentication, Firestore rules and indexes, Room migrations, app navigation, and two test users.
- Privacy and security: validation inspects ownership, protected collections, notification privacy, blocked-user enforcement, unsupported technical error exposure, and the absence of new Chat data.
- Dependencies: a compatible device or emulator, usable Firebase environment and credentials, and two test accounts are required for the full gate. Missing external test infrastructure may result in `BLOCKED`.
- Rollback: no runtime rollback is required because no application behavior or production data is changed; the validation change can be reverted or archived independently.
