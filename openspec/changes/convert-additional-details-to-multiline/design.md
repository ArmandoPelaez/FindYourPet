## Context

`CreatePetPostScreen` ya mantiene el estado `recognitionDetails` y lo envía al flujo existente de creación como `features`. El campo se muestra como un `OutlinedTextField` multilinea parcial, pero conserva la etiqueta y el texto de ayuda anteriores, no comunica el límite permitido y su actualización no está acotada a 500 caracteres.

El cambio es exclusivamente de presentación y entrada en la pantalla de creación. Debe respetar `MaterialTheme`, `AppFormTypography`, `FormFieldLabel`, `FormFieldPlaceholder`, `AppShapes`, `AppSpacing` y los temas claro/oscuro definidos por el proyecto.

## Goals / Non-Goals

**Goals:**

- Mostrar `Descripcion adicional` como etiqueta del campo existente.
- Mostrar `Contanos cómo reconocerla...` como placeholder cuando el campo está vacío.
- Mantener una entrada multilinea con la altura y el espaciado establecidos por el Design System.
- Limitar el texto aceptado a 500 caracteres desde `onValueChange`.
- Mostrar un contador discreto `actual/500` como supporting text.
- Mantener exactamente el mapeo existente de `recognitionDetails` hacia `features`.
- Cubrir la presentación, el límite y el mapeo con pruebas enfocadas.

**Non-Goals:**

- No agregar otro campo ni modificar `PetPostEntity`, Room, Firestore, mappers o ViewModel.
- No cambiar la validación de nombre, foto, ubicación o autenticación.
- No modificar permisos, ubicación, cámara, galería, navegación ni el flujo de publicación.
- No introducir colores, tamaños, paddings o radios hardcodeados.

## Decisions

1. **Reutilizar `recognitionDetails` y `features`.**
   - La persistencia del campo ya existe y SCRUM-12 solicita modificarlo, no agregar otro dato.
   - Alternativa descartada: crear un nuevo atributo, porque duplicaría información y requeriría cambios de modelo y migración.

2. **Aplicar el límite en `onValueChange`.**
   - El estado local conservará como máximo los primeros 500 caracteres, evitando que el formulario y la persistencia reciban valores fuera del alcance.
   - Alternativa descartada: limitar solo en el ViewModel, porque el contador dejaría de representar el valor que el usuario puede introducir en pantalla.

3. **Usar `supportingText` para el contador.**
   - El contador queda asociado semánticamente al campo y puede actualizarse junto con el texto sin agregar componentes visuales nuevos.
   - Se reutilizarán color y tipografía del tema o tokens existentes; no se agregará un color de advertencia mientras el valor esté dentro del límite.
   - Alternativa descartada: un texto independiente fuera del campo, porque debilita la relación visual y puede perderse durante el scroll.

4. **Conservar la geometría existente del formulario.**
   - Se mantiene `AppSpacing.formFieldHeight`, `AppShapes.chip`, `AppFormTypography.input` y los colores de `OutlinedTextFieldDefaults` ya usados por la pantalla.
   - Alternativa descartada: definir una altura o tipografía nueva en la pantalla, porque contradice el Design System.

5. **Verificar ambos temas mediante componentes existentes.**
   - El campo usará `MaterialTheme.colorScheme` y los tokens de formulario, por lo que Light Theme y Dark Theme conservarán contraste sin lógica específica por tema.

## Risks / Trade-offs

- [Risk] Usuarios con textos existentes de más de 500 caracteres podrían ver el valor truncado al editarlo. → Mitigation: el límite se aplica solo a la entrada nueva; la persistencia existente no se migra ni se elimina, y el contador comunica el límite.
- [Risk] El supporting text puede aumentar la altura percibida del campo. → Mitigation: mantener la altura, espaciado y tipografía definidos por los tokens actuales y validar el scroll en tamaños de teléfono soportados.
- [Risk] Una prueba estática podría validar solo textos y no el límite real. → Mitigation: combinar assertions estáticas del contrato visible con una prueba enfocada de la transformación de entrada/mapeo existente.

## Migration Plan

No hay migración de datos ni cambios de backend. Implementar la modificación en `CreatePetPostScreen`, actualizar pruebas y ejecutar OpenSpec, tests unitarios y `assembleDebug`.

Rollback: restaurar la etiqueta, placeholder, supporting text y comportamiento de entrada anteriores; no requiere modificar datos almacenados.

## Open Questions

Ninguna. El límite, el placeholder, la etiqueta y el alcance están definidos por SCRUM-12.
