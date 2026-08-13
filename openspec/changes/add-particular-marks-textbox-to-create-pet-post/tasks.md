## 1. Modelo y persistencia

- [x] 1.1 Agregar el atributo opcional `particularMarks` a `PetPostEntity` con valor por defecto vacío, preservando `characteristics` y `features`.
- [x] 1.2 Agregar `particularMarks` a `PetPostDocument` y a los mappers Room/Firestore, incluyendo fallback vacío para documentos legacy.
- [x] 1.3 Crear y registrar la migración Room 6→7 con la columna `particularMarks` no nula y default vacío.
- [x] 1.4 Propagar `particularMarks` por `PetViewModel.createNewPetPost` y el constructor del post sin convertirlo en campo obligatorio.

## 2. Formulario de creación

- [x] 2.1 Agregar el estado y textbox opcional `Señas particulares` inmediatamente después de `Características` y antes de `Detalles adicionales`.
- [x] 2.2 Renderizar la etiqueta con los componentes y tokens del Design System, sin indicador `*`, sin icono dentro del campo y sin valores visuales hardcodeados.
- [x] 2.3 Enviar el valor del nuevo campo como `particularMarks` manteniendo `Características` en `characteristics` y `Detalles adicionales` en `features`.
- [x] 2.4 Confirmar que la validación existente de nombre, foto, ubicación y publicación no se modifica por el nuevo campo opcional.

## 3. Pruebas enfocadas

- [x] 3.1 Actualizar o agregar pruebas estáticas/Compose que verifiquen el orden `Características` → `Señas particulares` → `Detalles adicionales`.
- [x] 3.2 Verificar en pruebas que `Señas particulares` es opcional, no muestra `*` y no contiene `leadingIcon` ni icono de etiqueta.
- [x] 3.3 Agregar pruebas de mappers para serializar/deserializar `particularMarks` y leer documentos legacy sin el campo.
- [x] 3.4 Agregar o actualizar pruebas de migración Room para validar 6→7 y el valor vacío en filas existentes.
- [x] 3.5 Actualizar pruebas del flujo/ViewModel para confirmar que los tres valores (`characteristics`, `particularMarks`, `features`) permanecen independientes.

## 4. Validación y entrega

- [x] 4.1 Ejecutar `openspec validate "add-particular-marks-textbox-to-create-pet-post" --strict`.
- [x] 4.2 Ejecutar `\.\gradlew.bat testDebugUnitTest` y corregir únicamente fallos relacionados con este change.
- [x] 4.3 Ejecutar `\.\gradlew.bat assembleDebug`.
- [x] 4.4 Revisar el diff contra el alcance OpenSpec y confirmar que no se modificaron otros formularios, navegación, permisos ni lógica no relacionada.
- [x] 4.5 Realizar verificación manual del formulario en Light/Dark Theme y viewport compacto: orden de campos, legibilidad, scroll y ausencia de clipping.
