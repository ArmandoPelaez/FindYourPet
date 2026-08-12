# Orchestration State: require-name-for-lost-pet-alert

state: PASSED_PENDING_INTEGRATION
phase: VERIFYING
parallel_work_authorized: true
integration_status: PENDING
integrated_commit:
integration_evidence:

## Jira

- issue: SCRUM-9
- title: Agregar etiqueta Nombre al campo y hacerlo obligatorio para el reporte de mascotas perdidas
- type: Task
- status: To Do
- priority: Medium
- sprint: SCRUM Sprint 1 (active)
- due_date: 2026-08-13
- assignee: Armando Pelaez
- jira_url: https://pelaezarmando.atlassian.net/browse/SCRUM-9

## Scrum normalizado

- Objetivo: hacer más intuitivo el ingreso del nombre de la mascota en el formulario de publicación de mascota perdida.
- Alcance funcional: mostrar la etiqueta `Nombre` con `*` en el campo de nombre y validar que no esté vacía al guardar.
- Mensaje requerido: mostrar `Campo obligatorio` cuando se intenta guardar sin nombre.
- Alcance visual: conservar colores, tipografías, tamaños, formas, espaciados y componentes existentes; usar Material 3 estable y tokens del Design System.
- Fuera de alcance: otros formularios, lógica de negocio no relacionada, backend, Firebase, repositorios, navegación y rediseños.
- Dependencias declaradas en Jira: ninguna.
- Adjuntos, comentarios o enlaces adicionales: ninguno.
- Supuesto resuelto: el campo objetivo es el de `CreatePetPostScreen`, porque allí se introduce el nombre de una mascota perdida; `SightingAlertScreen` recibe el nombre desde la publicación seleccionada y no ofrece ese campo.

## Preflight y sincronización

- `git status --short --branch` inicial: `## main...origin/main`.
- `git status --porcelain=v1`: vacío.
- `git switch main`: correcto.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: `Already up to date.`
- `base_branch: main`
- `base_commit: a5acd0143211043301034ae90d1685216990fa57`
- `remote_base_commit: a5acd0143211043301034ae90d1685216990fa57`
- Ramas/changess previos no integrados fueron revisados y se autorizó explícitamente trabajo paralelo.
- Rama creada: `ops/require-name-for-lost-pet-alert`.
- `git rev-parse HEAD` después de crear la rama: `a5acd0143211043301034ae90d1685216990fa57`.

## Contraste técnico

- `docs/design-system.md` leído antes de preparar el cambio visual.
- Campo actual en `app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt`: usa placeholder `Nombre de la mascota (Ej. Toby, Mia)` sin `label`.
- El botón actual ya se deshabilita con `petName.isNotBlank()`, pero la especificación requiere además validación al guardar y mensaje explícito `Campo obligatorio`.
- `RealProductValidators.validatePost` ya rechaza nombres vacíos con otro mensaje; el change debe alinear la presentación y el mensaje visible del formulario sin alterar contratos backend.

## OpenSpec

- Nombre derivado: `require-name-for-lost-pet-alert`.
- Duplicado en orquestación, OpenSpec o ramas: no encontrado.
- Estado y artefactos: pendientes de generación mediante OpenSpec CLI.

## Artefactos OpenSpec

- `openspec status --change "require-name-for-lost-pet-alert"`: 4/4 artifacts complete.
- `openspec validate "require-name-for-lost-pet-alert" --strict`: passed.
- Artefactos: `proposal.md`, `design.md`, `specs/pet-posts/spec.md`, `tasks.md`.

## Evidencia de delegación

- delegation_status: SPAWNED
- handoff_mode: SUBAGENT
- agent_id: 019ff665-cd05-7042-bd17-7633372fc683
- agent_role: findyourpet-implementer
- delegation_error:

## Implementer report

- status: `READY_FOR_VERIFICATION`
- progress: `10/10`
- agent_id: `019ff665-cd05-7042-bd17-7633372fc683`
- implementation: `CreatePetPostScreen` now exposes `Nombre` with a primary-token `*` indicator and validates blank/whitespace-only names with `Campo obligatorio` before publication.
- tests: static form assertions, validation unit tests and Robolectric Light/Dark compact/tall presentation coverage updated.
- reported commands: OpenSpec strict validation, `testDebugUnitTest`, `assembleDebug`, `git diff --check`, and apply status all passed.

## Verification evidence

- `openspec instructions apply --change "require-name-for-lost-pet-alert" --json` => `10/10`, `all_done`.
- `openspec validate "require-name-for-lost-pet-alert" --strict` => passed.
- `git diff --check` => passed; only expected LF/CRLF normalization warnings.
- `.\\gradlew.bat testDebugUnitTest` => `BUILD SUCCESSFUL`; 35 actionable tasks up to date.
- `.\\gradlew.bat assembleDebug` => `BUILD SUCCESSFUL`; 41 actionable tasks up to date.
- `git status --short --branch` => only SCRUM-9 implementation/tests and OpenSpec/orchestration artifacts on `ops/require-name-for-lost-pet-alert`; no IDE deployment-target change remains.
- Scope review: no backend, Firebase, repository, ViewModel, navigation, permission or unrelated-form files changed.
- Manual/visual evidence: Robolectric verified Light/Dark Theme and compact/tall phone layouts; no physical device or emulator evidence is required beyond the available screenshot harness for this change.

## Integration

- integration_status: PENDING
- integrated_commit:
- integration_evidence:
- branch_ready_for_integration: `ops/require-name-for-lost-pet-alert`

## Alcance ampliado autorizado por el usuario

- Se incorporó un refactor transversal de tipografía de campos solicitado explícitamente después de la corrección visual de SCRUM-9.
- Se documentaron los contratos de label, placeholder y texto ingresado en `docs/design-system.md`.
- Se agregaron `AppFormTypography`, `FormFieldLabel` y `FormFieldPlaceholder`.
- Se migraron los `OutlinedTextField` existentes de autenticación, chat, publicación y alerta para usar los tokens comunes.
- Se agregó cobertura de contrato tipográfico en `TypographyConsistencyTest`.

## Reference alignment correction

- User feedback: the first implementation did not match the supplied reference because it used a floating `OutlinedTextField` label and omitted the section header icon/placeholder layout.
- Correction: the form now uses the existing primary circular `Pets` header treatment, an independent `Nombre` label with primary-token `*`, and placeholder `Ej. Toby, Mia` inside the field.
- Explicitly excluded per user feedback: person/account icon inside the text field.
- Initial focused screenshot assertions failed because the asterisk text included a leading space; the spacing was moved to the `Row` arrangement so the semantic node is exactly `*`.
- `:app:recordRoborazziDebug --tests "com.findyourpet.app.CreatePetPostScreenScreenshotTest"` => `BUILD SUCCESSFUL`.
- Focused form/validation/screenshot tests after correction => `BUILD SUCCESSFUL`.
- Full `.\\gradlew.bat testDebugUnitTest` after correction => `BUILD SUCCESSFUL`.
- `.\\gradlew.bat assembleDebug` after correction => `BUILD SUCCESSFUL`.
- Visual inspection of regenerated compact Light and tall Dark screenshots confirmed the requested hierarchy and no person icon.
- Android Studio deployment-target metadata was restored after Robolectric changed its timestamp; it is excluded from the final diff.
