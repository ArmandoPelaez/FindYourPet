## Why

La barra de navegación principal y el CTA `¡Lo he visto!` forman las acciones persistentes de la experiencia de publicaciones, pero actualmente pueden desplazarse junto con el contenido o dejar de estar disponibles al recorrer una pantalla larga. El usuario debe poder navegar y reportar un avistamiento sin perder esas acciones, independientemente de la cantidad de información visible.

## What Changes

- Mantener la barra de navegación principal fija respecto del viewport en las pantallas autenticadas donde se muestra.
- Separar la barra de navegación del contenido desplazable para que no se desplace con la publicación ni quede dentro del área scrollable.
- Eliminar el CTA sticky `¡Lo he visto!` y reemplazarlo por un botón inline `La vi` alineado a la derecha del nombre de la mascota.
- Mostrar el botón inline con el ojo cerrado inicialmente; al pulsarlo, cambiar el ojo a abierto y navegar al flujo existente de alerta de avistamiento.
- Mover la etiqueta de estado al extremo superior izquierdo de la foto y ajustar su tratamiento cromático al diseño de referencia.
- Adaptar el contenedor de la foto a una proporción visual consistente con la referencia.
- Mostrar debajo de la ubicación la fila `Última vez visto` con icono de calendario y fecha al lado.
- Reservar espacio inferior basado en los tokens existentes para que el último contenido siga siendo completamente visible y táctil.
- Respetar safe areas, área de gestos, Light Theme y Dark Theme sin cambiar los callbacks, destinos ni reglas de elegibilidad actuales.
- Conservar la identidad visual existente y evitar cambios en datos, ViewModels, repositorios, Firebase, Room o lógica de dominio.

## Navigation refinement

The global bar follows the reference order `Inicio`, `Perfil`, `Publicar`,
`Mensajes`, `Alertas`, with visible labels, active/inactive colors, and the
existing unread badge. Home no longer duplicates the alert action in its top
app bar; the existing Notifications destination is owned by `Alertas`.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `primary-navigation`: la barra inferior pasa a ser una superficie sticky persistente, separada del scroll de las pantallas autenticadas y sin alterar sus destinos.
- `home-feed-presentation`: la acción de avistamiento se presenta inline junto al nombre, la foto usa la proporción definida por el Design System y los metadatos de ubicación/fecha siguen la jerarquía de la referencia.

## Impact

- Código potencialmente afectado: `HomeScreen.kt`, `CommonComponents.kt`, tokens del Design System y pruebas de presentación relacionadas.
- No se esperan cambios de APIs, dependencias, backend, almacenamiento, permisos, autenticación ni navegación funcional.
- El cambio es visual y de composición de insets/scroll; debe usar Jetpack Compose, Material 3 estable y tokens del Design System.
- Los usuarios existentes conservan la misma acción, callback, ruta y reglas de elegibilidad para reportar avistamientos; solo cambia su ubicación y presentación.
- Rollback: restaurar la composición previa del shell y el posicionamiento del CTA, sin revertir cambios de datos ni lógica de negocio.
