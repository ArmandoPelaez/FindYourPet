## Why

La acción central de la Bottom Navigation todavía se presenta como `+ Publicar`, una combinación genérica que no comunica con claridad que inicia un reporte relacionado con una mascota. SCRUM-18 solicita hacer explícita esa intención sin alterar el flujo existente ni confundir el acceso al flujo con el CTA final `Publicar ficha` del formulario.

## What Changes

- Reemplazar el icono `+` de la acción central por el icono de huella disponible en el sistema de iconos del proyecto.
- Cambiar la etiqueta central de `Publicar` a `Reportar`.
- Mantener la posición, jerarquía visual, estados y destino actual de la acción central.
- Mantener `Publicar ficha` como CTA independiente dentro del formulario.
- Agregar o actualizar pruebas de presentación, estados y navegación para verificar la nueva semántica.
- Mantener sin cambios la lógica de publicación, validaciones, persistencia y los otros destinos de navegación.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `primary-navigation`: la acción central pasa a comunicarse como `Reportar` con icono de huella, conservando su destino de inicio del flujo y la estructura existente de la navegación.

## Impact

- Afecta la presentación Compose de la Bottom Navigation y sus pruebas asociadas.
- Puede requerir actualizar la especificación existente de navegación primaria y los contratos de accesibilidad/content descriptions.
- No modifica APIs, persistencia, backend, permisos ni lógica de negocio.
- Respeta los tokens de `docs/design-system.md`, incluyendo la posición central, el tratamiento circular, dimensiones, elevación, estados y soporte Light/Dark.
- La reversión consiste en restaurar el icono y etiqueta anteriores manteniendo el mismo destino, sin migración de datos.
