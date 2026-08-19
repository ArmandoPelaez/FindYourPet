## 1. Reconocimiento y estructura

- [x] 1.1 Revisar `AuthScreen.kt`, `AuthScreenPresentationStaticTest.kt`, `DesignTokens.kt` y `docs/design-system.md` para identificar la columna actual, tokens y preservación de `verticalScroll()`/`imePadding()`.
- [x] 1.2 Delimitar el hero y el bloque completo de autenticación como regiones visuales independientes sin alterar sus controles internos.

## 2. Distribución vertical responsive

- [x] 2.1 Implementar separación flexible entre hero y bloque de autenticación usando distribución Compose adaptable, sin `offset(y = ...)`, márgenes arbitrarios ni valores hardcodeados por dispositivo.
- [x] 2.2 Mantener el bloque de autenticación aproximadamente centrado en el espacio disponible cuando la altura lo permita.
- [x] 2.3 Mantener el orden, spacing interno, anchos, estilos, jerarquía y callbacks existentes de Email, Contraseña, Entrar, Google y Crear una cuenta.
- [x] 2.4 Preservar `verticalScroll()` e `imePadding()` y asegurar que la composición no oculte campos ni acciones en alturas pequeñas o con teclado abierto.

## 3. Pruebas de presentación

- [x] 3.1 Actualizar o agregar pruebas estáticas que cubran la separación/agrupación vertical y la ausencia de `offset(y = ...)` o espaciado hardcodeado.
- [x] 3.2 Verificar por pruebas que se conservan tokens, scroll, IME, labels, semantics, callbacks y estados de autenticación.
- [x] 3.3 Verificar que no se introducen controles, textos, estilos, colores, tamaños, dependencias ni APIs experimentales fuera de SCRUM-43.

## 4. Validación final

- [x] 4.1 Ejecutar `openspec validate "center-login-authentication-block-vertically" --strict`.
- [x] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest --no-daemon --console=plain` y `./gradlew.bat assembleDebug --no-daemon --console=plain`.
- [x] 4.3 Validar manualmente pantalla estándar, pantalla pequeña, Light/Dark Theme, teclado abierto e interacción con Email, Contraseña, Entrar, Google y Crear una cuenta.
- [x] 4.4 Ejecutar `git diff --check`, revisar el diff contra SCRUM-43 y documentar limitaciones de dispositivo/emulador.

> La validación manual de `Medium_Phone` fue ejecutada después de reconstruir e instalar la APK. `Small_Phone` quedó offline; teclado abierto y Light Theme independiente no produjeron evidencia concluyente.
