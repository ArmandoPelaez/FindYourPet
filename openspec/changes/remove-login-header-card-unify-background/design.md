## Context

`AuthScreen.kt` actualmente compone el Login con una imagen de fondo, una capa de legibilidad y una `Surface` superior que contiene el hero y el encabezado del formulario. Esa superficie usa shape, elevación y color propios, por lo que introduce la separación visual que SCRUM-39 solicita eliminar.

El cambio es exclusivamente de presentación. Debe conservar los estados visuales y funcionales existentes, los tokens de `AppSpacing`, `AppTypography`, `AppShapes`, `AppElevation` y `AppOpacity`, el comportamiento de scroll/IME y el fondo aprobado. `docs/design-system.md` es la fuente de verdad para cualquier decisión visual.

## Goals / Non-Goals

**Goals:**

- Quitar la superficie contenedora superior del Login.
- Mantener identidad, textos del hero, encabezado del formulario y separación visual mediante layout tokenizado.
- Preservar Email, Contraseña, Entrar, Google, Crear una cuenta, navegación, estados de carga/error y accesibilidad.
- Mantener funcionamiento en Light Theme, Dark Theme, tamaños pequeños y teclado abierto.

**Non-Goals:**

- Cambiar autenticación, ViewModel, repositorios, Firebase o navegación.
- Cambiar el asset de fondo, textos, campos, botones o animaciones.
- Introducir colores, dimensiones, shapes, elevaciones u opacidades nuevas.

## Decisions

### 1. Sustituir la card por layout transparente

Se eliminará el `Surface` de `AuthScreen.kt` y se conservará su `Column` como un bloque de layout sin superficie visual. Así el contenido permanece visible directamente sobre las capas de fondo ya existentes.

Alternativas consideradas:

- Cambiar la card por otra `Card` o `Surface` transparente con shape/elevación: descartado porque mantendría un contenedor equivalente o una semántica visual innecesaria.
- Eliminar también el contenido del hero: descartado porque contradice los criterios de aceptación.

### 2. Conservar spacing y jerarquía existentes

El spacing entre hero y formulario seguirá proviniendo de `AppSpacing.formGap` y de los paddings ya definidos, ajustándose solo si el implementador demuestra que el padding interno de la antigua card era indispensable para evitar solapamiento. No se agregarán valores `dp` ni estilos tipográficos locales.

Alternativas consideradas:

- Agregar márgenes arbitrarios para compensar la eliminación: descartado por las Design Rules.
- Rediseñar tipografía o componentes del formulario: descartado por estar fuera de alcance.

### 3. Mantener la estructura de accesibilidad

Se conservará el orden composable del contenido y no se modificarán labels, campos, botones, focus order ni content descriptions. El cambio no añade elementos interactivos.

## Risks / Trade-offs

- [El contenido podría quedar más compacto o perder separación visual] → Mantener `Arrangement.spacedBy` y revisar en emulador con viewport pequeño y teclado abierto.
- [La legibilidad podría variar al eliminar la superficie] → Conservar las capas de fondo existentes y validar Light/Dark; no inventar una nueva opacidad.
- [Una prueba visual existente podría depender del nodo de la superficie] → Revisar tests de Login y actualizar solo aserciones estructurales que describan la card eliminada, sin cambiar comportamiento.

## Migration Plan

1. Implementar el cambio en `AuthScreen.kt` y ajustar únicamente pruebas UI si existen aserciones sobre la card.
2. Ejecutar `testDebugUnitTest`, `assembleDebug` y validación OpenSpec.
3. Validar manualmente Login, teclado, scroll, interacción de campos y continuidad del fondo.
4. Rollback: revertir el diff de la rama; no hay migraciones ni datos persistentes.

## Open Questions

- Ninguna para iniciar la implementación. Si la eliminación requiere un nuevo token visual, el implementador debe detenerse y reportar la necesidad antes de inventarlo.
