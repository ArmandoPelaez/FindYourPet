## Why

Los avistamientos pueden incluir comentarios y fotografías aportados por terceros, pero el propietario solo puede consultarlos y no tiene mecanismos básicos para denunciar contenido o impedir nuevos avistamientos de un usuario problemático. SCRUM-25 agrega moderación mínima, persistente y separada de Chat sin borrar evidencia histórica.

## What Changes

- Agregar en Detalle de Avistamiento un menú contextual con `Reportar contenido` y `Bloquear usuario`.
- Permitir seleccionar un motivo, cancelar y confirmar un reporte asociado a `sightingId`, al autor del avistamiento y al usuario reportante.
- Persistir cada reporte con fecha y estado inicial pendiente, mostrar feedback y evitar envíos duplicados.
- Permitir confirmar el bloqueo del reportante desde el detalle y persistir una relación única `blockerUserId` + `blockedUserId`.
- Aplicar el bloqueo antes de aceptar nuevos avistamientos, impidiendo sighting, notificación y datos de Chat derivados.
- Conservar los avistamientos históricos y no implementar desbloqueo, panel administrativo, moderación automática ni suspensión de cuentas.
- Cubrir loading, success, error, accesibilidad y Light/Dark usando componentes y tokens existentes.

## Capabilities

### New Capabilities

- `content-moderation`: reportes de contenido y bloqueos de usuarios asociados a avistamientos, con persistencia y estados de operación.

### Modified Capabilities

- `sightings`: la creación de un nuevo avistamiento debe rechazar un par propietario/reportante bloqueado antes de cualquier persistencia o fan-out.
- `backend-access-rules`: las colecciones de moderación y la validación de nuevos avistamientos deben aplicar autorización por usuario y evitar que la UI sea el único control.

## Impact

- Android Compose: `SightingDetailScreen`, menú/dialogs, feedback y estados de operación.
- Estado y datos: ViewModel, repository, entidades Room, DAO, mappers y migración de base local si corresponde.
- Backend: nuevas colecciones/documentos de reportes y bloqueos, reglas Firestore y validación dentro del flujo de creación de avistamientos.
- Seguridad y privacidad: moderación solo para el propietario autorizado; no se exponen detalles internos del bloqueo ni excepciones de Firebase; Chat queda fuera del flujo.
- Usuarios existentes: podrán moderar nuevos avistamientos sin perder históricos; los usuarios bloqueados recibirán un rechazo controlado al intentar nuevos reportes sobre publicaciones del bloqueador.
- Dependencias/permisos: no se agregan dependencias ni permisos.
- Rollback: ocultar las acciones y deshabilitar nuevas escrituras de moderación, manteniendo intactos los avistamientos históricos; retirar la validación de bloqueo solo mediante una migración/regla explícita y coordinada.

Este change contribuye a los objetivos de privacidad, autorización real y MVP verificable. Respeta `docs/design-system.md`, Material 3 estable, tokens existentes y soporte Light/Dark.
