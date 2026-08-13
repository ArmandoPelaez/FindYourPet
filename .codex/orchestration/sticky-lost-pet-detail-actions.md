# Orchestration State: sticky-lost-pet-detail-actions

state: BLOCKED
phase: VERIFYING
issue: SCRUM-15
issue_url: https://pelaezarmando.atlassian.net/browse/SCRUM-15
base_branch: main
base_commit: 600139c46cc8418ce708cedcf838986e53ff03e6
remote_base_commit: 600139c46cc8418ce708cedcf838986e53ff03e6
branch: ops/sticky-lost-pet-detail-actions
work_parallel_authorized: true
integration_status: PENDING
integrated_commit:
integration_evidence:

## Jira

- Título: `Convertir el boton BottomNavigation y ReportPetButton sticky`.
- Tipo: Task.
- Estado: To Do.
- Prioridad: Medium.
- Sprint: `SCRUM Sprint 1` (active).
- Épica: `SCRUM-1` — MVP — FindYourPet.
- Dependencias, adjuntos, enlaces y comentarios: ninguno declarado.

## Scrum normalizado

### Objetivo

Mantener fijos en la parte inferior de la pantalla de detalle de una mascota perdida el CTA `Vi a esta mascota` y la barra de navegación principal mientras el usuario recorre la publicación.

### Criterios de aceptación recibidos

- La pantalla de detalle permite scroll vertical de todo el contenido.
- El CTA `Vi a esta mascota` permanece fijo sobre la barra inferior durante el scroll.
- La barra de navegación inferior permanece fija.
- CTA y navegación quedan fuera del contenido desplazable y no se superponen.
- El último elemento de la publicación puede visualizarse completamente.
- El contenido no queda oculto detrás del CTA o la navegación y existe padding inferior suficiente.
- Se respetan márgenes y safe areas en cantidades variables de texto.

### Alcance y restricciones

- Solo comportamiento visual y de scroll de la pantalla de detalle.
- No modificar el flujo ejecutado por el CTA ni el comportamiento funcional de navegación.
- Para cambios visuales aplican Jetpack Compose, Material 3 estable, tokens existentes, Light/Dark Theme y `docs/design-system.md`.
- No se deben modificar ViewModel, repositories, Firebase, Room ni lógica de dominio.

### Aclaración del usuario

- La barra de navegación principal debe ser sticky siempre.
- El CTA rojo `¡Lo he visto!` debe ser sticky en cualquier pantalla donde figure.
- Se autoriza explícitamente el trabajo en paralelo con los changes OpenSpec activos.

## Preflight y sincronización

- `git status --short --branch` => `## main...origin/main`.
- `git status --porcelain=v1` => vacío antes de iniciar el flujo.
- `git switch main` => correcto; ya estaba en `main`.
- `git fetch origin --prune` => correcto.
- `git pull --ff-only origin main` => `Already up to date.`
- `git rev-parse main` => `600139c46cc8418ce708cedcf838986e53ff03e6`.
- `git rev-parse origin/main` => `600139c46cc8418ce708cedcf838986e53ff03e6`.
- `git status --short --branch` después del sync => `## main...origin/main`.

### Ramas no fusionadas revisadas

- Locales: `archive/remove-personal-data-sharing`, `ops/add-transparency-to-bottom-navigation`, `ops/redesign-lost-pets-feed`, `ops/remove-share-button`.
- Remotas: `origin/Eliminar-mensaje-de-sistema-del-chat`, `origin/Rediseño-de-la-pantalla-principal-de-posteo`, `origin/archive/remove-personal-data-sharing`, `origin/archive/simplify-lost-pet-post-form`, `origin/ops/add-transparency-to-bottom-navigation`, `origin/ops/redesign-lost-pets-feed`, `origin/ops/remove-share-button`.
- Las ramas `ops/add-transparency-to-bottom-navigation`, `ops/redesign-lost-pets-feed` y `ops/remove-share-button` tienen evidencia de integración documentada; no se reutilizan como base.
- La rama `archive/remove-personal-data-sharing` documenta un bloqueo histórico y no se reutiliza.

## Contraste técnico

- `docs/design-system.md` fue leído antes de evaluar el cambio visual.
- `main` contiene `MainActivity.kt` con `Scaffold` y `BottomPrimaryActionBanner` como chrome del shell.
- La publicación se presenta en `HomeScreen.kt` mediante `PetPostCard`, cuyo contenido ya usa `verticalScroll` y cuyo CTA es `AppButton` con texto `¡Lo he visto!`.
- No existe en `main` una `PetDetailScreen` ni un componente `ReportPetButton` con esos nombres.
- El change completo `infinite-scroll-fixed-bottom-navigation` ya documenta el shell fijo, el scroll del feed, el CTA, el padding/insets y la validación de Light/Dark Theme. Su alcance se solapa materialmente con SCRUM-15 y debe revisarse antes de crear otro change.

## Changes OpenSpec activos

- `optimize-sighting-messaging-flow`: `in-progress`, 0/28 tareas.
- `prepare-production-release`: `in-progress`, 16/25 tareas.
- La skill prohíbe iniciar un nuevo change mientras exista otro change activo (`PASSED_PENDING_INTEGRATION`, `IMPLEMENTING`, `READY_FOR_VERIFICATION` o `VERIFYING`) sin autorización explícita de trabajo paralelo. En este repositorio esos changes aparecen como `in-progress` y no tienen un handoff/orchestration state verificable equivalente.

## OpenSpec y rama

- `openspec list --json` no mostró un change llamado `sticky-lost-pet-detail-actions`.
- Rama creada desde `main`: `ops/sticky-lost-pet-detail-actions`.
- `git rev-parse HEAD` de la rama => `600139c46cc8418ce708cedcf838986e53ff03e6`.
- El archivo de estado permanece sin seguimiento y se incorporará al change en esta rama.
- `openspec new change "sticky-lost-pet-detail-actions"` => correcto.
- Artefactos generados: `proposal.md`, `design.md`, `specs/primary-navigation/spec.md`, `specs/home-feed-presentation/spec.md`, `tasks.md`.
- `openspec status --change "sticky-lost-pet-detail-actions"` => 4/4 artifacts complete.
- `openspec validate "sticky-lost-pet-detail-actions" --strict` => `Change 'sticky-lost-pet-detail-actions' is valid`.

## Bloqueos resueltos

- El usuario aclaró que el alcance cubre la barra de navegación siempre y el CTA `¡Lo he visto!` donde figure.
- El usuario autorizó explícitamente el trabajo paralelo.
- La diferencia entre los nombres del Scrum y la implementación actual se resolvió tomando como objetivo la publicación presentada por `PetPostCard` y cualquier superficie existente que renderice el CTA, sin inventar una nueva ruta ni cambiar callbacks.

## Delegación e integración

delegation_status: MANUAL_HANDOFF
handoff_mode: MANUAL
agent_id:
agent_role: findyourpet-implementer
delegation_error: spawn_agent rejected the payload because message and items were supplied together; no agent_id was created.

La implementación se ejecutó secuencialmente mediante la skill `findyourpet-implementer`. No hay evidencia de integración.

## Implementer report

Status: `BLOCKED`

Change: `sticky-lost-pet-detail-actions`

Progress: 16/17 tareas OpenSpec completadas; 4.5 queda pendiente por dependencia externa.

Archivos modificados:

- `app/src/main/java/com/findyourpet/app/MainActivity.kt`
- `app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt`
- `app/src/test/java/com/findyourpet/app/HomeFeedPresentationScreenshotTest.kt`
- `app/src/test/java/com/findyourpet/app/HomeFeedPresentationTest.kt`
- `app/src/test/java/com/findyourpet/app/PrimaryNavigationShellStaticTest.kt`
- `app/src/test/java/com/findyourpet/app/StaticProjectGuardrailsTest.kt`
- `openspec/changes/sticky-lost-pet-detail-actions/tasks.md`

Tareas completadas: 1.1–3.4 y 4.1–4.4.

Tarea pendiente/no verificada:

- 4.5 validación manual de todas las rutas autenticadas, scroll largo, safe areas y controles sticky.

Comandos ejecutados:

- `openspec validate "sticky-lost-pet-detail-actions" --strict` => válido.
- `.\gradlew.bat --no-build-cache testDebugUnitTest` => código 0.
- `.\gradlew.bat --no-build-cache assembleDebug` => `BUILD SUCCESSFUL`.
- `.\gradlew.bat --no-build-cache testDebugUnitTest --tests com.findyourpet.app.HomeFeedPresentationScreenshotTest` => `BUILD SUCCESSFUL`.
- `openspec instructions apply --change "sticky-lost-pet-detail-actions" --json` => 12/17 antes de completar validaciones; queda solo 4.5.
- `adb devices` => `adb: unavailable`.

Validación de alcance:

- La barra global conserva callbacks/destinos y se renderiza desde el shell autenticado.
- `SightingActionBar` queda fuera del `verticalScroll`/`HorizontalPager` y conserva elegibilidad/callback.
- No se modificaron ViewModel, repositories, datos, Firebase, Room, permisos ni rutas funcionales.
- Pruebas estáticas y Compose cubren navegación global, elegibilidad, callback, scroll y temas Light/Dark.

Riesgo/bloqueo: la validación manual de rutas completas requiere `adb` y un dispositivo o emulador no disponible en este entorno. No se marca como completada ni se declara `PASSED`.

## Verificación del orquestador

- `git diff --check` pendiente de ejecutar.
- `openspec validate --strict`, tests debug unitarios, screenshots y `assembleDebug` tienen evidencia exitosa.
- El change no puede pasar a `PASSED_PENDING_INTEGRATION` hasta completar la validación manual pendiente.

## Actualización de alcance solicitada por el usuario

La referencia visual agregó estos requisitos al mismo change:

- Eliminar el CTA sticky `¡Lo he visto!`; la navegación global sigue siendo sticky siempre.
- Mostrar `La vi` alineado con el nombre, con ojo cerrado inicialmente y ojo abierto después del click, reutilizando la navegación existente a la alerta.
- Ubicar `PERDIDO` arriba a la izquierda de la foto, con color rojo semántico y sin icono en esta presentación.
- Usar `AppSpacing.cardImageAspectRatio` para aproximar la proporción de la referencia.
- Mostrar debajo de la ubicación la fila de calendario, `Última vez visto` y la fecha.
- Registrar la decisión en `docs/design-system.md` y en los artefactos OpenSpec.

## Implementación actualizada

- `HomeScreen.kt`: eliminado `SightingActionBar`; la acción inline se renderiza en `PetIdentitySection`, conserva elegibilidad/callback y alterna `VisibilityOff`/`Visibility`.
- `CommonComponents.kt`: `PetStatusChip` admite presentación sin icono.
- `DesignTokens.kt`: `PERDIDO` usa rojo de alerta con contenido legible y se agregó `AppSpacing.cardImageAspectRatio`.
- `HomeFeedPresentationTest.kt` y `HomeFeedPresentationScreenshotTest.kt`: cubren la acción inline, ausencia del CTA eliminado, scroll, proporción y metadatos.
- `docs/design-system.md`, `proposal.md`, `design.md`, `spec.md` y `tasks.md`: sincronizados con la referencia visual.

Validación adicional:

- `openspec validate "sticky-lost-pet-detail-actions" --strict` => válido.
- Pruebas dirigidas (`HomeFeedPresentationTest`, `HomeFeedPresentationScreenshotTest`, `PetStatusChipComposeTest`, `PrimaryNavigationShellStaticTest`, `StaticProjectGuardrailsTest`) => `BUILD SUCCESSFUL`.
- `./gradlew.bat --no-build-cache testDebugUnitTest --no-daemon` => en ejecución/verificación final de esta actualización.
- `git diff --check` => sin errores de whitespace.

La validación manual 4.5 sigue bloqueada porque `adb` no está disponible en el entorno. No se realizó commit, merge ni integración.

## Actualización de navegación inferior

- La barra ahora muestra `Inicio`, `Perfil`, `Publicar`, `Mensajes`, `Alertas` en ese orden, con labels visibles y estado activo/inactivo basado en el destino actual.
- `Alertas` reutiliza la ruta existente de `NotificationsScreen` y el badge de no leídas.
- El acceso de alertas fue retirado del `HomeScreen` top app bar.
- Se actualizaron tokens, `docs/design-system.md`, OpenSpec y pruebas de presentación/accesibilidad.
- La verificación manual con dispositivo continúa pendiente por ausencia de `adb`.

## Refinamiento final de superficie

- La barra dejó de usar card flotante e inset horizontal; ahora es una superficie plana de ancho completo con padding de gestos interno.
- Publicar tiene un pozo circular tokenizado y elevación hacia arriba; los iconos secundarios usan tamaño cuadrado reducido.
- La primera compilación detectó un import faltante de `CircleShape`; fue corregido y las pruebas dirigidas posteriores pasaron.
- Suite completa `testDebugUnitTest`, `assembleDebug`, OpenSpec estricto y `git diff --check` terminaron correctamente.

## Compactación solicitada

- Se interpretó “reducir el ancho” como reducir la altura útil, manteniendo el ancho completo de la banca.
- `AppSpacing.bannerHeight` pasó a 72dp; iconos, slot, acción Publicar, pozo y elevación tienen tokens independientes.
- El label `Publicar` comparte el mismo slot/baseline que los demás labels.
- El pozo de Publicar usa `surface` y sombra compartida para producir la extensión oscura de la referencia.
- La primera prueba estática esperaba `iconLarge`; se actualizó al token compacto y la suite completa volvió a pasar.

## Corrección de forma solicitada

- Se eliminó por completo la forma `AppShapes.bottomNavigation`.
- La barra ahora usa la forma rectangular por defecto de `Surface`, sin esquinas redondeadas y ocupando todo el ancho.
- OpenSpec estricto, suite completa, build debug y pruebas específicas de la barra pasan correctamente.
