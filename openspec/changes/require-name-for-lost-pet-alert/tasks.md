## 1. Formulario de publicación

- [x] 1.1 Reemplazar el placeholder del campo de nombre en `CreatePetPostScreen` por una etiqueta visible `Nombre` y un indicador `*` usando Material 3 y los tokens existentes.
- [x] 1.2 Agregar la guarda de submit para nombres vacíos o compuestos solo por espacios, mostrando exactamente `Campo obligatorio` y evitando invocar el flujo de publicación.
- [x] 1.3 Mantener el flujo válido de publicación, el mapeo de campos existentes y el comportamiento del resto del formulario sin cambios.

## 2. Pruebas automatizadas

- [x] 2.1 Actualizar o agregar tests estáticos/de presentación que verifiquen la etiqueta `Nombre`, el indicador `*` y la ausencia del placeholder como única identificación.
- [x] 2.2 Verificar que el estado de nombre vacío produce `Campo obligatorio` y no ejecuta la publicación, incluyendo entradas con espacios.
- [x] 2.3 Verificar que un nombre válido conserva habilitación y envío del formulario, y que no se afectan otros formularios.

## 3. Validación final

- [x] 3.1 Ejecutar `openspec validate "require-name-for-lost-pet-alert" --strict`.
- [x] 3.2 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [x] 3.3 Revisar el diff para confirmar que solo contiene el formulario, tests y artefactos OpenSpec/orquestación del SCRUM-9.
- [x] 3.4 Inspeccionar la presentación en Light/Dark Theme y en tamaños de teléfono disponibles, verificando legibilidad del label, asterisco, error y ausencia de cambios visuales fuera del alcance.
