## 1. Preparación y columna principal

- [x] 1.1 Revisar `AuthScreen.kt`, `DesignTokens.kt`, `CommonComponents.kt`, `docs/design-system.md` y pruebas de presentación para identificar los tokens y variantes existentes.
- [x] 1.2 Ajustar el contenedor común del Login para que hero, formulario y acciones compartan una columna horizontal contenida usando únicamente tokens existentes.
- [x] 1.3 Verificar márgenes laterales, ancho máximo, `fillMaxWidth`, scroll e IME en pantallas pequeñas y anchas sin introducir valores hardcodeados.

## 2. Jerarquía de acciones

- [x] 2.1 Confirmar o ajustar `Entrar` para que use exclusivamente la variante primaria existente y conserve callback, loading, enabled state y semantics.
- [x] 2.2 Confirmar o ajustar `Continuar con Google` para que use una variante secundaria existente, mantenga el asset oficial y conserve callback, loading y errores.
- [x] 2.3 Confirmar o ajustar `Crear una cuenta` para que permanezca como acción terciaria sin superficie primaria ni competencia visual con Entrar.
- [x] 2.4 Verificar que el color de énfasis, shapes, elevaciones y tipografías provengan de componentes/tokens existentes.
- [x] 2.5 Ajustar el ritmo vertical con tokens existentes para elevar y compactar el hero, reducir separaciones entre hero, título, campos y acciones, y reducir el espacio inferior excesivo.
- [x] 2.6 Mantener los controles existentes agrupados visualmente en el centro y confirmar que no se agreguen `Recordarme`, recuperación de contraseña ni textos adicionales de la referencia.
- [ ] 2.7 Preservar `verticalScroll()` e `imePadding()` y verificar que la composición compacta siga siendo usable con teclado abierto y en pantallas pequeñas.

## 3. Cobertura automatizada

- [x] 3.1 Actualizar o agregar pruebas de presentación para la columna común y el límite de ancho tokenizado.
- [x] 3.2 Agregar aserciones para la jerarquía primaria/secundaria/terciaria y la preservación del branding oficial de Google.
- [x] 3.3 Verificar con pruebas estáticas que no se introduzcan anchos, `maxWidth`, colores, spacing, paddings, tamaños o APIs experimentales hardcodeados.
- [x] 3.4 Verificar que callbacks, navegación, foco, labels, semantics y estados de autenticación existentes permanezcan presentes.

## 4. Validación final

- [x] 4.1 Ejecutar `openspec validate "adjust-login-width-and-action-hierarchy" --strict`.
- [x] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [ ] 4.3 Revisar manualmente anchos y márgenes en pantalla pequeña y ancha, Light/Dark Theme, teclado abierto e interacción con Entrar, Google y Crear una cuenta.
- [x] 4.4 Ejecutar `git diff --check`, revisar el diff contra SCRUM-42 y documentar cualquier limitación de dispositivo/emulador.

> Reparación aplicada: la columna raíz inicia desde arriba con ritmo compacto; no se usa la captura anterior como evidencia de validación visual.

> Limitación de validación manual: `adb` no está disponible en este entorno; la comprobación concluyente de pantalla pequeña, teclado abierto y Light Theme permanece pendiente.
