## Context

`HomeScreen` actualmente usa un `HorizontalPager` con `PetPostCard`; cada publicación se renderiza dentro de un `Card` que aplica `AppShapes.card`, `AppElevation.card`, superficie propia y padding horizontal/vertical del pager. La navegación autenticada monta `HomeScreen` dentro del `Scaffold` de `MainActivity`, cuyo `bottomBar` es `BottomPrimaryActionBanner`.

El cambio es visual y de layout. La fuente de verdad son la implementación existente, los tokens de `AppSpacing`/`AppShapes`/`AppElevation` y `docs/design-system.md`. No se agregan dependencias, APIs experimentales, datos ni lógica de negocio.

## Goals / Non-Goals

**Goals:**

- Renderizar la publicación como una superficie continua integrada al fondo del feed.
- Quitar únicamente el contenedor externo flotante: márgenes externos de la card, forma redondeada, elevación/sombra y color de superficie independiente.
- Mantener imagen, estado, identidad, información reportada, fecha, scroll interno y acciones actuales.
- Hacer que el contenido del feed pueda continuar visualmente bajo la barra inferior, manteniendo un inset/padding inferior tokenizado para revelar y accionar el final de la publicación.
- Validar compact phone y un viewport más alto, incluyendo el último contenido y los estados de tema.

**Non-Goals:**

- No cambiar `PetPostEntity`, filtros, paginación, carga de imágenes, permisos, ViewModel, repository, backend, navegación de destinos o texto funcional.
- No rediseñar `BottomPrimaryActionBanner` ni la identidad visual de la app.
- No eliminar las cards internas que expresan la sección de información reportada si el requisito solo afecta la card externa de la publicación.

## Decisions

### 1. Eliminar el contenedor externo, conservar la composición interna

Se mantendrá `PetPostCard` como punto de composición para no propagar cambios de API a `HomeScreen` y sus tests, pero su raíz pasará a ser un layout continuo (`Column`/`Box`) sin `Card` externo, `AppShapes.card` ni `AppElevation.card`. La imagen conservará su fuente y escala actuales; la información y acciones seguirán en el mismo orden.

Alternativa descartada: reescribir el feed completo o cambiar el modelo de una publicación. Aumentaría el riesgo y no está pedido por SCRUM-5.

### 2. Integrar el contenido con la superficie existente del tema

El fondo visible será el provisto por el contenedor del feed y `MaterialTheme.colorScheme`; no se introducirá un color nuevo ni un `Color(...)` literal. Los espacios internos seguirán usando tokens existentes. Si el layout necesita un token adicional, se agregará de forma coherente al Design System y se reutilizará en todos los tamaños.

Alternativa descartada: hardcodear paddings/radios para simular una captura concreta. Contradice `docs/design-system.md` y puede romper tema oscuro o tamaños distintos.

### 3. Separar desplazamiento visual e inset de accesibilidad

El shell debe permitir que el contenido del feed ocupe el área visual bajo el `bottomBar`, pero el feed conservará un espacio inferior tokenizado o equivalente a los insets necesarios para que el último texto y las acciones puedan desplazarse hasta una posición completamente visible y accionable. La barra seguirá respetando `navigationBarsPadding()`.

Alternativa descartada: eliminar todo el padding inferior. Haría que el último contenido quedara cubierto por la barra, incumpliendo SCRUM-5.

### 4. Validar comportamiento mediante pruebas de composición y guardrails

Se actualizarán pruebas existentes del home para verificar ausencia de la superficie flotante, preservación de contenido/acciones, scroll y último contenido en viewports compactos y altos. Se agregarán assertions estructurales solo si son observables y no dependen de detalles frágiles del árbol Compose.

## Risks / Trade-offs

- [Riesgo] El cambio de padding del `Scaffold` puede afectar otras rutas principales. → Limitar el ajuste al contenido del home o a una abstracción de inset ya existente y ejecutar la suite de navegación/presentación.
- [Riesgo] El contenido puede quedar parcialmente cubierto por el banner o por la navegación gestual. → Probar el final del scroll en compact phone y viewport alto, usando tokens/insets existentes.
- [Riesgo] Quitar la superficie de la card puede reducir contraste en tema oscuro. → Usar la superficie del tema existente y verificar Light/Dark sin agregar colores nuevos.
- [Riesgo] `HorizontalPager` puede conservar padding que se perciba como margen externo. → Revisar el padding de página y distinguir el espacio necesario para paginación del margen propio de la card; cambiarlo solo si la spec lo exige.

## Migration Plan

1. Implementar el delta en la rama `ops/remove-lost-pet-feed-cards`.
2. Ejecutar tests focalizados, `testDebugUnitTest`, `assembleDebug` y `openspec validate --strict`.
3. Verificar capturas o inspección visual en un viewport compacto y otro alto, en ambos temas cuando el arnés lo permita.
4. Rollback: revertir el cambio de composición/insets y restaurar el contenedor externo usando los tokens existentes; no requiere migración de datos ni backend.

## Open Questions

- El issue no define dimensiones exactas de viewport ni aporta mockup adjunto. Se usarán los tamaños ya cubiertos por el arnés del proyecto y la jerarquía visual actual.
