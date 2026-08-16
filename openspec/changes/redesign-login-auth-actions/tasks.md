## 1. Presentación de acciones

- [x] 1.1 Reorganizar `AuthScreen` para que Entrar/Crear cuenta sea la única acción primaria, Google sea secundaria y el cambio de modo sea terciario, preservando callbacks existentes.
- [x] 1.2 Sustituir el icono genérico actual de Google por un asset oficial/preaprobado de Sign in with Google o un botón renderizado por Google, conservando su procedencia.
- [x] 1.3 Mantener la composición compatible con Light Theme y Dark Theme usando `AppButton`, `MaterialTheme` y tokens del Design System.
- [x] 1.4 Integrar el contenedor con FindYourPet solo fuera del área de marca y respetar las proporciones, colores, tamaños y espaciados exigidos por Google.
- [x] 1.5 Actualizar las pruebas para exigir el asset oficial y rechazar `Icons.Outlined.AccountCircle` como representación de Google.

## 2. Estado y protección de acciones

- [x] 2.1 Añadir o ajustar estado local de operación Google para deshabilitar acciones durante la solicitud de credenciales.
- [x] 2.2 Mostrar un indicador de carga consistente durante autenticación y evitar submits simultáneos en email/password y Google.
- [x] 2.3 Conservar mensajes de cancelación y error como feedback recuperable, reactivando las acciones cuando la operación finalice.

## 3. Pruebas y validación

- [x] 3.1 Actualizar o crear pruebas estáticas de presentación para verificar jerarquía, callbacks, etiquetas accesibles, estados disabled y ausencia de valores visuales arbitrarios.
- [x] 3.2 Ejecutar `openspec validate "redesign-login-auth-actions" --strict`.
- [x] 3.3 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [ ] 3.4 Realizar revisión manual en emulator/device: login, registro, Google, cancelación/error, carga, doble toque, Light Theme y Dark Theme.
- [x] 3.5 Revisar el diff para confirmar que no se modificaron ViewModel, Firebase, repositorios, permisos ni contratos de autenticación.
