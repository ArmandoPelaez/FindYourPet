## 1. Presentación de la pantalla

- [x] 1.1 Actualizar en `CreatePetPostScreen` el título a `Crea un aviso para ayudar a encontrarla`, conservando la jerarquía tipográfica tokenizada y el mismo peso visual que `Toca para agregar una foto`.
- [x] 1.2 Actualizar la etiqueta y `contentDescription` del CTA a `Publicar aviso`, reemplazando el ícono de publicación por `Icons.Filled.Send` sin modificar `submitPost()`, validaciones ni estados de carga.

## 2. Pruebas automatizadas

- [x] 2.1 Actualizar las pruebas estáticas y Compose del formulario para verificar el nuevo título, CTA, descripción de accesibilidad e ícono, manteniendo las aserciones de validación, estado disabled y publicación existentes.
- [x] 2.2 Ejecutar una búsqueda acotada para confirmar que la pantalla ya no contiene los textos visibles anteriores ni un CTA duplicado fuera del alcance del cambio.

## 3. Verificación

- [x] 3.1 Ejecutar `openspec validate "change-publication-text-and-actions" --strict`.
- [ ] 3.2 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [ ] 3.3 Validar manualmente `CreatePetPostScreen` en Light Theme y Dark Theme, incluyendo formulario incompleto, formulario válido, estado de publicación y viewport compacto, confirmando que el texto largo y `Publicar aviso` no se recortan ni se solapan.
- [x] 3.4 Revisar el diff contra el alcance OpenSpec y registrar el resultado en el estado de orquestación.
