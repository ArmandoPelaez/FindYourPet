## Context

FindYourPet ya tiene conceptos de avistamiento, chat privado y notificaciones, pero el resultado visible del reporte no lleva toda la información al canal donde A y B pueden colaborar. El diseño debe funcionar para el backend objetivo de Firebase/Firestore y conservar compatibilidad con el prototipo local y con mensajes ya persistidos.

El flujo involucra cuatro momentos: B completa y confirma el reporte, el backend hace el fan-out, A recibe una notificación y A abre una conversación cuyo primer elemento es la alerta enriquecida. La confirmación exitosa del formulario termina el flujo en Home; los errores no deben abandonar la pantalla ni crear reportes duplicados.

## Goals / Non-Goals

**Goals:**

- Crear una alerta conversacional idempotente y vinculada a un avistamiento, una publicación y una conversación A/B.
- Mostrar dentro del chat foto opcional, ubicación autorizada y datos generales del reporte con un componente legible como mensaje/adjunto.
- Mantener las notificaciones mínimas y dirigirlas al chat correcto.
- Permitir mensajes normales antes y después de la alerta inicial.
- Regresar al Home solamente después de confirmar que el fan-out terminó correctamente.
- Mantener autorización por Firebase `uid`, acceso solo de participantes y compatibilidad con datos heredados.

**Non-Goals:**

- No crear chats grupales ni compartir teléfono, email, domicilio o coordenadas precisas.
- No agregar un nuevo proveedor de notificaciones, almacenamiento de fotos o mapas.
- No convertir el snapshot del mensaje en la fuente canónica del avistamiento.
- No modificar avistamientos existentes ni permitir edición/borrado posterior al envío.

## Decisions

1. **Representar el aviso como `sighting_alert` dentro de `chatMessages`.**

   El fan-out crea o reutiliza una sesión determinista para `postId + ownerId + reporterId` y agrega un mensaje con `type = sighting_alert`, `sightingId`, `postId`, `ownerId`, `reporterId`, `senderId = reporterId`, `createdAt` y un snapshot autorizado. El snapshot contiene `photoAttachment` opcional, `locationDisplay` de uso conversacional, detalles generales, timestamp y referencia de mascota; los valores precisos permanecen en el documento de avistamiento protegido.

   Se descarta conservar un mensaje de sistema genérico y abrir un detalle separado: hace que A pierda el contexto y no cumple que la información aparezca en la conversación.

2. **Usar una tarjeta/burbuja de mensaje con adjunto opcional.**

   El timeline tratará `sighting_alert` como una entrada conversacional diferenciada visualmente, pero no como un aviso técnico. La tarjeta mostrará primero la foto si existe, luego ubicación autorizada, datos generales, fecha y contexto de la mascota. La foto se cargará como adjunto con estado de carga, error y alternativa sin foto; nunca se dejará un hueco vacío ni se bloqueará el texto.

   El componente no mostrará teléfono, email, domicilio ni coordenadas precisas. Si se requiere más detalle, el vínculo `sightingId` abrirá una superficie autorizada para participantes.

3. **Hacer el fan-out atómico e idempotente.**

   La operación válida debe persistir el avistamiento, la sesión, el mensaje `sighting_alert` y la notificación de A en una transacción/batch. Un identificador determinista del reporte o una condición de existencia evita duplicar mensaje y notificación cuando B reintenta por timeout. Un avistamiento distinto sobre la misma publicación agrega otra alerta al chat existente.

   La validación de identidad, publicación, campos obligatorios, foto y ubicación ocurre antes del fan-out. Un self-sighting o una validación fallida no crea ninguno de los cuatro registros.

4. **Navegar al Home solo después del resultado exitoso.**

   El ViewModel expondrá estados `Idle`, `Submitting`, `Success` y `Error` (o equivalentes existentes). La pantalla consume `Success` una sola vez y realiza una navegación que limpia el formulario y destinos intermedios hasta Home. En `Error`, conserva los datos editados, muestra un mensaje reintentable y no navega.

   Se descarta navegar inmediatamente al abrir la confirmación porque ocultaría fallos de red y podría hacer creer al usuario que B fue notificado.

5. **Minimizar notificaciones y previews.**

   La notificación guardará `recipientId`, `chatId`, `sightingId`, `postId`, tipo y texto genérico como “Nuevo avistamiento”. El deep link/routing resolverá el chat después de autenticar al usuario. El preview de la lista de chats también será genérico y no copiará notas, ubicación precisa ni contenido de foto.

6. **Mantener autorización en backend, no en la UI.**

   Las reglas validarán que el mensaje `sighting_alert` pertenece al chat indicado, que `senderId` es el reporter autenticado, que `sightingId` coincide con post/owner/reporter y que ambos usuarios son participantes. Solo participantes pueden leer el mensaje y el avistamiento. La UI puede ocultar acciones, pero no es el control de seguridad.

7. **Mantener fallback para datos heredados.**

   Los mensajes de texto y mensajes de sistema ya existentes se seguirán mapeando. Si falta `type`, `sightingId` o snapshot, el cliente usará el renderer legado seguro. Un cliente anterior que desconozca `sighting_alert` debe mostrar un fallback genérico sin romper el timeline.

## Risks / Trade-offs

- [El snapshot puede quedar desactualizado] → Mantener `sightingId` como fuente canónica, guardar solo datos de render necesarios y permitir abrir el detalle autorizado.
- [Una foto sensible puede quedar disponible en caché] → Reutilizar el mecanismo existente de media privada, no copiar archivos adicionales y limpiar/invalidar caché según la política actual.
- [Duplicación por reintentos o doble toque] → Deshabilitar el CTA durante `Submitting` y aplicar idempotencia en repositorio y reglas/batch.
- [La navegación puede perder el estado de error] → Emitir el evento de éxito solo al confirmar el commit y mantener `Error` en la pantalla hasta que el usuario reintente o cancele.
- [Reglas Firestore más complejas] → Cubrir camino feliz, ids cruzados, sender spoofing, self-sighting, no participante y campos prohibidos con tests de reglas.
- [Usuarios esperan ver más precisión de ubicación] → Mostrar una etiqueta autorizada/coarse dentro del chat y reservar la precisión para la superficie protegida que corresponda.

## Migration Plan

1. Ampliar entidades, mappers y serialización con `sighting_alert` y fallback de mensajes legacy.
2. Implementar el fan-out atómico/idempotente y la notificación enlazada al chat.
3. Actualizar reglas de acceso y pruebas de autorización antes de activar la escritura del nuevo tipo.
4. Incorporar el renderer de alerta, estados de adjunto, input normal y previews minimizados.
5. Conectar el estado de éxito del formulario con la navegación a Home y verificar que los errores no navegan.
6. Activar por defecto el nuevo flujo; los datos antiguos se leen con fallback y no requieren migración destructiva.

Rollback: detener la escritura de `sighting_alert`, conservar la lectura tolerante y volver al renderer/fan-out anterior. No borrar mensajes nuevos ni avistamientos ya aceptados; el rollback debe seguir permitiendo que clientes compatibles los lean de forma segura.

## Open Questions

No quedan decisiones de producto bloqueantes para implementar: se usará una alerta por cada avistamiento enviado, un chat reutilizado por pareja A/B y publicación, ubicación conversacional no precisa y retorno a Home únicamente tras éxito confirmado. Las decisiones visuales menores del adjunto pueden resolverse dentro del sistema de componentes existente.

