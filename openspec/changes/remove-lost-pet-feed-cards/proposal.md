## Why

La pantalla principal presenta cada publicación de mascota perdida como una card flotante, separando visualmente el contenido del fondo y generando una relación incómoda con la barra de navegación inferior. SCRUM-5 solicita una superficie continua que mejore la lectura y el uso del espacio sin alterar la información ni el comportamiento funcional de las publicaciones.

## What Changes

- Reemplazar la presentación flotante de cada publicación del feed por contenido continuo integrado al fondo principal.
- Eliminar de la superficie de publicación los márgenes externos, la elevación/sombra y las esquinas redondeadas propias de la card contenedora.
- Mantener la jerarquía existente de imagen, estado, nombre, ubicación, información, fecha y acciones.
- Permitir que el contenido desplazable continúe visualmente bajo la barra de navegación inferior, conservando el área segura necesaria para que el último contenido sea visible y accionable.
- Conservar los filtros, paginación horizontal, carga de imágenes, acciones de avistamiento y compartir, y estados de carga/vacío.
- Mantener compatibilidad con Light Theme y Dark Theme usando los tokens existentes del Design System.
- No modificar lógica de negocio, datos mostrados, backend, permisos, autenticación, navegación funcional ni contratos de almacenamiento.

## Capabilities

### New Capabilities

- Ninguna.

### Modified Capabilities

- `home-feed-presentation`: cambia el requisito de presentar cada publicación como una card flotante por una superficie continua sin contenedor elevado ni esquinas externas, manteniendo la jerarquía y acciones existentes.
- `primary-navigation`: ajusta el requisito de convivencia entre el contenido del feed y la barra inferior para permitir el desplazamiento visual bajo ella sin ocultar el último contenido.

## Impact

- Código UI afectado: `HomeScreen.kt` y los componentes/tokens de presentación del feed únicamente si la implementación demuestra que son necesarios.
- Tests afectados: pruebas de presentación, viewport, scroll/insets y guardrails estáticos del home feed.
- OpenSpec: nuevas delta specs para `home-feed-presentation` y `primary-navigation`.
- No hay impacto esperado en APIs, datos, Room, Firebase, ViewModels, repositories, permisos o seguridad.
- Usuarios existentes verán una presentación más continua del mismo contenido y conservarán las mismas acciones.
- Rollback: restaurar el contenedor `Card` del feed, sus tokens de forma/elevación y el espaciado externo previo, manteniendo las pruebas actualizadas.
- Guardrails aplicables: respetar Design System, no inventar datos, no cambiar lógica de negocio, soportar ambos temas y evitar APIs experimentales.
