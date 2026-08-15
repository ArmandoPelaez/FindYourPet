change: navigate-sighting-alert-to-detail
issue: SCRUM-22
title: Navegar desde alerta de avistamiento al detalle usando sightingId
status: BLOCKED
phase: VERIFYING
base_branch: main
base_commit: d376a02891c025f7978416ffa2f6d9ce6b8852db
remote_base_commit: d376a02891c025f7978416ffa2f6d9ce6b8852db
branch: ops/navigate-sighting-alert-to-detail
integration_status: PENDING
integrated_commit:
integration_evidence:

## Branch creation

- `git switch -c ops/navigate-sighting-alert-to-detail main`: passed.
- Branch `HEAD`: `d376a02891c025f7978416ffa2f6d9ce6b8852db`, matching the synchronized base.

## Preflight

- `git status --short --branch`: `## main...origin/main`
- `git status --porcelain=v1`: empty
- `main` was already active and clean.

## Main synchronization

- `git switch main`: passed; already on `main`.
- `git fetch origin --prune`: passed.
- `git pull --ff-only origin main`: passed; already up to date.
- `git rev-parse main`: `d376a02891c025f7978416ffa2f6d9ce6b8852db`
- `git rev-parse origin/main`: `d376a02891c025f7978416ffa2f6d9ce6b8852db`
- Final `git status --short --branch`: `## main...origin/main`

## Jira Scrum recibido

- Issue: `SCRUM-22`
- Resumen: navegar desde una alerta de avistamiento al detalle usando `sightingId`.
- Alcance: detectar alertas de avistamiento, validar `sightingId`, navegar a `SightingDetailScreen`, conservar el marcado como leído y el comportamiento de otros tipos de notificación.
- Restricciones: no navegar a `ChatScreen`, no usar `chatId` ni `targetId` legacy de Chat, no modificar la creación del avistamiento, la pantalla de detalle ni otras áreas fuera de la navegación de la alerta.
- Manejo inválido: sin crash, sin abrir Chat, con manejo seguro, logs y estado/error conforme a los patrones existentes.
- Estado Jira al recibirlo: `To Do`; prioridad `Medium`; sprint `SCRUM Sprint 1`.

## Ramas no integradas detectadas

Local:

- `archive/remove-personal-data-sharing`
- `ops/add-transparency-to-bottom-navigation`
- `ops/redesign-lost-pets-feed`
- `ops/remove-share-button`

Remotas:

- `origin/Eliminar-mensaje-de-sistema-del-chat`
- `origin/Rediseño-de-la-pantalla-principal-de-posteo`
- `origin/archive/remove-personal-data-sharing`
- `origin/archive/simplify-lost-pet-post-form`
- `origin/ops/add-transparency-to-bottom-navigation`
- `origin/ops/redesign-lost-pets-feed`
- `origin/ops/remove-share-button`

No existe documentación en `.codex/orchestration/` que certifique el estado integrado o activo de estas ramas.

## Changes OpenSpec activos

- `optimize-sighting-messaging-flow`: `0/28` tareas, `in-progress`.
- `sticky-lost-pet-detail-actions`: `20/21` tareas, `in-progress`.
- `prepare-production-release`: `16/25` tareas, `in-progress`.

El conflicto crítico es `optimize-sighting-messaging-flow`: sus artefactos indican que la alerta debe crear/reutilizar un chat y navegar a `ChatDetailScreen`, mientras SCRUM-22 exige explícitamente `sightingId` → `SightingDetailScreen` y prohíbe usar Chat como destino para nuevas alertas.

## Bloqueo

No se puede crear la rama `ops/navigate-sighting-alert-to-detail` ni generar artefactos OpenSpec hasta que el usuario autorice una decisión de alcance sobre el change de mensajería y confirme el estado de las ramas no integradas. Crear el change ahora podría duplicar o contradecir trabajo activo.

delegation_status: SPAWNED
handoff_mode: SUBAGENT
agent_id: 01a005d9-d7cc-7bd3-af82-7866a0e5b0cf
agent_role: findyourpet-implementer
delegation_error:

## Implementer report

- Result: `READY_FOR_VERIFICATION`.
- Progress: `13/15` tasks.
- Modified files: `app/src/main/java/com/findyourpet/app/MainActivity.kt`, `app/src/main/java/com/findyourpet/app/ui/screens/NotificationsScreen.kt`, `app/src/test/java/com/findyourpet/app/NotificationRoutingContractTest.kt`, and the change `tasks.md`.
- Completed: sighting routing by `sightingId`, no Chat fallback for invalid identifiers, preserved chat routing and read-state behavior, diagnostic logging, tests, OpenSpec validation, and build.
- Pending: manual validation of valid and invalid flows on a device.
- Implementer commands: `openspec validate ... --strict`, `.\gradlew.bat testDebugUnitTest`, `.\gradlew.bat assembleDebug`, and `openspec instructions apply ... --json`; all reported success except the two manual tasks remaining.

## Verification evidence

- `openspec instructions apply --change "navigate-sighting-alert-to-detail" --json`: `13/15`; only manual tasks 4.4 and 4.5 remain.
- `openspec validate "navigate-sighting-alert-to-detail" --strict`: passed.
- Orchestrator rerun `.\gradlew.bat testDebugUnitTest`: passed.
- Orchestrator rerun `.\gradlew.bat assembleDebug`: passed.
- `git diff --check`: passed.
- APK installation and launch on `emulator-5554`: passed via `android run`; `com.findyourpet.app.MainActivity` launched.
- Manual flow blocker: the app opens at login and no authenticated test account or sighting-notification fixture is available. Completing the valid/invalid notification taps requires external Firebase data or credentials not provided for this task.

## Blocker

Manual validation tasks 4.4 and 4.5 cannot be completed without an authorized authenticated test session and seeded sighting notification data. No account, backend fixture, or production data was created or modified.

## OpenSpec artifacts

- `proposal.md`: generated from SCRUM-22.
- `design.md`: generated after reviewing the current notification and navigation handlers.
- `specs/notifications/spec.md`: valid sighting routing, safe invalid handling, and preserved non-sighting destinations.
- `specs/sightings/spec.md`: direct owner notification target to sighting detail.
- `tasks.md`: implementation, tests, and final validation checklist.
- `openspec validate "navigate-sighting-alert-to-detail" --strict`: passed.

## Scope decision

The user confirmed SCRUM-22 and authorized obviating pending branch states. The conflicting navigation destination in `optimize-sighting-messaging-flow` is superseded for this change; no duplicate implementation scope is being carried over.

## Authorization to continue

The user confirmed SCRUM-22 as the active scope and authorized ignoring the pending branch states. SCRUM-22 is authoritative over the conflicting navigation destination documented by `optimize-sighting-messaging-flow`; that older change is not reused or duplicated.
