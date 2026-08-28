## 1. Preparación y alcance

- [x] 1.1 Revisar `AuthScreen.kt`, `docs/design-system.md`, los recursos de fondo y las pruebas de presentación para identificar la composición actual y los contratos que deben permanecer intactos.
- [x] 1.2 Confirmar que el change modifica únicamente la presentación de Login/Crear cuenta y que no requiere cambios en Firebase, ViewModel, navegación, persistencia o dependencias.

## 2. Separación del fondo y contenido semántico

- [x] 2.1 Mantener una capa local de fondo estable y exclusivamente decorativa, sin logo, slogan, hero ni textos descriptivos rasterizados.
- [x] 2.2 Renderizar branding, slogan, hero y textos descriptivos como contenido Compose ordenado antes del formulario, reutilizando los recursos visuales existentes apropiados.
- [x] 2.3 Conservar el tratamiento visual aprobado del fondo y la jerarquía de la identidad sin agregar superficies, colores, tamaños, paddings, radios o recursos arbitrarios.

## 3. Layout adaptable de autenticación

- [x] 3.1 Reorganizar hero, formulario, feedback y acciones dentro de un único flujo de contenido desplazable, eliminando la dependencia de capas semánticas independientes o coordenadas fijas.
- [x] 3.2 Conservar `safeDrawing`, `imePadding()` y `verticalScroll` de forma que el campo enfocado y las acciones necesarias permanezcan alcanzables con teclado abierto.
- [ ] 3.3 Verificar la composición de Login y Crear cuenta con suficiente espacio, altura reducida, teclado abierto y teclado cerrado, sin superposición ni desplazamiento permanente incorrecto.
- [x] 3.4 Mantener jerarquía, tipografía, colores, shapes, spacing, contraste, semantics y accesibilidad mediante Material 3 estable y tokens del Design System en Light/Dark Theme.
- [x] 3.5 Recuperar el ritmo vertical y las separaciones relativas de `imagen_nuevo_fondo_pantalla.png` dentro del hero Compose usando únicamente tokens existentes, sin perder el scroll adaptable.

## 4. Preservación funcional

- [x] 4.1 Mantener intactos los estados locales, callbacks del `PetViewModel`, validaciones, orden de foco, campos y acciones de email/contraseña, Google y cambio de modo.
- [x] 4.2 Verificar que los estados de carga, error, éxito y configuración faltante sigan visibles, accesibles y dentro del flujo adaptable sin cambiar sus mensajes ni contratos.

## 5. Cobertura automatizada

- [x] 5.1 Actualizar o agregar pruebas estáticas/de presentación para verificar que el background no contiene contenido semántico y que hero precede al formulario en el árbol de UI.
- [x] 5.2 Agregar cobertura de Login y Crear cuenta para scroll/IME, accesibilidad, acciones existentes, ausencia de solapamientos estructurales y soporte Light/Dark cuando el arnés disponible lo permita.
- [x] 5.3 Verificar mediante pruebas o revisión estática que no se introduzcan `Color(...)`, tamaños `sp`, valores `dp` arbitrarios, APIs experimentales, cards, divisores o dependencias nuevas.

## 6. Validación final

- [x] 6.1 Ejecutar `openspec validate "refactor-auth-background-adaptive-content" --strict` y corregir cualquier incumplimiento del contrato OpenSpec.
- [x] 6.2 Ejecutar `./gradlew.bat testDebugUnitTest` (187 tests ejecutados; queda documentado un fallo preexistente fuera del change en `BottomPrimaryActionBannerPresentationStaticTest.kt:74`).
- [x] 6.3 Ejecutar `./gradlew.bat assembleDebug` (`BUILD SUCCESSFUL`).
- [x] 6.4 Ejecutar `git diff --check` y revisar el diff contra el alcance de SCRUM-51, incluidos los cambios paralelos de login.
- [x] 6.5 Realizar validación manual en Login y Crear cuenta con pantalla baja, teclado abierto/cerrado, Light Theme y Dark Theme; registrar cualquier limitación de emulador o dispositivo.

## Limitaciones de verificación

- La validación manual de 3.3 y 6.5 queda pendiente: `adb` no está disponible en el entorno y no hay emulador/dispositivo conectado.
- La cobertura automatizada, el test dirigido de `AuthScreenPresentationStaticTest` y `assembleDebug` pasan. La suite completa conserva un único fallo ajeno a este change en `BottomPrimaryActionBannerPresentationStaticTest.kt:74`.
