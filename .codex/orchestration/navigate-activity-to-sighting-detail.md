state: BLOCKED
phase: VERIFYING
issue: SCRUM-24
change: navigate-activity-to-sighting-detail
branch: ops/navigate-activity-to-sighting-detail
base_branch: main
base_commit: 0c189a2a81b1269742751ddfb9680e2e1726c237
remote_base_commit: 0c189a2a81b1269742751ddfb9680e2e1726c237
integration_status: PENDING
integrated_commit:
integration_evidence:
delegation_status: COMPLETED
handoff_mode: SUBAGENT
agent_id: 01a00627-6690-7a30-8692-e41d9ec4bafc
agent_role: findyourpet-implementer
delegation_error:

# Orquestación de SCRUM-24

## Autorización y alcance

- Autorización explícita de trabajo paralelo recibida del usuario el 2026-08-15.
- Issue Jira: `SCRUM-24`.
- Título: `Navegar desde Actividad al Detalle de Avistamiento usando sightingIdaa`.
- Estado Jira: `To Do`.
- Prioridad: `Medium`.
- Sprint: `SCRUM Sprint 1`.
- Referencia: https://pelaezarmando.atlassian.net/browse/SCRUM-24

SCRUM-24 requiere que cada elemento de Actividad sea seleccionable y navegue a la pantalla existente de Detalle de Avistamiento usando únicamente su `sightingId`. El flujo debe conservar Back y el estado de Actividad cuando la arquitectura lo permita, no abrir Chat, no crear conversaciones y manejar identificadores inválidos sin crash.

Fuera de alcance: formulario o creación de avistamientos, generación de alertas, navegación existente desde Alertas, rediseño de Actividad o Detalle de Avistamiento, Reportar/Bloquear usuario, eliminación del Chat legacy y migración de conversaciones.

## Preflight y sincronización

- `git status --short --branch` inicial: `main...origin/main`.
- `git status --porcelain=v1` inicial: vacío.
- `git switch main`: correcto.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: `Already up to date`.
- `git rev-parse main`: `0c189a2a81b1269742751ddfb9680e2e1726c237`.
- `git rev-parse origin/main`: `0c189a2a81b1269742751ddfb9680e2e1726c237`.
- Ramas no fusionadas revisadas contra `.codex/orchestration/`; el usuario autorizó trabajo paralelo.
- Rama creada: `ops/navigate-activity-to-sighting-detail` desde `main`.
- `git rev-parse HEAD` de la rama: `0c189a2a81b1269742751ddfb9680e2e1726c237`.

## Contraste técnico

- `ActivityScreen` ya conserva la identidad `sighting.id` en cada item.
- `MainActivity` ya expone `sighting/{sightingId}` y reutiliza `SightingDetailScreen`.
- `PetViewModel` ya mantiene el detalle de sighting independiente del estado de Chat.
- La implementación pendiente está limitada al callback de selección del item, validación segura y navegación.
- Se leyó `docs/design-system.md`: usar Material 3 estable, tokens existentes, touch target/pressed state, Light/Dark y sin valores visuales hardcodeados.

## OpenSpec

Los artefactos se generarán siguiendo las instrucciones del CLI de OpenSpec. No existe carpeta, rama ni change equivalente previo para `navigate-activity-to-sighting-detail`.

- Artefactos generados: `proposal.md`, `design.md`, `specs/activity-sighting-navigation/spec.md`, `tasks.md`.
- `openspec status --change "navigate-activity-to-sighting-detail"`: 4/4 artefactos completos.
- `openspec validate "navigate-activity-to-sighting-detail" --strict`: válido.
- `openspec instructions apply --change "navigate-activity-to-sighting-detail" --json`: ready, 0/13 tareas completas.

## Delegación

La implementación debe ejecutarse mediante `findyourpet-implementer` en un subagente. Handoff requerido:

`Change: navigate-activity-to-sighting-detail`

`Issue Jira: SCRUM-24`

`delegation_required: true`

`handoff_mode: SUBAGENT`

- Agent nickname: `Meitner`.
- Handoff enviado con `delegation_required: true`.

## Reporte del implementador

- Estado reportado: `READY_FOR_VERIFICATION`.
- Progreso reportado: `11/13` tareas.
- Archivos de implementación: `ActivityScreen.kt`, `MainActivity.kt`.
- Archivos de prueba: `ActivityContractStaticTest.kt`, `NotificationRoutingContractTest.kt`, `ActivityScreenComposeTest.kt`.
- `openspec validate ... --strict`: válido.
- `\.\gradlew.bat testDebugUnitTest`: `BUILD SUCCESSFUL`.
- `\.\gradlew.bat assembleDebug`: `BUILD SUCCESSFUL`.
- `git diff --check`: sin errores.
- Tareas pendientes reportadas: 4.1 por cierre de apply y 4.4 por flujo manual no ejecutado; `adb` no está disponible.

## Verificación del orquestador

- `openspec validate "navigate-activity-to-sighting-detail" --strict`: `Change ... is valid`.
- `openspec instructions apply --change "navigate-activity-to-sighting-detail" --json`: `11/13`; tareas 4.1 y 4.4 permanecen pendientes por la validación manual.
- `\.\gradlew.bat testDebugUnitTest`: `BUILD SUCCESSFUL`.
- `\.\gradlew.bat assembleDebug`: `BUILD SUCCESSFUL`.
- `git diff --check`: sin errores; solo advertencias de normalización LF/CRLF.
- El diff queda limitado a `ActivityScreen.kt`, `MainActivity.kt`, pruebas de Activity/routing, artefactos OpenSpec y el estado de orquestación.
- No se modificaron backend, Room, Firestore, reglas, creación de avistamientos, notificaciones, Alertas ni Chat legacy.

## Bloqueo

- `Get-Command adb`: `ADB_NOT_AVAILABLE`.
- No hay dispositivo/emulador disponible para ejecutar el flujo `Actividad → Detalle de Avistamiento → Back → Actividad` en Light/Dark ni el caso manual de identificador inválido.
- La cobertura automatizada de Compose y routing pasó, pero no sustituye la validación manual exigida para este cambio visual.
- Estado final: `BLOCKED` hasta disponer de `adb` y un dispositivo/emulador verificable o autorización para aceptar una excepción explícita.

## Integración

El change no se considerará `INTEGRATED` por pasar tests. Después de la verificación deberá quedar `PASSED_PENDING_INTEGRATION` hasta contar con merge autorizado a `main` y sincronización con `origin/main`.
