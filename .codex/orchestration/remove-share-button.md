# Orchestration: remove-share-button

## Estado actual

PASSED_PENDING_INTEGRATION

## Issue Jira

- Clave: `SCRUM-7`
- Título: `Eliminar boton de compartir en las pantallas de la app`
- URL: https://pelaezarmando.atlassian.net/browse/SCRUM-7
- Tipo: Task
- Estado Jira: To Do
- Prioridad: Low
- Sprint: `SCRUM Sprint 1` (activo)
- Fecha límite: `2026-08-12`
- Épica: `SCRUM-1` — MVP — FindYourPet
- Dependencias/enlaces: no informados; adjuntos y comentarios: ninguno

## Scrum normalizado

### Objetivo

Retirar el botón Share de las pantallas de la aplicación y eliminar la funcionalidad actual de compartir.

### Criterios de aceptación recibidos

- El botón Share no aparece en la aplicación.
- La funcionalidad actual de Share queda eliminada de la aplicación.

### Fuera de alcance y dudas

- Jira no especifica las pantallas concretas ni el mecanismo técnico actual de compartir.
- La inspección del código identifica actualmente el control y el flujo en `HomeScreen.kt`; no se observan otros flujos de compartir de publicaciones en código Kotlin.
- No se agregan requisitos sobre navegación, permisos, datos, backend o migraciones.

## Preflight y sincronización

- `git status --short --branch` inicial => `## main...origin/main`.
- `git status --porcelain=v1` inicial => vacío después de retirar temporalmente el estado no versionado creado durante el bloqueo anterior.
- `git switch main` => `Already on 'main'`.
- `git fetch origin --prune` => OK.
- `git pull --ff-only origin main` => `Already up to date`.
- `git rev-parse main` => `7d872b3961c17d2b0a567bc7bb900dac0d77e381`.
- `git rev-parse origin/main` => `7d872b3961c17d2b0a567bc7bb900dac0d77e381`.
- `main` quedó limpia y sincronizada antes de crear la rama.
- Ramas locales no integradas: `archive/remove-personal-data-sharing`, `ops/redesign-lost-pets-feed`.
- Ramas remotas no integradas: `origin/Eliminar-mensaje-de-sistema-del-chat`, `origin/Rediseño-de-la-pantalla-principal-de-posteo`, `origin/archive/remove-personal-data-sharing`, `origin/archive/simplify-lost-pet-post-form`, `origin/ops/redesign-lost-pets-feed`.
- El change previo `remove-lost-pet-feed-cards`, inicialmente documentado como pendiente, tiene el commit `0aac4ca` contenido en `main` mediante el merge `d2f8e5d`; se considera integrado para esta revisión.

## Contraste técnico

- Se leyó `docs/design-system.md` por afectar una acción visual.
- El control actual está en `app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt`.
- El flujo actual usa `Intent.ACTION_SEND`, `Intent.EXTRA_TEXT` e `Intent.createChooser`.
- La función `buildPetPostShareText` y su cobertura asociada están en `HomeScreen.kt` y `HomeFeedPresentationTest.kt`.
- Las pruebas de presentación y screenshots todavía esperan el botón `Compartir` y deberán ajustarse al alcance del change.
- No se modificará identidad visual, tokens, temas, backend, permisos ni lógica de dominio.

## Rama

- `base_branch: main`
- `base_commit: 7d872b3961c17d2b0a567bc7bb900dac0d77e381`
- `remote_base_commit: 7d872b3961c17d2b0a567bc7bb900dac0d77e381`
- Rama creada: `ops/remove-share-button`
- `git rev-parse HEAD` => `7d872b3961c17d2b0a567bc7bb900dac0d77e381`

## OpenSpec

- Nombre derivado: `remove-share-button`.
- `openspec list --json` => no existe un change `remove-share-button`.
- Rama equivalente previa => no existe.
- `openspec new change "remove-share-button"` => OK.
- `openspec status --change "remove-share-button"` => 4/4 artefactos completos.
- Artefactos generados: `proposal.md`, `design.md`, `specs/home-feed-presentation/spec.md`, `tasks.md`.
- `openspec validate "remove-share-button" --strict` => `Change 'remove-share-button' is valid`.

## Estado de implementación

- Alcance validado: retirar únicamente el control y flujo de Share de las tarjetas de publicaciones, actualizar pruebas y preservar el resto del feed.
- Estado operativo: `PASSED_PENDING_INTEGRATION`.

## Reporte del implementador

- Estado: `READY_FOR_VERIFICATION`.
- Progreso: `9/9` tareas.
- Agente: `Erdos` (`019ff2d4-65f2-7802-ad0e-78c5a875c578`).
- Archivos reportados: `HomeScreen.kt`, `HomeFeedPresentationTest.kt`, `HomeFeedPresentationScreenshotTest.kt`, `openspec/changes/remove-share-button/tasks.md`.
- Resultado reportado: Share eliminado; acción `¡Lo he visto!` preservada; OpenSpec, pruebas focalizadas, assembleDebug y diff check exitosos.
- Riesgo reportado: fallo preexistente del suite completo en `PrimaryNavigationShellStaticTest.kt:94`, relacionado con una ruta de perfil ausente en `MainActivity.kt`.

## Reparación de verificación 1

- Hallazgo: `HomeScreen.kt` conserva `LocalContext` y `val context`, aunque ya no existe el flujo Share que los usaba.
- Acción delegada: eliminar únicamente ese código residual y repetir las validaciones focalizadas y `assembleDebug`.

### Resultado de reparación

- Reparación delegada a `Hilbert` (`019ff2db-2942-7213-81b3-daab157e523d`).
- `LocalContext` y `val context` eliminados.
- `ImageRequest.Builder` conserva el comportamiento existente usando `post.photoUri`.
- OpenSpec strict, tests focalizados, `assembleDebug`, búsqueda de restos Share y `git diff --check` reportados exitosos.
- `testDebugUnitTest` completo mantiene un fallo preexistente en `PrimaryNavigationShellStaticTest.kt:94`.

## Delegación

- `delegation_status: SPAWNED`
- `handoff_mode: SUBAGENT`
- `agent_id: 019ff2d4-65f2-7802-ad0e-78c5a875c578`
- `agent_role: findyourpet-implementer`
- `delegation_error:`
- `repair_agent_id: 019ff2db-2942-7213-81b3-daab157e523d`
- `baseline_repair_agent_id: 019ff347-bc20-72f2-937b-bac673230268`
- `scope_repair_agent_id: 019ff34a-ef77-7ed2-b7b3-e1cac58a844d`

## Resultado de reparacion de alcance

- Se restauro `ImageRequest.Builder(context).data(post.photoUri).crossfade(true).build()` en `HomeScreen.kt`.
- No se reintrodujo Share ni se modificaron otros archivos durante esta reparacion.
- `testDebugUnitTest` y `assembleDebug` reportados exitosos.

## Reparacion autorizada de baseline

- Se autorizo corregir el fallo dentro de esta ejecucion, dejando evidencia.
- Archivo modificado: `app/src/test/java/com/findyourpet/app/PrimaryNavigationShellStaticTest.kt`.
- Correccion: `source()` normaliza finales `CRLF` a `LF` antes de las comparaciones de texto.
- No se modifico produccion ni se relajaron assertions.
- `testDebugUnitTest` reportado por el reparador: `117 tests`, `0 failures`, `0 errors`.

## Reparacion de alcance 2

- Hallazgo del orquestador: la limpieza de `context` cambio `ImageRequest(...).crossfade(true)` por `model = post.photoUri`.
- Accion delegada: restaurar exactamente el comportamiento original de carga de imagen y conservar solo la eliminacion del Share.

## Resultado de verificacion

- `openspec instructions apply --change "remove-share-button" --json` => `9/9`, `all_done`.
- `openspec validate "remove-share-button" --strict` => valido.
- `git diff --check` => sin errores; solo advertencias de conversion LF/CRLF.
- Busqueda de `ACTION_SEND`, `createChooser`, `buildPetPostShareText`, Share icons y etiquetas Share en produccion => sin coincidencias.
- Tests focalizados Home Feed (`HomeFeedPresentationTest`, `HomeFeedPresentationScreenshotTest`) => exitosos.
- `.\\gradlew.bat assembleDebug` => `BUILD SUCCESSFUL`.
- `.\\gradlew.bat testDebugUnitTest` => `117 tests completed, 1 failed`.
- Fallo persistente: `PrimaryNavigationShellStaticTest.kt:94`, porque `MainActivity.kt` no contiene la ruta esperada de Profile; ninguno de esos archivos forma parte del diff de SCRUM-7.

## Bloqueo final

El bloqueo del suite completo quedo resuelto mediante la reparacion autorizada del helper de lectura del test, sin ampliar la produccion ni relajar assertions.

## Verificacion final del orquestador

- `openspec validate "remove-share-button" --strict` => valido.
- `openspec instructions apply --change "remove-share-button" --json` => `9/9`, `all_done`.
- `git diff --check` => sin errores; solo advertencias LF/CRLF.
- `.\\gradlew.bat testDebugUnitTest` => `BUILD SUCCESSFUL`.
- `.\\gradlew.bat assembleDebug` => `BUILD SUCCESSFUL`.
- Diff final: eliminacion de Share en Home Feed, pruebas actualizadas y normalizacion autorizada del helper de `PrimaryNavigationShellStaticTest`.
- `HomeScreen.kt` conserva `ImageRequest.Builder(context).data(post.photoUri).crossfade(true).build()`.
- Busqueda de restos Share en produccion => sin coincidencias.

## Integración

- `integration_status: PENDING`
- `integrated_commit:`
- `integration_evidence:`
