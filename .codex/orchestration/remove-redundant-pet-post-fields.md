# Orchestration: remove-redundant-pet-post-fields

## Estado actual

INTEGRATED

## Issue Jira

- Clave: `SCRUM-14`
- Título: `Eliminar informacion solicitada para reportar a la mascota`
- URL: https://pelaezarmando.atlassian.net/browse/SCRUM-14
- Proyecto: `SCRUM` / FindYourPet
- Tipo: Task
- Estado: To Do
- Prioridad: Medium
- Sprint: `SCRUM Sprint 1` (active)
- Fecha límite: `2026-08-13`
- Épica: `SCRUM-1` — MVP — FindYourPet
- Dependencias, enlaces, adjuntos y comentarios: no informados

## Autorización de trabajo paralelo

- Autorizado explícitamente por el usuario el 2026-08-13.
- Change paralelo existente: `remove-lost-pet-feed-cards`, en `PASSED_PENDING_INTEGRATION`.
- No se modificará ni se reutilizará la rama del change paralelo.

## Scrum normalizado

### Objetivo

Eliminar la información redundante solicitada al reportar una mascota perdida, retirando los campos `Características` y `Señas particulares`.

### Alcance recibido

- Retirar visualmente los campos `Características` y `Señas particulares` de la pantalla de creación/reporte de mascota perdida.
- Eliminar la lógica relacionada con ambos campos en la capa visual, dominio y persistencia.
- Mantener `Descripción adicional` como el campo de reconocimiento restante.
- Restringir el cambio a lo mencionado y respetar las pautas de diseño existentes.

### Criterios de aceptación derivados

- La pantalla no muestra `Características` ni `Señas particulares`.
- La creación de una publicación no transporta ni persiste nuevos valores para esos campos.
- `Descripción adicional` continúa disponible y conserva su comportamiento actual.
- No quedan contratos, mappers, modelos o migraciones activas para esos campos dentro del flujo vigente.
- El cambio mantiene Material 3 estable, los tokens de `docs/design-system.md` y los temas Light/Dark.

### Fuera de alcance y dudas

- No eliminar `breed`, `color` ni otros campos legacy del modelo si Jira no lo solicita explícitamente.
- No eliminar el texto `features`/`Descripción adicional`; representa el campo de reconocimiento que Jira conserva.
- Los datos históricos ya almacenados en Room o Firestore requieren una estrategia de compatibilidad/migración que deberá definirse en OpenSpec antes de implementar.

## Preflight y sincronización

- `git status --short --branch` inicial: `## main...origin/main`.
- `git status --porcelain=v1`: sin salida.
- `git switch main`: OK.
- `git fetch origin --prune`: OK.
- `git pull --ff-only origin main`: `Already up to date.`
- `git rev-parse main`: `e570864e91ca175d677ff213ef9c4c2b08b3394e`.
- `git rev-parse origin/main`: `e570864e91ca175d677ff213ef9c4c2b08b3394e`.
- `main` quedó limpia y sincronizada.
- Ramas locales no fusionadas detectadas: `archive/remove-personal-data-sharing`, `ops/redesign-lost-pets-feed`, `ops/remove-share-button`.
- Ramas remotas no fusionadas detectadas: `origin/Eliminar-mensaje-de-sistema-del-chat`, `origin/Rediseño-de-la-pantalla-principal-de-posteo`, `origin/archive/remove-personal-data-sharing`, `origin/archive/simplify-lost-pet-post-form`, `origin/ops/add-transparency-to-bottom-navigation`, `origin/ops/redesign-lost-pets-feed`, `origin/ops/remove-share-button`.
- La autorización de trabajo paralelo permite continuar sin integrar ni alterar esos changes.

## Contraste técnico previo

- Se leyó `docs/design-system.md` por tratarse de un cambio visual.
- La UI vigente está en `app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt`.
- `characteristics` y `particularMarks` se propagan actualmente por `PetViewModel`, `PetPostEntity`, `PetPostDocument`, `RemoteMappers` y las migraciones Room 5→6 y 6→7.
- Las pruebas que documentan esos campos están en `CreatePetPostFormStaticTest.kt`, `PetPostParticularMarksStaticTest.kt` y `RemoteMappersTest.kt`.
- La especificación base `openspec/specs/pet-posts/spec.md` conserva el contrato general de creación y reconocimiento, por lo que el nuevo delta deberá precisar la retirada de ambos atributos sin eliminar `features`.

## Change

- Nombre derivado: `remove-redundant-pet-post-fields`.
- `openspec list --json`: no existe un change con ese nombre.
- No existe carpeta de orquestación, carpeta OpenSpec ni rama `ops/remove-redundant-pet-post-fields` equivalente.

## Rama y OpenSpec

- `base_branch: main`
- `base_commit: e570864e91ca175d677ff213ef9c4c2b08b3394e`
- `remote_base_commit: e570864e91ca175d677ff213ef9c4c2b08b3394e`
- Rama creada: `ops/remove-redundant-pet-post-fields`.
- `git rev-parse HEAD` después de crear la rama: `e570864e91ca175d677ff213ef9c4c2b08b3394e`.
- Artefactos completos: `proposal.md`, `design.md`, `specs/pet-posts/spec.md`, `tasks.md`.
- `openspec status --change "remove-redundant-pet-post-fields"`: 4/4 artefactos completos.
- `openspec validate "remove-redundant-pet-post-fields" --strict`: válido.

## Delegación

- `delegation_status: COMPLETED`
- `handoff_mode: SUBAGENT`
- `agent_id: 019ffc14-ac9e-7453-be95-c348c1075537`
- `agent_role: findyourpet-implementer`
- `delegation_error:`

## Reporte del implementador

- Estado inicial reportado: `BLOCKED` por la verificación visual pendiente.
- Progreso inicial: `18/19` tareas.
- Implementación reportada: eliminación de los campos en UI, ViewModel, contratos, mappers y persistencia; migración Room 7→8; pruebas actualizadas.
- Validaciones reportadas: OpenSpec estricto, `testDebugUnitTest`, `assembleDebug` y `git diff --check` correctos.
- Bloqueo resuelto por el orquestador: se ejecutó `./gradlew.bat recordRoborazziDebug`, se regeneraron las capturas `create-post-*` y se inspeccionaron visualmente las variantes Light/Dark compacta y alta.

## Verificación del orquestador

- `openspec instructions apply --change "remove-redundant-pet-post-fields" --json`: `19/19`, `all_done`.
- `openspec validate "remove-redundant-pet-post-fields" --strict`: válido.
- `git diff --check`: correcto; solo advertencias de conversión LF/CRLF.
- `./gradlew.bat testDebugUnitTest`: `BUILD SUCCESSFUL`.
- `./gradlew.bat assembleDebug`: `BUILD SUCCESSFUL`.
- `./gradlew.bat recordRoborazziDebug`: `BUILD SUCCESSFUL`.
- Verificación visual: las capturas `create-post-compact-top.png`, `create-post-compact-scrolled.png`, `create-post-tall-top.png` y `create-post-tall-scrolled.png` muestran Light/Dark Theme, `Descripción adicional`, ubicación y publicación, sin `Características` ni `Señas particulares`.
- Revisión de alcance: producción limitada a `AppDatabase`, `PetPostEntity`, `PetPostDocument`, `RemoteMappers`, `CreatePetPostScreen` y `PetViewModel`; pruebas y capturas correspondientes actualizadas.
- Referencias productivas restantes de los nombres retirados: únicamente migraciones históricas 5→6 y 6→7, necesarias para actualizar instalaciones existentes.

## Integración

- `integration_status: MERGED`
- `integrated_commit: 18da6c9e217a180a81fb6159a65970db337ee949`
- `integration_evidence: PR #34 mergeado en origin/main; commit de implementación 5cbdfef integrado; main local sincronizada con origin/main mediante fast-forward.`
