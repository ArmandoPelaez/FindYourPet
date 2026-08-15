## 1. Activity Item Interaction

- [x] 1.1 Add an `onSightingClick` callback to `ActivityScreen` and pass each item's non-normalized `sighting.id` through the existing list rendering path.
- [x] 1.2 Make the Activity card an accessible Material 3 interactive component, preserving `AppShapes`, `AppSpacing`, theme colors, typography, image semantics and existing test tags.

## 2. Sighting Detail Navigation

- [x] 2.1 Connect the Activity route in `MainActivity` to validate and trim the selected `sightingId` before constructing `sighting/{sightingId}`.
- [x] 2.2 Navigate to the existing `SightingDetailScreen` route with single-top protection for repeated requests, preserving Activity in the back stack and the existing Alertas route.
- [x] 2.3 Log invalid Activity selections through the existing diagnostic pattern without navigating, opening Chat or constructing a fallback identifier.

## 3. Contract and UI Tests

- [x] 3.1 Add or update Activity contract tests for the selection callback, stable item identity and absence of `chatId`, `ChatSessionEntity`, `ChatMessageEntity` and Chat navigation dependencies.
- [x] 3.2 Add routing tests for valid normalized `sightingId`, invalid/null/blank identifiers, the existing sighting detail route and unchanged non-Activity routes.
- [x] 3.3 Add Compose or equivalent presentation coverage for interactive touch target/pressed semantics and Light/Dark Theme readability when the existing test harness supports it.
- [x] 3.4 Add coverage proving Back from sighting detail returns to the existing Activity destination without an unnecessary duplicate Activity entry.

## 4. Verification

- [x] 4.1 Run `openspec validate "navigate-activity-to-sighting-detail" --strict` and `openspec instructions apply --change "navigate-activity-to-sighting-detail" --json`, then confirm all tasks are complete.
- [x] 4.2 Run `\.\gradlew.bat testDebugUnitTest` and review failures for scope, routing, accessibility and theme regressions.
- [x] 4.3 Run `\.\gradlew.bat assembleDebug` and inspect the final diff to confirm no backend, persistence, notification or Chat behavior was changed outside the approved scope.
- [x] 4.4 Perform the manual flow `Actividad → seleccionar avistamiento → Detalle de Avistamiento → Back → Actividad` in Light and Dark Theme, including an invalid-id case when test data permits.
