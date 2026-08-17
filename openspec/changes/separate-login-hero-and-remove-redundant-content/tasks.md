## 1. Preparación y composición

- [x] 1.1 Revisar `AuthScreen.kt`, `docs/design-system.md` y las pruebas de presentación existentes para confirmar tokens y contratos que deben permanecer intactos.
- [x] 1.2 Separar en la composición del Login el bloque hero —identidad, headline y supporting text— del bloque formulario sin agregar superficies ni componentes visuales nuevos.
- [x] 1.3 Aplicar entre hero y formulario el spacing existente del Design System y conservar la jerarquía tipográfica actual sin valores hardcodeados.

## 2. Contenido y comportamiento

- [x] 2.1 Mantener `Conectá con avisos cerca tuyo.` y `Reportá, buscá y ayudá a reencontrar mascotas.` agrupados y visibles como contenido del hero.
- [x] 2.2 Mantener `Iniciar sesión` asociado directamente con Email, Contraseña y las acciones existentes.
- [x] 2.3 Eliminar en modo Login el supporting text redundante y no agregar un subtítulo equivalente; conservar mensajes funcionales de validación y error.
- [x] 2.4 Verificar que callbacks, navegación, orden de foco, semantics, scroll, `imePadding()` y modo registro permanezcan sin cambios.

## 3. Cobertura automatizada

- [x] 3.1 Actualizar o agregar pruebas de presentación que verifiquen el orden hero → separación → formulario y la presencia del supporting text aprobado.
- [x] 3.2 Agregar una aserción de que el subtítulo redundante del modo Login no se renderiza, sin eliminar las validaciones de campos.
- [x] 3.3 Verificar mediante pruebas estáticas o existentes que no se introduzcan `Color(...)`, tamaños `sp` arbitrarios, spacing `dp` hardcodeado, APIs experimentales, cards o divisores nuevos.

## 4. Validación final

- [x] 4.1 Ejecutar `openspec validate "separate-login-hero-and-remove-redundant-content" --strict`.
- [x] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest` y `./gradlew.bat assembleDebug`.
- [x] 4.3 Revisar manualmente el Login en pantalla pequeña, Light Theme, Dark Theme y teclado abierto; confirmar separación, legibilidad, acceso a campos/acciones y ausencia de regresiones.
- [x] 4.4 Ejecutar `git diff --check`, revisar el diff contra el alcance del Scrum y registrar cualquier limitación de dispositivo/emulador.
