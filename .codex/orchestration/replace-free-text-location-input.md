# Orchestration: replace-free-text-location-input

state: INTEGRATED
issue: SCRUM-13
issue_url: https://pelaezarmando.atlassian.net/browse/SCRUM-13
base_branch: main
base_commit: 2758cd2948f31ece337c7bab8ec71ca9c5257d69
remote_base_commit: 2758cd2948f31ece337c7bab8ec71ca9c5257d69
branch: ops/replace-free-text-location-input
integration_status: MERGED
integrated_commit: 2c116d24a110d6c74e4b014950e294b921b491ca
integration_evidence: PR #33 merged into main; main and origin/main synchronized at 2c116d24a110d6c74e4b014950e294b921b491ca on 2026-08-13.
delegation_status: COMPLETED
handoff_mode: SUBAGENT
agent_id: 019ffb7b-0985-7193-8fc2-fbbebd4bbe88
agent_role: findyourpet-implementer
delegation_error:

## Reporte del implementador

- Agente: `019ffb49-9ab6-7302-80e3-55b47138c544`.
- Resultado reportado: `BLOCKED`.
- Progreso reportado: 0/26 tareas marcadas; implementacion parcial.
- `:app:compileDebugKotlin`: correcto.
- `testDebugUnitTest`: 8 fallos en una ejecucion previa; se ajustaron tests despues, sin nueva ejecucion.
- `assembleDebug`: no ejecutado.
- `openspec validate --strict`: no ejecutado despues de la implementacion.
- Validacion manual de Google Maps: no ejecutada.
- Bloqueos: falta API key real restringida/billing/Google Cloud para validacion manual; faltan validaciones finales.

## Cambio de alcance aprobado

El usuario retiro la busqueda de direcciones del requerimiento por su posible costo. El alcance vigente queda limitado a Google Maps SDK/Maps Compose, GPS con consentimiento y referencia manual.

OpenSpec fue actualizado y `openspec validate "replace-free-text-location-input" --strict` paso.

## Reporte del implementador de alcance reducido

- Agente: `019ffb7b-0985-7193-8fc2-fbbebd4bbe88`.
- Places eliminado de dependencias, imports, inicializacion, clases, UI, tests y documentacion funcional.
- Quedan Maps SDK/Maps Compose, GPS con consentimiento y referencia manual.
- Tests reportados: 136 tests, 0 fallos.
- `openspec validate --strict`: OK.
- `git diff --check`: OK, con advertencias CRLF.

## Verificacion del orquestador de alcance reducido

- `rg` de Places en codigo, dependencias y documentacion funcional: sin restos; las coincidencias remanentes son solo menciones negativas en OpenSpec/tests que verifican su ausencia.
- `openspec validate "replace-free-text-location-input" --strict`: OK.
- `openspec instructions apply --change "replace-free-text-location-input" --json`: 24/25 tareas completas; queda validacion manual.
- `.\gradlew.bat testDebugUnitTest`: OK; `BUILD SUCCESSFUL`.
- `.\gradlew.bat assembleDebug`: OK despues de detener un daemon que mantenia bloqueado `classes3.dex`.

## Bloqueo restante

La validacion manual de Maps requiere una API key restringida y un dispositivo/emulador con Google Play Services. No se declara `PASSED` ni `PASSED_PENDING_INTEGRATION` hasta completar esa comprobacion.

## Ajuste autorizado de reglas de Firestore

El usuario autorizo actualizar las reglas para que `petPosts` pueda guardar la latitud y longitud del lugar donde se perdio la mascota.

- Se quitaron `latitude` y `longitude` de la lista de campos de contacto prohibidos para publicaciones.
- Se agrego `validPetPostLocation`, que exige ambos valores numericos y dentro de los rangos geograficos validos.
- Se agrego validacion opcional para actualizaciones de publicaciones antiguas que no tengan coordenadas.
- Se mantienen bloqueados `ownerLatitude`, `ownerLongitude` y todos los campos de contacto.
- La validacion de consentimiento GPS para `sightings` permanece sin cambios.
- Se agregaron aserciones estaticas en `FirestoreRulesStaticTest` y se actualizaron los artefactos OpenSpec.
- `firebase` CLI no esta instalado en el entorno, por lo que las reglas aun deben desplegarse y probarse en Firebase.

## Reparacion del cierre del mapa

La reproduccion en el emulador `emulator-5554` mostro:

- `FATAL EXCEPTION: Thread-13`
- `NoClassDefFoundError: org/apache/http/ProtocolVersion`
- renderer legacy de Google Maps cargado desde Google Play Services.

Se agrego `uses-library` opcional para `org.apache.http.legacy` en el manifest. Luego se reinstalo el APK, se abrio el selector, se mostro el mapa, se toco un punto y se confirmo la ubicacion sin nuevo crash.

## Referencia automatica con Geocoder

Por decision del usuario, el selector de mapa y la captura GPS intentan una geocodificacion inversa con Android `Geocoder`.

- Si devuelve una direccion o localidad util, esa etiqueta se conserva junto con las coordenadas.
- Si no hay implementacion, falla la consulta o devuelve un resultado vacio, se mantiene la etiqueta segura de la fuente y no se abre fallback manual automatico.
- En el emulador `emulator-5554` se verifico un resultado automatico (`Av. Belgrano 3666...`) y la confirmacion regreso al formulario con la referencia detectada.
- No se incorporo Places ni otro proveedor de geocodificacion.

## Verificacion del orquestador previa

- `openspec validate "replace-free-text-location-input" --strict`: OK.
- `openspec instructions apply --change "replace-free-text-location-input" --json`: OK; la implementacion queda pendiente de verificacion final.
- `git diff --check`: OK; solo advertencias de conversion CRLF.
- `.\gradlew.bat testDebugUnitTest`: OK; `BUILD SUCCESSFUL`.
- `.\gradlew.bat assembleDebug`: OK; `BUILD SUCCESSFUL`.
- Busqueda de secretos: no se encontro ninguna API key real; solo `DEFAULT_API_KEY` en la configuracion segura.
- Busqueda de coordenadas en UI publica: no se agrego renderizado de latitud/longitud; los campos quedan en persistencia y contratos existentes.

## Bloqueo final

El change no puede declararse `PASSED` ni `PASSED_PENDING_INTEGRATION` porque falta ejecutar la validacion manual con Google Maps usando una API key real restringida, billing/cuotas configurados y un dispositivo o emulador con Google Play Services. La configuracion de fallback queda disponible mediante `DEFAULT_API_KEY` y referencia manual.

## Scrum normalizado

- Titulo: Modificar la entrada de Ubicacion para que no dependa de texto libre.
- Objetivo: permitir ubicacion actual, mapa o referencia manual; mostrar la ubicacion seleccionada; preservar el mapeo y la persistencia existentes.
- Textos requeridos: etiqueta `¿Donde fue vista por ultima vez?`; placeholder `Seleccionar ubicacion`.
- Fuera de alcance: agregar la etiqueta o icono de “Ultima ubicacion” de la imagen; cambios fuera de las pantallas y logica necesarias.
- Restriccion: respetar `docs/design-system.md` para etiquetas, textos y placeholders.

## Preflight y sincronizacion

- `git status --short --branch` inicial: `main...origin/main`, limpio.
- `git status --porcelain=v1`: sin salida.
- `git switch main`: OK.
- `git fetch origin --prune`: OK.
- `git pull --ff-only origin main`: OK, ya actualizado.
- `git rev-parse main`: `2758cd2948f31ece337c7bab8ec71ca9c5257d69`.
- `git rev-parse origin/main`: `2758cd2948f31ece337c7bab8ec71ca9c5257d69`.
- Rama creada desde `main`: `ops/replace-free-text-location-input`.
- `git rev-parse HEAD` en la rama: `2758cd2948f31ece337c7bab8ec71ca9c5257d69`.

## Riesgos y decisiones pendientes

- La especificacion base de `pet-posts` indica que la creacion no agrega captura de ubicacion actual, mientras SCRUM-13 solicita ofrecerla. El delta del change debe resolver explicitamente esta contradiccion a favor del Scrum.
- El repositorio ya dispone de `DeviceLocationProvider`, `LocationSource` y persistencia de ubicacion; no se debe reemplazar ese contrato sin necesidad.
- El Scrum requiere mapa, pero no identificaba un proveedor o SDK existente. Se debe mantener el alcance tecnico acotado y documentar cualquier dependencia o limitacion antes de implementar.

## Bloqueo resuelto

El repositorio no contiene un SDK de mapas, un proveedor de geocodificacion ni un flujo de retorno desde una aplicacion externa. Solo existen `DeviceLocationProvider`, `LocationSource.DEVICE_GPS` y el ingreso manual persistido.

Para continuar se necesitaba definir una de estas alternativas:

- incorporar un proveedor/SDK de mapas;
- abrir una aplicacion externa de mapas, si acepta el alcance y la perdida de seleccion de retorno;
- limitar SCRUM-13 al selector visual, GPS actual y referencia manual, dejando el mapa para otro work item.

La decision fue resuelta por el usuario: incorporar Google Maps SDK for Android + Maps Compose.

## OpenSpec

- `openspec new change "replace-free-text-location-input"`: OK.
- Artefactos completos: `proposal.md`, `design.md`, `specs/pet-posts/spec.md`, `specs/location/spec.md`, `tasks.md`.
- `openspec validate "replace-free-text-location-input" --strict`: OK.

## Delegacion

delegation_status: PENDING
handoff_mode: SUBAGENT
agent_id:
agent_role: findyourpet-implementer
delegation_error:
