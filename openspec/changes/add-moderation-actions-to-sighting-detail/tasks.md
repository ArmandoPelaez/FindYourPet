# Implementation Tasks

## 1. Moderation domain and persistence

- [x] 1.1 Define `ContentReportEntity`, `UserBlockEntity`, their local DAO queries and mappers using deterministic identifiers for equivalent reports and owner/reporter blocks.
- [x] 1.2 Add the moderation tables/indices to Room and create the version 8 to 9 migration without altering historical sightings, notifications or chat records.
- [x] 1.3 Add moderation collection names, serializers and repository operations for creating pending reports, creating idempotent blocks, checking an owner/reporter block and returning controlled failures.

## 2. Sighting submission protection

- [x] 2.1 Update sighting submission validation to check the target post owner and reporter block before optional media upload or any sighting/fan-out side effect.
- [x] 2.2 Ensure a blocked submission produces no `SightingAlertEntity`, notification, chat session, chat message or other Chat record, while an unrelated owner's post remains unaffected.
- [x] 2.3 Preserve existing historical sightings and keep the rejection message controlled, actionable and free of raw backend exception details.

## 3. Moderation authorization and backend rules

- [x] 3.1 Add Firestore rules for content reports and user blocks, enforcing authenticated owner authorization, sighting/reporter identity consistency, valid reason/status fields and immutable client-created records.
- [x] 3.2 Extend sighting-create rules to deny a reporter blocked by the target post owner before dependent fan-out writes can succeed, without crossing owner boundaries.
- [x] 3.3 Add or update focused rule tests covering authorized owner writes, non-owner denial, forged identities, duplicate-safe identifiers and direct sighting-create bypass after a block.

## 4. Sighting detail moderation UI

- [x] 4.1 Add the owner-only contextual menu to Sighting Detail with `Reportar contenido` and conditional `Bloquear usuario`, independent of Chat identifiers and hidden when the reporter is unavailable or already blocked.
- [x] 4.2 Implement the report reason selector with cancel/dismiss, confirm, loading, success and retryable error states, disabling repeated confirmation while the write is pending.
- [x] 4.3 Implement the block confirmation dialog with cancel/dismiss, loading, success and retryable error states, preserving the existing detail content and exposing no unblock action.
- [x] 4.4 Wire ViewModel state and one-shot feedback to repository operations, keeping the prior state on failure and mapping technical failures to user-safe messages.
- [x] 4.5 Apply existing Material 3 components and design-system tokens for Light/Dark Theme, accessibility semantics and touch targets; do not introduce hardcoded visual values or experimental APIs.

## 5. Verification

- [x] 5.1 Add or update unit/repository/ViewModel tests for report persistence, report idempotency, block persistence, block idempotency, owner boundaries, cancellation and controlled failures.
- [x] 5.2 Add or update UI tests for owner/non-owner menu visibility, missing/already-blocked reporter behavior, report and block confirmation flows, loading/error/success feedback and historical detail preservation.
- [x] 5.3 Run the relevant automated test suites and a debug build, recording the exact commands and results in the orchestration log.
- [ ] 5.4 Perform the required manual/emulator verification for the end-to-end report flow and the blocked-reporter submission flow, including the absence of sighting, notification and Chat records.
