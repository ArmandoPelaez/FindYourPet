## Why

La pantalla de perfil mezcla información de cuenta, colaboración y acciones que no ayudan a gestionar las publicaciones propias. Además, marcar una mascota como reencontrada actualmente permite reabrirla y no establece de forma consistente que deje de aparecer en los espacios públicos. SCRUM-49 simplifica el perfil y define `REUNIDO` como un estado terminal, manteniendo la publicación visible para su dueño.

## What Changes

- Simplificar el perfil para conservar avatar, nombre y rol, eliminar email, tarjeta de comunidad y el acceso de cierre de sesión del encabezado.
- Mostrar las publicaciones propias como tarjetas compactas sin foto, con acción `Marcar reunida` solo para publicaciones `PERDIDO`.
- Solicitar confirmación antes de cambiar una publicación propia de `PERDIDO` a `REUNIDO` y no ofrecer reactivación posterior.
- Mantener las publicaciones `PERDIDO` en el feed público y ocultar las `REUNIDO` para otros usuarios y búsquedas.
- Mantener las publicaciones `REUNIDO` visibles para el dueño dentro de `Perfil → Mis publicaciones`.
- Mover `Cerrar sesión` al final del perfil preservando la lógica existente.
- Cambiar el texto `Mis Mascotas Publicadas` por `Mis publicaciones`.
- Al confirmar `REUNIDO`, eliminar físicamente todos los avistamientos de la publicación y sus alertas/notificaciones relacionadas del backend y del cache local.
- Mantener sin cambios la navegación inferior, el flujo de creación, la eliminación/edición de publicaciones y el estado `OCULTO`.

## Capabilities

### New Capabilities

- Ninguna.

### Modified Capabilities

- `user-profile`: define la presentación simplificada del perfil, la gestión de publicaciones propias, el cierre de sesión al final y la limpieza de actividad/alertas al reunir.
- `pet-posts`: define la transición terminal a `REUNIDO`, la autorización del dueño, la visibilidad pública de publicaciones reunidas y la limpieza en cascada.
- `home-feed-presentation`: excluye publicaciones `REUNIDO` del feed y de las búsquedas públicas, conservando sus reglas de acciones visibles.

## Impact

- Afecta `ProfileScreen`, el estado/filtrado del feed, `PetViewModel`, `PetRepository`, reglas Firestore, Room/DAO y las pruebas asociadas.
- Reutiliza el campo de estado existente `REUNIDO` y la autorización del dueño; no requiere nuevas dependencias ni cambios de navegación.
- El cambio afecta a usuarios existentes solo en la presentación del perfil y en la visibilidad pública de publicaciones ya marcadas como reunidas.
- La reversión de código consiste en retirar el filtro y restaurar la presentación/acción anterior; los registros eliminados por la nueva operación no son recuperables desde la aplicación.
- Aplican los guardrails de identidad autenticada, autorización owner-only, privacidad de publicaciones y validación con tests/build.
