## Context

`SightingDetailScreen` ya recibe un `sightingId`, carga `SightingAlertEntity` y presenta información de la mascota, ubicación, foto y notas. El modelo de avistamiento ya contiene `ownerId` y `reporterId`, pero no existen entidades, tablas, colecciones ni reglas para reportes de moderación o bloqueos.

SCRUM-25 es un cambio cross-layer: agrega acciones de UI para el propietario, persistencia local/cache, escritura remota segura y una validación obligatoria en `PetRepository.submitSightingAlert` antes de subir medios o ejecutar el fan-out existente. La UI no puede ser el único control porque un cliente podría invocar directamente la creación de un avistamiento.

## Goals / Non-Goals

**Goals:**

- Mostrar las acciones solo al propietario de la publicación cuando el avistamiento identifica a un reportante.
- Persistir reportes de contenido y relaciones de bloqueo sin depender de Chat.
- Hacer que un bloqueo `ownerId → reporterId` impida nuevos avistamientos de ese reportante sobre publicaciones del propietario.
- Mantener intactos los avistamientos históricos y el fan-out normal de avistamientos no bloqueados.
- Aplicar autorización equivalente en repository/local y Firestore rules.
- Proveer estados de operación, feedback controlado, reintento seguro, accesibilidad y temas Light/Dark.

**Non-Goals:**

- No implementar panel administrativo, resolución de reportes, moderación automática, suspensión de cuentas o desbloqueo.
- No eliminar contenido, avistamientos históricos, Chat legacy, conversaciones ni mensajes.
- No cambiar la navegación hacia Detalle, la creación normal de alertas salvo la validación del bloqueo, ni el diseño general de Actividad.
- No exponer teléfono, email, dirección, coordenadas precisas ni excepciones internas de Firebase en la UI.

## Decisions

1. **Separar moderación de `SightingAlertEntity`.**

   Crear `ContentReportEntity` con `id`, `sightingId`, `reportedUserId`, `reportingUserId`, `reason`, `createdAt` y `status`, y `UserBlockEntity` con `id`, `blockerUserId`, `blockedUserId`, `sourceSightingId` y `createdAt`. El `sourceSightingId` permite demostrar que el bloqueo nació de un detalle autorizado sin alterar el contrato del avistamiento.

   Alternativa descartada: agregar flags `reported` o `blocked` a `SightingAlertEntity`. Esos flags mezclan evidencia inmutable con estado de moderación, no permiten múltiples reportes y complican la autorización por usuario.

2. **Usar documentos remotos con claves deterministas.**

   Persistir reportes en `contentReports/{reportId}` y bloqueos en `userBlocks/{blockerUserId}_{blockedUserId}`. El report id se deriva de `sightingId`, `reportingUserId` y una clave estable del motivo para que taps repetidos no creen duplicados del mismo reporte. El bloqueo usa la pareja de usuarios como identidad única.

   Alternativa descartada: guardar moderación dentro de `users/{userId}` o del documento de sighting. Las colecciones independientes permiten reglas de acceso más explícitas, no mutan evidencia histórica y mantienen el alcance de moderación separado.

3. **Autorizar acciones desde el vínculo propietario/reportante.**

   El cliente solo muestra acciones cuando el usuario autenticado coincide con `sighting.ownerId` y `sighting.reporterId` no está vacío. Las reglas Firestore deben verificar además que el `sightingId` exista, que su propietario sea el actor y que el reportante coincida con `reportedUserId`/`blockedUserId`. Los reportes y bloqueos no serán editables ni eliminables desde el cliente.

   Alternativa descartada: autorizar por la visibilidad general del sighting. El reportante puede leer su propio sighting, pero no debe poder moderarse ni bloquear a su propietario desde esa pantalla.

4. **Aplicar el bloqueo antes de cualquier efecto de creación.**

   `PetRepository.submitSightingAlert` consultará la relación `ownerId + reporterId` antes de subir la foto, crear `SightingAlertEntity`, notificación o registros relacionados con Chat. La misma condición se verificará en Firestore mediante la existencia del documento de bloqueo; el fallback Room consultará `UserBlockEntity`.

   Alternativa descartada: filtrar solo en `SightingAlertScreen` o después de crear el documento. Eso permitiría bypass directo y podría dejar un sighting o una notificación ya persistidos.

5. **Mantener operaciones de UI separadas y reintentables.**

   `PetViewModel` expondrá estados independientes para reportar y bloquear. El detalle usará un menú Material 3, selector de motivo y diálogo de confirmación. Mientras una operación está en curso se deshabilitarán acciones duplicadas; al éxito se mostrará feedback existente y al error se conservará el detalle y se permitirá reintentar sin mostrar excepciones internas.

   Alternativa descartada: navegar a otra pantalla de moderación o usar Chat como confirmación. El requisito pide acciones directas sobre el `sightingId` y conserva el flujo unidireccional.

6. **Migrar Room de forma compatible.**

   Incrementar la base local desde v8 a v9 con las dos tablas nuevas, índices/constraints necesarios para las claves deterministas y una migración no destructiva. Las colecciones remotas serán la fuente compartida cuando Firebase esté disponible; Room solo mantiene el comportamiento local/cache existente.

7. **Reutilizar tokens y componentes existentes.**

   El menú, dialogs, botones, estados y feedback usarán Material 3 estable, `AppSpacing`, `AppShapes`, `AppColors`/`MaterialTheme`, `AppTypography` y componentes existentes. No se introducirán colores, tamaños, radios, paddings ni APIs experimentales nuevos.

## Risks / Trade-offs

- [Una regla remota y el repository local divergen] → cubrir ambos caminos con tests estáticos/unitarios y pruebas de reglas; validar el bloqueo antes del upload.
- [Un usuario reintenta después de un timeout y crea duplicados] → ids deterministas, estado de operación y escrituras idempotentes para el mismo sighting/motivo o pareja de usuarios.
- [Un bloqueo antiguo afecta publicaciones nuevas del propietario] → es intencional: la relación es por propietario/reportante y no elimina históricos; se documenta en la UI con copy controlado.
- [El reportante no está disponible en datos legacy] → no mostrar `Bloquear usuario`; `Reportar contenido` solo se habilita si se puede asociar el sighting y actor autorizado.
- [Una migración Room falla en una base local existente] → migración incremental v8→v9, sin borrar tablas existentes y con pruebas de migración/DAO.
- [El menú o dialogs recortan contenido en tamaños compactos] → usar componentes Material 3 y tokens existentes, validar viewport compacto/alto y ambos temas.
- [El cambio se solapa con otros changes de navegación/detalle] → mantener la ruta y pantalla existentes, limitar el diff a moderación y no modificar Chat ni navegación no relacionada.

## Migration Plan

1. Agregar entidades, DAO, mappers, colecciones y migración Room sin alterar registros históricos.
2. Agregar repository/ViewModel para reportes, bloqueos y consulta previa al submit.
3. Agregar reglas Firestore para crear/leer moderación autorizada y denegar nuevos sightings bloqueados.
4. Agregar menú, selector de motivo, confirmación de bloqueo, estados, feedback y tests de Sighting Detail.
5. Ejecutar validación de OpenSpec, tests unitarios, pruebas de reglas y `assembleDebug`.
6. Validar manualmente reportar/cancelar, bloquear/cancelar, éxito/error, Light/Dark y rechazo de un nuevo avistamiento.

Rollback: retirar las acciones de UI y dejar de invocar operaciones de moderación, conservando las tablas/colecciones y avistamientos existentes. La eliminación de reglas de bloqueo requiere una decisión de seguridad explícita; no se borran relaciones ni históricos automáticamente.

## Open Questions

Ninguna para el alcance de SCRUM-25. Los textos definitivos de motivos y feedback deben seguir el copy vigente del proyecto, pero no cambian el contrato funcional.
