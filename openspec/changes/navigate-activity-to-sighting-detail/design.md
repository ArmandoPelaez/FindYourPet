## Context

La pantalla `ActivityScreen` ya muestra avistamientos recibidos del usuario autenticado y cada `SightingAlertEntity` conserva su `id`. `MainActivity` ya define la ruta `sighting/{sightingId}`, `SightingDetailScreen` ya carga el detalle mediante `PetViewModel` y el estado de detalle es independiente de Chat.

El cambio se limita a conectar ambos puntos. No requiere cambios de backend, Room, Firestore, entidades, generación de alertas ni contenido de la pantalla de detalle. La interacción visual debe seguir `docs/design-system.md`, Material 3 estable y los tokens existentes en Light y Dark Theme.

## Goals / Non-Goals

**Goals:**

- Exponer un callback de selección desde cada item de Actividad.
- Validar el `sightingId` en el límite de navegación antes de construir la ruta.
- Navegar a la ruta existente `sighting/{sightingId}` y reutilizar `SightingDetailScreen`.
- Mantener la lista de Actividad en el back stack para que Back regrese a ella sin crear otra instancia.
- Mantener los items accesibles, con touch target y pressed state de Material 3, sin valores visuales nuevos hardcodeados.
- Registrar el identificador/tipo de item cuando la navegación sea ignorada por un id inválido.

**Non-Goals:**

- No crear otra pantalla, ViewModel, repository o consulta de detalle.
- No modificar la navegación existente desde Alertas.
- No usar `chatId`, `conversationId`, `messageId`, `targetId`, `ChatSessionEntity`, `ChatMessageEntity` ni `ChatScreen` para resolver el destino.
- No cambiar la lista, su orden, sus datos, su estado de carga/error/vacío o los demás destinos principales.
- No agregar backend, permisos, dependencias ni migraciones de almacenamiento.

## Decisions

1. **El item comunica una intención de selección, no una ruta.**

   `ActivityScreen` recibirá una función `onSightingClick` y la pasará al item junto con el `sighting.id`. El item usará el comportamiento clickable estable del componente Material 3 existente, conservando `Card`, `AppShapes.content`, `AppSpacing` y colores del tema.

   Alternativa descartada: hacer que `ActivityScreen` conozca `NavController` o construya strings de rutas. Eso acoplaría la pantalla a la navegación y dificultaría probarla de forma aislada.

2. **La validación y construcción de ruta viven en `MainActivity`.**

   El callback de la ruta `ROUTE_ACTIVITY` recibirá el id, aplicará `trim()`, rechazará valores vacíos y solo entonces llamará a `sightingDetailRoute`. Los ids inválidos se registrarán con el logger existente y no producirán navegación.

   Alternativa descartada: permitir que la ruta vacía llegue a `SightingDetailScreen`. El repository exige un identificador no vacío y esa estrategia convertiría un dato de UI inválido en un error de carga.

3. **Se reutiliza la ruta de detalle existente y el back stack estándar.**

   La selección navegará a `sighting/{sightingId}` sin reemplazar `ROUTE_ACTIVITY`. Back ejecutará el `popBackStack` ya usado por `SightingDetailScreen`, devolviendo al elemento Activity previo. Se usará `launchSingleTop` únicamente si es necesario para evitar una duplicación accidental del mismo detalle por taps repetidos; no se hará `popUpTo` de Actividad.

   Alternativa descartada: navegar al detalle con una nueva instancia de Actividad o crear una ruta específica para Actividad. Eso duplicaría la pantalla y rompería la convergencia exigida con Alertas.

4. **La independencia de Chat se protege con contratos.**

   Las pruebas verificarán que el callback usa `sightingId`, que la ruta de detalle se conserva y que la implementación de Activity no introduce símbolos de Chat. Los tests existentes de Activity, routing de notificaciones y navegación primaria deben seguir pasando.

5. **La interacción visual reutiliza Material 3 y tokens.**

   No se agregarán colores, tamaños, paddings ni radios específicos. La semántica de selección provendrá del componente clickable/Card y el contenido descriptivo existente seguirá identificando la mascota o el avistamiento. Se cubrirán Light/Dark y el estado pressed cuando el arnés actual lo permita.

## Risks / Trade-offs

- [Un item contiene un id vacío por datos corruptos o legado] → se ignora de forma segura, se registra diagnóstico y no se abre Chat ni detalle inválido.
- [Taps repetidos generan múltiples detalles] → se usa navegación single-top para el mismo destino y se conserva una sola Activity en el back stack.
- [El detalle no existe o no está autorizado] → se reutilizan los estados loading/error/empty ya implementados por `SightingDetailScreen`; no se agrega una consulta alternativa.
- [Cambio visual inconsistente entre temas o tamaños] → se conservan `Card`, `AppShapes`, `AppSpacing`, `MaterialTheme` y los componentes existentes; se validan variantes Light/Dark.
- [El cambio se solapa con navegación de Alertas] → solo se conecta Activity con la ruta ya existente; el callback y resolver de Alertas quedan sin modificar.

## Migration Plan

1. Añadir el callback de selección en `ActivityScreen` y conectarlo al item.
2. Conectar el callback de `ROUTE_ACTIVITY` en `MainActivity` con validación y ruta de detalle.
3. Añadir o ampliar pruebas de selección, ruta, back stack, invalidación y ausencia de Chat.
4. Ejecutar validación OpenSpec, tests unitarios y `assembleDebug`.

Rollback: quitar el callback, la navegación desde Activity y sus pruebas; no requiere migrar ni revertir datos persistidos.

## Open Questions

Ninguna. Jira define el flujo, el identificador y el alcance; la ruta y la pantalla de detalle ya existen.
