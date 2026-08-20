# Orchestration State: unify-report-with-bottom-navigation-structure

state: BLOCKED
phase: SYNC_MAIN_AND_REVIEW_UNMERGED_BRANCHES
issue: SCRUM-47
change: unify-report-with-bottom-navigation-structure
base_branch: main
base_commit: 53f8bfaca9ca5c7443c943b6de2a603a8c048e77
remote_base_commit: 53f8bfaca9ca5c7443c943b6de2a603a8c048e77
branch:
branch_head_after_creation:
delegation_status:
handoff_mode:
agent_id:
agent_role: findyourpet-implementer
delegation_error:
integration_status: PENDING
integrated_commit:
integration_evidence:

## Jira Scrum normalizado

- Clave: `SCRUM-47`.
- Título: `Unificar “Reportar” con la estructura visual de la barra de navegación inferior`.
- Tipo: Task.
- Estado: To Do.
- Prioridad: Medium.
- Épica: `SCRUM-1` — MVP — FindYourPet.
- Dependencias, enlaces, adjuntos y comentarios: no informados.
- Alcance: integrar visualmente `Reportar` dentro de la barra inferior, eliminando la composición tipo FAB y usando la misma estructura vertical que los otros cuatro elementos.
- Criterios principales: barra de `60.dp`, círculo visible de `40.dp`, huella de `22.dp`, área táctil mínima de `48.dp`, sin offsets/arco/well de elevación, labels e iconos alineados, navegación y espaciado horizontal sin cambios, Light/Dark funcionales.
- Fuera de alcance: altura de la barra, destinos, labels existentes, iconos ajenos a `Reportar`, colores globales y lógica funcional.

## Preflight y sincronización

- `git status --short --branch`: `## ops/align-bottom-navigation-horizontal-margins...origin/ops/align-bottom-navigation-horizontal-margins`.
- `git status --porcelain=v1`: vacío.
- `git switch main`: OK.
- `git fetch origin --prune`: OK.
- `git pull --ff-only origin main`: OK; fast-forward de `74b3ce6` a `53f8bfa`.
- `git rev-parse main`: `53f8bfaca9ca5c7443c943b6de2a603a8c048e77`.
- `git rev-parse origin/main`: `53f8bfaca9ca5c7443c943b6de2a603a8c048e77`.
- `git status --short --branch`: `## main...origin/main`; árbol limpio.

## Ramas no integradas revisadas

- `ops/adjust-login-width-and-action-hierarchy`: change `SCRUM-42`, estado `PASSED_PENDING_INTEGRATION`, integración `PENDING`.
- `ops/align-bottom-navigation-horizontal-margins`: change `SCRUM-46`, estado `PASSED_PENDING_INTEGRATION`, integración `PENDING`.
- `ops/remove-home-screen-header`: change `SCRUM-19`, estado `BLOCKED`, integración `PENDING`.
- Otras ramas no integradas con estado documentado como integrado: `ops/add-transparency-to-bottom-navigation`, `ops/redesign-lost-pets-feed` y `ops/remove-share-button`; no se consideran el bloqueo activo.
- Ramas remotas históricas revisadas: `origin/Eliminar-mensaje-de-sistema-del-chat`, `origin/Rediseño-de-la-pantalla-principal-de-posteo` y las ramas `origin/ops/*` correspondientes a changes ya documentados.

## OpenSpec y decisión

- `openspec list --json`: no existe `unify-report-with-bottom-navigation-structure`.
- No existe la rama `ops/unify-report-with-bottom-navigation-structure`.
- No se creó rama ni se generaron artefactos OpenSpec porque hay changes activos sin integrar (`SCRUM-42` y `SCRUM-46`) y el orquestador no puede iniciar otro change sin autorización explícita de trabajo paralelo.

## Bloqueo

Se requiere autorización explícita del usuario para continuar SCRUM-47 en paralelo, o resolver primero la integración/estado de `SCRUM-42` y `SCRUM-46`. No se modificaron ramas existentes ni se ejecutaron comandos de implementación.
