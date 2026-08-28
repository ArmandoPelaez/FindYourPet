## 1. Preparación y alcance

- [x] 1.1 Confirmar que `app/src/main/res/drawable-nodpi/imagen_nuevo_fondo_pantalla.png` está disponible y que la referencia queda limitada a `AuthScreen`.
- [x] 1.2 Revisar la composición actual de `AuthScreen.kt` y preservar el orden de capas, `safeDrawing`, `imePadding`, `verticalScroll`, `AppSpacing.authMaxWidth` y el overlay de legibilidad.

## 2. Implementación visual

- [x] 2.1 Reemplazar únicamente el `painterResource` del `Image` de fondo por `R.drawable.imagen_nuevo_fondo_pantalla`.
- [x] 2.2 Mantener la imagen decorativa sin interacción ni semántica, detrás del overlay y de todo el contenido interactivo.
- [x] 2.3 Verificar que el cambio no introduzca colores, opacidades, tamaños, paddings, radios, offsets ni dependencias hardcodeadas, y que Light/Dark Theme sigan usando los tokens existentes.
- [x] 2.4 Confirmar mediante diff que no se modificaron ViewModel, repositorios, Firebase, navegación, validaciones, permisos ni lógica de autenticación.

## 3. Pruebas automatizadas

- [x] 3.1 Actualizar o agregar aserciones de presentación para verificar el nuevo drawable, su ubicación detrás del contenido y la permanencia del overlay.
- [x] 3.2 Mantener o ejecutar las aserciones existentes de campos, botones, callbacks, estados de autenticación, scroll, IME, semántica y accesibilidad; no cambiar pruebas de lógica sin causa relacionada.
- [x] 3.3 Ejecutar `./gradlew.bat testDebugUnitTest --no-daemon --console=plain`.
- [x] 3.4 Ejecutar `./gradlew.bat assembleDebug --no-daemon --console=plain`.

## 4. Validación manual responsive

- [ ] 4.1 Validar Login en teléfono compacto y teléfono amplio: fondo visible, formulario accesible, campos editables y botones operables.
- [ ] 4.2 Validar Login en tablet: fondo sin deformación problemática, contenido centrado dentro del ancho máximo tokenizado y controles completamente accesibles.
- [ ] 4.3 Validar Light Theme y Dark Theme: contraste suficiente de texto, campos, botones, errores y estados de carga sobre la imagen.
- [ ] 4.4 Abrir el teclado y comprobar que email, contraseña, Entrar, Google y Crear una cuenta siguen alcanzables mediante `imePadding` y scroll.
- [ ] 4.5 Ejecutar login, registro, cancelación/error de Google, validaciones y cambio entre login/registro para confirmar que el fondo no altera los flujos existentes.
- [x] 4.6 Ejecutar `openspec validate "add-login-background-image" --strict` y `git diff --check` antes de cerrar el change.
