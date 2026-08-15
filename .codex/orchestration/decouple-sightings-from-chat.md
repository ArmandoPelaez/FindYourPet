# Orchestration: decouple-sightings-from-chat

state: PASSED_PENDING_INTEGRATION
phase: PASSED_PENDING_INTEGRATION
issue: SCRUM-20
change: decouple-sightings-from-chat
branch: ops/decouple-sightings-from-chat
base_branch: main
base_commit: f345596087811e1bb5013c8ea6430f036d968065
remote_base_commit: f345596087811e1bb5013c8ea6430f036d968065
delegation_status: SPAWNED
handoff_mode: SUBAGENT
agent_id: 01a002c6-0f81-73e3-a305-721e0a48095b
agent_role: findyourpet-implementer
delegation_error:
integration_status: PENDING
integrated_commit:
integration_evidence:
superseded_change: optimize-sighting-messaging-flow
superseded_change_decision: Usuario autorizó cancelar/superseder el change y usar SCRUM-20 como alcance implementable.

## Preflight y sincronización

- `git status --short --branch` inicial => `main...origin/main`; árbol limpio.
- `git status --porcelain=v1` inicial => salida vacía.
- `git switch main` => correcto.
- `git fetch origin --prune` => correcto.
- `git pull --ff-only origin main` => `Already up to date.`
- `git rev-parse main` => `f345596087811e1bb5013c8ea6430f036d968065`.
- `git rev-parse origin/main` => `f345596087811e1bb5013c8ea6430f036d968065`.
- `git status --short --branch` después de sincronizar => `main...origin/main`; árbol limpio.
- `git branch --no-merged main` y `git branch -r --no-merged origin/main` => solo ramas históricas documentadas en changes previos; no se detectó una rama activa equivalente a SCRUM-20.
- `git switch -c ops/decouple-sightings-from-chat main` => rama creada correctamente.
- `git rev-parse HEAD` en la rama nueva => `f345596087811e1bb5013c8ea6430f036d968065`.

## Scrum normalizado desde Jira

- Clave: `SCRUM-20`.
- Título: `Desacoplar Avistamientos del Chat`.
- Estado Jira: `To Do`.
- Prioridad: `Medium`.
- Sprint: `SCRUM Sprint 1`.
- Alcance: el envío del formulario existente debe crear únicamente `SightingAlertEntity` y `AppNotificationEntity`; las nuevas notificaciones deben conservar `sightingId`, usar el avistamiento como `targetId` y dejar de depender de `chatId`.
- Debe conservarse: `SightingAlertEntity.notes`, generación estable de `sightingId`, `idempotencyKey`, autenticación, propietario, ubicación y permisos existentes.
- No debe ocurrir: creación de `ChatSessionEntity`, creación de `ChatMessageEntity`, escritura de `chatSessions/{chatId}` o de sus mensajes, ni copia de `notes` a `ChatMessageEntity.generalDetails`.
- Fuera de alcance: modificar el formulario o su UX, cambiar la UI de Alertas, eliminar pantallas/entidades/código legacy del Chat, migrar conversaciones históricas o cambiar validaciones no relacionadas.
- Referencia: `https://pelaezarmando.atlassian.net/browse/SCRUM-20`.

## Contraste con el repositorio

- El flujo actual contiene `PetViewModel.submitSightingAlert` y un repositorio que coordina sighting, chat y notificación.
- `SightingAlertEntity` ya conserva `notes`, `sightingId` e `idempotencyKey`.
- Las reglas y tests actuales todavía expresan referencias sighting-chat, por lo que el change deberá revisar el contrato de escritura permitido sin eliminar el legacy.
- El cambio no es visual; no requiere leer `docs/design-system.md` para esta etapa.

## Cancelación de change previo

El usuario autorizó cancelar/superseder `optimize-sighting-messaging-flow`, que queda fuera del alcance de esta implementación. Sus artefactos no se duplican ni se usan como especificación: SCRUM-20 es la única fuente funcional para este change.

## Artefactos OpenSpec

- `openspec new change "decouple-sightings-from-chat"` => creado en la rama de trabajo.
- `openspec status --change "decouple-sightings-from-chat"` => 4/4 artefactos completos.
- `openspec validate "decouple-sightings-from-chat" --strict` => válido.
- `proposal.md` => alcance, impacto, privacidad y rollback.
- `design.md` => fan-out remoto/local de dos escrituras, compatibilidad legacy, reglas y contrato del ViewModel.
- `specs/sightings/spec.md` => delta del fan-out y fuente de verdad del sighting.
- `specs/notifications/spec.md` => target por `sightingId` y compatibilidad Chat legacy.
- `specs/backend-data-model/spec.md` => contrato de documentos sin Chat para nuevas alertas.
- `specs/backend-access-rules/spec.md` => autorización de sighting/notificación sin dependencia nueva de Chat.
- `specs/private-chat/spec.md` => Chat legacy preservado, sin Chat creado por el nuevo envío.
- `tasks.md` => tareas de implementación, pruebas, validación y handoff.

El change queda listo para delegación al implementador.

## Delegación

- `multi_agent_v1__spawn_agent` => creado el agente `Bernoulli` con `agent_id: 01a002c6-0f81-73e3-a305-721e0a48095b`.
- Payload => `findyourpet-implementer`, change `decouple-sightings-from-chat`, issue `SCRUM-20`, `delegation_required: true`, `handoff_mode: SUBAGENT`.
- El orquestador queda en espera del reporte final del implementador antes de iniciar `VERIFYING`.

## Reporte del implementador

- `agent_id: 01a002c6-0f81-73e3-a305-721e0a48095b` confirmado.
- Estado reportado: `READY_FOR_VERIFICATION`.
- Progreso reportado: 20/20 tareas.
- Resultado reportado: submit con solo sighting + notificación; `targetId = sightingId`; sin nuevas sesiones/mensajes de Chat; ViewModel sin actualizar `activeChatId`; Chat legacy preservado.
- Validaciones reportadas: OpenSpec válido, apply `all_done`, `testDebugUnitTest` exitoso, `assembleDebug` exitoso y `git diff --check` sin errores.
- Verificación manual Firebase/dispositivo: no ejecutada por requerir proyecto configurado, autenticación y emulador/dispositivo.

## Reparación durante verificación

- Hallazgo: la primera versión de reglas eliminaba la aceptación de notificaciones `ALERT` legacy con `chatId`.
- Reparación delegada al mismo `agent_id: 01a002c6-0f81-73e3-a305-721e0a48095b` con evidencia y alcance concretos.
- Resultado reportado: se conserva la rama nueva `ALERT` sin `chatId` y `targetId == sightingId`, y se restaura una rama explícita legacy `ALERT` con `chatId` y `targetId == chatId`.
- Cobertura reportada: `FirestoreRulesStaticTest` cubre ambas ramas.
- Validaciones reportadas después de reparar: OpenSpec válido, `testDebugUnitTest` exitoso, `assembleDebug` exitoso, apply `all_done` y `git diff --check` sin errores.

## Verificación final del orquestador

- `openspec validate "decouple-sightings-from-chat" --strict` => válido.
- `openspec instructions apply --change "decouple-sightings-from-chat" --json` => `20/20`, `all_done`.
- `.\gradlew.bat testDebugUnitTest` => `BUILD SUCCESSFUL`.
- `.\gradlew.bat assembleDebug` => `BUILD SUCCESSFUL`.
- `git diff --check` => sin errores.
- El diff queda limitado a `PetRepository.kt`, `PetViewModel.kt`, `firestore.rules`, tests contractuales/mappers, artefactos OpenSpec y este estado de orquestación.
- Revisión de alcance => no se modificaron formulario, UI de Alertas, modelos/tablas legacy de Chat, migraciones destructivas ni lógica ajena.
- `Get-Command adb` => `ADB_NOT_AVAILABLE`; no fue posible ejecutar validación manual en dispositivo/emulador.
- Firebase config local => `app/google-services.json` presente; no se ejecutó una prueba remota por falta de dispositivo/flujo autenticado disponible.

Resultado: `PASSED_PENDING_INTEGRATION`. La rama `ops/decouple-sightings-from-chat` queda pendiente de integración autorizada en `main`.
