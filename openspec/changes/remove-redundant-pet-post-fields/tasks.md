## 1. Retirar los atributos de los contratos de datos

- [x] 1.1 Eliminar `characteristics` y `particularMarks` de `PetPostEntity` y `PetPostDocument`, preservando todos los demás campos.
- [x] 1.2 Eliminar ambos parámetros de `PetViewModel.createNewPetPost` y de la construcción de nuevas publicaciones.
- [x] 1.3 Actualizar `RemoteMappers` para que las nuevas escrituras no incluyan `characteristics` ni `particularMarks` y que la lectura de documentos legacy ignore esas claves sin fallar.
- [x] 1.4 Localizar y actualizar fixtures, builders y llamadas de tests que construyan `PetPostEntity` o `PetPostDocument` con los atributos retirados.

## 2. Migrar la persistencia local

- [x] 2.1 Incrementar `AppDatabase` a la versión 8 y registrar una migración 7→8.
- [x] 2.2 Implementar la reconstrucción de `pet_posts` sin `characteristics` ni `particularMarks`, copiando todas las columnas vigentes y preservando los datos existentes.
- [x] 2.3 Mantener las migraciones históricas 5→6 y 6→7 para permitir que instalaciones antiguas alcancen la versión 8.

## 3. Simplificar la pantalla de creación

- [x] 3.1 Eliminar de `CreatePetPostScreen` el estado, las secciones y los valores de envío de `Características` y `Señas particulares`.
- [x] 3.2 Mantener `Descripción adicional`/`recognitionDetails` como el único campo de reconocimiento, conservando sus tokens, límite, contador y comportamiento actual.
- [x] 3.3 Actualizar las pruebas estáticas o de screenshot para verificar la ausencia de ambos campos y la continuidad del flujo de foto, nombre, descripción, ubicación y publicación.
- [x] 3.4 Verificar que no se introduzcan cambios de identidad visual, valores hardcodeados nuevos ni APIs Compose experimentales.

## 4. Cubrir compatibilidad y comportamiento

- [x] 4.1 Agregar o actualizar pruebas de migración para comprobar que 7→8 elimina solo las columnas retiradas y conserva los demás valores de una publicación.
- [x] 4.2 Actualizar `RemoteMappersTest` para comprobar serialización sin las claves retiradas y lectura tolerante de documentos legacy.
- [x] 4.3 Ejecutar búsqueda de referencias activas a `characteristics`, `particularMarks`, `Características` y `Señas particulares`, justificando solo las coincidencias históricas, negativas o del propio change.

## 5. Validación final

- [x] 5.1 Ejecutar `openspec instructions apply --change "remove-redundant-pet-post-fields" --json` y dejar todas las tareas implementables completas.
- [x] 5.2 Ejecutar `openspec validate "remove-redundant-pet-post-fields" --strict`.
- [x] 5.3 Ejecutar `./gradlew.bat testDebugUnitTest`.
- [x] 5.4 Ejecutar `./gradlew.bat assembleDebug`.
- [x] 5.5 Verificar manualmente el formulario en Light Theme y Dark Theme, confirmando que los campos retirados no aparecen, `Descripción adicional` sigue funcionando y el flujo de publicación conserva foto, nombre y ubicación.
