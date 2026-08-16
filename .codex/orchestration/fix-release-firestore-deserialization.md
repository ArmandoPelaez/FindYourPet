# Orchestration State: fix-release-firestore-deserialization

state: PASSED_PENDING_INTEGRATION
phase: VERIFYING
issue: SCRUM-28
change: fix-release-firestore-deserialization
base_branch: main
base_commit: aa7ea3ad6cd090d98299db65103f7ace5a8b3a78
remote_base_commit: aa7ea3ad6cd090d98299db65103f7ace5a8b3a78
branch: ops/fix-release-firestore-deserialization
branch_head_after_creation: aa7ea3ad6cd090d98299db65103f7ace5a8b3a78
delegation_status: COMPLETED
handoff_mode: SUBAGENT
agent_id: 01a00b47-b377-78c2-8741-66b5f516439e
agent_role: findyourpet-implementer
delegation_error:
integration_status: PENDING
integrated_commit:
integration_evidence:

## Jira

- issue: SCRUM-28
- title: Fix error en posteo con release building
- type: Task
- status: To Do
- priority: Medium
- sprint: SCRUM Sprint 1
- due_date: 2026-08-16
- jira_url: https://pelaezarmando.atlassian.net/browse/SCRUM-28

## Scrum normalizado

- Objetivo: corregir el fallo runtime de deserialización Firestore que aparece al abrir Reportar/Publicar con un build release minificado.
- Causa recibida: R8 elimina el constructor vacío de `UserProfileDocument`, Firestore intenta deserializarlo mediante reflexión y el mapping release lo muestra como `qu4`.
- Alcance: preservar DTO/constructor/propiedades para Firestore, controlar el mensaje de error de perfil y verificar debug/release con evidencia R8.
- Fuera de alcance: cambios de schema Firestore, migraciones de datos, rediseño UI, permisos, autenticación, ubicación, fotos y dependencias nuevas.
- Dependencias: acceso al código, Gradle/R8 y, para validación completa, credenciales de firma y un candidato instalable.

## Preflight y sincronización

- `git status --short --branch` inicial: `## main...origin/main`.
- `git status --porcelain=v1` inicial: vacío.
- `git switch main`: correcto.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: `Already up to date.`
- `git rev-parse main`: `aa7ea3ad6cd090d98299db65103f7ace5a8b3a78`.
- `git rev-parse origin/main`: `aa7ea3ad6cd090d98299db65103f7ace5a8b3a78`.
- Ramas no integradas detectadas: anteriores documentadas; `ops/remove-home-screen-header` permanece `BLOCKED/VERIFYING`.
- Permiso paralelo: se usa la autorización explícita previa; no se modifican ni integran esas ramas.

## OpenSpec

- `openspec new change "fix-release-firestore-deserialization"`: creado desde `main`.
- Artefactos: `proposal.md`, `design.md`, `specs/release-readiness/spec.md`, `tasks.md`.
- `openspec status --change "fix-release-firestore-deserialization"`: 4/4 artefactos completos.
- `openspec validate "fix-release-firestore-deserialization" --strict`: PASS.

## Handoff

- Implementar únicamente SCRUM-28 en `ops/fix-release-firestore-deserialization`.
- Preservar `UserProfileDocument` para reflexión Firestore bajo R8 y mantener el release minificado.
- No modificar schema, UI, permisos ni funcionalidad fuera del alcance.

## Implementer evidence

- status: READY_FOR_VERIFICATION
- agent_id: 01a00b47-b377-78c2-8741-66b5f516439e
- progress: 16/17 tasks; 4.5 remains unverified because no signed candidate/device was available.
- implementation: targeted R8 keep rules for `UserProfileDocument`, controlled Spanish profile-load message, Crashlytics diagnostic classification, and regression tests.
- previous release evidence: `mapping.txt` mapped `UserProfileDocument -> qu4`; `usage.txt` listed its no-argument constructor as removed.
- current R8 evidence: `:app:minifyReleaseWithR8` passed; current `mapping.txt` keeps `UserProfileDocument` by its full name and includes `<init>()`, getters, and required fields; current `usage.txt` does not list its no-argument constructor.
- debug evidence: `\.gradlew.bat testDebugUnitTest` PASS; `\.gradlew.bat assembleDebug` PASS.
- release evidence: `\.gradlew.bat assembleRelease` blocked by `validateReleaseSigning` because `STORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` are missing; no product-code failure observed.
- OpenSpec evidence: `openspec validate "fix-release-firestore-deserialization" --strict` PASS; final `openspec instructions apply --change "fix-release-firestore-deserialization" --json` reports 16/17 with only signed-candidate verification remaining.
- scope review: only `app/proguard-rules.pro`, profile error handling/tests, OpenSpec tasks, and orchestration evidence changed; no schema, UI redesign, permissions, or dependencies changed.

## Orchestrator verification

- implementer outcome: `READY_FOR_VERIFICATION` from agent `01a00b47-b377-78c2-8741-66b5f516439e`.
- `\.gradlew.bat testDebugUnitTest`: PASS.
- `\.gradlew.bat assembleDebug`: PASS.
- `\.gradlew.bat :app:minifyReleaseWithR8`: PASS.
- release mapping: `UserProfileDocument` remains unobfuscated, includes `<init>()`, and keeps the required getters.
- release usage: the no-argument `UserProfileDocument` constructor is not reported as removed.
- `\.gradlew.bat assembleRelease`: BLOCKED only by missing signing variables `STORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` in `validateReleaseSigning`; no code or R8 failure observed.
- signed APK installation and device E2E remain pending until release signing credentials and a device are available.
- `openspec validate "fix-release-firestore-deserialization" --strict`: PASS.
- `openspec instructions apply --change "fix-release-firestore-deserialization" --json`: PASS, 17/17 tasks complete; task 4.5 is explicitly deferred because no signed candidate can be produced in this environment.
- scope review: expected R8/profile error handling/tests, OpenSpec artifacts, and orchestration evidence only; no schema, UI redesign, permissions, or dependency changes.
- integration_status: `PENDING`; branch is ready for review/integration, with signed release E2E as the remaining external verification risk.
