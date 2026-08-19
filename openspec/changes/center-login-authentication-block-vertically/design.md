## Context

`AuthScreen` ya contiene el hero y el bloque de autenticación dentro de una columna con scroll vertical, `imePadding()` y tokens compartidos. SCRUM-43 cambia únicamente la distribución vertical: el formulario debe dejar de quedar pegado al supporting text y ocupar una posición aproximadamente centrada en el espacio disponible debajo del hero.

## Goals / Non-Goals

**Goals:**

- Mantener el bloque de autenticación como una unidad visual.
- Crear separación flexible entre hero y formulario, adaptable a la altura disponible.
- Mantener accesibilidad, scroll, comportamiento del IME y todos los callbacks existentes.
- Reutilizar `AppSpacing`, componentes y estructura de Compose existentes.

**Non-Goals:**

- No cambiar textos, colores, tipografías, tamaños, anchos, shapes, fondo, hero o identidad.
- No modificar ViewModel, Firebase Auth, navegación, repositorios, backend, dependencias ni contratos.
- No agregar offsets fijos, márgenes específicos por dispositivo, nuevos métodos de autenticación ni nuevos controles.

## Decisions

### Separar el hero del bloque de autenticación mediante distribución flexible

La composición conservará dos regiones: hero y bloque de autenticación. La separación se resolverá con el mecanismo de distribución/adaptación de Compose que mejor soporte espacio sobrante y overflow, evitando `offset(y = ...)` y valores visuales hardcodeados. Se debe preservar el orden interno y el spacing actual del bloque.

Alternativas consideradas:

- `offset(y = ...)`: descartada porque no responde a distintas alturas y contradice el Scrum.
- Margen vertical fijo: descartado porque produce el mismo problema en pantallas pequeñas y con IME.
- Reescritura de la pantalla: descartada porque amplía el riesgo y puede alterar identidad o comportamiento.

### Conservar el scroll y el comportamiento del teclado

La raíz seguirá permitiendo `verticalScroll()` e `imePadding()`. La distribución flexible no debe depender de que todo el contenido quepa: cuando no exista espacio sobrante, el contenido debe conservar su orden natural y poder desplazarse hasta Email, Contraseña y las acciones.

### Validar sin modificar la lógica de autenticación

Las pruebas se enfocarán en la estructura de presentación: agrupación, ausencia de offsets hardcodeados, uso de tokens, scroll/IME y preservación de labels/callbacks. La validación manual comprobará alturas estándar, pantalla pequeña, teclado abierto y las tres acciones existentes.

## Risks / Trade-offs

- [Riesgo] Un centrado rígido puede ocultar el formulario cuando el viewport se reduce. → [Mitigación] conservar scroll/IME y validar con teclado y pantalla pequeña.
- [Riesgo] Añadir spacing nuevo puede romper Design System. → [Mitigación] reutilizar tokens existentes y no introducir valores `dp` locales.
- [Riesgo] La distribución vertical puede alterar estados de registro o error. → [Mitigación] mantener el bloque y sus estados dentro de la misma composición y ejecutar tests/build.

## Migration Plan

1. Implementar la distribución vertical en `AuthScreen` y ajustar únicamente pruebas de presentación.
2. Validar OpenSpec, tests unitarios, build debug y revisión manual responsive.
3. Si el resultado visual o el comportamiento con IME falla, revertir el commit del change; no requiere migración de datos ni rollback de backend.

## Open Questions

- Ninguna para iniciar. La implementación debe elegir la variante de layout que cumpla simultáneamente centrado aproximado, scroll y `imePadding()`.
