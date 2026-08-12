## Why

La pantalla de creación de publicaciones necesita un campo independiente para que el usuario registre las características de la mascota, como color, tamaño o edad. El SCRUM 10 solicita un textbox nuevo, separado de `Detalles adicionales`, y ese valor debe persistirse junto con el resto de los atributos de la publicación.

## What Changes

- Agregar un nuevo textbox independiente inmediatamente después del campo `Nombre` en `CreatePetPostScreen`.
- Mostrar `Características` como etiqueta visible del campo, igual que `Nombre`, pero opcional y sin indicador `*` ni icono de etiqueta dentro del campo.
- Usar el placeholder exacto `Ej: color,raza,tamaño`.
- Mantener `Detalles adicionales` como un campo separado y conservar su valor en `features`.
- Transportar el nuevo valor por el flujo de creación y persistirlo como un atributo independiente `characteristics` en Room y Firestore.
- Agregar la migración local necesaria para instalaciones existentes.
- Mantener los tokens existentes de tipografía, espaciado, forma, colores y soporte para Light Theme y Dark Theme.
- No modificar otros formularios, navegación ni lógica de negocio no relacionada.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `pet-posts`: precisar la presentación del campo de características en el formulario simplificado de creación de publicaciones.

## Impact

- Código afectado: `CreatePetPostScreen.kt`, `PetViewModel`, `PetPostEntity`, `AppDatabase`, `RemoteMappers`, documento remoto y pruebas del formulario/mappers.
- Firestore debe recibir y devolver `characteristics` como campo independiente; no se requiere una nueva colección ni dependencia.
- Room requiere una migración compatible para agregar la columna no nula con valor por defecto vacío.
- No hay impacto de privacidad, seguridad o permisos: el nuevo valor es parte de la publicación existente y queda sujeto a las mismas reglas de acceso.
- Usuarios existentes conservan el flujo de publicación; solo cambia la identificación visual del campo.
- Rollback: revertir la UI y el mapeo después de aplicar una migración compatible; no se eliminan datos existentes ni se cambia el contrato de los campos actuales.
