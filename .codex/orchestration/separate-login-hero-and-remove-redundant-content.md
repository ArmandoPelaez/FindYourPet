# Orchestration State: separate-login-hero-and-remove-redundant-content

state: PASSED_PENDING_INTEGRATION
phase: VERIFYING
issue: SCRUM-41
change: separate-login-hero-and-remove-redundant-content
base_branch: main
base_commit: a195cddaa9ca300274dec7ec87d25c05fbfac3e8
remote_base_commit: a195cddaa9ca300274dec7ec87d25c05fbfac3e8
branch: ops/separate-login-hero-and-remove-redundant-content
branch_head_after_creation: a195cddaa9ca300274dec7ec87d25c05fbfac3e8
delegation_status: COMPLETED
handoff_mode: SUBAGENT
agent_id: 01a010b5-46c7-7722-a8c8-fed96da422de
agent_role: findyourpet-implementer
delegation_error:
integration_status: PENDING
integrated_commit:
integration_evidence:

## Preflight y sincronización

- `git status --short --branch`: `## main...origin/main`.
- `git status --porcelain=v1`: vacío.
- `git switch main`: OK.
- `git fetch origin --prune`: OK.
- `git pull --ff-only origin main`: OK; Already up to date.
- `git rev-parse main`: `a195cddaa9ca300274dec7ec87d25c05fbfac3e8`.
- `git rev-parse origin/main`: `a195cddaa9ca300274dec7ec87d25c05fbfac3e8`.
- `main` quedó limpia y sincronizada.
- Ramas no fusionadas revisadas: ramas históricas o documentadas como trabajo integrado; no se detectó una rama activa desconocida que bloquee este change.

## Jira Scrum normalizado

- Issue: `SCRUM-41` — `Separar hero del formulario y eliminar contenido redundante del Login`.
- Tipo: Task.
- Estado Jira: In Progress.
- Prioridad: Medium.
- Fecha límite: `2026-08-17`.
- Dependencias, subtareas, enlaces y adjuntos: no declarados.
- Objetivo: separar claramente el hero contextual del formulario de autenticación y eliminar el subtítulo redundante del Login.
- Jerarquía requerida: identidad FindYourPet → fondo/contexto visual → headline → supporting text → separación visual → `Iniciar sesión` → Email → Contraseña → acciones.
- Mantener el headline y supporting text aprobados, incluyendo `Reportá, buscá y ayudá a reencontrar mascotas.`.
- Eliminar el subtítulo antiguo del Login y no reemplazarlo por otro texto equivalente.
- Separar mediante jerarquía tipográfica y spacing tokenizado; no agregar cards, divisores ni superficies nuevas.
- Mantener comportamiento de Email, Contraseña, Entrar, Google y Crear una cuenta.
- Mantener responsive, scroll/IME, accesibilidad, Light Theme y Dark Theme.
- Fuera de alcance: lógica de autenticación, ViewModel, campos, botones, Google Sign-In, asset de fondo, identidad visual y nuevas funcionalidades.
- Validación requerida: revisión visual, pantalla pequeña, teclado abierto, acciones de autenticación, `testDebugUnitTest` y `assembleDebug`.

## Contraste técnico y de diseño

- Se leyó `docs/design-system.md`; es la fuente de verdad visual.
- El cambio está limitado a `AuthScreen.kt` y sus pruebas de presentación si fueran necesarias.
- La pantalla ya usa `MaterialTheme`, `AppSpacing`, `AppTypography`/tipografía Material, `AppFormTypography` y `AppShapes`; no se deben introducir valores visuales arbitrarios.
- La implementación actual agrupa hero y encabezado del formulario en una misma columna con `AppSpacing.compactGap` y aún renderiza supporting text debajo del título funcional; OpenSpec debe precisar la separación y la eliminación del texto redundante sin alterar el flujo de autenticación.
- No se detectó un change OpenSpec, archivo de orquestación o rama equivalente con este nombre.

## OpenSpec

- `openspec new change "separate-login-hero-and-remove-redundant-content"`: OK; schema `spec-driven`.
- Artefactos completos: `proposal.md`, `design.md`, `specs/login-presentation/spec.md`, `tasks.md`.
- `openspec status --change "separate-login-hero-and-remove-redundant-content"`: 4/4 artefactos completos.
- `openspec validate "separate-login-hero-and-remove-redundant-content" --strict`: OK.

## Resultado actual

- Estado operativo: `READY_FOR_IMPLEMENTATION`.
- Delegación: subagente `Parfit` (`01a010b5-46c7-7722-a8c8-fed96da422de`) ejecutando `findyourpet-implementer`.
- Reporte del implementador: `READY_FOR_VERIFICATION`, progreso `13/14`.
- Archivos modificados reportados: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt`, `app/src/test/java/com/findyourpet/app/AuthScreenPresentationStaticTest.kt` y `tasks.md`.
- Implementado: separación del hero con `AppSpacing.formGap`, eliminación del subtítulo redundante del Login, preservación del texto de registro, callbacks, campos, foco, scroll, `imePadding`, temas y validaciones.
- Evidencia reportada: `openspec validate --strict`, `testDebugUnitTest`, `assembleDebug` y `git diff --check` exitosos.
- Tarea pendiente reportada: revisión manual en dispositivo/emulador con pantalla pequeña, Light/Dark Theme y teclado abierto; no hay dispositivo disponible.
- Estado operativo: `VERIFYING`.

## Verificación final del orquestador

- `openspec instructions apply --change "separate-login-hero-and-remove-redundant-content" --json`: 13/14 tareas; solo queda 4.3 por validación manual no disponible.
- `openspec validate "separate-login-hero-and-remove-redundant-content" --strict`: OK.
- `./gradlew.bat testDebugUnitTest --no-daemon --console=plain`: BUILD SUCCESSFUL.
- `./gradlew.bat assembleDebug --no-daemon --console=plain`: BUILD SUCCESSFUL.
- `git diff --check`: OK; solo advertencias normales de conversión LF/CRLF de Git.
- Diff de implementación revisado: únicamente `AuthScreen.kt` y `AuthScreenPresentationStaticTest.kt`, además de artefactos OpenSpec y este estado de orquestación.
- `adb`: no disponible (`ADB_NOT_AVAILABLE`); no fue posible validar manualmente pantalla pequeña, Light/Dark Theme y teclado abierto.
- La tarea 4.3 queda justificada como limitación externa del entorno; no se afirma evidencia manual inexistente.

## Resultado

- Estado: `PASSED_PENDING_INTEGRATION`.
- La rama está validada y pendiente de integración autorizada en `main`.
- No se creó commit, merge, push ni PR automáticamente.
