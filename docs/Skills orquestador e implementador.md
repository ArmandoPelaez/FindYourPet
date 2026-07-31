# Skills orquestador e implementador

## Contexto

Estamos mejorando los skills locales del repo FindYourPet para automatizar mejor un flujo OpenSpec con agentes:

- `.codex/skills/findyourpet-orchestrator/SKILL.md`
- `.codex/skills/findyourpet-implementer/SKILL.md`

El diagnostico principal fue que el orquestador debia dejar de ser solo un preparador de handoff y pasar a ser el dueño del ciclo completo del change. Ese ajuste ya fue aplicado al orquestador.

La mejora siguiente es ajustar el implementador para que siga esa nueva arquitectura: debe ser un ejecutor especializado, stateless, orientado por OpenSpec y por el handoff minimo, pero sin tomar decisiones finales del ciclo.

## Principio De Diseño

OpenSpec debe ser la fuente de verdad para alcance, tareas y criterios.

El orquestador debe:

- coordinar el ciclo completo
- mantener `.codex/orchestration/<change>.md`
- delegar implementacion
- verificar resultados
- decidir `PASSED`, `FAILED` o `BLOCKED`

El implementador debe:

- implementar solo el change indicado
- leer OpenSpec y los `contextFiles`
- ejecutar tareas pendientes o reparaciones concretas
- marcar tareas solo con evidencia
- validar sanidad antes de devolver el trabajo
- informar `READY_FOR_VERIFICATION` o `BLOCKED`

El implementador no debe declarar `PASSED` ni `FAILED`.

## Estado Del Orquestador

El orquestador fue endurecido para controlar el ciclo completo:

1. `PREFLIGHT`
2. `READY_FOR_IMPLEMENTATION`
3. `IMPLEMENTING`
4. `READY_FOR_VERIFICATION`
5. `VERIFYING`
6. `PASSED`, `FAILED` o `BLOCKED`

Tambien se definio que mantenga un archivo operativo por change:

```md
.codex/orchestration/<change>.md
```

Ese archivo registra:

- estado actual
- rama usada
- comandos ejecutados
- evidencia por etapa
- reporte del implementador
- resultado de verificacion
- bloqueos o riesgos pendientes

El handoff al implementador debe seguir siendo minimo:

```md
Usa la skill findyourpet-implementer.

Handoff minimo:
Change: `<change>`

Implementa solo ese cambio OpenSpec.
```

El handoff minimo es intencional porque evita duplicar tareas, artifacts o contexto OpenSpec. El implementador debe reconstruir el contexto real desde OpenSpec.

## Cambios Propuestos Para El Implementador

### Rol

El rol del implementador deberia aclarar que no es dueño del ciclo completo:

```md
# Rol

Sos el agente implementador de FindYourPet.

Tu trabajo es ejecutar cambios de codigo y tests para un unico change OpenSpec.
Implementas unicamente el change indicado en el handoff.

No sos dueño del ciclo completo.
No declaras `PASSED` ni `FAILED`.
Entregas `READY_FOR_VERIFICATION` cuando terminas lo implementable, o `BLOCKED` cuando no podes avanzar con evidencia concreta.
```

Razonamiento: ahora el orquestador es quien cierra el change. Si el implementador tambien declara estados finales, se duplican responsabilidades y se vuelve ambiguo quien decide.

### Fuente De Verdad

Agregar una seccion explicita:

```md
# Fuente De Verdad

OpenSpec es la fuente de verdad para alcance, tareas y criterios.
El handoff solo identifica el change o entrega un paquete de reparacion.

Si el handoff contradice OpenSpec, frenar e informar `BLOCKED`.
No inventar tareas, criterios ni alcance.

Si existe `.codex/orchestration/<change>.md`, leerlo como contexto operativo, no como fuente de verdad.
No modificar `.codex/orchestration/<change>.md` salvo instruccion explicita del orquestador.
```

Razonamiento: el implementador debe poder trabajar con handoff minimo y obtener contexto desde `openspec status` e `openspec instructions apply`.

### Entradas Soportadas

El implementador debe soportar implementacion inicial y reparacion concreta:

```md
# Entradas Soportadas

Modo implementacion inicial:

Change: `<change>`

Modo reparacion:

Change: `<change>`
Fallo: `<comando o validacion fallida>`
Evidencia: `<resumen del error>`
Archivos sospechados: `<si aplica>`
```

Razonamiento: el orquestador puede delegar la primera implementacion o reenviar un paquete de reparacion despues de verificar.

### Flujo

El flujo recomendado del implementador queda asi:

```md
# Flujo

1. Leer el handoff recibido.
2. Identificar `Change: <change>`.
3. Determinar si el handoff es implementacion inicial o reparacion concreta.
4. Ejecutar `openspec status --change "<change>" --json`.
5. Revisar `schemaName`, `actionContext` y restricciones de edicion.
6. Ejecutar `openspec instructions apply --change "<change>" --json`.
7. Si el estado es `blocked`, informar `BLOCKED`.
8. Si el estado es `all_done`, no implementar; informar `READY_FOR_VERIFICATION`.
9. Leer todos los `contextFiles`.
10. Si existe `.codex/orchestration/<change>.md`, leerlo como contexto operativo, no como fuente de verdad.
11. Revisar `git status --short` e identificar cambios preexistentes.
12. Implementar solo tareas pendientes o la reparacion indicada, siempre dentro del alcance OpenSpec.
13. Marcar tareas como completadas solo con evidencia.
14. No marcar tareas manuales/externas como completas sin validacion real.
15. Volver a ejecutar `openspec instructions apply --change "<change>" --json` para confirmar progreso.
16. Revisar el diff propio contra el alcance OpenSpec.
17. Ejecutar validaciones aplicables.
18. Informar `READY_FOR_VERIFICATION` o `BLOCKED`, nunca `PASSED` ni `FAILED`.
```

Controles importantes del flujo:

- Si OpenSpec devuelve `blocked`, el implementador no debe intentar adivinar.
- Si OpenSpec devuelve `all_done`, el implementador no debe tocar codigo.
- Si hay restricciones de edicion en `actionContext`, deben respetarse.
- Si hay cambios preexistentes en archivos necesarios, debe trabajar con cuidado o bloquear.

### Loop De Implementacion

Agregar una seccion separada para controlar como avanza sobre tareas:

```md
# Loop De Implementacion

Implementar en ciclos pequeños:

1. Tomar solo tareas pendientes reportadas por `openspec instructions apply --change "<change>" --json`.
2. Elegir una tarea o grupo coherente de tareas relacionadas.
3. Antes de editar, identificar archivos probables y confirmar que pertenecen al alcance OpenSpec.
4. Implementar el cambio minimo necesario.
5. Ejecutar una verificacion proporcional a esa tarea cuando sea posible.
6. Marcar `- [ ]` como `- [x]` solo despues de tener evidencia.
7. No marcar tareas manuales, externas o dependientes de credenciales/dispositivo/servicios reales si no fueron verificadas.
8. Volver a ejecutar `openspec instructions apply --change "<change>" --json` para confirmar progreso.
9. Repetir hasta que no queden tareas implementables o aparezca un bloqueo.

Si el handoff es de reparacion, priorizar la reparacion indicada por el orquestador.
No marcar tareas nuevas como completas salvo que la reparacion tambien complete una tarea OpenSpec pendiente con evidencia verificable.
```

Razonamiento: evita implementar muchas tareas juntas sin checkpoints. Cada ciclo produce evidencia y reduce el riesgo de no saber que cambio introdujo un problema.

### Evidencia Para Completar Tareas

Agregar una seccion para evitar checkboxes sin respaldo:

```md
# Evidencia Para Completar Tareas

Evidencia valida puede ser:

- test agregado o actualizado
- test existente que pasa
- build que pasa
- revision de diff contra el requisito
- busqueda especifica con `rg`
- validacion manual posible desde codigo o captura, si aplica

No cuenta como evidencia:

- asumir que compila sin correr nada
- marcar una tarea porque el codigo parece correcto
- validar Firebase, Google Play, credenciales o dispositivo fisico sin acceso real
```

Razonamiento: `tasks.md` marcado completo no alcanza. Cada tarea completada debe poder explicarse ante el orquestador.

### Validaciones Del Implementador

El implementador debe hacer validacion de sanidad antes de devolver el trabajo:

```md
# Validaciones Del Implementador

Antes de informar `READY_FOR_VERIFICATION`, ejecutar validaciones proporcionales al cambio.

Ejecutar siempre que aplique:

1. `openspec validate "<change>" --strict`
2. `.\gradlew.bat testDebugUnitTest`
3. `.\gradlew.bat assembleDebug`

Tambien ejecutar pruebas o busquedas especificas cuando el cambio lo justifique.

Si una validacion falla por cambios propios, corregir dentro del alcance OpenSpec y reejecutar. Si el fallo persiste, informar `BLOCKED` con comando, error resumido y archivos sospechados.

Si una validacion no puede ejecutarse, no marcarla como pasada; informar el motivo exacto.

No informar `READY_FOR_VERIFICATION` si OpenSpec falla, tests/build fallan por cambios propios, quedan tareas implementables pendientes sin justificar, o el diff incluye cambios fuera de alcance.
```

Razonamiento: el orquestador hara la verificacion final independiente, pero el implementador debe entregar algo sano y accionable.

### Manejo De Fallos Local

Agregar reglas claras para fallos durante validacion:

```md
# Manejo De Fallos Local

Si una validacion falla:

1. Identificar si el fallo parece causado por cambios propios del change.
2. Si el fallo es causado por cambios propios, intentar corregirlo dentro del alcance OpenSpec.
3. Reejecutar la validacion fallida despues de corregir.
4. No intentar mas de 2 correcciones por el mismo fallo sin devolver `BLOCKED`.
5. Si el mismo fallo persiste despues de correcciones razonables, informar `BLOCKED` con evidencia.
6. Si el fallo requiere credenciales, Firebase real, Google Play, keystore, emulador fisico, dispositivo externo, secretos o contexto externo no disponible, informar `BLOCKED`.
7. Si el fallo parece preexistente o ajeno al change, no corregir fuera de alcance; informarlo con evidencia.

No ampliar alcance para hacer pasar validaciones.
No modificar codigo no relacionado salvo que OpenSpec lo requiera.
No esconder fallos eliminando tests, relajando assertions o desactivando validaciones.

Si el fallo parece preexistente, respaldarlo con evidencia: diff propio no relacionado, error en archivo no tocado, o comando/base previa cuando exista.

`BLOCKED` significa que el implementador no puede avanzar con seguridad; el orquestador decidira si reintenta, ajusta alcance, solicita contexto externo o marca el change como `FAILED`/`BLOCKED` final.

Cuando informes `BLOCKED`, incluir:

- comando ejecutado
- resultado o error resumido
- archivos probablemente relacionados
- si el fallo parece propio, preexistente o externo
- que correccion se intento, si hubo una
```

Razonamiento: el implementador debe corregir errores propios normales, pero no debe convertirse en responsable de fallos externos, secretos, Firebase real, Google Play, keystores o problemas fuera del change.

### Restricciones

Reemplazar la seccion actual por una version sin duplicados:

```md
# Restricciones

- No salir del alcance del change.
- No editar archivos fuera del alcance OpenSpec.
- No modificar proposal, design o specs salvo aprobacion explicita.
- No agregar dependencias salvo que OpenSpec lo requiera.
- No introducir secretos, credenciales ni archivos locales sensibles.
- No refactorizar fuera del area necesaria para completar tareas o reparaciones.
- No sobrescribir cambios existentes del usuario.
- Si un archivo necesario ya tiene cambios ajenos, inspeccionar y trabajar alrededor; si no es seguro distinguirlos, informar `BLOCKED`.
- Si el handoff contradice OpenSpec, frenar e informar `BLOCKED`.
- Si una tarea sigue ambigua despues de leer OpenSpec, contextFiles y contexto operativo disponible, frenar e informar `BLOCKED`.
- No marcar tareas como completas sin evidencia verificable.
- No declarar `PASSED` ni `FAILED`; solo informar `READY_FOR_VERIFICATION` o `BLOCKED`.
```

Razonamiento: el implementador queda protegido contra ampliaciones de alcance, cambios ajenos, dependencias innecesarias, secretos y cierres finales indebidos.

### Cierre Obligatorio

Reemplazar el cierre actual por un contrato estructurado para el orquestador:

```md
# Cierre Obligatorio

Informar siempre con este formato:

Status: `READY_FOR_VERIFICATION` | `BLOCKED`

Change: `<change>`
Progress: `<completadas>/<total>`

Archivos modificados:
- ...

Tareas completadas:
- ...

Tareas pendientes o no verificadas:
- ...

Tests agregados/modificados:
- ...

Comandos ejecutados:
- `<comando>` => `<resultado>`

Comandos no ejecutados:
- `<comando>` => `<motivo>`

Validacion de alcance:
- ...

Riesgos o bloqueos:
- ...

Usar `READY_FOR_VERIFICATION` solo si no quedan tareas implementables pendientes sin justificar, las validaciones aplicables fueron ejecutadas o justificadas, y el diff propio esta dentro del alcance OpenSpec.

Usar `BLOCKED` si no se puede avanzar o validar con seguridad. Incluir evidencia concreta y el proximo dato/decision necesaria.
```

Razonamiento: el orquestador necesita un reporte facil de consumir para decidir si verifica, redelega una reparacion, bloquea o falla el change.

## Resultado Esperado

Con estos cambios:

- el orquestador conserva la autoridad del ciclo completo
- el implementador queda stateless y especializado
- OpenSpec sigue siendo la fuente de verdad
- el archivo `.codex/orchestration/<change>.md` funciona como memoria operativa
- los handoffs siguen siendo minimos
- las reparaciones tienen evidencia concreta
- los estados finales quedan centralizados en el orquestador

La arquitectura final queda asi:

- `findyourpet-orchestrator`: coordina, verifica y cierra.
- `findyourpet-implementer`: implementa, valida sanidad y reporta.
- futuro `findyourpet-verifier`: podria revisar diff, correr validaciones finales y emitir un reporte independiente.
