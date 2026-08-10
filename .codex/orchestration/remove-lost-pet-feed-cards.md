# Orchestration: remove-lost-pet-feed-cards

## Estado actual

PASSED_PENDING_INTEGRATION

## Issue Jira

- Clave: `SCRUM-5`
- Título: `Re diseñar la pantalla de posteo de mascotas para que no use cards`
- URL: https://pelaezarmando.atlassian.net/browse/SCRUM-5
- Tipo: Task
- Estado Jira: To Do
- Prioridad: Medium
- Sprint: SCRUM Sprint 1
- Dependencias/links: no informadas

## Scrum normalizado

### Problema y objetivo

La pantalla/feed de publicaciones de mascotas perdidas usa tarjetas flotantes con márgenes, esquinas redondeadas y sombras. El cambio debe mostrar cada publicación como contenido continuo sobre el fondo principal y permitir que el contenido se desplace visualmente detrás de la barra de navegación inferior, manteniendo lectura clara.

### Alcance

- Eliminar márgenes externos, sombras y esquinas redondeadas de las cards de publicaciones.
- Mostrar la publicación sobre una superficie de fondo continua.
- Mantener la jerarquía actual: imagen, estado, nombre, ubicación, información y fecha.
- Permitir el scroll detrás de la barra de navegación inferior.
- Mantener visible completamente el último contenido.
- Aplicar márgenes internos y áreas seguras existentes.
- Mantener Light Theme y Dark Theme.
- Verificar distintos tamaños de pantalla.

### Fuera de alcance y restricciones

- No modificar lógica de negocio ni la información mostrada.
- No modificar ViewModels, repositories, Firebase, dominio, modelo de datos, navegación funcional o permisos salvo necesidad técnica explícita del layout.
- No introducir colores, tipografías, paddings, tamaños o radios hardcodeados; usar tokens del Design System.

### Dudas y supuestos

- El issue no adjunta referencias visuales ni especifica tamaños concretos; se tomará la UI actual y `docs/design-system.md` como fuente de verdad.
- “Desplazarse detrás de la barra inferior” se interpretará como permitir que el contenido del feed continúe bajo la barra, conservando el inset inferior necesario para que el último contenido sea completamente alcanzable/visible.

## Git y sincronización

- `base_branch: main`
- `base_commit: fbcc4f9cd641e182cf0d7c602753de7b4b5b88ff`
- `remote_base_commit: fbcc4f9cd641e182cf0d7c602753de7b4b5b88ff`
- `branch: ops/remove-lost-pet-feed-cards`
- `HEAD after branch creation: fbcc4f9cd641e182cf0d7c602753de7b4b5b88ff`
- Árbol inicial: limpio.

### Evidencia de sync

- `git switch main` => OK.
- `git fetch origin --prune` => OK.
- `git pull --ff-only origin main` => fast-forward de `88d177d` a `fbcc4f9`.
- `git rev-parse main` y `git rev-parse origin/main` => ambos `fbcc4f9cd641e182cf0d7c602753de7b4b5b88ff`.
- `git status --short --branch` en `main` => limpio y sincronizado.
- Ramas no fusionadas revisadas; existe `ops/redesign-lost-pets-feed` con estado documentado `PASSED`, pero su alcance no equivale a SCRUM-5 y no se reutiliza como base.

## Historial de etapas

### PREFLIGHT

- Skill `findyourpet-orchestrator` leída completamente.
- Preflight inicial ejecutado antes de consultar y normalizar el Scrum.
- SCRUM-5 consultado mediante integración Atlassian después del sync.
- `docs/design-system.md` leído por tratarse de un cambio visual.
- Se verificó que no existían carpeta, archivo de orquestación ni rama equivalente para `remove-lost-pet-feed-cards`.
- Rama de trabajo creada desde `main` sincronizada.

### READY_FOR_IMPLEMENTATION

- `openspec new change "remove-lost-pet-feed-cards"` => change creado con schema `spec-driven`.
- `openspec status --change "remove-lost-pet-feed-cards"` => 4/4 artefactos completos.
- `openspec validate "remove-lost-pet-feed-cards" --strict` => `Change 'remove-lost-pet-feed-cards' is valid`.
- `openspec instructions apply --change "remove-lost-pet-feed-cards" --json` => estado `ready`, 0/17 tareas completadas.
- Artefactos listos: `proposal.md`, `design.md`, `specs/home-feed-presentation/spec.md`, `specs/primary-navigation/spec.md`, `tasks.md`.
- No se implementó código Kotlin, UI ni tests durante la preparación del change; la implementación posterior fue delegada al implementador.

### IMPLEMENTING / VERIFYING

- Implementador delegado: `findyourpet-implementer`, agente `Euler` (`019fece3-7634-7050-afff-244b6e37ba5d`).
- Primer reporte: `BLOCKED`, con cambios en `MainActivity.kt`, `HomeScreen.kt`, `HomeFeedPresentationTest.kt` y `PrimaryNavigationShellStaticTest.kt`; la prueba focalizada había fallado por imports/layout y la revalidación quedó interrumpida.
- Revisión del orquestador: se detectó que el primer ajuste del `NavHost` podía quitar insets a rutas no relacionadas. Se restauró el padding original para crear post, alertas, detalle de chat, chats, notificaciones y perfil; solo Home conserva el contenido bajo la barra.
- Se agregó `HomeFeedPresentationScreenshotTest.kt` con evidencia Roborazzi para viewport compacto Light Theme y viewport alto Dark Theme, en estados superior y desplazado.
- Las capturas fueron inspeccionadas y muestran superficie continua sin card externa, jerarquía conservada y acciones finales visibles.

## Resultado de verificación

- `openspec instructions apply --change "remove-lost-pet-feed-cards" --json` => `all_done`, 17/17 tareas.
- `openspec validate "remove-lost-pet-feed-cards" --strict` => válido.
- `.\gradlew.bat testDebugUnitTest --tests com.findyourpet.app.HomeFeedPresentationTest --tests com.findyourpet.app.PrimaryNavigationShellStaticTest` => BUILD SUCCESSFUL.
- `.\gradlew.bat testDebugUnitTest` => BUILD SUCCESSFUL.
- `.\gradlew.bat assembleDebug` => BUILD SUCCESSFUL.
- `.\gradlew.bat app:recordRoborazziDebug --tests com.findyourpet.app.HomeFeedPresentationScreenshotTest` => BUILD SUCCESSFUL.
- `git diff --check` => sin errores; solo warnings de normalización LF/CRLF.
- Revisión de alcance: cambios limitados a presentación del home, insets del shell necesarios para SCRUM-5, tests y capturas OpenSpec/orquestación; sin cambios en ViewModel, repositories, modelos, Firebase, permisos o destinos de navegación.

## Estado de integración

- `integration_status: PENDING`
- `integrated_commit:` pendiente
- `integration_evidence:` pendiente de merge autorizado a `main` y sincronización posterior con `origin/main`.

## Riesgos pendientes

- Debe contrastarse el alcance con el código y specs actuales antes de generar artefactos.
- La validación visual requerirá el arnés disponible o un dispositivo/emulador si las tareas OpenSpec lo exigen.
