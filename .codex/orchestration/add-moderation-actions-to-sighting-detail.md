state: BLOCKED
phase: VERIFICATION_BLOCKED
issue: SCRUM-25
change: add-moderation-actions-to-sighting-detail
branch: ops/add-moderation-actions-to-sighting-detail
base_branch: main
base_commit: 2c578c3d7b575798e3b85a7b699d7c58017e21a6
remote_base_commit: 2c578c3d7b575798e3b85a7b699d7c58017e21a6
integration_status: PENDING
integrated_commit:
integration_evidence:
delegation_status: COMPLETED_BLOCKED
handoff_mode: SUBAGENT
delegation_required: true
agent_id: 01a0065e-fb1a-7681-90cb-41fadcf65d28
agent_role: findyourpet-implementer
delegation_error:

## Implementación delegada

- `findyourpet-implementer` ejecutó el change en la rama `ops/add-moderation-actions-to-sighting-detail`.
- Progreso OpenSpec: `17/18`; queda `5.4` sin marcar porque requiere Firebase/emulador y verificación end-to-end real.
- Persistencia: `ContentReportEntity`, `UserBlockEntity`, DAOs, mappers, colecciones deterministas y migración Room `8 -> 9`.
- Protección: `PetRepository` consulta el bloqueo antes de upload, sighting y notificación; el flujo no crea ni toca registros Chat.
- UI: menú owner-only, selector de motivos, confirmación de bloqueo, estados loading/success/error, retry y feedback controlado en Light/Dark con tokens existentes.
- Seguridad: reglas para reportes/bloqueos inmutables y rechazo de sighting bloqueado por pareja propietario/reportante.
- Tests agregados/actualizados: `ModerationContractTest`, contrato de migración Room v9 y reglas estáticas enfocadas.

## Verificaciones del implementador

- `openspec validate "add-moderation-actions-to-sighting-detail" --strict` => PASSED.
- `.\gradlew.bat testDebugUnitTest --tests com.findyourpet.app.ModerationContractTest --tests com.findyourpet.app.PetPostRetiredAttributesStaticTest` => PASSED.

## Reparación posterior: alertas rechazadas para todos los usuarios

- Evidencia del emulador: Firestore devolvía `PERMISSION_DENIED` al consultar `userBlocks/{owner}_{reporter}` cuando el documento de bloqueo no existía.
- Causa: la regla intentaba evaluar `resource.data.blockerUserId` sobre un documento inexistente; el pre-check fallaba antes de crear la alerta.
- Corrección: `userBlocks` usa `allow get` permitiendo comprobar ausencia (`!exists(...)`) y restringe la lectura de documentos existentes al propietario del bloqueo; `list` queda denegado.
- Requiere publicar `firestore.rules` en el proyecto Firebase utilizado por la app antes de validar producción.
- `.\gradlew.bat assembleDebug` => PASSED.
- `.\gradlew.bat testDebugUnitTest` => BLOCKED/PREEXISTING: 168 tests, falla `NotificationRoutingContractTest.activityDetailNavigationKeepsActivityInBackStackAndLeavesOtherRoutesUntouched` en línea 93; `MainActivity.kt` no forma parte del diff de este change.
- `git diff --check` => PASSED, con advertencias de normalización LF/CRLF de Git.
- `firebase --version` => no ejecutable disponible.
- `adb version` => no ejecutable disponible.
- Verificación manual/emulador `5.4` => no ejecutada por ausencia de `adb`, Firebase CLI y sesión/backend de prueba.

## Cierre del subagente

- Status: `BLOCKED`
- Causa: `MANUAL_VERIFICATION_UNAVAILABLE`; además el suite completo conserva un fallo preexistente ajeno al diff.
- Próximo dato requerido: ejecutar el flujo en emulador/backend de prueba y confirmar ausencia de sighting, notification y Chat para un reporter bloqueado; resolver o aceptar explícitamente el contrato de navegación preexistente.

## Reparación posterior: crash de arranque en emulador

- Síntoma reproducido: la app volvía al launcher durante el arranque.
- Evidencia: `IllegalStateException: Migration didn't properly handle: content_reports(...)`.
- Causa: la migración creaba restricciones `UNIQUE(...)`, pero Room esperaba índices explícitos con nombres generados por `@Index`.
- Corrección: `MIGRATION_8_9` ahora crea explícitamente los índices únicos `index_content_reports_sightingId_reportingUserId_reason` y `index_user_blocks_blockerUserId_blockedUserId`.
- Verificación: APK reinstalado sobre datos existentes del AVD `emulator-5554`; Room migró, `com.findyourpet.app` permaneció activo y Home cargó correctamente.
- `.\gradlew.bat assembleDebug` => PASSED.
- `.\gradlew.bat testDebugUnitTest --tests com.findyourpet.app.ModerationContractTest --tests com.findyourpet.app.PetPostRetiredAttributesStaticTest` => PASSED.

## Estado actual

- Relectura de Jira SCRUM-25 completada con los requisitos funcionales, de seguridad, UI y fuera de alcance.
- `openspec status --change "add-moderation-actions-to-sighting-detail" --json`: todos los artefactos completos.
- `openspec validate "add-moderation-actions-to-sighting-detail" --strict`: PASSED.
- `openspec instructions apply --change "add-moderation-actions-to-sighting-detail" --json`: 17/18 tareas completas; queda 5.4 bloqueada por verificación manual no disponible.
- Delegación obligatoria al agente `findyourpet-implementer`; el orquestador no implementa Kotlin ni reglas directamente.
- Agente delegado: `Averroes` (`01a0065e-fb1a-7681-90cb-41fadcf65d28`).
- Implementación: 17/18 tareas completadas; queda abierta `5.4` por falta de entorno emulador/backend.
- Verificación local del orquestador: `openspec validate "add-moderation-actions-to-sighting-detail" --strict` => PASSED.
- Verificación local del orquestador: `git diff --check` => PASSED.
- Verificación local del orquestador: `.\gradlew.bat testDebugUnitTest --tests com.findyourpet.app.ModerationContractTest --tests com.findyourpet.app.PetPostRetiredAttributesStaticTest --no-daemon` => BUILD SUCCESSFUL.
- Verificación local del orquestador: `.\gradlew.bat assembleDebug --no-daemon` => exit code 0.
- Entorno: `adb` => NOT_FOUND; `firebase` => NOT_FOUND.
- Suite completa reportada por el implementador: un fallo preexistente en `NotificationRoutingContractTest.kt:93`; `MainActivity.kt` no pertenece al diff.
- Estado de cierre: `BLOCKED` por `MANUAL_VERIFICATION_UNAVAILABLE`; no habilitado para merge ni integración.

# Orquestación de SCRUM-25

## Autorización y Scrum normalizado

- Autorización explícita de trabajo paralelo recibida del usuario el 2026-08-15.
- Issue Jira: `SCRUM-25`.
- Título: `Implementar Reportar contenido y Bloquear usuario en Detalle de Avistamiento`.
- Estado: `To Do`.
- Prioridad: `Medium`.
- Sprint: `SCRUM Sprint 1`.
- Vencimiento: `2026-08-15`.
- Referencia: https://pelaezarmando.atlassian.net/browse/SCRUM-25

### Alcance funcional

- Agregar menú contextual `Reportar contenido` y `Bloquear usuario` en `SightingDetailScreen`.
- Las acciones usan `sightingId`, el propietario de la publicación como actor y `reporterId` como usuario afectado; no usan Chat.
- Reportar muestra motivos seleccionables, permite cancelar, persiste un registro independiente con sighting, usuarios, motivo, fecha y estado inicial pendiente, evita duplicados por taps y conserva el avistamiento histórico.
- Bloquear solicita confirmación, persiste una relación única `blockerUserId + blockedUserId`, no borra históricos y no ofrece desbloqueo.
- La creación de nuevos avistamientos debe rechazar bloqueos antes de persistir `SightingAlertEntity`, alerta o cualquier dato de Chat, sin revelar información innecesaria.
- La UI debe cubrir loading, success y error sin crash, usando componentes/tokens existentes, Light/Dark y accesibilidad.

### Fuera de alcance

- Chat, respuestas, mensajería, desbloqueo, panel administrativo, moderación automática, suspensión/eliminación de cuentas, eliminación automática de contenido, migración histórica y rediseño general de Actividad.

## Preflight y sincronización

- `git status --short --branch` inicial: `main...origin/main`.
- `git status --porcelain=v1` inicial: vacío.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: `Already up to date`.
- `git rev-parse main`: `2c578c3d7b575798e3b85a7b699d7c58017e21a6`.
- `git rev-parse origin/main`: `2c578c3d7b575798e3b85a7b699d7c58017e21a6`.
- Rama creada desde `main`: `ops/add-moderation-actions-to-sighting-detail`.
- `git rev-parse HEAD` de la nueva rama: `2c578c3d7b575798e3b85a7b699d7c58017e21a6`.

## Contraste técnico

- `SightingDetailScreen` ya carga el avistamiento por `sightingId`, muestra datos de mascota, ubicación, foto y notas, y usa Material 3/tokens.
- `SightingAlertEntity` ya contiene `id`, `ownerId`, `reporterId`, `reporterName`, datos de publicación, notas y timestamp.
- `PetRepository.submitSightingAlert` es el punto de persistencia y fan-out donde debe aplicarse el bloqueo antes de crear sighting/notification.
- Room v8 y Firestore no tienen entidades/colecciones/reglas de moderación; el change deberá agregar la persistencia y autorización necesarias explícitamente.
- El cambio visual respeta `docs/design-system.md`: Material 3 estable, componentes/tokens existentes, Light/Dark, sin valores hardcodeados ni APIs experimentales.
- Se detectaron changes OpenSpec paralelos/in-progress relacionados con navegación/detalle/chat; el usuario autorizó trabajo paralelo y este change no reutilizará Chat.

## OpenSpec

Los artefactos se generarán mediante las instrucciones del CLI de OpenSpec en el orden requerido. No existe change equivalente previo.

## Integración

Después de implementar y verificar, el change quedará `PASSED_PENDING_INTEGRATION` hasta contar con merge autorizado a `main` y sincronización con `origin/main`.
