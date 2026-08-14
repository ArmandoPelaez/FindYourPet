## Why

La cabecera superior de Home consume espacio vertical y repite información que no es necesaria para explorar las publicaciones. SCRUM-19 solicita simplificar esa zona para que el contenido principal tenga mayor protagonismo, manteniendo visible la Status Bar y la navegación inferior.

## What Changes

- Eliminar la cabecera superior actual de `HomeScreen`, incluyendo su identidad visual y subtítulo.
- Mantener el fondo de Home integrado visualmente con la Status Bar del sistema.
- Reservar el margen superior requerido después del safe area usando los tokens existentes del Design System.
- Mantener el contenido de publicaciones, sus acciones y el espaciado inferior necesario para la Bottom Navigation.
- Mantener la Bottom Navigation fija, sus destinos, callbacks, orden y comportamiento.
- Actualizar las pruebas y la documentación de presentación que describan la cabecera visible de Home.
- No modificar lógica de negocio, persistencia, ViewModels, repositorios, Firebase, permisos ni datos personales.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `home-feed-presentation`: la cabecera de Home deja de mostrarse y el contenido conserva un inicio usable y el espaciado inferior.
- `primary-navigation`: la navegación inferior conserva su contrato; la cabecera ya no contiene branding ni acciones duplicadas porque se elimina del Home.

## Impact

- Código de UI Compose: `HomeScreen.kt` y, si las pruebas existentes lo requieren, sus pruebas de presentación/screenshot.
- Especificaciones: delta specs para `home-feed-presentation` y `primary-navigation`.
- Design System: reutilización de `AppSpacing`, `MaterialTheme` e insets existentes; no se agregan dependencias.
- APIs, backend, almacenamiento, autenticación y permisos: sin cambios.
- Usuarios existentes: Home muestra más contenido verticalmente y conserva las publicaciones, acciones de reporte y Bottom Navigation.
- Rollback: restaurar el `topBar` de `HomeScreen` y sus escenarios de especificación sin afectar rutas, datos ni lógica de dominio.
