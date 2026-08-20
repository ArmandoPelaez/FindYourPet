## Context

`ProfileScreen` actualmente consume el listado general de publicaciones, muestra datos de cuenta y permite alternar una publicación entre `PERDIDO` y `REUNIDO`. El feed también consume publicaciones sin una regla explícita que excluya `REUNIDO`, mientras que Firestore ya permite la actualización únicamente al dueño y admite ese valor de estado.

El cambio debe preservar la navegación inferior, la identidad visual existente, el almacenamiento local/Firestore y la lógica de cierre de sesión. La autorización debe seguir basándose en el usuario autenticado y `ownerId`, nunca en nombres o ids hardcodeados.

## Goals / Non-Goals

**Goals:**

- Separar conceptualmente el listado público de descubrimiento del listado completo de publicaciones del dueño.
- Hacer que `REUNIDO` sea una transición terminal desde `PERDIDO`, con confirmación y manejo de errores.
- Excluir `REUNIDO` del feed y de las búsquedas públicas, pero conservarlo en las publicaciones propias.
- Simplificar la presentación del perfil usando los tokens y componentes Compose existentes en ambos temas.
- Mantener la autorización owner-only en cliente y reglas existentes de Firestore.

**Non-Goals:**

- Cambiar la navegación inferior, el flujo de creación, edición o eliminación de publicaciones.
- Introducir un nuevo estado, fecha de reencuentro, reactivación, permisos o dependencia.
- Exponer publicaciones `REUNIDO` a otros usuarios, aunque exista un filtro de estado en la UI.
- Rediseñar la identidad visual o modificar ViewModels/repositorios ajenos al flujo de perfil/feed.

## Decisions

### 1. Streams separados para descubrimiento y dueño

El repositorio/ViewModel mantendrá una fuente de publicaciones propias que incluya todos los estados del dueño y una fuente de descubrimiento que emita únicamente estados públicos (`PERDIDO` y los estados públicos ya soportados, como `AVISTADO`). El filtro se aplicará defensivamente antes de alimentar el feed y sus búsquedas, incluso si la consulta remota o el cache local devuelve documentos adicionales.

Se elige esta separación sobre reutilizar `allPosts` directamente en `ProfileScreen` porque permite conservar publicaciones `REUNIDO` para el dueño sin reintroducirlas en el feed público. La implementación puede usar una consulta remota por estados permitidos y un filtro local equivalente; cualquier fallo de consulta debe conservar la regla de visibilidad en la lista emitida.

### 2. Transición terminal y autorización

La acción de perfil solo solicitará `PERDIDO -> REUNIDO`. El ViewModel y la capa de datos rechazarán intentos de reactivar un `REUNIDO` o de cambiar otro estado mediante esta acción. La mutación conservará `ownerId`; las reglas Firestore existentes seguirán siendo la autoridad final para impedir cambios de no dueños.

Se elige reforzar la transición en más de una capa porque el estado se actualiza contra backend y cache local. La UI por sí sola no es suficiente para proteger una mutación sensible.

### 2.1 Limpieza en cascada de actividad y alertas

La operación de reunificación consultará los avistamientos por `postId` y las notificaciones del propietario relacionadas por `postId` o `sightingId`, y ejecutará su eliminación junto con la transición de estado. Room eliminará las mismas filas del cache local. Las reglas Firestore deberán permitir el borrado de avistamientos únicamente al dueño de la publicación y solo como parte de una publicación reunida; el borrado de notificaciones seguirá limitado al usuario propietario de su colección.

Se elige borrado físico porque la actividad y las alertas dejan de tener valor después del reencuentro y el requisito pide que no se conserven. La operación no debe mostrar éxito si la limpieza requerida falla; la UI debe conservar un estado de error y refrescar la fuente de verdad.

### 3. Confirmación antes de persistir

Al tocar `Marcar reunida`, el perfil mostrará un diálogo de confirmación que explique que la publicación dejará de ser visible públicamente y seguirá disponible para el dueño. Solo la confirmación ejecutará la mutación; cancelación o error no cambiarán el estado mostrado.

### 4. Presentación del perfil

`ProfileScreen` conservará avatar, nombre y rol `Colaborador`, eliminará email/tarjeta de comunidad/logout del encabezado y moverá `Cerrar sesión` al final. Las publicaciones propias serán tarjetas compactas sin foto; `PERDIDO` mostrará `Marcar reunida` y `REUNIDO` solo su estado. Se reutilizarán componentes, colores, tipografía, espaciado y formas del design system.

La pantalla no renderizará `AppBar`, `TopAppBar` ni el título `Perfil`; la card compacta del usuario será el primer elemento visual. Su contenedor reutilizará el mismo token semántico de superficie que usa la barra de navegación inferior, extrayendo o exponiendo el helper compartido si es necesario, sin duplicar color u opacidad.

Las cards de mascotas usarán el chip de estado existente (`PetStatusChip`/`PetStatusColors`) y una acción secundaria compacta para `Marcar reunida`, sin botones de tamaño primario ni valores visuales hardcodeados.

### 5. Compatibilidad y errores

Los errores de carga o actualización se expondrán mediante el estado existente del ViewModel o un estado equivalente ya utilizado por la pantalla. La lista propia debe seguir mostrando el estado confirmado por la fuente de verdad y no asumir éxito optimista si backend rechaza la operación.

## Risks / Trade-offs

- [Risk] Una consulta remota de estados públicos puede requerir un índice de Firestore → Mitigación: conservar filtro defensivo en memoria/cache y documentar el error sin mostrar `REUNIDO`; agregar índice solo si el entorno lo exige.
- [Risk] Un cache antiguo contiene publicaciones `REUNIDO` → Mitigación: aplicar el filtro de descubrimiento en cada emisión, no solo al persistir datos.
- [Risk] El diálogo o la mutación puede duplicarse por recomposición → Mitigación: disparar la operación desde una acción explícita y probar doble toque/estado de carga.
- [Risk] La UI puede ocultar accidentalmente publicaciones propias → Mitigación: probar explícitamente usuario dueño con estados `PERDIDO`, `REUNIDO` y error de backend.

## Migration Plan

1. Implementar streams/filtros, transición terminal, confirmación y presentación en la rama del change.
2. Ejecutar validación OpenSpec, tests unitarios y `assembleDebug`; realizar revisión manual en Light/Dark.
3. Desplegar sin migración de datos: `REUNIDO` ya es un valor soportado.
4. Para rollback, revertir el cambio de aplicación; no se requieren transformaciones de documentos. Los documentos ya marcados `REUNIDO` conservarán su estado.

## Open Questions

- Si el entorno Firestore requiere índice para la consulta de estados públicos, se resolverá durante la implementación usando el filtro defensivo existente y se documentará el resultado.
