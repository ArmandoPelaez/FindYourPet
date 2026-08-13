## Why

El campo existente de detalles adicionales se presenta como un input grande sin una indicación suficientemente clara de que admite texto multilinea ni de su límite. SCRUM-12 busca mejorar la comprensión y previsibilidad del formulario de creación de publicaciones sin cambiar el dato persistido ni ampliar el flujo.

## What Changes

- Convertir la presentación de `Detalles adicionales` en un campo multilinea dentro de `CreatePetPostScreen`.
- Mantener el valor en el campo preexistente que se persiste como `features`.
- Limitar la entrada a 500 caracteres.
- Mostrar un contador discreto con el formato `actual/500`.
- Usar el placeholder `Contanos cómo reconocerla...`.
- Cambiar la etiqueta visible a `Descripcion adicional` y mantenerla alineada con los demás campos del formulario.
- Preservar el soporte para Light Theme y Dark Theme mediante los tokens existentes del Design System.
- No modificar ViewModel, repositorios, backend, permisos, validaciones de autenticación ni el modelo de datos.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `pet-posts`: el formulario de creación debe presentar el campo de detalles adicionales como entrada multilinea, con límite visible de 500 caracteres y texto guía definido.

## Impact

- Código afectado: `CreatePetPostScreen.kt` y pruebas de presentación del formulario.
- Persistencia: sin cambios; el valor continúa enviándose por el mapeo existente hacia `features`.
- Usuarios existentes: las publicaciones y datos ya guardados conservan su comportamiento; solo cambia la edición/creación de nuevas publicaciones.
- Privacidad, seguridad y permisos: sin impacto; no se agregan datos sensibles, permisos ni integraciones.
- Rollback: restaurar la presentación anterior del mismo campo y sus expectativas de prueba, sin migración de datos.
- Guardrails aplicables: usar Material 3 estable, tokens del Design System, ambos temas y ningún valor visual hardcodeado.
