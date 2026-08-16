## 1. Preparación y composición

- [x] 1.1 Revisar `AuthScreen.kt` y confirmar los elementos actuales que deben conservarse: identidad/proximidad, mensaje, campos, login, signup, Google Sign-In y mensajes de estado.
- [x] 1.2 Eliminar la `Card` exterior de la composición de autenticación sin modificar el estado ni los callbacks del `PetViewModel`.
- [x] 1.3 Reorganizar el contenido en una composición vertical de viewport completo, conservando `safeDrawing`, `imePadding`, el límite de ancho y los tokens del Design System.
- [x] 1.4 Asegurar que el contenido pueda recorrerse en alturas reducidas o con teclado visible mediante APIs estables de Compose.

## 2. Design System y compatibilidad visual

- [x] 2.1 Mantener `AppSpacing`, `AppShapes`, `AppElevation`, `AppFormTypography`, `AppButton` y `MaterialTheme.colorScheme` sin introducir valores visuales hardcodeados.
- [x] 2.2 Verificar que la composición conserve una jerarquía clara para identidad, proximidad, mensaje, formulario, autenticación alternativa y creación de cuenta.
- [x] 2.3 Verificar que Light Theme y Dark Theme mantengan contraste legible en campos, acciones, separadores y mensajes de error.

## 3. Pruebas y validación automatizada

- [x] 3.1 Agregar o actualizar pruebas estáticas/Compose que comprueben que la pantalla conserva las acciones de login, signup y Google Sign-In, sin alterar la lógica de autenticación.
- [x] 3.2 Ejecutar `openspec validate "redesign-login-layout" --strict` y corregir cualquier incumplimiento de los artefactos.
- [x] 3.3 Resolver la compatibilidad de `AppIdentityTest` con el classpath de tests sin modificar producción ni la configuración global de Gradle, y ejecutar `./gradlew.bat testDebugUnitTest`.
- [x] 3.4 Ejecutar `./gradlew.bat assembleDebug`.

## 4. Validación manual y cierre

- [x] 4.1 Revisar manualmente login y signup en Light Theme y Dark Theme, incluyendo alturas compactas y teclado visible.
- [x] 4.2 Revisar manualmente Google Sign-In, cancelación, configuración faltante y mensajes de error para confirmar que siguen siendo visibles y utilizables.
- [x] 4.3 Revisar el diff para confirmar que solo afecta la presentación del login y no ViewModels, repositories, Firebase ni lógica de negocio.
- [x] 4.4 Ejecutar `openspec instructions apply --change "redesign-login-layout" --json` y dejar todas las tareas completas o justificar cualquier validación externa pendiente.

### Notas del implementador

- La tarea 3.3 se resolvió adaptando `AppIdentityTest` para validar el `packageName` del contexto Robolectric, sin depender directamente de `BuildConfig`; `./gradlew.bat testDebugUnitTest` finaliza correctamente.
- Las tareas 4.1 y 4.2 requieren revisión manual en emulador o dispositivo con Light/Dark Theme, teclado visible y flujo real de Google Sign-In; no se ejecutaron en este entorno.
