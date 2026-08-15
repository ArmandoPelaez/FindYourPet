## Context

El método `PetRepository.submitSightingAlert` valida el reporte y luego construye cuatro documentos: sighting, chat session, mensaje inicial de alerta y notificación. La ruta local replica esas cuatro escrituras y `PetViewModel` guarda el valor retornado como `activeChatId`; la pantalla del formulario solo usa el callback para volver al destino principal.

SCRUM-20 cambia únicamente el fan-out posterior a una validación exitosa. `SightingAlertEntity` debe seguir siendo el registro canónico del avistamiento y `AppNotificationEntity` debe seguir informando al propietario, pero el flujo nuevo no debe crear documentos de Chat ni copiar `notes` a un mensaje. Los modelos, DAOs, pantallas y datos históricos de Chat permanecen para no romper conversaciones existentes.

## Goals / Non-Goals

**Goals:**

- Escribir exactamente el sighting y la notificación en el camino remoto válido.
- Replicar la misma separación en el fallback local sin insertar sesiones ni mensajes.
- Mantener generación estable de `sightingId`, `idempotencyKey`, validaciones y metadata sensible existente.
- Usar `sightingId` como destino de una nueva notificación de avistamiento y conservar `postId`/`sightingId` para navegación futura.
- Mantener el contrato visual y funcional del formulario: submit, estados de éxito/error y retorno a Home no cambian.
- Mantener la escritura y lectura de Chat normal y legacy fuera de este fan-out.
- Alinear reglas y tests con el nuevo batch sin relajar autorización de sightings o notificaciones.

**Non-Goals:**

- No eliminar `ChatSessionEntity`, `ChatMessageEntity`, tablas, colecciones, pantallas ni mappers legacy.
- No modificar el formulario, su UX, la UI de Alertas, rutas de Chat o navegación de notificaciones en esta task.
- No crear todavía una pantalla de detalle de avistamiento ni migrar notificaciones históricas.
- No cambiar validaciones de autenticación, propietario, reporter, ubicación, permisos o idempotencia.

## Decisions

1. **El repositorio conserva `SightingAlertEntity` como fuente de verdad.**

   El método seguirá derivando el propietario desde el post, validando identidad y ubicación, subiendo la foto cuando corresponda y construyendo el mismo `SightingAlertEntity`. Después construirá únicamente la notificación. La alternativa de mantener una copia en `ChatMessageEntity.generalDetails` se descarta porque duplica datos y mantiene la dependencia que SCRUM-20 elimina.

2. **El resultado exitoso del repositorio será el `sightingId`, no un `chatId`.**

   El ViewModel mantendrá el callback `String` para no cambiar la UX del formulario, pero dejará de actualizar `activeChatId` y propagará el identificador del sighting. La pantalla puede ignorar ese argumento y regresar a Home como hoy. La alternativa de devolver un `chatId` vacío se descarta porque conserva una semántica falsa y facilita nuevas navegaciones incorrectas.

3. **El batch remoto tendrá dos escrituras y la ruta local tendrá dos inserciones.**

   Firestore escribirá `sightings/{sightingId}` y `users/{ownerId}/notifications/{notificationId}` en una única operación atómica. Room insertará el sighting y la notificación dentro de la misma transacción. No se construirán `ChatSessionEntity` ni `ChatMessageEntity`, ni se tocarán `chat_sessions`/`chat_messages`.

4. **Las nuevas notificaciones de tipo `ALERT` apuntarán al sighting.**

   `targetId` será `sightingId`, `sightingId` y `postId` se conservarán, y `chatId` se omitirá para nuevas alertas. El campo nullable y la compatibilidad del mapper se mantienen para notificaciones históricas y de Chat. No se modifica en esta task la navegación de la UI; el destino queda preparado para la futura pantalla de detalle.

5. **Las reglas separarán el camino de sighting del camino de Chat.**

   Se agregará/ajustará una validación específica para una notificación `ALERT` con `sightingId`, `postId` y `targetId == sightingId`, comprobando que el sighting creado en el batch coincide con el post, propietario y reporter autenticado. La validación de mensajes `sighting_alert` y la autorización participant-only de Chat se conservarán para documentos legacy, pero ya no serán requeridas por el batch de avistamientos.

6. **La cobertura será contractual y de regresión.**

   Se actualizarán tests de repositorio/ViewModel, mappers y reglas para probar el camino válido, ausencia de chat, ausencia de copia de `notes`, target por sighting, idempotencia y rechazo de reportes inválidos. Se conservarán tests de Chat normal y de mappers legacy para demostrar compatibilidad.

## Risks / Trade-offs

- [La UI de notificaciones actual intenta abrir Chat con cualquier `targetId`] → No se cambia esa UI dentro de SCRUM-20; se mantiene `chatId` solo para datos históricos y se documenta el destino de sighting para el cambio futuro de navegación.
- [Clientes o datos antiguos esperan `chatId` en alertas] → Se dejan campos nullable, mappers legacy y colecciones de Chat intactos; solo las escrituras nuevas omiten `chatId`.
- [Las reglas actuales dependen de `getAfter(chatSessions)` para alertas] → Se agrega una rama de validación específica para el batch de sighting sin eliminar las reglas de mensajes existentes.
- [La ruta local puede divergir del batch remoto] → Se cubren ambas rutas con tests y se conserva una transacción Room con exactamente dos inserciones.
- [Un reintento podría duplicar documentos] → Se mantiene el `sightingId` estable derivado del `idempotencyKey` y el mismo identificador de notificación.
- [El propietario no tiene todavía una pantalla de detalle de sighting] → El cambio solo corrige el contrato de datos; `targetId` queda listo para navegación futura y el formulario continúa regresando a Home.

## Migration Plan

1. Actualizar el repositorio y ViewModel para producir solo sighting/notificación y devolver `sightingId`.
2. Actualizar el contrato de notificación y las reglas Firestore para aceptar la nueva forma y preservar la forma legacy.
3. Añadir o ajustar tests unitarios/estáticos y validar que el formulario no cambió.
4. Ejecutar OpenSpec validate, tests unitarios y assembleDebug.
5. Rollback: revertir únicamente el commit de la rama para restaurar el fan-out anterior; no se borran documentos existentes ni se ejecuta migración destructiva.

## Open Questions

- La navegación de `targetId == sightingId` hacia una futura pantalla de detalle queda fuera de SCRUM-20 y deberá definirse en una task posterior.
