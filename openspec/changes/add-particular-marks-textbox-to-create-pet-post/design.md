## Context

`CreatePetPostScreen` ya presenta `Características` como un campo opcional independiente y conserva `Detalles adicionales` en `recognitionDetails`, que se persiste como `features`. SCRUM-11 agrega una tercera entrada independiente para señas particulares, ubicada inmediatamente después de `Características`, para evitar mezclar información de reconocimiento con detalles generales.

La extensión cruza la UI de Compose, el estado y la creación del post, el modelo Room, el documento Firestore y sus mappers. La base local actual está en la versión 6 por el campo `characteristics`, por lo que el nuevo atributo debe agregarse mediante una migración aditiva compatible. El cambio visual debe respetar `docs/design-system.md`: Material 3 estable, `AppFormTypography`, `FormFieldLabel`, `FormFieldPlaceholder`, `AppShapes`, `AppSpacing`, colores del tema y soporte Light/Dark.

## Goals / Non-Goals

**Goals:**

- Mostrar un campo opcional `Señas particulares` inmediatamente después de `Características` y antes de `Detalles adicionales`.
- Transportar el valor sin perder la separación entre `particularMarks`, `characteristics` y `features`.
- Persistir y recuperar `particularMarks` en Room y Firestore.
- Mantener compatibilidad con filas Room y documentos Firestore legacy sin el nuevo campo.
- Verificar la posición, etiqueta opcional, ausencia de icono, legibilidad en ambos temas y continuidad del flujo existente.

**Non-Goals:**

- No renombrar ni reutilizar `features` o `characteristics`.
- No cambiar validaciones obligatorias, navegación, otros formularios, permisos, reglas de acceso ni colecciones backend.
- No agregar un placeholder, icono, color, tamaño, padding o radio que no esté definido por el Design System; si la implementación usa placeholder, debe seguir los tokens y convenciones existentes.
- No modificar la presentación del feed para mostrar el nuevo atributo.

## Decisions

### Atributo técnico independiente `particularMarks`

El valor se llamará `particularMarks` en el código, Room y Firestore. Este nombre mantiene la semántica del change y evita confundir las señas particulares con `characteristics` o con el texto general `features`.

Alternativas consideradas:

- Reutilizar `features`: rechazado porque impediría distinguir el nuevo dato y contradice la persistencia independiente solicitada.
- Usar `distinctiveMarks`: rechazado para mantener una única clave técnica alineada con el alcance y la nomenclatura del change.

### Campo Compose después de `Características`

Agregar estado local `particularMarks` y un `OutlinedTextField` independiente después del bloque de `Características`. La etiqueta visible será `Señas particulares`, con `required = false`, sin `leadingIcon` ni indicador `*`. Se reutilizarán `AppFormTypography.input`, `FormFieldLabel`, `FormFieldPlaceholder` cuando corresponda, `AppShapes.chip`, `AppSpacing.formFieldHeight` y los colores de `MaterialTheme`.

Alternativa considerada:

- Agregar el texto dentro de `Detalles adicionales`: rechazado porque el requisito exige un textbox independiente y el valor debe viajar como atributo propio.

### Propagación por el flujo de creación

`CreatePetPostScreen` pasará `particularMarks` a `PetViewModel.createNewPetPost`. El ViewModel construirá `PetPostEntity` con el valor normalizado según la convención existente, sin convertirlo en obligatorio ni cambiar la validación de nombre, foto o ubicación.

La entidad y el documento remoto incluirán el campo con valor por defecto vacío. Los mappers Room/Firestore escribirán y leerán la misma clave `particularMarks`; un documento remoto legacy sin esa clave devolverá `""`.

### Migración Room 6→7

Agregar una migración aditiva que ejecute `ALTER TABLE pet_posts ADD COLUMN particularMarks TEXT NOT NULL DEFAULT ''` y registrar la migración en `AppDatabase`. No se eliminarán ni renombrarán columnas existentes. La compatibilidad con instalaciones anteriores se probará mediante el harness de migraciones o las pruebas existentes de persistencia.

### Cobertura enfocada

Actualizar o agregar pruebas estáticas/Compose para confirmar que el campo aparece después de `Características`, antes de `Detalles adicionales`, es opcional y no contiene icono de etiqueta. Agregar pruebas de mappers para round-trip con valor y fallback vacío para documentos legacy. Mantener las verificaciones existentes de creación y de ambos temas.

## Risks / Trade-offs

- [Riesgo] El valor puede perderse entre UI, ViewModel, entidad y Firestore → Mitigación: pruebas de propagación y round-trip de mappers con una cadena representativa.
- [Riesgo] Una migración incompleta puede romper instalaciones existentes → Mitigación: migración explícita 6→7, default vacío y prueba de esquema/migración.
- [Riesgo] El campo puede heredar accidentalmente el indicador obligatorio o un icono → Mitigación: prueba estática que inspeccione el bloque exacto del campo y exija `required = false` y ausencia de `leadingIcon`.
- [Riesgo] El nuevo campo puede alterar el viewport compacto → Mitigación: conservar tokens de altura/espaciado y ejecutar las pruebas visuales existentes en Light/Dark Theme.
- [Riesgo] La etiqueta puede presentar problemas de legibilidad en un tema → Mitigación: usar exclusivamente tipografía y colores del Design System y validar ambos temas.

## Migration Plan

1. Agregar el campo opcional a UI, ViewModel, entidad, documento remoto y mappers.
2. Registrar la migración Room 6→7 con valor por defecto vacío.
3. Actualizar pruebas del formulario, mappers y migración.
4. Ejecutar validación OpenSpec, tests unitarios y `assembleDebug`.
5. Para rollback, revertir el código de UI/mapeo y conservar la columna aditiva; no se requiere borrar datos ni modificar documentos anteriores.

## Open Questions

- Jira no define un placeholder textual para el nuevo campo; la implementación debe mantenerlo sin placeholder específico o elegir uno solo si existe una convención vigente en el Design System, sin inventar un requisito de aceptación.
