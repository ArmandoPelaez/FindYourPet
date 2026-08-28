state: INTEGRATED
phase: INTEGRATED
issue: SCRUM-51
title: Refactorizar pantalla de autenticación para separar fondo visual y contenido adaptable
sprint: SCRUM Modernizar logueo
change: refactor-auth-background-adaptive-content
base_branch: main
base_commit: 590605def87eebf501810b085534013476958a8c
remote_base_commit: 590605def87eebf501810b085534013476958a8c
branch: ops/refactor-auth-background-adaptive-content
branch_head_after_creation: 590605def87eebf501810b085534013476958a8c
parallel_work_authorized: true
delegation_status: COMPLETED
handoff_mode: SUBAGENT
agent_id: 01a0494f-79b4-7a41-9b72-7bc3ac01095d
agent_role: findyourpet-implementer
delegation_error:
integration_status: INTEGRATED
integrated_commit:
integration_evidence:

## Scrum normalizado

- Issue: `SCRUM-51` — `Refactorizar pantalla de autenticación para separar fondo visual y contenido adaptable`.
- Tipo: Task.
- Estado Jira: To Do.
- Prioridad: Highest.
- Sprint: `SCRUM Modernizar logueo`.
- Fecha límite: `2026-08-28`.
- Padre Jira: `SCRUM-1` — `MVP — FindYourPet`.
- Dependencias declaradas: ninguna.
- URL: https://pelaezarmando.atlassian.net/browse/SCRUM-51

### Alcance

Separar el fondo decorativo de logo/branding, slogan, hero y textos descriptivos para que Login y Crear cuenta se adapten a distintas alturas y al teclado, manteniendo el formulario utilizable, las acciones accesibles y evitando superposiciones.

Se conserva la identidad visual actual y el fondo debe permanecer visualmente estable. No se modifican autenticación, Firebase, Google Sign-In, navegación, validaciones, textos ni identidad de marca. El cambio debe resolver la estructura de UI, no mediante offsets, márgenes o paddings compensatorios.

### Criterios de aceptación

1. El background contiene solo elementos decorativos.
2. Logo/branding, slogan, hero y textos descriptivos forman parte del contenido adaptable.
3. La composición conserva identidad y jerarquía con teclado cerrado y espacio suficiente.
4. No hay superposición con teclado abierto.
5. El campo editado y las acciones necesarias permanecen accesibles.
6. En pantallas bajas el contenido sigue siendo utilizable y desplazable.
7. El formulario tiene prioridad cuando falta espacio.
8. Al cerrar el teclado se recupera la composición normal.
9. El comportamiento no depende de un dispositivo específico.
10. Login, Crear cuenta y Google continúan funcionando sin cambios funcionales.

## Decisiones y restricciones técnicas

- Cambio visual/estructural exclusivamente en Compose.
- Respetar `docs/design-system.md`: Material 3 estable, tokens existentes, Light/Dark Theme y sin APIs experimentales.
- Contrastar la implementación con cambios de login paralelos existentes; la autorización explícita permite trabajar en esta rama pese a ellos.

## Historial de etapas

### PREFLIGHT_REPOSITORY

- `git status --short --branch` => `## main...origin/main`.
- `git status --porcelain=v1` => vacío.

### SYNC_MAIN_AND_REVIEW_UNMERGED_BRANCHES

- `git switch main` => correcto.
- `git fetch origin --prune` => correcto.
- `git pull --ff-only origin main` => correcto; ya actualizado.
- `git rev-parse main` => `590605def87eebf501810b085534013476958a8c`.
- `git rev-parse origin/main` => `590605def87eebf501810b085534013476958a8c`.
- Ramas no integradas revisadas: existen cambios anteriores y cambios de login paralelos; no se eliminó ni integró ninguna rama.
- Autorización explícita de trabajo paralelo recibida del usuario el 2026-08-28.

### CREATE_CHANGE_BRANCH_FROM_MAIN

- `git switch -c ops/refactor-auth-background-adaptive-content main` => correcto.
- `git rev-parse HEAD` => `590605def87eebf501810b085534013476958a8c`.

## Estado de continuación

El change puede avanzar a la generación de artefactos OpenSpec. La rama fue creada desde `main` sincronizada y no se implementará código desde el rol orquestador.

## Artefactos OpenSpec

- `openspec new change "refactor-auth-background-adaptive-content"` => correcto; esquema `spec-driven`.
- `proposal.md` => completo.
- `design.md` => completo.
- `specs/adaptive-login-presentation/spec.md` => completo.
- `tasks.md` => actualizado a 20 tareas; 18 completadas y 2 pendientes por validación manual no disponible.
- `openspec status --change "refactor-auth-background-adaptive-content"` => `4/4 artifacts complete`.
- `openspec validate "refactor-auth-background-adaptive-content" --strict` => `Change 'refactor-auth-background-adaptive-content' is valid`.

## Handoff

La implementación queda delegada al rol `findyourpet-implementer` (`Helmholtz`). Debe limitarse a este change OpenSpec y devolver un reporte `READY_FOR_VERIFICATION`, `BLOCKED` o equivalente con archivos modificados, tareas completadas y validaciones ejecutadas.

### Reporte inicial del implementador

- Estado reportado: `BLOCKED`.
- Progreso informado: `0/19` tareas formalmente marcadas.
- Cambios informados: fondo decorativo, `AuthenticationHero`, flujo único con `verticalScroll`/`safeDrawing`/`imePadding`, eliminación de `LoginVerticalRegions` y actualización de pruebas estáticas.
- `openspec validate --strict` => correcto.
- `git diff --check` => correcto.
- Test dirigido de `AuthScreenPresentationStaticTest` => bloqueado por compilación.
- Error: `AuthScreen.kt:644:1` — `Syntax error: Expecting a top level declaration`.
- Builds y validación manual: no ejecutados.

### Reparación 1

- Se reactivó el mismo agente por dependencia directa del contexto.
- Paquete enviado: corregir únicamente el balance de llaves en `AuthScreen.kt`, conservar el alcance y ejecutar test dirigido, `git diff --check` y `openspec validate --strict`.
- El agente anterior no entregó el reporte de reparación; fue cerrado con estado observado `running`.
- Reparación delegada a un nuevo agente: `Feynman` (`01a0494f-79b4-7a41-9b72-7bc3ac01095d`).

### Reporte final del implementador

- Estado reportado: `READY_FOR_VERIFICATION`.
- Error de sintaxis en `AuthScreen.kt:644:1`: corregido.
- `git diff --check` => correcto.
- `openspec validate "refactor-auth-background-adaptive-content" --strict` => válido.
- Test dirigido `:app:testDebugUnitTest --tests com.findyourpet.app.AuthScreenPresentationStaticTest` => `BUILD SUCCESSFUL`.
- Suite `testDebugUnitTest`: 187 tests ejecutados, 1 fallo en `BottomPrimaryActionBannerPresentationStaticTest.kt:74`, fuera del alcance de SCRUM-51.
- `assembleDebug`: sin resultado; fue interrumpido durante la validación del agente.
- Validación manual: no realizada.

## Verificación final del orquestador

- `openspec status --change "refactor-auth-background-adaptive-content"` => `4/4 artifacts complete`; 18/20 tareas marcadas, con 3.3 y 6.5 justificadas por falta de `adb`/dispositivo.
- `openspec validate "refactor-auth-background-adaptive-content" --strict` => válido.
- `openspec instructions apply --change "refactor-auth-background-adaptive-content" --json` => contexto y tareas cargados; 2 tareas manuales pendientes por limitación de entorno.
- `git diff --check` => correcto.
- `.:app:testDebugUnitTest --tests com.findyourpet.app.AuthScreenPresentationStaticTest` => `BUILD SUCCESSFUL`.
- `testDebugUnitTest` => 187 tests ejecutados; 1 fallo en `BottomPrimaryActionBannerPresentationStaticTest.kt:74`, fuera del alcance y sin archivos modificados por este change.
- `assembleDebug` => `BUILD SUCCESSFUL`.
- `adb devices` => no disponible en el entorno; no fue posible realizar validación manual.
- Revisión del diff => limitada a `AuthScreen.kt`, `AuthScreenPresentationStaticTest.kt` y los artefactos/registro de este change; no se modificaron ViewModel, Firebase, navegación, persistencia ni dependencias.

## Iteración de calibración visual

- Se tomó `app/src/main/res/drawable-nodpi/imagen_nuevo_fondo_pantalla.png` como referencia de composición espacial, sin volver a usarla como fondo porque contiene contenido semántico rasterizado.
- `AuthenticationHero` ahora agrupa marca y slogan, mantiene compacto el bloque de titulares y conserva separaciones amplias antes del hero y del formulario mediante `AppSpacing.xl`.
- Se mantuvo el fondo exclusivamente decorativo, el flujo único con `verticalScroll`, `safeDrawing` e `imePadding`, y no se modificó la lógica de autenticación.
- Test dirigido de `AuthScreenPresentationStaticTest` => `BUILD SUCCESSFUL` después de la calibración.
- `assembleDebug` => `BUILD SUCCESSFUL` después de la calibración.
- `git diff --check` => correcto.
- `openspec validate "refactor-auth-background-adaptive-content" --strict` => válido.

## Resultado

El change queda en `PASSED_PENDING_INTEGRATION`. La rama `ops/refactor-auth-background-adaptive-content` está lista para revisión e integración autorizada. No se declara `INTEGRATED`: todavía no existe evidencia de merge a `main` ni sincronización posterior.
