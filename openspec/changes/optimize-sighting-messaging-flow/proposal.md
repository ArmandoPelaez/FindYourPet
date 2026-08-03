## Why

El flujo de avistamientos debe llevar al dueno de la mascota directamente a una conversacion util con quien reporto el avistamiento. Hoy el chat puede mostrar bloques genericos de sistema y avisos internos en lugar de la foto, ubicacion y detalles autorizados del avistamiento, lo que retrasa la respuesta en un momento sensible.

## What Changes

- Cuando el usuario B envia una alerta de avistamiento sobre la publicacion de mascota perdida de A, A recibe una notificacion de mensaje nuevo asociada a esa conversacion.
- Al abrir la notificacion, la lista de chats o el chat del avistamiento, A entra a una conversacion activa con B, vinculada a la mascota/publicacion correspondiente.
- El primer contenido visible del chat es la alerta de avistamiento enviada por B, con foto opcional, lugar de ubicacion autorizado, detalles adicionales, hora y referencia al avistamiento.
- Desde esa alerta inicial, el intercambio normal de mensajes queda habilitado entre A y B como chat privado participante-a-participante.
- El flujo de envio y recepcion de chats ya no muestra los bloques marcados en la captura: el aviso "Chat interno" ni los mensajes genericos "MENSAJE DEL SISTEMA / Nuevo avistamiento reportado. Abre el detalle...".
- Las notificaciones siguen minimizando informacion sensible fuera de la app: pueden avisar que hay un nuevo mensaje o avistamiento, pero no exponen foto, ubicacion precisa, detalles completos, telefono, email, direccion o cuerpo completo del mensaje.
- El cambio conserva los guardrails de privacidad: no agrega datos personales de contacto, no reabre contact sharing, no expone telefono/email/direccion del dueno o reportante, y no convierte coordenadas precisas en previews publicos o push.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `sightings`: el fan-out de un avistamiento aceptado debe producir una conversacion activa y un contenido inicial de alerta con los datos autorizados del avistamiento.
- `private-chat`: el chat derivado de avistamiento debe renderizar la alerta real como primer item conversacional y permitir mensajes normales entre A y B, sin bloques genericos de sistema ni aviso interno en el flujo.
- `notifications`: la notificacion para A debe comportarse como mensaje nuevo y navegar al chat correcto, manteniendo previews/payloads minimizados.
- `contact-privacy`: la UI de chat debe retirar la copia/advertencia marcada en la captura del flujo de mensajeria, sin relajar las reglas que impiden compartir datos personales gestionados por la app.
- `backend-data-model`: los documentos de chat/mensaje deben poder vincular de forma estable el mensaje inicial con el avistamiento y sus datos autorizados.
- `backend-access-rules`: las reglas deben validar que el mensaje inicial de avistamiento pertenece al chat, post, owner y reporter correctos, y que solo participantes autorizados pueden leerlo.

## Impact

- Android UI: pantallas de detalle/lista de chat, componentes de mensaje, routing desde notificaciones, previews de chat, y posible eliminacion de los componentes/textos genericos marcados en la captura.
- Dominio/datos: modelos de sighting, chat session, chat message, mappers, repositorios y ViewModels que construyen o consumen el mensaje inicial de avistamiento.
- Backend/security: batch o transaccion de avistamiento, chat, mensaje inicial y notificacion; validacion en Firestore rules; indices/consultas si se agrega un tipo de mensaje o referencia `sightingId`.
- Tests/validacion: pruebas unitarias de fan-out, reglas de acceso, rendering del chat, navegacion desde notificacion y ausencia de los textos marcados.
- Privacidad/seguridad: afecta datos sensibles de ubicacion, fotos, mensajes privados e historial de avistamientos; aplica el guardrail de no enviar datos sensibles completos en notificaciones push y de no exponer telefono/email/direccion.
- Usuarios existentes: las conversaciones de avistamiento pasan a mostrar la alerta real en el chat en vez de mensajes genericos; chats ya existentes deben renderizarse con fallback seguro si no tienen `sightingId` o snapshot de alerta.
- Rollback: revertir el render del item de alerta y la creacion del mensaje inicial al comportamiento anterior; los documentos nuevos deben ser ignorables por clientes anteriores mediante tipo de mensaje/fallback generico.
