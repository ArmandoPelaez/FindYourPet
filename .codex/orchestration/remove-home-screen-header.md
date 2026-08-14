# Orchestration: remove-home-screen-header

state: BLOCKED
phase: VERIFYING
issue: SCRUM-19
change: remove-home-screen-header
branch: ops/remove-home-screen-header
base_branch: main
base_commit: dfda7acb42b20de6fd474a770fab13ea327be32f
remote_base_commit: dfda7acb42b20de6fd474a770fab13ea327be32f
delegation_status: SPAWNED
handoff_mode: SUBAGENT
agent_id: 01a0024d-9828-78f3-b71b-e4f70d1c5e2a
agent_role: findyourpet-implementer
delegation_error:
integration_status: PENDING
integrated_commit:
integration_evidence:

## Scrum normalizado

- Clave: `SCRUM-19`.
- Título: Eliminar cabecera de la pantalla Home de la aplicación.
- Estado Jira: To Do.
- Prioridad: Medium.
- Sprint: `SCRUM Sprint 1`.
- Alcance: eliminar completamente la cabecera superior de Home que actualmente muestra identidad y subtítulo, reduciendo el espacio vertical y dando mayor protagonismo a las publicaciones.
- Status Bar: mantenerla visible e integrada visualmente con el mismo fondo de la pantalla.
- Espaciado: conservar un margen superior de `16 dp` después del safe area y ubicar el componente de carga de foto/publicación inmediatamente después, manteniendo los márgenes existentes donde corresponda.
- Bottom Navigation: mantenerla fija en la parte inferior y sin cambiar su comportamiento ni sus destinos.
- Design Rules: usar Jetpack Compose + Material 3 estable, tokens existentes, Light/Dark Theme y accesibilidad; no introducir valores visuales hardcodeados.
- Fuera de alcance: lógica de negocio, ViewModels, repositorios, Firebase, navegación, persistencia, publicaciones y rediseño de la Bottom Navigation.
- Referencia Jira: `https://pelaezarmando.atlassian.net/browse/SCRUM-19`.

## Dudas y contraste con el repositorio

- Jira describe una cabecera con el título `Publicar Mascota Perdida`, pero el código actual de `HomeScreen.kt` muestra `Mascotas Perdidas` y `Red Segura de Búsqueda`. Se toma como fuente de verdad el comportamiento solicitado y la implementación actual: retirar la cabecera superior de Home sin alterar el contenido de publicaciones.
- El requerimiento menciona un componente para agregar la foto; en el código actual la carga de foto pertenece a `CreatePetPostScreen`, mientras Home renderiza publicaciones existentes. Los artefactos deben conservar esta separación y no mover componentes entre rutas.
- El margen de 16 dp deberá expresarse con el token equivalente del Design System, previsiblemente `AppSpacing.md`, sujeto a confirmación durante la implementación.

## Evidencia de preflight y sincronización

- `git status --short --branch`: `## main...origin/main`.
- `git status --porcelain=v1`: vacío.
- `git switch main`: correcto.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: correcto; Already up to date.
- `git rev-parse main`: `dfda7acb42b20de6fd474a770fab13ea327be32f`.
- `git rev-parse origin/main`: `dfda7acb42b20de6fd474a770fab13ea327be32f`.
- `git status --short --branch` después de sincronizar: `## main...origin/main`.
- Rama creada desde `main`: `ops/remove-home-screen-header`.
- `git rev-parse HEAD` en la rama nueva: coincide con `base_commit`.

## Revisión de ramas y changes existentes

- `openspec list --json` no contiene `remove-home-screen-header`.
- No existe carpeta `openspec/changes/remove-home-screen-header`.
- No existe rama local ni remota equivalente `ops/remove-home-screen-header`.
- Las ramas no mergeadas detectadas están documentadas como históricas o integradas en sus bitácoras; no se integraron ni eliminaron.
- Existen changes OpenSpec históricos/en progreso sin una rama activa equivalente, incluyendo `optimize-sighting-messaging-flow` y `prepare-production-release`. Se continúa con autorización explícita previa del usuario para trabajo paralelo; no se modifican esos changes.

## Revisión técnica y Design System

- `docs/design-system.md` fue leído antes de preparar el cambio visual.
- `HomeScreen.kt` posee un `Scaffold` con `topBar` propio: `Surface`, spacer de `WindowInsets.statusBars`, logo circular, título y subtítulo. Su contenido usa `WindowInsets.safeDrawing`.
- `MainActivity.kt` posee el `Scaffold` del shell autenticado y mantiene `BottomPrimaryActionBanner` como `bottomBar`; esta navegación queda fuera del alcance.
- `CreatePetPostScreen.kt` posee su propio formulario y `Scaffold`; no se debe modificar por inferencia desde la discrepancia textual de Jira salvo que los artefactos demuestren una dependencia necesaria.

## Registro operativo

- 2026-08-14: preflight y sincronización de `main` completados.
- 2026-08-14: SCRUM-19 consultado en Jira y normalizado.
- 2026-08-14: se reutilizó la autorización explícita previa para trabajo paralelo.
- 2026-08-14: rama `ops/remove-home-screen-header` creada desde `main` sincronizada.

## OpenSpec

- Artefactos: `proposal.md`, `design.md`, `specs/home-feed-presentation/spec.md`, `specs/primary-navigation/spec.md`, `tasks.md`.
- `openspec status --change remove-home-screen-header`: 4/4 artefactos completos.
- `openspec validate remove-home-screen-header --strict`: válido.
- Implementación: delegada a `findyourpet-implementer` mediante el subagente `Mill`.

## Reporte del implementador

- Status: `READY_FOR_VERIFICATION`.
- Agent: `01a0024d-9828-78f3-b71b-e4f70d1c5e2a` (`Mill`).
- Progress: `9/10` tareas.
- Archivos de producción: `app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt`.
- Archivos de pruebas: `app/src/test/java/com/findyourpet/app/HomeFeedPresentationTest.kt`.
- OpenSpec tasks: `9/10`; la tarea `4.3` queda sin completar por falta de dispositivo/emulador.
- Comportamiento: se eliminó la cabecera completa de Home, se conservaron `WindowInsets.safeDrawing`, `AppSpacing.md`, feed, acciones y Bottom Navigation.
- Validaciones del implementador: `openspec validate --strict`, tests unitarios, `assembleDebug` y `git diff --check` correctos.

## Verificación del orquestador

- `openspec instructions apply --change "remove-home-screen-header" --json`: `9/10`, una tarea restante.
- `openspec validate remove-home-screen-header --strict`: válido.
- `git diff --check`: correcto.
- Diff revisado: limitado a `HomeScreen.kt` y `HomeFeedPresentationTest.kt`; no modifica `MainActivity`, `CreatePetPostScreen`, ViewModels, repositorios, Firebase ni rutas.
- `./gradlew.bat testDebugUnitTest`: `BUILD SUCCESSFUL`.
- `./gradlew.bat assembleDebug`: `BUILD SUCCESSFUL`.
- `adb devices`: no ejecutable; `adb` no está disponible en el entorno.
- Bloqueo: falta validación manual en viewport compacto/alto, Light/Dark Theme y navegación por gestos. Se requiere un emulador o dispositivo Android para cerrar `4.3`.
- Estado: `BLOCKED`.
