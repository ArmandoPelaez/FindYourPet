## 1. Preparación y alcance

- [x] 1.1 Revisar `HomeScreen.kt`, el shell de `MainActivity.kt`, los tokens de `AppSpacing` y las pruebas de Home para confirmar el punto único de eliminación de la cabecera.
- [x] 1.2 Confirmar que el cambio no requiere modificar `CreatePetPostScreen`, ViewModels, repositorios, Firebase, rutas ni Bottom Navigation.

## 2. Eliminación de la cabecera de Home

- [x] 2.1 Retirar de `HomeScreen` la `topBar` completa, incluyendo Surface, logo, título, subtítulo y manejo duplicado de la Status Bar.
- [x] 2.2 Mantener `WindowInsets.safeDrawing` y aplicar después del safe area el margen superior tokenizado equivalente a 16 dp.
- [x] 2.3 Verificar que el feed, los estados vacíos, el scroll de las publicaciones y las acciones de reporte conserven su comportamiento y espacio inferior frente a la Bottom Navigation.

## 3. Pruebas y documentación

- [x] 3.1 Actualizar o agregar pruebas de presentación/screenshot para verificar ausencia de la cabecera, presencia del contenido de Home y preservación de la navegación inferior.
- [x] 3.2 Revisar que las pruebas y documentación de Home no describan branding, título o subtítulo de una cabecera que ya no existe.

## 4. Validación

- [x] 4.1 Ejecutar `openspec validate remove-home-screen-header --strict` y `openspec instructions apply --change "remove-home-screen-header" --json`.
- [x] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [x] 4.3 Validar manualmente Home en viewport compacto y alto, Light/Dark Theme y navegación por gestos: Status Bar visible e integrada, margen superior correcto, feed desplazable y acciones/Bottom Navigation sin solapamiento.
