## Context

FindYourPet ya define que un avistamiento aceptado crea o reutiliza una sesion de chat entre el dueno del post y el reportante, junto con un mensaje inicial y una notificacion. El problema a resolver esta en la experiencia y el contrato del mensaje inicial: el dueno no debe encontrar tarjetas genericas de "MENSAJE DEL SISTEMA" ni el bloque de aviso "Chat interno"; debe encontrar la alerta enviada por el reportante con la evidencia autorizada y el canal de chat listo para continuar.

El cambio toca datos sensibles: foto de avistamiento, lugar/ubicacion, detalles privados, mensajes privados e historial de avistamientos. Por eso el contenido completo se muestra solo dentro del chat participante-a-participante o pantallas autorizadas; las notificaciones y previews externos siguen siendo minimizados.

Este cambio debe coordinarse con `remove-personal-data-sharing`: no debe reintroducir contact grants, telefono, email, direccion del dueno/reportante ni acciones externas de contacto.

## Goals / Non-Goals

**Goals:**

- Crear o reutilizar una conversacion activa A/B cuando B envia un avistamiento valido sobre una publicacion de A.
- Notificar a A como nuevo mensaje/avistamiento y navegar al chat correcto.
- Mostrar dentro del chat una alerta inicial de avistamiento con foto opcional, ubicacion autorizada y detalles escritos por B.
- Habilitar mensajes normales entre A y B inmediatamente despues de la alerta inicial.
- Eliminar del flujo de chat el aviso "Chat interno" y las tarjetas genericas "MENSAJE DEL SISTEMA / Nuevo avistamiento reportado...".
- Mantener previews, push payloads y chat-list snippets sin datos sensibles completos.

**Non-Goals:**

- No agregar compartir telefono, email, direccion, coordenadas como contacto, ni controles de contacto externo.
- No cambiar el formulario de avistamiento salvo lo necesario para transportar los datos ya validados al chat.
- No prometer cifrado extremo a extremo ni nueva infraestructura de privacidad.
- No permitir que usuarios no participantes lean avistamientos, mensajes o evidencia.
- No resolver conversaciones grupales, moderacion o edicion/borrado de mensajes.

## Decisions

1. Representar la alerta inicial como un mensaje de chat de tipo `sighting_alert`.

   El batch/transaccion de avistamiento crea o reutiliza `chatSession`, crea el `sighting` y agrega un `chatMessage` inicial con `type = sighting_alert`, `sightingId`, `postId`, `ownerId`, `reporterId`, `senderId = reporterId`, `createdAt` y un snapshot autorizado para render rapido.

   Alternativa considerada: mantener un system message generico y abrir un detalle separado. Se descarta porque reproduce el problema de la captura y obliga al dueno a navegar antes de ver informacion accionable.

2. El snapshot del mensaje inicial contiene solo datos autorizados del avistamiento.

   El item puede incluir `photoUrl`/metadata si existe, `locationDisplay`, detalles/notas, timestamp y estado de disponibilidad. No debe incluir telefono, email, direccion de contacto del dueno/reportante, ni coordenadas exactas en previews o notificaciones. Si la app tiene una pantalla autorizada de mapa/detalle, el mensaje puede enlazar al `sightingId` para abrirla, pero el bubble principal debe ser suficiente para entender el reporte.

   Alternativa considerada: duplicar todos los campos del avistamiento dentro del mensaje. Se evita porque aumenta riesgo de datos obsoletos y exposicion innecesaria; el snapshot es para UX y el `sightingId` sigue siendo la referencia canonica.

3. La UI trata `sighting_alert` como contenido conversacional, no como aviso del sistema.

   En `ChatDetailScreen`, el primer item visible debe renderizar una tarjeta/burbuja de avistamiento dentro del timeline con foto opcional, ubicacion y detalles. El header puede seguir contextualizando la mascota y la contraparte, pero no debe mostrar el bloque de texto "Chat interno" ni la advertencia larga marcada en la captura. La lista de chats debe usar un preview minimizado, por ejemplo "Nuevo avistamiento de Poppy".

   Alternativa considerada: conservar el aviso de seguridad encima del chat. Se descarta por requerimiento de producto; las protecciones se mantienen en reglas, validaciones y politicas fuera del flujo inmediato de mensajeria.

4. La notificacion de A apunta al chat y usa contenido minimizado.

   El fan-out crea una notificacion para A con `recipientId = ownerId`, `chatId`, `sightingId`, `postId`, `type` compatible con nuevo mensaje/avistamiento y texto generico. Al tocarla, la app navega al chat A/B y marca la notificacion como leida cuando corresponda.

   Alternativa considerada: notificar al detalle del avistamiento. Se descarta porque el flujo requerido es entrar al chat activo y continuar la conversacion con B.

5. Las reglas de backend validan el acoplamiento sighting-chat-message.

   Firestore rules deben aceptar el mensaje `sighting_alert` solo cuando el chat contiene exactamente a `ownerId` y `reporterId`, el `senderId` coincide con el usuario autenticado reportante, el `sightingId` pertenece al mismo `postId`, `ownerId` y `reporterId`, y la escritura ocurre en el fan-out autorizado. Solo participantes pueden leer el mensaje y el sighting asociado.

   Alternativa considerada: confiar en la UI para ocultar o construir el mensaje. Se descarta porque el mensaje contiene datos sensibles y debe estar autorizado por backend.

## Risks / Trade-offs

- [Duplicacion parcial de datos sensibles] -> Mantener `sightingId` como fuente canonica y limitar el snapshot a campos necesarios para render autorizado.
- [Clientes antiguos muestran tipo desconocido] -> Renderizar fallback seguro para tipos no reconocidos y previews genericos.
- [Reglas complejas para batch atomico] -> Cubrir con tests de reglas para camino feliz, sender mismatch, owner/reporter incorrectos, sighting inexistente y no participante.
- [La eliminacion del aviso reduce copy educativo dentro del chat] -> Mantener privacidad por diseno tecnico y mover cualquier educacion de seguridad fuera del flujo de envio/recepcion de mensajes.
- [Notificaciones podrian filtrar detalles] -> Generar texto/payload generico y cargar el detalle solo despues de autenticacion dentro del chat.

## Migration Plan

1. Agregar soporte de modelo para `sighting_alert` con `sightingId` y snapshot autorizado, manteniendo fallback para mensajes existentes.
2. Actualizar el fan-out de avistamiento para crear la sesion, alerta inicial y notificacion en una operacion atomica.
3. Actualizar UI de chat y lista de chats para renderizar la alerta real y retirar los bloques/textos marcados.
4. Ajustar reglas de backend y pruebas para proteger lectura/escritura del nuevo tipo de mensaje.
5. Validar manualmente el flujo B envia avistamiento -> A recibe notificacion -> A abre chat -> ve alerta -> A/B conversan.

Rollback: mantener el tipo de mensaje ignorado por clientes anteriores y revertir el render/fan-out a mensaje generico si es necesario. Antes de rollback de backend, confirmar que los documentos `sighting_alert` existentes no rompen previews ni timelines.

## Open Questions

- Debe mostrarse `locationDisplay` como texto libre capturado por B, como etiqueta normalizada, o ambos cuando existan?
- La notificacion visible debe decir "Nuevo mensaje" o "Nuevo avistamiento" mientras internamente navega al chat?
- Si B envia multiples avistamientos sobre la misma mascota, deben aparecer como multiples alertas dentro del mismo chat o solo actualizar/crear una conversacion por par A/B/post?
