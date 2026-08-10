## Why

Cuando una persona encuentra una mascota y reporta un avistamiento, el dueño necesita recibir la información accionable sin reconstruir el contexto desde varias pantallas. El flujo debe convertir cada alerta en el inicio de una conversación clara entre ambas personas y cerrar el formulario de confirmación llevando al usuario de vuelta al Home para continuar usando la aplicación.

## What Changes

- Cada avistamiento válido enviado por el usuario B sobre una publicación del usuario A crea o reutiliza una conversación privada entre A y B.
- El usuario A recibe una notificación privada, persistente y vinculada a la conversación y al avistamiento.
- El primer elemento visible de la conversación es un mensaje de alerta de avistamiento con la foto opcional como adjunto/preview, la ubicación autorizada, los datos generales aportados por B, la fecha y el contexto de la mascota/publicación.
- La alerta se renderiza como contenido conversacional accionable, no como un bloque genérico de sistema, y permite continuar con mensajes normales en el mismo chat.
- La vista de confirmación del avistamiento navega automáticamente al Home después de confirmar exitosamente; ante error permanece en la vista y permite reintentar sin duplicar el reporte.
- Las notificaciones, previews externos y lista de chats usan texto minimizado y no incluyen notas completas, foto, coordenadas precisas, teléfono, email ni dirección.
- Los datos del avistamiento y del mensaje permanecen disponibles únicamente para los participantes autorizados, respetando las reglas actuales de privacidad y autenticación.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `sightings`: cada avistamiento aceptado debe fan-out a una alerta conversacional y la confirmación debe finalizar en Home.
- `private-chat`: el chat debe mostrar la alerta con sus datos autorizados y adjunto opcional como primer mensaje, manteniendo el intercambio participante-a-participante.
- `notifications`: el dueño debe recibir una notificación vinculada al chat y al avistamiento, con preview minimizado y navegación al chat correcto.
- `backend-data-model`: el contrato de mensajes debe soportar el tipo de alerta, su referencia al avistamiento y un snapshot autorizado de render.
- `backend-access-rules`: las lecturas y escrituras del mensaje de alerta deben validar participantes, identidad, publicación y avistamiento relacionados.
- `primary-navigation`: el resultado exitoso de confirmar un avistamiento debe restablecer la navegación al destino Home sin dejar al usuario en el formulario.
- `contact-privacy`: foto, ubicación, notas y datos de contacto deben seguir clasificados y protegidos; ningún dato personal debe filtrarse por notificaciones o previews.

## Impact

- Android/Compose: formulario y estado de confirmación de avistamiento, navegación al Home, lista/detalle de chats, componente de mensajes con adjunto de foto, ubicación y detalles, y manejo de estados de carga/error.
- Datos locales y remotos: entidades, mappers, repositorios, sincronización y migraciones compatibles para mensajes de tipo `sighting_alert`, referencias a `sightingId` y snapshots autorizados.
- Backend: fan-out atómico de avistamiento, conversación, mensaje inicial y notificación; reglas de acceso para impedir spoofing, auto-avistamientos y acceso de no participantes.
- Notificaciones: payload y texto genéricos con deep link o ruta segura al chat, sin contenido sensible completo.
- Usuarios existentes: mensajes heredados y chats sin referencia a un avistamiento deben seguir renderizando con fallback seguro; nuevos reportes adoptan el formato de alerta enriquecida.
- Privacidad y seguridad: se afectan fotos, ubicación, mensajes privados e historial de avistamientos. No se agregan permisos Android ni se habilita compartir teléfono, email, dirección o coordenadas precisas.
- Rollback: conservar compatibilidad de lectura con mensajes existentes, desactivar la creación/render de `sighting_alert` y volver al fan-out anterior si fuera necesario; la navegación de confirmación puede volver al comportamiento previo sin borrar reportes ya aceptados.

