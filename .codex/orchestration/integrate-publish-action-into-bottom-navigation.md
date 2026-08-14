# Orchestration State: integrate-publish-action-into-bottom-navigation

state: INTEGRATED
phase: INTEGRATED
issue: SCRUM-17
issue_url: https://pelaezarmando.atlassian.net/browse/SCRUM-17
base_branch: main
base_commit: a1ad150069d4470a4af22deb1d4efb07af5b8fa1
remote_base_commit: a1ad150069d4470a4af22deb1d4efb07af5b8fa1
branch: ops/integrate-publish-action-into-bottom-navigation
work_parallel_authorized: true
integration_status: INTEGRATED
integrated_commit: 2009eb4
integration_evidence: PR #37 mergeado en origin/main; main local limpia y sincronizada.

## Jira

- Titulo: `Integrar el boton de Publicar Ficha en la pantalla de edicion en la barra de navegacion mientras se este creando`.
- Tipo: Task.
- Estado: To Do.
- Prioridad: Medium.
- Proyecto: `SCRUM` / FindYourPet.
- Sprint: `SCRUM Sprint 1` (active).
- Fecha limite: 2026-08-15.
- Dependencias, adjuntos, enlaces y comentarios: ninguno declarado.

## Scrum normalizado

### Objetivo

Integrar la accion principal `Publicar ficha` dentro de la Bottom Navigation durante el flujo `Publicar mascota perdida`, reemplazando temporalmente el FAB central `+ Publicar`.

### Criterios de aceptacion recibidos

- El boton independiente `Publicar ficha` deja de mostrarse encima de la Bottom Navigation.
- En la pantalla de publicacion, el FAB `+ Publicar` es reemplazado por `Publicar ficha`.
- Solo existe una accion principal de publicacion visible.
- El CTA queda integrado en la Bottom Navigation y permanece visible durante el scroll.
- El CTA central tiene mayor ancho que los items secundarios, aproximadamente el equivalente a dos posiciones normales.
- Inicio, Perfil, Mensajes y Alertas permanecen visibles y funcionales.
- El CTA queda disabled si el formulario es invalido y enabled cuando se cumplen sus condiciones actuales.
- El CTA reutiliza la logica existente, sin duplicar el flujo de publicacion.
- Al abandonar el flujo, se restaura `+ Publicar`.
- La distribucion funciona en diferentes tamanos de pantalla, sin solapamientos ni recortes.
- Se respetan Design Rules, tokens existentes, Light/Dark Theme, accesibilidad y touch targets.

### Fuera de alcance

- Cambiar validaciones funcionales del formulario.
- Cambiar persistencia, proceso de publicacion o destinos de navegacion.
- Modificar pantallas fuera del flujo de publicacion.

## Contraste tecnico previo

- `docs/design-system.md` leido antes de generar artefactos.
- El shell autenticado ya centraliza la Bottom Navigation en `MainActivity.kt` mediante `BottomPrimaryActionBanner`.
- `CreatePetPostScreen.kt` contiene actualmente el boton independiente `Publicar ficha` y conserva la logica de validacion/publicacion.
- `BottomPrimaryActionBanner` actualmente renderiza el destino central como accion circular `Publicar`.
- El cambio debe pasar el estado contextual y la accion existente al componente compartido sin mover la logica de dominio.
- Se autoriza explicitamente el trabajo paralelo con changes OpenSpec activos.

## Preflight y sincronizacion

- `git status --short --branch` => `## main...origin/main`.
- `git status --porcelain=v1` => vacio.
- `git switch main` => correcto.
- `git fetch origin --prune` => correcto.
- `git pull --ff-only origin main` => `Already up to date.`
- `git rev-parse main` => `a1ad150069d4470a4af22deb1d4efb07af5b8fa1`.
- `git rev-parse origin/main` => `a1ad150069d4470a4af22deb1d4efb07af5b8fa1`.
- `main` quedo limpia y sincronizada.

## Ramas no fusionadas revisadas

- Locales: `archive/remove-personal-data-sharing`, `ops/add-transparency-to-bottom-navigation`, `ops/redesign-lost-pets-feed`, `ops/remove-share-button`.
- Remotas: `origin/Eliminar-mensaje-de-sistema-del-chat`, `origin/Rediseño-de-la-pantalla-principal-de-posteo`, `origin/archive/remove-personal-data-sharing`, `origin/archive/simplify-lost-pet-post-form`, `origin/ops/add-transparency-to-bottom-navigation`, `origin/ops/redesign-lost-pets-feed`, `origin/ops/remove-share-button`.
- Las ramas no fusionadas no se reutilizaron ni modificaron; el usuario autorizo trabajo paralelo.

## Rama de trabajo

- `git switch -c ops/integrate-publish-action-into-bottom-navigation main` => correcto.
- `git rev-parse HEAD` => `a1ad150069d4470a4af22deb1d4efb07af5b8fa1`.

## OpenSpec

- Change: `integrate-publish-action-into-bottom-navigation`.
- Artefactos: `proposal.md`, `design.md`, `specs/primary-navigation/spec.md`, `specs/pet-posts/spec.md`, `tasks.md`.
- `openspec status --change "integrate-publish-action-into-bottom-navigation"` => `4/4 artifacts complete`.
- `openspec validate "integrate-publish-action-into-bottom-navigation" --strict` => valido.
- El alcance queda limitado a la presentacion contextual de la accion de publicacion y su wiring con el callback existente.

## Delegacion

delegation_status: SPAWNED
handoff_mode: SUBAGENT
agent_id: 01a001d2-c9e1-76b0-b965-a8bcb984fd22
agent_role: findyourpet-implementer
delegation_error:
repair_agent_id: 01a001d2-c9e1-76b0-b965-a8bcb984fd22
repair_attempt: 2
regression_repair_agent_id: 01a001df-db66-78c1-83c0-494e9cf4ddf1
scope_repair_agent_id: 01a001ee-83ff-72e1-8f17-785894b37c83

## Verificacion final

- `openspec validate "integrate-publish-action-into-bottom-navigation" --strict` => valido.
- `openspec instructions apply --change "integrate-publish-action-into-bottom-navigation" --json` => `15/15`, `all_done`.
- `.\gradlew.bat testDebugUnitTest --no-parallel --no-daemon` => `BUILD SUCCESSFUL`.
- `.\gradlew.bat assembleDebug --no-daemon` => `BUILD SUCCESSFUL`.
- `git diff --check` => sin errores; solo advertencias normales de LF/CRLF.
- Diff final revisado: `MainActivity.kt`, `CommonComponents.kt`, `CreatePetPostScreen.kt` y pruebas directamente relacionadas; la navegacion base de `main` quedo sin cambios funcionales.
- No se modificaron ViewModels, repositories, Firebase, Room, persistencia, dependencias ni destinos de navegacion.

### Evidencia manual y automatizada

- `Small_Phone` (`emulator-5556`): CTA contextual fijo, disabled con formulario incompleto, etiqueta completa `Publicar ficha` antes y despues del scroll, sin solapamiento.
- `Medium_Phone` (`emulator-5554`): CTA contextual fijo y etiqueta completa; al volver a Inicio se restauro `+ Publicar`.
- Capturas revisadas visualmente: `C:\Users\Dell\AppData\Local\Temp\findyourpet-scrum17-after-repair-create.png` y `C:\Users\Dell\AppData\Local\Temp\findyourpet-scrum17-after-repair-scrolled-2.png`.
- Compose tests cubren estados enabled/disabled/busy, click del CTA, ausencia del boton duplicado, scroll y Light/Dark en viewport compacto/alto.

## Resultado

- El change queda `PASSED_PENDING_INTEGRATION`.
- Rama pendiente: `ops/integrate-publish-action-into-bottom-navigation`.
- No se creo commit ni se ejecuto merge/push.
- `integration_status: PENDING`; falta PR o merge autorizado a `main` para declarar `INTEGRATED`.

## Reporte del implementador

Status: `READY_FOR_VERIFICATION`
Progress: `13/15` tareas OpenSpec.

Completado:

- Accion contextual `Publicar ficha` integrada en la Bottom Navigation.
- Eliminado el boton duplicado del formulario.
- Estados disabled/busy y prevencion de envios duplicados.
- CTA responsive con slot central ampliado.
- Tests unitarios, estaticos y screenshots actualizados.
- `openspec validate --strict` correcto.
- `testDebugUnitTest` exitoso.
- `assembleDebug` exitoso.
- `git diff --check` sin errores.

Pendiente:

- Tarea 4.2 queda condicionada a completar la validacion final.
- Tarea 4.6: validacion manual en dispositivos compactos/altos, Light/Dark Theme y navegacion.
- Bloqueo residual: la validacion manual requiere emulador o dispositivo.

## Verificacion manual - reparacion requerida

- AVD: `Small_Phone`, dispositivo `emulator-5556`.
- APK debug instalado y lanzado correctamente mediante `android run`.
- El layout confirmo `Publicar ficha` fijo en el centro de la Bottom Navigation y disabled con formulario incompleto.
- Captura visual `C:\Users\Dell\AppData\Local\Temp\findyourpet-scrum17-create-small.png` mostro el texto truncado como `Publicar ...` en el ancho compacto.
- Resultado: fallo de responsive presentation; no se declara `PASSED` hasta corregir el truncamiento y repetir la validacion.
- Reparacion solicitada: mantener el CTA legible completo (`Publicar ficha`) en `Small_Phone` sin solapamiento, sin valores visuales hardcodeados y respetando las Design Rules; repetir tests/build y reportar evidencia.

## Fallo de verificacion tecnica y reparacion 2

- `testDebugUnitTest --no-parallel` fallo en `PrimaryNavigationRoutingTest.kt:47`: esperaba `home` y obtuvo `create`.
- El mismo ciclo fallo en `SightingNavigationContractTest.kt:30` porque `MainActivity.kt` ya no contiene el contrato existente `popBackStack(ROUTE_HOME, inclusive = false)`.
- Causa confirmada en el diff: se modifico `navigateToPrimaryDestination` fuera del alcance del SCRUM.
- Reparacion solicitada: restaurar el comportamiento de navegacion de `main` (incluido el `popBackStack` de Home y `popUpTo(graph.findStartDestination().id)`), conservar solo el wiring contextual del CTA y repetir la suite completa secuencialmente.
- Fallo transitorio adicional: una ejecucion paralela de Gradle produjo `StreamCorruptedException: unexpected EOF` en KSP; la ejecucion secuencial posterior es la evidencia valida.

## Revisión de alcance y reparacion 3

- La suite secuencial ya pasa, pero la revision del diff detecto cambios de navegacion fuera del SCRUM: se elimino `findStartDestination`, se cambio la visibilidad y se reescribio `navigateToPrimaryDestination` en lugar de conservar exactamente la implementacion de `main`.
- Tambien se agrego `PrimaryNavigationRoutingTest.kt`, que no es necesario para el alcance contextual del CTA.
- Reparacion solicitada: restaurar literalmente el helper de navegacion y sus imports desde `main`; restaurar las aserciones originales de `PrimaryNavigationShellStaticTest` y conservar solo sus nuevas aserciones del CTA; eliminar la prueba de routing adicional si no aporta cobertura directa al cambio.
