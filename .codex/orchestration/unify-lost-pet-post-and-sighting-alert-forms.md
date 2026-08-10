# Orchestration: unify-lost-pet-post-and-sighting-alert-forms

## Estado actual

PASSED

## Issue Jira

- Clave: `SCRUM-4`
- Título: Unificar diseño de Crear Post de mascota perdida y Crear Alerta
- Estado: To Do
- Prioridad: High
- URL: https://pelaezarmando.atlassian.net/browse/SCRUM-4

## Scrum normalizado

### Alcance

Alinear las pantallas de creación de publicaciones de mascotas perdidas y de creación de alertas para compartir lenguaje visual y comportamiento, usando la pantalla actual de publicación como fuente de verdad visual.

### Criterios de aceptación

- Ambas pantallas mantienen una apariencia visual consistente.
- Se respetan tipografía, colores, espaciados, formas y componentes existentes.
- Se reutilizan componentes comunes cuando sea posible.
- No se introducen colores, tamaños o estilos hardcodeados innecesariamente.
- Se mantiene Material 3.
- La funcionalidad existente solo cambia cuando sea necesario para la unificación visual.
- La aplicación compila y ambas pantallas continúan funcionando correctamente.

### Restricciones y fuera de alcance

- Usar Jetpack Compose y Material 3 estable.
- Respetar el diseño existente y sus tokens; revisar `docs/design-system.md` antes de cambios visuales.
- Soportar Light Theme y Dark Theme.
- No modificar lógica de negocio salvo que sea imprescindible para la unificación visual.
- El change existente `align-sighting-alert-form-with-pet-post` se obvia por decisión explícita del usuario; este change es independiente.

## Git y sincronización

- `base_branch: main`
- `base_commit: 88d177dfc310b8f2abc5cb202ee18acac3897fa8`
- `remote_base_commit: 88d177dfc310b8f2abc5cb202ee18acac3897fa8`
- `branch: ops/unify-lost-pet-post-and-sighting-alert-forms`
- `branch_head_after_preservation: ac725b4769d91c5b97ff7520086a3727b4ebe41e`
- Commit de preservación: `chore: preserve local orchestration updates`
- Árbol posterior al commit: limpio.
- `git diff --check`: correcto.

## Evidencia de preflight

- El preflight inicial detectó cambios locales en `.codex/skills/findyourpet-orchestrator/SKILL.md` y `.gitignore`.
- El usuario confirmó conservarlos en la nueva rama.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: `Already up to date.`
- `git rev-parse main` y `git rev-parse origin/main`: coinciden.
- Ramas locales no fusionadas con `main`: `archive/remove-personal-data-sharing`, `ops/redesign-lost-pets-feed`.
- Ramas remotas no fusionadas con `origin/main`: `origin/Eliminar-mensaje-de-sistema-del-chat`, `origin/Rediseño-de-la-pantalla-principal-de-posteo`, `origin/archive/remove-personal-data-sharing`, `origin/archive/simplify-lost-pet-post-form`, `origin/ops/redesign-lost-pets-feed`.
- Las ramas revisadas tienen estados documentados como cambios anteriores completados; no se detectó otro change explícitamente activo en estado de implementación o verificación.

## Artefactos y verificación

- Change existente similar detectado y obviado explícitamente: `align-sighting-alert-form-with-pet-post`.
- Change nuevo creado con OpenSpec CLI: `unify-lost-pet-post-and-sighting-alert-forms`.
- Artefactos completos: `proposal.md`, `design.md`, `specs/pet-posts/spec.md`, `specs/sightings/spec.md`, `tasks.md`.
- `openspec status --change "unify-lost-pet-post-and-sighting-alert-forms"`: 4/4 artifacts complete.
- `openspec validate "unify-lost-pet-post-and-sighting-alert-forms" --strict`: válido.
- `git diff --check`: correcto.
- Handoff al implementador ejecutado mediante subagente.
- Reporte del implementador: `READY_FOR_VERIFICATION`, progreso `14/15`.
- Validaciones reportadas: `openspec validate --strict`, `testDebugUnitTest`, `assembleDebug` y `git diff --check` correctos.
- Archivos de implementación reportados: `FormPresentationComponents.kt`, `CreatePetPostScreen.kt`, `SightingAlertScreen.kt` y pruebas de presentación asociadas.
- Tarea pendiente: `4.4`, revisión manual en Light/Dark Theme, teclado, errores, fotos presentes/ausentes y tamaños compacto/expandido.
- Reparación solicitada: la verificación visual mostró que `SightingAlertScreen` conservaba título rojo, variante `Danger`, acciones de foto y barra inferior con identidad propia; la extracción de componentes no produjo la unificación visual requerida.
- Reparación completada por el implementador: TopAppBar neutral, CTA `Primary`, barra inferior neutral/accesible y pruebas contra el título rojo y la variante `Danger`.
- Validaciones de reparación: `openspec validate --strict`, `testDebugUnitTest`, `assembleDebug` y `git diff --check` correctos.
- Verificación manual pendiente: Light/Dark Theme, teclado, fotos presentes/ausentes, errores y tamaños compacto/expandido; el emulador no está disponible porque `android.exe` devuelve acceso denegado y `adb` no está disponible.
- Reparación adicional solicitada: la alerta aún renderizaba acciones inline `Galeria`/`Camara` y abría la galería directamente; debe usar el mismo `ModalBottomSheet` de Crear Post al tocar la superficie.
- Reparación completada: la superficie del avistamiento abre `SightingPhotoOptionsSheet` con las mismas acciones modales de Crear Post, elimina botones inline y conserva los launchers existentes.
- `npx.cmd openspec validate "unify-lost-pet-post-and-sighting-alert-forms" --strict`: válido.
- `testDebugUnitTest`: correcto.
- `assembleDebug`: correcto; APK actualizado en `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check`: correcto.
- El subagente fue detenido tras permanecer ejecutándose sin entregar cierre; la validación final de esta reparación fue ejecutada directamente por el orquestador.
- `integration_status: PENDING`
