## Context

`AuthScreen.kt` ya renderiza la identidad FindYourPet, el headline y el supporting text aprobados sobre el fondo continuo, pero actualmente coloca el encabezado del formulario dentro de la misma columna compacta del hero y muestra un supporting text adicional debajo del título funcional. SCRUM-41 solicita que el hero y el formulario se perciban como áreas distintas sin introducir una nueva superficie visual.

El cambio es exclusivamente de presentación en Compose. Debe respetar `docs/design-system.md`, la composición existente, Light/Dark Theme, scroll vertical y `imePadding()`. No modifica autenticación, estado, navegación, persistencia ni backend.

## Goals / Non-Goals

**Goals:**

- Mantener identidad, headline y supporting text del hero agrupados.
- Crear una separación visual clara entre el final del hero y el encabezado del formulario usando spacing existente.
- Mantener `Iniciar sesión` asociado con Email, Contraseña y las acciones.
- Eliminar el supporting text redundante del modo Login.
- Conservar accesibilidad, orden de foco, responsive, scroll/IME y comportamiento de autenticación.

**Non-Goals:**

- No rediseñar campos, botones, Google Sign-In, navegación o ViewModel.
- No cambiar el headline, supporting text aprobado, identidad, fondo o identidad visual.
- No agregar card, divisor, superficie, elevación o componente nuevo para separar áreas.
- No introducir colores, tamaños, paddings, márgenes, radios o spacing hardcodeados.
- No modificar el contenido explicativo propio del modo registro salvo que una prueba existente requiera preservar su composición.

## Decisions

1. **Separar la composición por bloques semánticos dentro de `AuthScreen`.**
   - El bloque hero contendrá identidad, headline y supporting text.
   - El bloque formulario comenzará con `Iniciar sesión` y continuará con los campos y acciones existentes.
   - Alternativa descartada: conservar una sola columna compacta, porque mantiene la ambigüedad visual que SCRUM-41 busca eliminar.

2. **Usar spacing y tipografía existentes del Design System.**
   - La separación se resolverá con `AppSpacing` ya disponible y la jerarquía de `MaterialTheme.typography` existente.
   - Alternativa descartada: agregar un nuevo valor `dp` o un token específico sin evidencia de necesidad; el ticket prohíbe valores arbitrarios.

3. **Eliminar solo el texto redundante del modo Login.**
   - El encabezado `Iniciar sesión` permanecerá como introducción autosuficiente del formulario.
   - No se agregará otro texto equivalente entre el encabezado y Email.
   - Los mensajes funcionales de validación y error de los campos permanecerán.
   - Alternativa descartada: reemplazar el subtítulo por una redacción nueva, porque contradice el alcance del Scrum.

4. **Mantener el contenedor continuo y la interacción existente.**
   - No se crearán cards ni divisores; el fondo, scroll, `imePadding`, orden de foco y callbacks permanecerán sin cambios.
   - Alternativa descartada: separar con superficies, porque contradice SCRUM-41 y el cambio integrado de fondo continuo.

## Risks / Trade-offs

- [Risk] Un spacing insuficiente puede volver a mezclar visualmente hero y formulario. → Mitigation: reutilizar el token de separación existente apropiado y validar en teléfono pequeño y con teclado abierto.
- [Risk] Un spacing excesivo puede empujar acciones fuera de la pantalla inicial. → Mitigation: conservar scroll vertical, `imePadding()` y validar accesibilidad de campos y acciones.
- [Risk] Una eliminación amplia podría afectar el modo registro o mensajes funcionales. → Mitigation: limitar la eliminación al supporting text del modo Login y conservar validaciones/errors.
- [Risk] Pruebas estáticas existentes pueden depender del orden de composición. → Mitigation: actualizar solo aserciones de presentación necesarias y mantener contratos de autenticación y Design System.

## Migration Plan

1. Aplicar el ajuste de composición y el retiro del texto redundante en la rama del change.
2. Actualizar o agregar cobertura de presentación para orden hero → separación → formulario y ausencia del subtítulo Login.
3. Ejecutar validación OpenSpec, tests unitarios y `assembleDebug`.
4. Realizar revisión visual en pantalla pequeña, Light/Dark y teclado abierto si hay emulador/dispositivo disponible.
5. Rollback: revertir los cambios de `AuthScreen.kt` y sus pruebas; no requiere migraciones ni cambios remotos.

## Open Questions

- La implementación actual usa `AppSpacing.formGap` y paddings existentes; el implementador debe confirmar cuál combinación tokenizada consigue la separación requerida sin crear un token nuevo.
- La validación manual con dispositivo/emulador queda condicionada a que el entorno de ejecución esté disponible.
