## Why

El envío de un avistamiento todavía acopla una alerta válida a la creación de una conversación y un mensaje de Chat, aunque el producto ya debe tratar `SightingAlertEntity` como la fuente de verdad del reporte. SCRUM-20 elimina esa escritura innecesaria para evitar datos duplicados, destinos de notificación incorrectos y dependencia del Chat en el procesamiento de avistamientos.

## What Changes

- Mantener el fan-out de avistamientos limitado a `SightingAlertEntity` y `AppNotificationEntity`.
- Conservar `notes`, `sightingId`, `postId`, `idempotencyKey`, validaciones de identidad, propietario, ubicación y permisos existentes.
- Generar nuevas notificaciones con `sightingId`, `postId` y `targetId` apuntando al avistamiento; dejar de producir nuevos valores de destino basados en `chatId`.
- Dejar de crear `ChatSessionEntity` y `ChatMessageEntity` durante el envío de un nuevo avistamiento.
- Mantener los modelos, pantallas, reglas y datos legacy del Chat para compatibilidad con conversaciones existentes y mensajería normal.
- Mantener sin cambios funcionales ni visuales el formulario de avistamiento y la UI de Alertas.
- **BREAKING**: el flujo nuevo de avistamientos ya no inicia ni escribe una conversación de Chat.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `sightings`: el fan-out aceptado deja de crear conversación y mensaje de Chat; el avistamiento queda como fuente de verdad.
- `notifications`: las notificaciones derivadas de avistamientos conservan `sightingId` y dirigen `targetId` al avistamiento, sin depender de `chatId`.
- `backend-data-model`: el contrato de escritura de una alerta nueva separa el documento de avistamiento y su notificación de los documentos legacy de Chat.
- `backend-access-rules`: las validaciones del batch de avistamiento dejan de exigir escrituras nuevas de Chat y mantienen las reglas de acceso existentes para Chat legacy.
- `private-chat`: la creación del primer mensaje de Chat deja de formar parte del flujo de envío de avistamientos; la mensajería participante existente continúa disponible para sesiones ya existentes o creadas por sus propios flujos.

## Impact

- Código Android: `PetRepository`/repositorio remoto, `PetViewModel` y helpers de notificación que ejecutan el envío de avistamientos.
- Datos y backend: batch de Firestore, mappers de `SightingAlertEntity` y `AppNotificationEntity`, y reglas de validación de sightings/notificaciones.
- Tests: contratos de fan-out, mappers, reglas estáticas, idempotencia y ausencia de escrituras `chatSessions`/`messages` en el camino nuevo.
- Usuarios existentes: las conversaciones y mensajes históricos no se eliminan ni migran; solo cambia la escritura de nuevos avistamientos.
- Privacidad: se conservan las restricciones de acceso por propietario/reportante y no se agregan datos sensibles a notificaciones.
- Rollback: restaurar el fan-out anterior en la rama de implementación si fuera necesario, conservando los documentos legacy; no se requiere migración destructiva.
