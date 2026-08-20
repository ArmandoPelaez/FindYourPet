## 1. Alinear la presentación

- [x] 1.1 Revisar el contenedor principal y `BottomPrimaryActionBanner` para identificar el token compartido de margen horizontal.
- [x] 1.2 Ajustar la regla de ancho/margen de la superficie flotante para que sus bordes coincidan con el contenedor principal en tamaños compacto, mediano, grande y tablet.
- [x] 1.3 Mantener `navigationBarsPadding()`, el centrado de la superficie, `bottomNavigationMaxWidth` y el tratamiento existente del botón `Reportar`.
- [x] 1.4 Confirmar que el cambio no altera destinos, iconos, labels, semantics, áreas táctiles ni lógica de navegación.

## 2. Pruebas automatizadas

- [x] 2.1 Actualizar o agregar pruebas estáticas/Compose que verifiquen el uso de tokens para márgenes, la posición centrada y el respeto del área segura.
- [x] 2.2 Verificar en pruebas la presencia única de `Reportar`, sus cinco destinos/labels actuales y sus content descriptions sin cambios funcionales.
- [x] 2.3 Ejecutar `npx.cmd openspec validate "align-bottom-navigation-horizontal-margins" --strict`.
- [x] 2.4 Ejecutar `./gradlew.bat testDebugUnitTest --no-daemon --console=plain`.
- [x] 2.5 Ejecutar `./gradlew.bat assembleDebug --no-daemon --console=plain`.

## 3. Verificación visual y cierre

- [ ] 3.1 Revisar visualmente la barra en Light Theme y Dark Theme.
- [ ] 3.2 Revisar teléfonos compacto/mediano/grande y una ventana tablet, verificando bordes alineados, centrado de `Reportar` y ausencia de solapamiento con la navegación del sistema.
- [ ] 3.3 Verificar accesibilidad con content descriptions, áreas táctiles y navegación por TalkBack o la inspección de layout disponible.
- [x] 3.4 Ejecutar `git diff --check`, revisar el diff contra el alcance de SCRUM-46 y actualizar la bitácora con la evidencia final.
