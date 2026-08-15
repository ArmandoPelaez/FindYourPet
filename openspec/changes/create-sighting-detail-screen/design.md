## Context

SCRUM-20 dejó los nuevos avistamientos como documentos canónicos en `sightings/{sightingId}` y las notificaciones conservan `sightingId` como destino lógico. La aplicación actual solo expone lecturas agrupadas por `postId` y la ruta de notificaciones todavía dirige a Chat; SCRUM-21 agrega la superficie de consulta que permitirá reemplazar ese destino en una task posterior.

La información de un avistamiento contiene datos sensibles: notas, fotografía, coordenadas y reporter. Firestore ya autoriza la lectura del documento únicamente al propietario o al reportante. La pantalla deberá conservar esa autorización y no convertir Room en autoridad productiva.

## Goals / Non-Goals

**Goals:**

- Exponer una pantalla Compose de solo lectura identificada por `sightingId`.
- Leer el `SightingAlertEntity` directamente desde el repositorio, con estado remoto/cache/error consistente con el resto de la app.
- Resolver el `postId` del avistamiento para mostrar la mascota asociada sin duplicar datos en el sighting.
- Renderizar ubicación, fecha/hora, notas y foto opcional solo cuando existan.
- Ofrecer una acción de ubicación en modo lectura mediante el mecanismo de mapas ya disponible, sin rediseñar Maps o Places.
- Mantener Light Theme, Dark Theme, Material 3 estable y tokens del Design System.

**Non-Goals:**

- No cambiar el click de las notificaciones; seguirá siendo una task posterior.
- No crear, leer ni modificar `ChatSessionEntity` o `ChatMessageEntity` para representar el detalle.
- No eliminar rutas, entidades, tablas ni documentos legacy de Chat.
- No modificar reglas de Firestore ni la autorización existente.
- No agregar una migración Room; cualquier cache nuevo debe reutilizar el esquema vigente o permanecer en memoria.

## Decisions

1. **Lectura directa por identificador.**
   - Agregar al repositorio un flujo `getSightingByIdState(sightingId)` que lea `sightings/{sightingId}` en Firestore y use una fuente local compatible como fallback/cache.
   - La pantalla y su estado usarán el identificador estable del documento, no `chatId`, `postId` como sustituto ni el contenido de una notificación.
   - Alternativa descartada: consultar todos los sightings del post y filtrar localmente, porque aumenta lecturas, complica loading/error y puede exponer datos no necesarios.

2. **Composición con la mascota asociada.**
   - Usar `sighting.postId` para cargar el `PetPostEntity` existente y mostrar nombre/foto/status solo como contexto visual.
   - El comentario, ubicación del avistamiento, coordenadas autorizadas y timestamp provendrán exclusivamente de `SightingAlertEntity`.
   - Alternativa descartada: copiar esos datos a `ChatMessageEntity` o crear un nuevo documento de detalle.

3. **Estado de pantalla separado de Chat.**
   - Crear un estado de detalle con loading, success, error y datos opcionales ausentes; la UI no observará `activeChatId`, `activeChatSession` ni `activeChatMessages`.
   - Un documento inexistente, no autorizado o fallido se mostrará como error de lectura y no como conversación vacía.

4. **Acción de ubicación sin exposición adicional.**
   - Mostrar `Ver ubicación` únicamente cuando el sighting tenga una ubicación válida y el usuario ya esté autorizado para leerlo.
   - Reutilizar la dependencia/mecanismo de mapas existente en modo lectura; no agregar Places, nuevos permisos ni una experiencia de selección de ubicación.

5. **Superficie visual tokenizada.**
   - Reutilizar componentes, colores, tipografía, formas, espaciado y estados existentes; cualquier pieza nueva seguirá `docs/design-system.md`.
   - No introducir valores `dp`, `sp`, colores o radios hardcodeados ni APIs alpha, beta o experimentales.

## Risks / Trade-offs

- [La navegación actual de Alertas todavía abre Chat] -> Se deja fuera de esta task y se conserva una ruta independiente lista para que una task posterior cambie `AppNotificationEntity.sightingId` a esta pantalla.
- [El documento contiene coordenadas sensibles] -> La lectura directa permanece protegida por las reglas actuales; la UI no añade esos datos a notificaciones ni a superficies públicas.
- [No existe todavía un visor de mapa de detalle claramente separado del selector] -> El implementador debe reutilizar el mecanismo disponible en modo lectura y documentar cualquier limitación sin ampliar el alcance a Maps/Places.
- [El post asociado puede no estar disponible aunque el sighting exista] -> El detalle debe conservar los datos del sighting y mostrar un estado contextual degradado para la mascota, sin fallar por datos opcionales.

## Migration Plan

1. Añadir el acceso directo al sighting y su estado de ViewModel.
2. Crear la pantalla y la ruta interna de detalle por `sightingId`, sin conectar todavía el click de notificaciones.
3. Agregar pruebas de repositorio/mapeo, estados y Compose para éxito, error, opcionales y ausencia de Chat.
4. Ejecutar OpenSpec, tests unitarios, build debug, `git diff --check` y validación manual autenticada.
5. Rollback: retirar la ruta, pantalla y acceso nuevo; no se requieren cambios de datos ni migraciones destructivas.

## Open Questions

- La task posterior que conectará las notificaciones deberá definir la transición exacta desde `NotificationsScreen` hacia la nueva ruta.
- Si el mecanismo actual de mapas no permite una vista de detalle sin selector, el implementador debe reportarlo antes de introducir una alternativa fuera del alcance.
