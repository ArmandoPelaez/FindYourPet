## Why

La pantalla de publicación utiliza una AppBar con título y navegación redundantes porque `Publicar` ya es un destino principal de la navegación inferior. El espacio vertical adicional reduce la visibilidad inicial del formulario y desvía la jerarquía del contenido; SCRUM-16 solicita simplificar esta cabecera manteniendo la navegación inferior y la identidad visual existente.

## What Changes

- Reemplazar la AppBar de la pantalla de creación de publicación por una cabecera integrada al contenido.
- Retirar la flecha de navegación superior sin cambiar el flujo de publicación ni la navegación inferior.
- Mantener la Status Bar visible e integrada con la superficie de la pantalla.
- Mostrar el título `Publicar mascota perdida` dentro del contenido, antes del componente de carga de foto.
- Mantener la separación superior posterior al safe area y la jerarquía tipográfica mediante tokens del Design System.
- Preservar el comportamiento de la Bottom Navigation, el formulario, la lógica de negocio, la validación y los flujos de medios.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `pet-posts`: ajustar el requisito de presentación del formulario de creación para definir la nueva cabecera integrada, su orden visual inicial y la conservación del comportamiento responsive.

## Impact

- Código de UI Compose en `CreatePetPostScreen.kt` y, si la validación lo requiere, sus pruebas de presentación estática/capturas.
- Tokens existentes de `AppSpacing` y `AppTypography`; no se requieren nuevas dependencias ni cambios de backend, APIs, permisos o datos.
- Impacto para usuarios existentes: la pantalla conserva las mismas acciones y validaciones, pero expone antes el contenido del formulario y elimina la navegación superior redundante.
- Rollback: revertir los cambios de presentación de la pantalla restaura la AppBar anterior sin afectar datos persistidos ni contratos de backend.
