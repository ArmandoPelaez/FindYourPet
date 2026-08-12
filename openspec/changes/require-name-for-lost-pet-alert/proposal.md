## Why

El formulario para publicar una mascota perdida muestra el nombre mediante un placeholder, por lo que no identifica de forma explícita el dato solicitado. SCRUM-9 busca hacer más intuitivo el ingreso y evitar que una publicación se intente guardar sin nombre, manteniendo la identidad visual existente.

## What Changes

- Mostrar la etiqueta `Nombre` en el campo de nombre de la mascota, con `*` como indicador visual de obligatoriedad.
- Validar explícitamente el nombre al intentar guardar la publicación.
- Mostrar el mensaje `Campo obligatorio` cuando el nombre esté vacío o contenga solo espacios.
- Mantener sin cambios los demás campos, formularios, contratos de datos, backend y flujo de publicación válido.

## Capabilities

### New Capabilities

- Ninguna.

### Modified Capabilities

- `pet-posts`: el formulario de publicación de mascota perdida debe identificar el nombre como obligatorio y rechazar el guardado sin ese dato con un mensaje visible.

## Impact

- Código afectado: `CreatePetPostScreen` y la cobertura de presentación/validación asociada.
- No se agregan dependencias, permisos, campos de datos ni cambios de backend.
- No afecta privacidad, seguridad, autenticación ni datos existentes.
- El rollback consiste en restaurar la presentación placeholder/label y retirar la validación/mensaje específicos del formulario.
- Guardrails aplicables: Material 3 estable, tokens del Design System, soporte Light/Dark Theme, sin colores/tamaños hardcodeados ni APIs experimentales.
