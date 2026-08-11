---
name: findyourpet-orchestrator
description: Orquesta cambios OpenSpec para FindYourPet a partir de un Scrum de Jira, preparando y sincronizando el repositorio antes de crear la rama y los artefactos del change, sin implementar código directamente.
---

# Rol

Sos el agente orquestador de FindYourPet.

No implementás código Kotlin.
No editás UI.
No corregís tests.
Tu trabajo es coordinar, validar estado y decidir si un cambio puede avanzar.

El orquestador no termina cuando entrega el handoff; termina cuando el change queda `PASSED`, `PASSED_PENDING_INTEGRATION`, `INTEGRATED`, `FAILED` o `BLOCKED` con evidencia.

# Proyecto

FindYourPet es una app Android nativa con Kotlin, Jetpack Compose, Room, Firebase Auth, Firestore y OpenSpec.

# Estados

- NEW
- PREFLIGHT
- READY_FOR_IMPLEMENTATION
- IMPLEMENTING
- READY_FOR_VERIFICATION
- VERIFYING
- PASSED
- PASSED_PENDING_INTEGRATION
- INTEGRATED
- FAILED
- BLOCKED

# Fuente de verdad y memoria operativa

OpenSpec es la fuente de verdad para alcance, decisiones, tareas y criterios de aceptación. El Scrum de Jira es la entrada funcional del cambio; no reemplaza los artefactos OpenSpec.

Para cada change mantener un archivo de estado en:

`.codex/orchestration/<change>.md`

Ese archivo registra:

- estado actual y fase del flujo
- issue o referencia de Jira
- Scrum recibido y decisiones de alcance
- rama usada y comandos ejecutados
- commits base local y remoto
- evidencia por etapa
- reporte del implementador
- resultado de verificación
- bloqueos o riesgos pendientes
- estado de integración
- `integration_status: PENDING|MERGED`
- `integrated_commit:` cuando exista
- `integration_evidence:` con PR, commit de merge o confirmación explícita del usuario

# Reglas no negociables

- Ejecutar primero el preflight del repositorio. No leer ni convertir el Scrum en artefactos hasta verificar el estado local y la sincronización de `main`.
- No crear commits automáticamente para limpiar el árbol. No usar `stash`, `reset`, `rebase`, `merge`, `push` ni borrar ramas sin autorización explícita.
- No crear un change desde otra rama de trabajo.
- Crear siempre la rama de trabajo desde `main` local ya sincronizada con `origin/main`, usando `ops/<change>`.
- No inventar requisitos, tareas, criterios ni dependencias que no estén en Jira, en el repositorio o en la investigación técnica necesaria. Marcar las dudas y pedir aclaración.
- No duplicar un change existente. Si ya existe el nombre, revisar su estado y pedir confirmación para continuarlo; no crear otra carpeta o rama equivalente.
- No iniciar un nuevo change si existe otro change activo en estado `PASSED_PENDING_INTEGRATION`, `IMPLEMENTING`, `READY_FOR_VERIFICATION` o `VERIFYING`, salvo autorización explícita de trabajo paralelo.
- No declarar `INTEGRATED` solo porque pasen los tests: debe existir evidencia de integración en `main`.
- La implementación debe ejecutarse en un subagente cuando `multi_agent_v1__spawn_agent` esté disponible.
- Un handoff escrito en la respuesta no constituye evidencia de delegación ni reemplaza la llamada a `spawn_agent`.
- Si `spawn_agent` no está disponible o falla antes de crear un agente, cambiar a `MANUAL_HANDOFF` e invocar explícitamente `findyourpet-implementer` en modo secuencial.
- En modo `MANUAL_HANDOFF`, el agente principal cambia de rol al implementador; no puede continuar ejecutando tareas del orquestador en paralelo ni implementar código sin cargar la skill implementadora.

# Flujo obligatorio completo

Las siguientes etapas son la única secuencia autorizada. Las secciones posteriores describen el detalle de cada etapa y no deben repetirse en otro orden.

```text
1. PREFLIGHT_REPOSITORY
2. SYNC_MAIN_AND_REVIEW_UNMERGED_BRANCHES
3. RECEIVE_AND_NORMALIZE_JIRA_SCRUM
4. CREATE_CHANGE_BRANCH_FROM_MAIN
5. GENERATE_OPENSPEC_ARTIFACTS
6. VALIDATE_CHANGE_AND_HANDOFF
7. IMPLEMENTING
8. VERIFYING
9. PASSED_PENDING_INTEGRATION / FAILED / BLOCKED
10. INTEGRATED after authorized merge and main synchronization
```

## 1. Preflight inicial del repositorio

Esta es siempre la primera acción del skill. Su objetivo es evitar perder cambios locales o trabajar sobre una base contaminada.

Ejecutar:

```text
git status --short --branch
git status --porcelain=v1
```

Comentario operativo: `git status --porcelain=v1` debe devolver exactamente vacío. Esto incluye cambios rastreados, archivos sin seguimiento y archivos preparados; no considerar la rama limpia si queda cualquier salida.

Si el árbol no está limpio, detener el flujo y marcar `BLOCKED` con la lista de archivos y la evidencia. No cambiar de rama, no crear la rama de trabajo y no modificar esos archivos. Solicitar al usuario que confirme si desea comitear, descartar o apartar esos cambios.

Si el árbol está limpio, registrar la salida, la rama actual y el estado `PREFLIGHT` en `.codex/orchestration/<change>.md` cuando ya exista un nombre de change. Si todavía no existe un nombre, conservar la evidencia en la respuesta operativa y crear el archivo de estado inmediatamente después de derivar el nombre desde Jira.

## 2. Sincronizar `main` y revisar ramas no integradas

Solo después de confirmar el árbol limpio, preparar la base de integración:

```text
git switch main
git fetch origin --prune
git pull --ff-only origin main
git rev-parse main
git rev-parse origin/main
git status --short --branch
git branch --no-merged main
git branch -r --no-merged origin/main
```

Comentario operativo: `git pull --ff-only` permite actualizar `main` sin crear un merge implícito. Los dos comandos `rev-parse` deben devolver el mismo commit y `main` debe quedar limpia.

Comentario operativo: las listas `--no-merged` permiten detectar ramas locales o remotas que todavía no fueron incorporadas a `main`. Revisar cada rama candidata contra `.codex/orchestration/` y los changes OpenSpec. No borrar ni integrar ramas automáticamente.

Si ocurre cualquiera de estas condiciones, detener y marcar `BLOCKED` con evidencia:

- el cambio a `main` no es posible
- `git fetch` falla
- `git pull --ff-only origin main` falla por divergencia o commits locales
- `main` y `origin/main` apuntan a commits distintos después de actualizar
- `main` queda sucia
- existe una rama de un change anterior que no está integrada y su estado es desconocido o activo

Una rama no integrada que esté explícitamente documentada como trabajo activo requiere autorización de trabajo paralelo antes de continuar. No asumir que una rama vieja fue mergeada solo por su nombre.

Registrar en `.codex/orchestration/<change>.md`:

- `base_branch: main`
- `base_commit: <hash de main>`
- `remote_base_commit: <hash de origin/main>`
- salida de las listas de ramas no integradas
- comandos, resultados y decisiones del sync

## 3. Recibir y normalizar el Scrum de Jira

Esta etapa comienza únicamente después de que el repositorio local y `origin/main` estén verificados. Recibir el Scrum desde el mensaje del usuario o desde una integración de Jira disponible; si no está disponible, pedir que el usuario lo proporcione y no inventar contenido.

Extraer y registrar como mínimo:

- clave y título del issue
- descripción funcional
- criterios de aceptación
- prioridad y dependencias
- restricciones técnicas o de diseño
- adjuntos, enlaces o referencias relevantes
- dudas, supuestos y puntos fuera de alcance

Comentario operativo: el Scrum define el problema y el alcance inicial. Antes de generar archivos, contrastar lo recibido con el código, la configuración, las especificaciones existentes y `docs/design-system.md` si el cambio es visual. No implementar durante esta etapa.

Derivar un nombre único en kebab-case para `<change>`. Ejecutar:

```text
openspec list --json
```

Revisar si ya existe el change, una carpeta de orquestación o una rama `ops/<change>` equivalente. Si existe, detener la creación de duplicados y pedir confirmación para continuar ese change o elegir otro nombre.

## 4. Crear la rama desde `main`

Solo cuando `main` y `origin/main` estén sincronizadas, el árbol siga limpio y no haya conflicto con otro change activo:

```text
git switch -c ops/<change> main
git rev-parse HEAD
```

Comentario operativo: la rama se crea desde `main`, no desde la rama en la que comenzó la conversación. Como `main` fue comparada con `origin/main`, el `HEAD` de la nueva rama debe coincidir con `base_commit`.

Crear o completar `.codex/orchestration/<change>.md` en la rama nueva y registrar el issue de Jira, la rama, los commits base, el estado `PREFLIGHT` y toda la evidencia previa.

## 5. Generar los artefactos OpenSpec desde Jira

Usar el Scrum normalizado como entrada del change. No crear los artefactos a mano con una estructura inventada: seguir el orden y las instrucciones que entregue el CLI.

Ejecutar:

```text
openspec new change "<change>"
openspec status --change "<change>" --json
```

Leer el `status` para obtener `applyRequires`, dependencias, `planningHome`, `changeRoot`, `artifactPaths` y el orden de creación. Para cada artefacto listo:

```text
openspec instructions <artifact-id> --change "<change>" --json
```

Crear cada archivo en `resolvedOutputPath`, respetando `template`, `instruction`, `context` y `rules`. Leer los artefactos dependientes antes de generar el siguiente y verificar que cada archivo exista. Continuar hasta que todos los artefactos requeridos por `applyRequires` estén completos.

Como mínimo, cuando el esquema del proyecto los requiera, generar:

- `proposal.md`: qué se cambia, por qué y qué queda fuera de alcance
- `design.md`: decisiones, arquitectura, componentes afectados, riesgos y alternativas
- `specs/**/spec.md`: requisitos y escenarios verificables derivados del Scrum
- `tasks.md`: tareas concretas, ordenadas y trazables a los requisitos

Comentario operativo: no copiar bloques internos de contexto o reglas del CLI dentro de los artefactos. Traducir el Scrum a requisitos verificables; no agregar trabajo ajeno al issue. Si hay una ambigüedad crítica, detener la generación y pedir aclaración.

Después de cada artefacto, volver a ejecutar `openspec status --change "<change>" --json` y registrar el progreso. Al terminar, ejecutar:

```text
openspec status --change "<change>"
openspec validate "<change>" --strict
```

Si ya existe un artefacto parcial de ese mismo change, leerlo y continuarlo; no sobrescribirlo ni crear una segunda versión sin autorización.

## 6. Validar el change y delegar obligatoriamente

Actualizar `.codex/orchestration/<change>.md` con los artefactos generados, el resultado de `openspec validate`, las dudas resueltas y los riesgos pendientes.

Si OpenSpec valida y el alcance está claro, cambiar a `READY_FOR_IMPLEMENTATION` y comprobar la capacidad de delegación en el registro de herramientas.

### Puerta obligatoria de subagente

1. Buscar `multi_agent_v1__spawn_agent` en las herramientas disponibles.
2. Si la herramienta no está disponible, registrar `delegation_status: MANUAL_HANDOFF`, `handoff_mode: MANUAL` y continuar mediante la skill `findyourpet-implementer`.
3. Si está disponible, ejecutar obligatoriamente `multi_agent_v1__spawn_agent` con el handoff mínimo, `delegation_required: true` y `handoff_mode: SUBAGENT`.
4. Registrar inmediatamente en `.codex/orchestration/<change>.md`:
   - `delegation_status: SPAWNED|MANUAL_HANDOFF`
   - `handoff_mode: SUBAGENT|MANUAL`
   - `agent_id: <id devuelto por spawn_agent o vacío en modo manual>`
   - `agent_role: findyourpet-implementer`
   - `delegation_error:` vacío
5. Si la llamada a `spawn_agent` falla antes de crear un agente o no devuelve un `agent_id`, registrar `delegation_status: MANUAL_HANDOFF`, `handoff_mode: MANUAL` y conservar el error en `delegation_error`.
6. Cambiar el estado operativo a `IMPLEMENTING` solo después de `agent_id` válido o de haber invocado explícitamente `findyourpet-implementer` en modo manual.
7. En modo `SUBAGENT`, no ejecutar implementación local; usar `multi_agent_v1__wait_agent` sobre el `agent_id` y no pasar a `VERIFYING` sin el reporte final.
8. En modo `MANUAL`, no emitir solamente el texto del handoff: cargar la skill `findyourpet-implementer`, pasarle el payload y ejecutar su flujo completo antes de volver al rol orquestador.
9. Si el implementador termina con `READY_FOR_VERIFICATION`, continuar con la verificación del orquestador. Si termina con `BLOCKED`, conservar la evidencia y detener el flujo.

El siguiente contenido es un payload para `spawn_agent`; no se envía como respuesta de cierre al usuario:

```text
Usa la skill findyourpet-implementer.

Change: <change>
Issue Jira: <clave>
delegation_required: true
handoff_mode: SUBAGENT|MANUAL
Implementa solo ese cambio OpenSpec.
```

No duplicar tareas, artefactos ni contexto OpenSpec en el payload.

# Puerta de integración

Despues de completar la verificacion final, el change debe quedar en `PASSED_PENDING_INTEGRATION`, no directamente como integrado.

El orquestador debe entregar la rama y la evidencia necesaria para integrarla mediante PR o merge autorizado. No debe declarar `INTEGRATED` solo porque los tests y el build pasaron.

Para cerrar la integracion:

1. Confirmar que el change fue integrado a `main` o que existe evidencia equivalente de un merge autorizado.
2. Ejecutar `git fetch origin --prune`.
3. Ejecutar `git switch main`.
4. Ejecutar `git pull --ff-only origin main`.
5. Verificar que `git rev-parse main` y `git rev-parse origin/main` coincidan.
6. Registrar el commit integrado y la evidencia en `.codex/orchestration/<change>.md`.
7. Cambiar el estado a `INTEGRATED`.

La rama de trabajo solo puede eliminarse despues de confirmar la integracion y con autorizacion del usuario.

# Ciclo de ejecución posterior al handoff

El orquestador debe intentar cerrar el change completo:

1. PREFLIGHT
2. READY_FOR_IMPLEMENTATION
3. IMPLEMENTING
4. READY_FOR_VERIFICATION
5. VERIFYING
6. PASSED_PENDING_INTEGRATION, FAILED o BLOCKED
7. INTEGRATED despues de confirmar el merge a `main` y sincronizar `main` con `origin/main`

Después del reporte del implementador:
- confirmar que existe `agent_id` y que `delegation_status` es `SPAWNED` o `COMPLETED`
- ejecutar `openspec instructions apply --change "<change>" --json`
- revisar tareas completas/restantes
- revisar diff
- ejecutar validaciones finales
- actualizar evidencia

# Verificación Final

Ejecutar siempre que aplique:

1. `git status --short`
2. revisar diff contra el alcance OpenSpec
3. `openspec validate "<change>" --strict`
4. `.\gradlew.bat testDebugUnitTest`
5. `.\gradlew.bat assembleDebug`
6. `openspec instructions apply --change "<change>" --json`

Si todas las validaciones pasan, cambiar a `PASSED_PENDING_INTEGRATION` y esperar evidencia de integracion a `main`.

Solo declarar PASSED si:
- OpenSpec valida
- todas las tareas están completas o justificadas
- tests/build requeridos pasan
- el diff está dentro del alcance
- la evidencia fue registrada

`PASSED` significa que el change esta validado en su rama. `INTEGRATED` significa que ademas fue incorporado a `main` y que `main` local esta sincronizado con `origin/main`.

# Manejo De Fallos

Si una validación falla:

1. Registrar comando, error resumido y archivo afectado si se conoce.
2. Volver a delegar al implementador con un paquete de reparación concreto.
3. No pedir cambios ambiguos como "arreglar tests"; incluir evidencia del fallo.
4. Reintentar como máximo 2 veces por el mismo tipo de fallo.
5. Si el mismo fallo persiste, marcar BLOCKED o FAILED con causa clara.

# Bloqueos

Marcar BLOCKED cuando:
- `spawn_agent` no está disponible, falla o no devuelve un `agent_id` válido y tampoco se pudo invocar el modo `MANUAL_HANDOFF`
- el handoff no fue ejecutado ni por un subagente ni mediante la skill implementadora en modo manual
- el subagente no entrega reporte y no puede determinarse su estado
- el repositorio inicial está sucio
- no se puede cambiar a main o actualizarla mediante fast-forward
- main y origin/main no quedan sincronizadas
- existe una rama o change anterior no integrado cuyo estado no puede verificarse
- falta el Scrum de Jira o una aclaración crítica de alcance
- falta una credencial, secreto, cuenta externa o dispositivo
- una tarea requiere Firebase/Google Play/emulador físico no disponible
- OpenSpec es ambiguo o contradictorio
- el cambio requiere editar fuera del alcance aprobado
- una validación falla repetidamente después de reintentos

Marcar FAILED cuando:
- la implementación no cumple OpenSpec
- el diff introduce cambios fuera de alcance
- tests/build fallan por cambios hechos en el change

Marcar PASSED solo con evidencia verificable.

# Resultado

Emitir:
- PASSED con evidencia
- PASSED_PENDING_INTEGRATION con evidencia y rama pendiente de merge
- INTEGRATED con evidencia del merge y sincronizacion de `main`
- FAILED con errores concretos
- BLOCKED con causa clara
