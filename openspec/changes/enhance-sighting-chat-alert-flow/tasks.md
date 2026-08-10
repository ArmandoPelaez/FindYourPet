## 1. Contrato de datos y compatibilidad

- [x] 1.1 Extender las entidades locales y remotas de chat para soportar `type = sighting_alert`, `sightingId`, `postId`, identidades inmutables, snapshot autorizado, ubicación conversacional y adjunto de foto opcional.
- [x] 1.2 Actualizar serializers, mappers y repositorios para mapear campos ausentes de mensajes heredados mediante un fallback seguro sin romper la carga del timeline.
- [x] 1.3 Agregar la migración o actualización de Room necesaria para los nuevos campos y verificar que conversaciones/textos existentes sigan siendo legibles.
- [x] 1.4 Definir la política de caché/media para el adjunto de avistamiento sin copiar datos sensibles fuera de los mecanismos existentes.

## 2. Fan-out de avistamiento

- [x] 2.1 Actualizar el caso de uso/repositorio de reporte para validar identidad autenticada, publicación, self-sighting, ubicación, notas y resultado de carga de foto antes de persistir.
- [x] 2.2 Implementar creación o reutilización idempotente de la conversación A/B por publicación, owner y reporter, agregando una alerta por cada avistamiento válido.
- [x] 2.3 Persistir en una operación atómica el avistamiento, sesión, `sighting_alert` y notificación del dueño, con una clave de idempotencia que evite duplicados por reintento.
- [x] 2.4 Mantener el preview de la conversación y el último mensaje minimizados, sin notas completas, foto, coordenadas precisas, teléfono, email o dirección.
- [x] 2.5 Garantizar que self-sighting, validación fallida o error de persistencia no creen registros parciales ni notifiquen al dueño.

## 3. Backend y autorización

- [x] 3.1 Actualizar `firestore.rules` para validar `sighting_alert` contra chat, sighting, post, owner, reporter y `senderId` autenticado en el batch inicial.
- [x] 3.2 Denegar en reglas payloads con teléfono, email, dirección, contact grants, flags públicos o coordenadas precisas dentro del snapshot de alerta.
- [x] 3.3 Asegurar lectura participante-a-participante del mensaje y avistamiento enlazado, y denegar lectura de no participantes.
- [x] 3.4 Mantener mensajes de alerta inmutables después de creados y denegar referencias cruzadas, auto-avistamientos y borrados.

## 4. Notificación y navegación de entrada

- [x] 4.1 Crear la notificación de A con texto genérico y referencias `chatId`, `sightingId` y `postId`, sin contenido sensible en registro, push o preview.
- [x] 4.2 Actualizar el deep link/router para resolver una notificación válida al `ChatDetailScreen` de la conversación autorizada.
- [x] 4.3 Marcar la notificación como leída al abrirla o mediante la acción existente, y manejar targets faltantes/no autorizados con un estado recuperable.

## 5. UI del chat

- [x] 5.1 Implementar el renderer `sighting_alert` como tarjeta/burbuja de timeline con contexto de mascota, fecha, ubicación autorizada y datos generales.
- [x] 5.2 Renderizar la foto como adjunto con estados de carga/error y fallback sin foto, evitando huecos de layout cuando no exista evidencia.
- [x] 5.3 Mantener el composer normal habilitado para A y B después de la alerta, sin mutar ni reemplazar el mensaje de avistamiento.
- [x] 5.4 Actualizar la lista de chats con preview minimizado y conservar renderer seguro para mensajes legacy.
- [x] 5.5 Retirar del nuevo flujo cualquier bloque genérico de sistema que sustituya la información real del avistamiento, sin eliminar compatibilidad de lectura histórica.

## 6. Confirmación y retorno al Home

- [x] 6.1 Exponer estados de envío del formulario/confirmación para diferenciar idle, envío, éxito y error sin permitir doble submit.
- [x] 6.2 Navegar a Home una sola vez después de confirmar el commit exitoso y limpiar la pila del formulario según el patrón de navegación existente.
- [x] 6.3 Mantener la vista de confirmación y sus datos reintentables ante validación, upload o backend error; no navegar en caso de fallo.

## 7. Pruebas automatizadas

- [x] 7.1 Agregar pruebas de entidad/mapper/serialización para `sighting_alert`, adjunto opcional, snapshot autorizado y fallback legacy.
- [x] 7.2 Agregar pruebas de repositorio/fan-out para camino feliz, conversación reutilizada, múltiples avistamientos, idempotencia, self-sighting y fallo sin registros parciales.
- [x] 7.3 Actualizar pruebas de reglas para sender spoofing, ids cruzados, campos de contacto prohibidos, no participante, update/delete y batch atómico.
- [x] 7.4 Agregar pruebas de notificación y routing para payload minimizado, navegación al chat, lectura y target inválido.
- [x] 7.5 Agregar pruebas Compose/UI para alerta con foto, sin foto, ubicación/detalles, composer activo, renderer legacy y ausencia de contenido sensible en previews.
- [x] 7.6 Agregar pruebas de navegación/ViewModel para retorno único a Home después de éxito y permanencia en confirmación ante error o doble toque.

## 8. Validación y entrega

- [x] 8.1 Ejecutar `openspec status --change "enhance-sighting-chat-alert-flow"` y confirmar que todos los artefactos están completos.
- [x] 8.2 Ejecutar `./gradlew test` o `\.\gradlew.bat test` y resolver regresiones.
- [x] 8.3 Ejecutar `./gradlew assembleDebug` o `\.\gradlew.bat assembleDebug` y verificar el build Android.
- [x] 8.4 Validar manualmente B reporta con foto, ubicación y detalles; el fan-out finaliza; A recibe notificación; A abre el chat y ve el mensaje enriquecido; ambos continúan conversando.
- [x] 8.5 Validar manualmente reporte sin foto, error de upload/backend, reintento y doble confirmación; confirmar que solo el éxito lleva al Home y no duplica el reporte.
- [x] 8.6 Validar manualmente que no participantes no acceden a la alerta y que notificaciones/previews no muestran foto, notas completas, coordenadas precisas, teléfono, email ni dirección.
- [x] 8.7 Revisar que no se agregaron permisos Android nuevos ni se reintrodujo lógica de identidad demo para autorizar chats o avistamientos.
