## 1. Implementación del formulario

- [x] 1.1 Agregar estado y textbox nuevo `characteristics` inmediatamente después del campo `Nombre`, usando los componentes y tokens existentes del Design System.
- [x] 1.2 Mantener el nuevo campo sin indicador `*`, sin icono de etiqueta y separado de `recognitionDetails`/`Detalles adicionales`.
- [x] 1.3 Propagar `characteristics` por `CreatePetPostScreen` y `PetViewModel.createNewPetPost` sin alterar el mapeo existente de `features`.
- [x] 1.4 Agregar `characteristics` a `PetPostEntity`, `PetPostDocument` y los mappers Room/Firestore, con fallback vacío para datos antiguos.
- [x] 1.5 Agregar y registrar la migración Room 5→6 con columna `characteristics` no nula y default vacío.

## 2. Pruebas y validación

- [x] 2.1 Actualizar o agregar pruebas de UI para confirmar el campo nuevo después de `Nombre`, la separación de labels, la ausencia del marcador obligatorio y la ausencia de un icono de etiqueta.
- [x] 2.2 Agregar pruebas de mappers para serializar/deserializar `characteristics` y leer documentos legacy sin el campo.
- [x] 2.3 Ejecutar las pruebas visuales existentes del formulario en Light Theme y Dark Theme, verificando scroll, tamaños compactos y ausencia de clipping.
- [x] 2.4 Ejecutar `openspec validate "add-characteristics-textbox-to-create-pet-post" --strict`.
- [x] 2.5 Ejecutar `./gradlew.bat testDebugUnitTest`.
- [x] 2.6 Ejecutar `./gradlew.bat assembleDebug`.
- [x] 2.7 Corregir la presentación del campo para que `Características` sea una etiqueta opcional, no un placeholder, y use exactamente `Ej: color,raza,tamaño` como placeholder; actualizar la prueba correspondiente.
