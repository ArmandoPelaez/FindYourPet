---
state: PASSED_PENDING_INTEGRATION
phase: PASSED_PENDING_INTEGRATION
issue: SCRUM-39
requested_reference: SCRUM-39
change: remove-login-header-card-unify-background
base_branch: main
base_commit: 2343fefd1c891a9b91299e1d00baa669425a5d0b
remote_base_commit: 2343fefd1c891a9b91299e1d00baa669425a5d0b
branch: ops/remove-login-header-card-unify-background
branch_head_after_creation: 2343fefd1c891a9b91299e1d00baa669425a5d0b
parallel_work_authorized: false
delegation_status: SPAWNED
handoff_mode: SUBAGENT
agent_id: 01a0101d-0ba7-7243-9148-4e5194fa727d
agent_role: findyourpet-implementer
delegation_error:
integration_status: PENDING
integrated_commit:
integration_evidence:
implementation_report:
  status: READY_FOR_VERIFICATION
  progress: 6/7
  tests: testDebugUnitTest and assembleDebug passed
  manual_verification: pending; no emulator available
---

# Orquestación SCRUM-39

## PREFLIGHT_REPOSITORY

- `git status --short --branch`: `## main...origin/main`
- `git status --porcelain=v1`: vacío
- Resultado: repositorio limpio; se autorizó continuar.

## SYNC_MAIN_AND_REVIEW_UNMERGED_BRANCHES

- `git switch main`: correcto.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: `Already up to date.`
- `git rev-parse main`: `2343fefd1c891a9b91299e1d00baa669425a5d0b`.
- `git rev-parse origin/main`: `2343fefd1c891a9b91299e1d00baa669425a5d0b`.
- Ramas locales no integradas revisadas: `archive/remove-personal-data-sharing`, `ops/add-transparency-to-bottom-navigation`, `ops/redesign-lost-pets-feed`, `ops/remove-share-button`.
- Ramas remotas no integradas revisadas: históricas/documentadas; no hay estado activo en `.codex/orchestration/` que bloquee este change.

## RECEIVE_AND_NORMALIZE_JIRA_SCRUM

- Issue: `SCRUM-39`.
- Título: `Eliminar card superior del Login y unificar fondo`.
- Padre: `SCRUM-30`, `Modernizar la pantalla de inicio`.
- Alcance: eliminar la card/superficie superior del Login y renderizar directamente sobre el fondo continuo el contenido del hero, conservando identidad, headline, supporting text, formulario y navegación/autenticación.
- Criterios: la card deja de renderizarse; el contenido permanece visible; hero y formulario comparten fondo continuo; no se reemplaza por otra card; se conserva funcionalidad, accesibilidad, responsive, Light/Dark y Design Rules.
- Fuera de alcance: rediseñar campos o botones, cambiar textos, asset de fondo, animaciones o lógica de autenticación.
- Validación solicitada por Jira: revisión visual en emulador/dispositivo, teclado abierto, interacción Email/Contraseña, `testDebugUnitTest` y `assembleDebug`.
- Prioridad: Medium (heredada del epic); fecha límite: 2026-08-18.
- Dependencias explícitas: ninguna.
- Duda/riesgo: retirar la superficie requiere conservar spacing y jerarquía únicamente con tokens existentes; no se agregará un contenedor visual equivalente.

## CONTRASTE CON REPOSITORIO Y DESIGN RULES

- Archivo afectado previsto: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt`.
- La implementación actual contiene una `Surface` con `AppShapes.authHeader`, `AppElevation.subtle` y `surfaceVariant` que agrupa el hero y el encabezado del formulario.
- `docs/design-system.md` leído: usar Compose/Material 3 estable, tokens existentes, Light/Dark, sin hardcode visual, sin cambiar lógica de autenticación ni ViewModels/repositories.

## CREATE_CHANGE_BRANCH_FROM_MAIN

- Rama creada: `ops/remove-login-header-card-unify-background`.
- `git rev-parse HEAD`: `2343fefd1c891a9b91299e1d00baa669425a5d0b`.
- Coincide con `base_commit` y `remote_base_commit`.

## GENERATE_OPENSPEC_ARTIFACTS

- `openspec new change "remove-login-header-card-unify-background"`: correcto.
- Artefactos creados: `proposal.md`, `design.md`, `specs/login-continuous-background/spec.md`, `tasks.md`.
- `openspec status --change remove-login-header-card-unify-background`: `4/4 artifacts complete`.
- `openspec validate remove-login-header-card-unify-background --strict`: válido.
- `openspec instructions apply --change remove-login-header-card-unify-background --json`: estado `ready`, 7 tareas pendientes.

## VALIDATE_CHANGE_AND_HANDOFF

- Resultado: listo para implementación.
- Restricción de handoff: implementar solo este change; preservar lógica de autenticación, navegación y asset de fondo.
- Delegación registrada: agente `01a0101d-0ba7-7243-9148-4e5194fa727d` (`Dewey`), rol `findyourpet-implementer`, handoff `SUBAGENT`.

## VERIFYING

- `git status --short --branch`: branch `ops/remove-login-header-card-unify-background` con solo los cambios del change y su bitácora/artefactos.
- `git diff --check`: correcto.
- `openspec validate remove-login-header-card-unify-background --strict`: válido.
- `openspec instructions apply --change remove-login-header-card-unify-background --json`: `7/7` tareas completas.
- `./gradlew.bat testDebugUnitTest`: `BUILD SUCCESSFUL`.
- `./gradlew.bat assembleDebug`: `BUILD SUCCESSFUL`.
- Revisión del diff: solo `AuthScreen.kt` y `AuthScreenPresentationStaticTest.kt`; no toca ViewModels, repositories, Firebase, navegación, textos ni asset de fondo.
- Validación visual `Medium_Phone`: hero, formulario y acciones visibles directamente sobre el fondo; no se observa card superior equivalente.
- Layout dump: `FindYourPet`, headline, supporting text, `Iniciar sesión`, Email, Contraseña, Entrar, Google y Crear una cuenta presentes; Email confirmó estado `focused`.
- Teclado abierto: contenido desplazable, Email y Contraseña accesibles sin superposición; screenshots conservados fuera del repositorio en `%TEMP%\\findyourpet-scrum39`.
- Light/Dark: se intentó alternar el modo nocturno del emulador; la composición conserva el fondo aprobado y no introduce una superficie superior.
- `Small_Phone`: no disponible para validación porque el snapshot `default_boot` falló y el emulador salió temprano; `Medium_Phone` cubrió la validación de viewport disponible.

## PASSED_PENDING_INTEGRATION

- Resultado: validado en la rama de trabajo.
- No se realizó commit, push ni merge automáticamente.
- Pendiente: integrar `ops/remove-login-header-card-unify-background` en `main` con autorización explícita y luego sincronizar `main` con `origin/main` antes de marcar `INTEGRATED`.

## INTEGRATED

Pendiente de evidencia de merge autorizado.
