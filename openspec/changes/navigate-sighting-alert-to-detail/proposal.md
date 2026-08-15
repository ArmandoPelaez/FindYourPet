## Why

Las alertas de avistamiento todavía pueden resolver su destino mediante identificadores legacy de Chat, aunque el producto ya dispone de una pantalla de Detalle de Avistamiento identificada por `sightingId`. Esto puede abrir una conversación incorrecta o fallar cuando la alerta no contiene datos de Chat válidos; SCRUM-22 requiere que la selección de una alerta lleve directamente al avistamiento correspondiente.

## What Changes

- Detectar las notificaciones de avistamiento al seleccionar un elemento de la bandeja de alertas.
- Validar que `sightingId` exista y no esté vacío antes de iniciar la navegación.
- Navegar directamente a `SightingDetailScreen` usando `sightingId` como identificador principal.
- Eliminar del flujo de nuevas alertas de avistamiento la resolución de destino mediante `chatId`, `targetId` legacy, `ChatSession` o `ChatMessage`.
- Mantener sin cambios el destino y comportamiento de otros tipos de notificación.
- Mantener el marcado de la notificación como leída según el flujo existente.
- Manejar alertas sin `sightingId` válido de forma segura, sin crash ni apertura de Chat, registrando información diagnóstica y mostrando el estado de error existente apropiado.
- Mantener compatibilidad controlada con alertas legacy sin reintroducir Chat como destino de nuevas alertas.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `notifications`: las alertas de avistamiento deben resolver su navegación mediante `sightingId`, conservar el estado leído y manejar identificadores inválidos sin crash.
- `sightings`: el propietario debe poder abrir el detalle del avistamiento seleccionado desde su alerta, sin pasar por una conversación.

## Impact

- Android: handler de selección de notificaciones, modelo/mapeo de `AppNotificationEntity` si fuera necesario, rutas de navegación y pruebas de routing/estado inválido.
- No se modifican la creación del avistamiento, el formulario, la pantalla `SightingDetailScreen`, la Bottom Navigation ni la generación de nuevas alertas.
- No se agregan permisos, dependencias, datos personales ni superficies de contacto.
- Las alertas existentes sin `sightingId` válido recibirán un manejo controlado; no se reconstruirá un `chatId` como fallback.
- Rollback: restaurar únicamente la resolución anterior del destino de la alerta, manteniendo la validación segura para datos inválidos.

El cambio aplica los guardrails de privacidad: no expone datos sensibles en la notificación, no habilita acceso a avistamientos no autorizados y conserva las reglas existentes de lectura y marcado.
