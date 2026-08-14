## Context

La publicación principal se compone en `HomeScreen.kt` mediante `PetPostCard`: la imagen ocupa actualmente todo el ancho sin recorte de forma, la acción inline reutiliza `AppButton` en variante outlined con el texto `La vi`, y la barra inferior se compone en `CommonComponents.kt` con tamaños compartidos para sus destinos y una línea superior con tratamiento especial alrededor de `Publicar`.

La referencia visual solicita una ficha más contenida: la foto debe vivir dentro de un marco con proporción y bordes redondeados, la acción debe ser compacta y naranja, y la acción central de la barra debe reducirse ligeramente mientras la separación superior continúa por todo el ancho. El cambio queda limitado a la capa de presentación Android; no requiere migración, backend, permisos ni cambios de lógica.

## Goals / Non-Goals

**Goals:**

- Hacer que el marco de foto respete `AppSpacing.cardImageAspectRatio`, el ancho/inset del contenido y la forma compartida del design system, manteniendo `ContentScale.Crop` y la URI actual de cada publicación.
- Mantener `PERDIDO` como overlay en la esquina superior izquierda, sin cubrir innecesariamente el sujeto principal y usando `PetStatusColors`.
- Presentar `He visto a esta mascota` como etiqueta accionable inline compacta, con fondo naranja basado en el color primario del tema, contraste suficiente y una forma equivalente a la etiqueta de estado.
- Mantener el icono de visibilidad y el callback actuales, incluyendo la elegibilidad de `OwnershipPolicy` y el flujo existente de alerta.
- Reducir de forma tokenizada el círculo y/o icono de `Publicar` sin desplazar los labels ni cambiar destinos.
- Dibujar la separación de la barra como una línea continua de ancho completo, conservando el pozo/realce circular central cuando corresponda al diseño vigente.
- Verificar Light Theme, Dark Theme, compact phones, tamaños de fuente accesibles, estado vacío y contenido largo.

**Non-Goals:**

- No cambiar el origen, carga, fallback o contenido de las fotos.
- No cambiar nombres de mascotas, ubicación, fecha, estado, permisos, navegación, ViewModels, repositorios, Firebase, Room o reglas de negocio.
- No crear una nueva acción de avistamiento ni cambiar la semántica del reporte.
- No introducir colores, tamaños, paddings, radios o alturas literales dentro de las pantallas.
- No usar APIs alpha, beta o experimentales ni agregar dependencias visuales.

## Decisions

1. **El marco de foto será una superficie contenida y recortada con tokens compartidos.**

   La imagen se envolverá en el contenedor de contenido de la tarjeta, se aplicará `aspectRatio(AppSpacing.cardImageAspectRatio)` y se recortará con la forma compartida de tarjeta/foto definida en `AppShapes`. Se conservará la carga de `post.photoUri` y `ContentScale.Crop`; la etiqueta de estado seguirá dentro del mismo `Box` para que acompañe al marco.

   Alternativa considerada: conservar la imagen edge-to-edge y aplicar solo una altura fija. Se descarta porque no reproduce la proporción responsiva de la referencia y puede cortar de manera distinta en anchos compactos.

2. **El CTA usará el sistema de botones, con una variante compacta si hace falta.**

   `InlineSightingButton` seguirá siendo el único punto de interacción y conservará el estado del icono y el callback. Se cambiará el texto visible y la descripción semántica a `He visto a esta mascota`; el contenedor usará un `AssistChip` Material 3 con el color primario/naranja del tema como fondo, contenido contrastante y una forma compacta equivalente a `AppShapes.chip`, mediante el componente reusable `AppActionChip`.

   Alternativa considerada: construir un `Button` local solo para Home. Se descarta porque duplicaría reglas de color, forma, accesibilidad y soporte de temas.

3. **La reducción de `Publicar` se realizará mediante tokens, no mediante escala visual.**

   Se revisarán `bottomNavigationCreateActionSize`, `bottomNavigationCreateIconSize`, `bottomNavigationWellSize` y el lift asociado. Se reducirá el token mínimo necesario para que el plus se acerque a la referencia, manteniendo el área táctil accesible y la alineación de `Publicar` con los otros cuatro destinos. Los labels y destinos permanecen intactos.

   Alternativa considerada: aplicar `scale` al icono o un tamaño literal en `BottomNavigationItemContent`. Se descarta porque puede reducir el área táctil o desalinear el pozo y rompe la intención del Design System.

4. **La línea de separación será responsabilidad de la superficie completa.**

   `BottomNavigationTopDivider` se mantendrá como overlay de la barra, pero su trazo base recorrerá todo el ancho del contenedor. El arco o interrupción alrededor del botón central solo se conservará si forma parte del tratamiento vigente y se resolverá con el mismo color, grosor y tokens; no se agregarán divisores entre destinos. La barra seguirá siendo un rectángulo de ancho completo.

   Alternativa considerada: colocar una línea separada en cada destino. Se descarta porque crea cortes visibles y no satisface el contorno continuo solicitado.

5. **La validación será visual y de contrato, sin tocar lógica de dominio.**

   Se actualizarán pruebas Compose, screenshots y guardrails estáticos para comprobar texto/semántica del CTA, ausencia de `Lo vi`, proporción y forma de la imagen, tamaños tokenizados de `Publicar`, línea continua y conservación de callbacks. Se incluirá revisión manual en tema claro/oscuro, compact phone, estado sin publicaciones y contenido desplazable.

## Risks / Trade-offs

- [Risk] El texto más largo puede competir con el nombre de la mascota o desbordar en anchos reducidos. → Mitigation: usar layout con pesos/constraints, tipografía de label del tema, icono tokenizado y verificar tamaños de fuente grandes en Compose tests y revisión manual.
- [Risk] Un fondo naranja puede perder contraste en alguno de los temas. → Mitigation: usar el par de colores del tema (`primary`/`onPrimary` o token equivalente), validar contraste y capturar ambos temas.
- [Risk] Reducir el botón `Publicar` puede afectar el objetivo táctil. → Mitigation: reducir solo el contenido visual dentro del área/pozo tokenizado, validar semántica y mantener un target accesible.
- [Risk] La línea completa puede solaparse con el arco del botón central. → Mitigation: dibujar primero el trazo continuo y aplicar el tratamiento central de forma controlada con tokens y screenshots.
- [Risk] Las pruebas existentes pueden depender de `La vi` o de los tamaños anteriores. → Mitigation: actualizar aserciones de texto, content descriptions y contratos estáticos sin cambiar las acciones verificadas.
- [Risk] El cambio visual podría afectar la lectura de la última información de la publicación junto a la navegación sticky. → Mitigation: conservar el padding inferior existente y comprobar estado vacío, contenido largo y navegación por gestos.

## Migration Plan

1. Revisar los tokens actuales y el uso de `PetPostCard`, `InlineSightingButton` y `BottomNavigationTopDivider`.
2. Ajustar tokens o componentes reutilizables para la forma/proporción de foto, CTA compacto y acción `Publicar`.
3. Aplicar la composición en Home y en la barra inferior, manteniendo callbacks y destinos.
4. Actualizar specs delta, pruebas Compose/estáticas y screenshots.
5. Ejecutar validación OpenSpec, tests unitarios y `assembleDebug`; completar revisión manual de las referencias en Light/Dark Theme.

No hay migración de datos ni despliegue backend. Para rollback, restaurar los tokens y composiciones anteriores y mantener intactos modelos, datos y flujos funcionales.

## Open Questions

Ninguna. La implementación debe tomar la imagen de referencia como guía visual, pero resolver dimensiones, colores y formas exclusivamente mediante los tokens existentes o nuevos tokens semánticos coherentes con el Design System.
