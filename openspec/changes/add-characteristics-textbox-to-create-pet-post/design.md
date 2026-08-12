## Context

`CreatePetPostScreen` ya mantiene el estado `recognitionDetails` y lo envía al flujo existente de creación como `features`. Actualmente ese campo se presenta con la etiqueta `Detalles adicionales`. El SCRUM 10 solicita un textbox nuevo, independiente, inmediatamente posterior a `Nombre`, cuyo valor se debe persistir como `characteristics`.

El cambio es visual y está limitado a Jetpack Compose con Material 3 estable. `docs/design-system.md` exige reutilizar `AppFormTypography`, `FormFieldLabel`, `FormFieldPlaceholder`, `AppShapes`, `AppSpacing` y colores del tema, además de soportar Light Theme y Dark Theme.

## Goals / Non-Goals

**Goals:**

- Mostrar un textbox nuevo con la etiqueta visible `Características`, como `Nombre`, pero opcional y sin `*`.
- Usar el placeholder `Ej: color,raza,tamaño`.
- Mantener `Detalles adicionales` y `Características` como dos entradas independientes.
- Mantener el mapeo existente de `recognitionDetails` hacia `features`.
- Persistir el nuevo estado `characteristics` en Room y Firestore y recuperarlo mediante los mappers.
- Conservar la jerarquía, espaciado, forma, colores y comportamiento responsive actuales.

**Non-Goals:**

- No cambiar reglas de acceso, navegación o validaciones no relacionadas; sí se actualizarán los modelos y mappers necesarios para persistir el nuevo atributo.
- No agregar un icono de etiqueta, un permiso, una dependencia ni un nuevo campo de datos.
- No rediseñar otros formularios.

## Decisions

### Agregar estado y campo independientes

Se agregará un estado `characteristics` y un `OutlinedTextField` nuevo después de `Nombre`. `recognitionDetails` continuará representando `Detalles adicionales`; no se reutilizará ni renombrará.

### Persistir `characteristics` como atributo propio

`PetPostEntity`, `PetPostDocument`, `RemoteMappers` y `PetViewModel.createNewPetPost` recibirán el nuevo valor. Firestore usará la clave `characteristics`; los documentos antiguos se mapearán con cadena vacía. Room avanzará de versión 5 a 6 con `ALTER TABLE pet_posts ADD COLUMN characteristics TEXT NOT NULL DEFAULT ''`.

### Usar `FormFieldLabel` como etiqueta

La etiqueta se renderizará con el componente del Design System y `required = false` o la configuración equivalente existente, para que no aparezca `*`. No se agregará `leadingIcon` ni otro icono al campo.

### Mantener tokens y configuración visual existente

Se conservarán `AppFormTypography.input`, `AppShapes.chip`, `AppSpacing.formFieldHeight` y los colores de `MaterialTheme`. No se introducirán tamaños, colores, paddings o radios hardcodeados.

### Validación visual enfocada

Se actualizarán las pruebas de UI/screenshot para afirmar la presencia del campo nuevo, la separación de ambos labels, la ausencia del indicador `*` en `Características` y la estabilidad del formulario en Light/Dark Theme y tamaños existentes. Las pruebas de mappers verificarán serialización, deserialización y compatibilidad con documentos antiguos.

## Risks / Trade-offs

- [Riesgo] El texto más largo puede afectar el espacio vertical en pantallas compactas → Mitigación: conservar el campo y sus tokens de altura/espaciado actuales y ejecutar las pruebas visuales existentes.
- [Riesgo] Una etiqueta no requerida podría reutilizar accidentalmente el marcador obligatorio → Mitigación: cubrir explícitamente la ausencia de `*` en la prueba del campo.
- [Riesgo] Un mapeo incompleto podría perder el nuevo valor entre UI, Room y Firestore → Mitigación: probar ambos sentidos del mapper y el contrato del ViewModel.
- [Riesgo] La migración local podría fallar en instalaciones existentes → Mitigación: usar una migración aditiva con default vacío y ejecutar las pruebas/build de Room.

## Migration Plan

Se agregará la migración Room 5→6 y el campo Firestore de forma compatible. Los documentos existentes continuarán funcionando con `characteristics = ""`. El rollback de código conserva la columna y el campo remoto sin afectar los atributos anteriores.

## Open Questions

- El placeholder requerido es `Ej: color,raza,tamaño`; la etiqueta `Características` no es un placeholder y el campo no es obligatorio.
