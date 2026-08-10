## 1. Preparación y layout del feed

- [x] 1.1 Revisar `HomeScreen`, `PetPostCard`, `MainActivity` y los tokens de tema para identificar el contenedor externo, el padding del pager y los insets del `bottomBar` que producen la separación visual actual.
- [x] 1.2 Reemplazar el contenedor externo elevado/redondeado de `PetPostCard` por una composición continua integrada a la superficie del feed, sin modificar su API pública ni el orden de contenido.
- [x] 1.3 Ajustar únicamente los márgenes externos/padding de página necesarios para eliminar la apariencia de card flotante y conservar el espacio interno tokenizado.
- [x] 1.4 Ajustar el contrato de insets del home y/o del shell para que el feed pueda desplazarse visualmente bajo `BottomPrimaryActionBanner` sin cambiar sus destinos ni acciones.
- [x] 1.5 Garantizar mediante tokens/insets que la información final y los botones de la última publicación puedan desplazarse completamente a una posición visible y tappable.

## 2. Compatibilidad visual y alcance

- [x] 2.1 Verificar que imagen, estado, nombre, ubicación, información reportada, fecha, reporte de avistamiento y compartir mantengan su jerarquía y comportamiento actuales.
- [x] 2.2 Verificar que la superficie continua use `MaterialTheme` y tokens existentes, sin colores, tamaños, paddings, radios ni elevaciones hardcodeados ni APIs experimentales.
- [x] 2.3 Verificar que no se modifiquen ViewModel, repositories, modelos, Firebase, permisos, autenticación, filtros ni navegación funcional.
- [x] 2.4 Revisar estados con publicaciones vacías, carga/error y múltiples publicaciones para confirmar que la eliminación de la card externa no rompe el feed ni la paginación horizontal.

## 3. Tests y evidencia visual

- [x] 3.1 Actualizar o agregar tests de presentación que comprueben que la publicación no expone una card externa flotante y que conserva la jerarquía y acciones existentes.
- [x] 3.2 Agregar o actualizar una prueba de viewport compacto que compruebe wrapping/truncado intencional, ausencia de solapamiento y lectura de la ubicación/información.
- [x] 3.3 Agregar o actualizar una prueba de scroll/insets que compruebe que el último contenido puede desplazarse por completo sobre la barra inferior y permanece tappable.
- [x] 3.4 Ejecutar evidencia visual reproducible en un viewport compacto y otro alto, revisando continuidad con la barra inferior, tema claro/oscuro cuando el arnés lo permita y ausencia de cortes.

## 4. Validación final

- [x] 4.1 Ejecutar `openspec validate "remove-lost-pet-feed-cards" --strict`.
- [x] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest` y corregir únicamente fallos causados por este change.
- [x] 4.3 Ejecutar `./gradlew.bat assembleDebug`.
- [x] 4.4 Revisar el diff contra SCRUM-5 y confirmar que solo contiene cambios de presentación, tests y artefactos OpenSpec/orquestación dentro del alcance.
