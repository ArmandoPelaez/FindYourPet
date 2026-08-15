## Context

SCRUM-26 retira una funcionalidad de producto, no solo una pantalla. La rama base ya desacopla el fan-out de nuevos avistamientos de Chat, pero el repositorio todavía contiene rutas Compose, pantallas, estados de `PetViewModel`, operaciones de `PetRepository`, entidades Room, mappers, documentos remotos, reglas Firestore y tests para conversaciones y mensajes.

El cambio debe mantener el flujo activo `sightingId -> Alertas/Actividad -> Detalle`, conservar documentos remotos historicos sin borrarlos y evitar que una migracion local destructiva afecte publicaciones, avistamientos, notificaciones, moderacion o usuarios.

## Goals / Non-Goals

**Goals:**

- Hacer inaccesible la UI, navegacion y acciones de Chat desde el APK.
- Eliminar codigo ejecutable de Chat sin consumidores: lecturas, writes, listeners, estado, modelos, mappers, recursos y tests exclusivos.
- Rechazar nuevos writes de sesiones/mensajes en Firestore, conservando lectura participant-only solo si es necesaria para retencion/compatibilidad historica.
- Migrar Room de la version 9 a la 10 eliminando solo `chat_sessions` y `chat_messages`, sin `fallbackToDestructiveMigration`.
- Mantener las alertas nuevas y la navegacion basadas en `sightingId`, y no alterar la UX de formulario, Alertas, Actividad, Detalle, Reportar o Bloquear.
- Dejar una auditoria global de referencias Chat y justificar cada ocurrencia historica, documental o de compatibilidad que permanezca.

**Non-Goals:**

- No borrar colecciones ni documentos remotos historicos.
- No migrar conversaciones, mensajes ni notificaciones antiguas.
- No agregar un reemplazo de mensajeria ni modificar la logica de moderacion.
- No rediseñar pantallas ni cambiar la identidad visual.

## Decisions

1. **La eliminacion se organiza por capas y consumidores.**

   Primero se quitaran rutas y pantallas; despues estados y operaciones de dominio; luego entidades/DAOs/mappers/documentos; finalmente reglas, recursos y tests. Se usara una busqueda global al cierre para evitar dejar imports o referencias ejecutables. No se eliminara una pieza compartida hasta demostrar que no tiene consumidores fuera de Chat.

2. **La navegacion de notificaciones deja de tener fallback Chat.**

   Las notificaciones `ALERT` continuaran resolviendo `sightingId`. Un tipo o documento historico `CHAT` se conservara como dato no enrutable y producira una ruta nula/controlada, sin abrir una conversacion. La alternativa de conservar `chatId` como destino activo contradice SCRUM-26.

3. **Room usa una migracion dirigida `9 -> 10`.**

   La migracion ejecutara `DROP TABLE IF EXISTS chat_messages` y `DROP TABLE IF EXISTS chat_sessions` solamente despues de retirar sus entidades del schema. Las tablas de publicaciones, sightings, notificaciones, reportes y bloqueos se conservan. La alternativa de `fallbackToDestructiveMigration` se descarta porque puede borrar datos no relacionados.

4. **Firestore conserva historicos, pero bloquea nuevos writes de Chat.**

   Las reglas para `chatSessions` y subcolecciones `messages` mantendran lecturas de participantes si el contrato actual las requiere, pero `create`, `update` y `delete` quedaran denegados para el cliente. No se ejecutara ningun borrado remoto. Esto evita que clientes antiguos o llamadas directas sigan creando la funcionalidad retirada.

5. **Compatibilidad de notificaciones es minima y explicita.**

   `AppNotificationEntity.chatId` y su mapeo nullable pueden conservarse temporalmente para leer registros historicos si Room/Firestore lo requiere, pero ningun flujo activo los usa para navegar, escribir o autorizar Chat. Los tipos y mappers exclusivos de `ChatSessionEntity`/`ChatMessageEntity` se eliminan cuando la auditoria confirme que no tienen consumidores.

6. **El contrato de exito del formulario deja de nombrar Chat.**

   `SightingAlertScreen` y `PetViewModel` conservaran el estado de submit y el retorno existente, pero el callback y las variables se expresaran como resultado de sighting o `Unit`, nunca como `chatId`. No se cambia la UX ni se modifica la persistencia de `SightingAlertEntity`.

7. **Tests de Chat se eliminan o se convierten en contratos de retiro.**

   Tests que solo prueban enviar, leer o renderizar Chat se eliminan junto con sus fixtures. Los contratos que protegen Alertas, Actividad, Detalle, moderacion, Room migration y ausencia de writes se conservan o se actualizan para verificar el nuevo limite.

## Risks / Trade-offs

- [Una referencia Chat compartida puede romper compilacion al retirarla] -> buscar consumidores antes de borrar cada tipo y compilar despues de cada capa.
- [Usuarios con datos historicos locales pierden la cache de conversaciones] -> la migracion elimina solo tablas locales de Chat y conserva el resto; los documentos remotos no se borran.
- [Un cliente antiguo intenta crear Chat] -> reglas Firestore deniegan nuevos writes; se conserva lectura historica participant-only cuando corresponda.
- [Notificaciones Chat antiguas quedan sin destino visible] -> se tratan como historicas/no enrutable y se evita crash o ruta incorrecta.
- [Specs y documentacion aun describen Chat activo] -> actualizar capacidades afectadas y revisar `docs/design-system.md`, reglas y contratos para que la fuente de verdad refleje Actividad.
- [El suite completo contiene contratos legacy] -> separar fallos preexistentes de regresiones del change y registrar comandos/resultados exactos.

## Migration Plan

1. Confirmar referencias y consumidores en la rama desde `main`; generar los artefactos y checklist antes de tocar codigo.
2. Retirar UI/rutas/estado/operaciones y ajustar el contrato de submit sin cambiar la experiencia de avistamiento.
3. Retirar modelos, mappers, documentos y DAOs de Chat; actualizar Room a version 10 con migracion dirigida.
4. Actualizar reglas para bloquear nuevos writes Chat sin borrar historicos y mantener autorizacion de Alertas, sightings y moderacion.
5. Adaptar tests, recursos y documentacion; ejecutar busqueda global de referencias justificadas.
6. Ejecutar OpenSpec, tests enfocados, suite relevante, `assembleDebug` y validacion manual de Alertas/Actividad/Detalle.
7. Rollback: revertir el commit del change y, si hiciera falta restaurar la cache local legacy, volver a una version de app compatible; no se restaura ni modifica Firestore historico.

## Open Questions

- Si la politica futura requiere borrar historicos remotos, debe ser un change separado y explicitamente aprobado; SCRUM-26 no lo decide.
- Si algun consumidor externo fuera de este repositorio necesita leer Chat historico, debe documentarse antes de retirar las reglas de lectura participant-only.
