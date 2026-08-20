## Why

La barra de navegación inferior actualmente se extiende casi hasta los bordes de la pantalla, mientras que el contenido principal usa márgenes laterales más amplios. SCRUM-46 alinea ambas superficies para recuperar una composición visual coherente sin alterar la navegación ni la identidad existente.

## What Changes

- Ajustar los márgenes horizontales de la superficie flotante de navegación inferior para alinearlos con el contenedor principal.
- Mantener la barra fija en la parte inferior y respetar los insets del sistema.
- Mantener centrado y contenido el botón `Reportar`.
- Verificar Light Theme, Dark Theme, tamaños de pantalla, accesibilidad y áreas táctiles.
- Reutilizar los tokens existentes del Design System; no introducir valores visuales hardcodeados.
- No modificar destinos, iconos, labels, lógica de navegación ni pantallas de contenido.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `primary-navigation`: la superficie flotante de navegación inferior debe usar márgenes horizontales alineados con el contenedor principal, manteniendo su posición, acciones, insets y accesibilidad.

## Impact

- Afecta únicamente la presentación Compose de la navegación inferior y las pruebas de presentación/screenshot relacionadas.
- No afecta APIs, backend, datos, permisos, autenticación ni lógica de negocio.
- No introduce cambios de privacidad o seguridad.
- Rollback: revertir el ajuste de márgenes y sus pruebas asociadas restaura el comportamiento visual previo sin migración de datos.
