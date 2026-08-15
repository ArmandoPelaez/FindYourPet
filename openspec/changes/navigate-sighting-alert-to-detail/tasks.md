## 1. Notification routing contract

- [x] 1.1 Update the notification click contract so the handler can distinguish sighting notifications and access `sightingId` without collapsing it into `chatId` or legacy `targetId`.
- [x] 1.2 Route valid sighting notifications from `NotificationsScreen`/`MainActivity` to the existing `sighting/{sightingId}` destination.
- [x] 1.3 Preserve the existing chat route for non-sighting chat notifications and preserve routing behavior for other notification types.
- [x] 1.4 Keep `markNotificationAsRead` in the selection flow for valid and invalid notifications.

## 2. Invalid and legacy notification handling

- [x] 2.1 Prevent navigation and Chat fallback when a sighting notification has a missing or blank `sightingId`.
- [x] 2.2 Add diagnostic logging for invalid sighting notification id/type using the project's existing logging conventions.
- [x] 2.3 Reuse existing detail loading/error behavior and confirm no notification schema, sighting creation, chat data, or backend rule changes are introduced.

## 3. Automated verification

- [x] 3.1 Update or add routing contract tests for valid sighting notifications, precedence over `chatId`/legacy `targetId`, preserved chat routing, and read-state behavior.
- [x] 3.2 Add coverage for missing/blank `sightingId` asserting no crash, no Chat navigation, and diagnostic handling.
- [x] 3.3 Run `openspec validate "navigate-sighting-alert-to-detail" --strict` and review the diff against SCRUM-22.

## 4. Final validation

- [x] 4.1 Run `openspec instructions apply --change "navigate-sighting-alert-to-detail" --json` and confirm all implementation tasks are complete.
- [x] 4.2 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 4.3 Run `.\gradlew.bat assembleDebug`.
- [ ] 4.4 Manually validate owner receives a sighting alert, selects it, opens the matching sighting detail, sees the notification marked read, and can navigate Back.
- [ ] 4.5 Manually validate an invalid/legacy sighting alert is handled safely without opening Chat and that normal chat notifications still open the chat detail screen.
