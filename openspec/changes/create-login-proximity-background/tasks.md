## 1. Preparación y diseño visual

- [x] 1.1 Revisar `AuthScreen.kt`, `CommonComponents.kt`, `DesignTokens.kt` y `docs/design-system.md` para identificar el punto de integración y los tokens reutilizables.
- [x] 1.2 Definir la composición geométrica abstracta de líneas, nodos, zonas circulares, conexiones y marcador principal usando proporciones del tamaño disponible.
- [x] 1.3 Confirmar que el recurso será decorativo, no interactivo, local y sin dependencia de mapas, red, ubicación o datos de usuarios.

## 2. Implementación de presentación

- [x] 2.1 Crear el composable visual de proximidad con `Canvas` y APIs estables de Compose, reutilizando colores y opacidades del tema.
- [x] 2.2 Integrar la capa visual en `AuthScreen` sin modificar campos, acciones, estados, callbacks ni el flujo de autenticación.
- [x] 2.3 Asegurar que la geometría se adapte a distintas resoluciones, ventanas compactas y teclado visible sin ocultar el contenido de Login.
- [x] 2.4 Verificar por código que Light Theme y Dark Theme usan colores del tema y que la capa no agrega interacciones.

## 3. Pruebas y validación automatizada

- [x] 3.1 Agregar o actualizar pruebas estáticas/presentación que comprueben el recurso de proximidad, su integración local y la conservación de las acciones de autenticación.
- [x] 3.2 Ejecutar `openspec validate "create-login-proximity-background" --strict` y corregir incumplimientos de los artefactos.
- [x] 3.3 Ejecutar `./gradlew.bat testDebugUnitTest`.
- [x] 3.4 Ejecutar `./gradlew.bat assembleDebug`.

## 4. Revisión manual y cierre

- [x] 4.1 Revisar Login en Light Theme y Dark Theme, confirmando contraste, ausencia de datos geográficos reales y legibilidad del formulario.
- [x] 4.2 Revisar teléfonos/ventanas de distintas dimensiones, incluyendo altura compacta y teclado visible.
- [x] 4.3 Confirmar que email/password, Google Sign-In, creación de cuenta y mensajes de error conservan su comportamiento y reciben interacción normalmente.
- [x] 4.4 Revisar el diff para confirmar que no se modificaron ViewModels, repositories, Firebase, permisos, navegación ni lógica de dominio.
- [x] 4.5 Ejecutar `openspec instructions apply --change "create-login-proximity-background" --json` y dejar todas las tareas completas o justificar validaciones externas pendientes.

> Las tareas 4.1, 4.2 y 4.3 quedan pendientes de revisión manual en emulador/dispositivo para confirmar temas, tamaños/teclado e interacción real.
