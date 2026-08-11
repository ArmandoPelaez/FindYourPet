## 1. Preparación y composición del shell

- [x] 1.1 Revisar el shell firmado, `BottomPrimaryActionBanner` y los destinos con `verticalScroll`/`LazyColumn` para identificar insets duplicados y puntos de solapamiento, sin modificar lógica de navegación ni datos.
- [x] 1.2 Ajustar la composición del shell para que la barra inferior permanezca fija respecto del viewport y el contenido primario pueda desplazarse visualmente por detrás de ella usando APIs estables de Compose.
- [x] 1.3 Mantener la barra y sus acciones con los tokens existentes `MaterialTheme`, `AppOpacity`, `AppShapes`, `AppElevation` y `AppSpacing`, respetando Light Theme y Dark Theme.

## 2. Scroll y accesibilidad visual

- [x] 2.1 Ajustar el contenido desplazable del home feed para que alcance el inicio y el final de cada post sin paginación, carga adicional ni cambios en ViewModel/repository.
- [x] 2.2 Revisar las pantallas con listas o contenido desplazable que muestran la barra para que sus últimos elementos y acciones sigan visibles y táctiles con la barra fija.
- [x] 2.3 Verificar que las pantallas secundarias sin barra inferior mantengan sus insets, navegación y comportamiento actual.

## 3. Pruebas

- [x] 3.1 Actualizar o agregar pruebas Compose/estáticas para comprobar que la barra permanece fija mientras se desplaza el contenido primario.
- [x] 3.2 Cubrir que el contenido del home feed llega al principio y al final y que las acciones finales no quedan cubiertas por la barra.
- [x] 3.3 Verificar la presentación en Light Theme y Dark Theme sin introducir colores, tamaños, paddings o radios hardcodeados.

## 4. Validación final

- [x] 4.1 Ejecutar `openspec validate "infinite-scroll-fixed-bottom-navigation" --strict`.
- [x] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [x] 4.3 Ejecutar validación manual en Home, Profile y Chats: desplazamiento arriba/abajo, visibilidad parcial detrás de la barra, área de gestos y acciones finales.
- [x] 4.4 Ejecutar validación manual en Create Post, Notifications, Sighting Alert y Chat Detail para confirmar que no aparece la barra ni cambia la navegación.
