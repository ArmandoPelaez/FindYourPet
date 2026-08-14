## 1. Cabecera integrada al formulario

- [x] 1.1 Actualizar `CreatePetPostScreen` para quitar la `TopAppBar`, retirar el callback `onBackClick` y conservar `WindowInsets.safeDrawing` para la Status Bar.
- [x] 1.2 Agregar el título `Publicar mascota perdida` dentro de la columna desplazable, después del safe area y antes de `FormPhotoUploadSurface`, usando tokens tipográficos y de espaciado existentes.
- [x] 1.3 Actualizar la composición en `MainActivity` y los harnesses afectados para eliminar referencias al callback de navegación superior sin modificar el back del sistema ni la Bottom Navigation.

## 2. Pruebas de presentación y regresión

- [x] 2.1 Actualizar o agregar pruebas estáticas que verifiquen la ausencia de `TopAppBar`/flecha en la pantalla, el título integrado y la conservación de `FormPhotoUploadSurface` y tokens del Design System.
- [x] 2.2 Actualizar el escenario de captura de `CreatePetPostScreenScreenshotTest` para comprobar el orden título → carga de foto y revisar viewport compacto/alto en Light Theme y Dark Theme.
- [x] 2.3 Verificar que las validaciones, carga de foto, campos del formulario, publicación y navegación inferior mantengan sus pruebas existentes sin cambios de lógica.

## 3. Validación final

- [x] 3.1 Ejecutar `openspec validate "optimize-publish-lost-pet-header" --strict` y revisar que el diff quede limitado al alcance del change.
- [x] 3.2 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [x] 3.3 Ejecutar validación visual disponible para la pantalla de creación en Light/Dark, tamaños soportados, scroll e IME; confirmar que la Status Bar y el último contenido no queden cubiertos.
