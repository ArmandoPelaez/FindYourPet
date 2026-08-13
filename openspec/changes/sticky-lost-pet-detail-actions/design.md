## Context

El shell autenticado de `MainActivity.kt` ya posee un `Scaffold` y `BottomPrimaryActionBanner`, pero actualmente la barra solo se muestra en las rutas primarias. Además, `HomeScreen.kt` renderiza el CTA `¡Lo he visto!` dentro del `verticalScroll` de `PetPostCard`, por lo que el CTA se desplaza junto con la publicación.

La solución debe conservar el diseño existente de `BottomPrimaryActionBanner`, mantener la navegación global sticky y trasladar la acción de avistamiento a un control inline junto al nombre. Debe trabajar con Material 3 estable y usar los tokens existentes de `AppSpacing`, `AppShapes`, `AppElevation`, `AppOpacity`, `PetStatusColors` y `MaterialTheme`. No se cambian datos, navegación funcional, elegibilidad ni lógica de negocio.

## Goals / Non-Goals

**Goals:**

- Mantener la barra de navegación principal sticky en todas las rutas autenticadas.
- Eliminar el CTA sticky contextual anterior.
- Presentar `La vi` inline, alineado a la derecha del nombre de la mascota, dentro del contenido de la publicación.
- Mantener el ojo cerrado inicialmente y cambiarlo a abierto antes de invocar el callback de navegación al flujo de alerta.
- Colocar la etiqueta de estado en la esquina superior izquierda de la foto con el color semántico de estado definido por el sistema.
- Mantener la ubicación como primer metadato y mostrar debajo `Última vez visto` con calendario y fecha en la misma fila.
- Usar una proporción de imagen definida como token para aproximar la referencia en distintos anchos.
- Permitir que el último contenido de la publicación se vea y toque completamente.
- Respetar el área de gestos, safe areas y los temas claro y oscuro.
- Mantener los destinos, callbacks y reglas actuales para el CTA y la navegación.

**Non-Goals:**

- No agregar una nueva ruta o una nueva `PetDetailScreen`.
- No modificar `PetViewModel`, repositories, Room, Firebase, permisos o contratos de datos.
- No cambiar la elegibilidad del CTA, su texto, su acción ni los destinos de navegación.
- No rediseñar colores, tipografía, forma, iconos o jerarquía visual.
- No introducir dependencias ni APIs alpha, beta o experimentales.

## Decisions

1. **El shell autenticado será el dueño de la navegación sticky.**

   `SignedInPetAppNavigation` seguirá renderizando `BottomPrimaryActionBanner` como `bottomBar` del `Scaffold` externo, pero sin limitarlo a `PRIMARY_DESTINATION_ROUTES`. El banner aparecerá en todas las rutas autenticadas, conservando sus callbacks y destinos.

   Alternativa descartada: duplicar la barra dentro de cada pantalla, porque produciría inconsistencias, doble inset y barras desplazables.

2. **El padding del shell tendrá un único dueño.**

   El `PaddingValues` del `Scaffold` externo se aplicará al contenedor del `NavHost`. Las envolturas de rutas que hoy aplican el mismo `shellPadding` se simplificarán para evitar doble espacio inferior. Los destinos internos conservarán sus propios insets de contenido cuando sean necesarios, pero no volverán a reservar la altura de la barra global.

   Alternativa descartada: usar offsets o alturas literales para compensar la barra, porque rompería compact phones, pantallas altas y navegación por gestos.

3. **La acción de avistamiento será inline junto al nombre.**

   Se eliminará `SightingActionBar` del `bottomBar` interno de `HomeScreen`. `PetPostCard` recibirá el callback y la elegibilidad actuales, y `PetIdentitySection` renderizará el botón `La vi` junto al nombre cuando corresponda.

   El callback seguirá invocando `viewModel.selectPost(post.id)` y `onNavigateToAlert(post.id)`. El estado local del icono se abrirá al pulsar, antes de ejecutar el callback.

   Alternativa descartada: conservar un `bottomBar` contextual o fijar el control con overlay, porque contradice la referencia y mantiene una segunda superficie fija innecesaria.

4. **Estado, foto y metadatos usarán tokens existentes.**

   `PetStatusChip` conservará `PetStatusColors`, pero su uso en la publicación se alineará arriba a la izquierda y usará el tratamiento visual de etiqueta de la referencia. La foto usará `AppSpacing.cardImageAspectRatio`; la fila de ubicación/fecha usará `AppSpacing` y los iconos Material existentes.

5. **La validación comprobará composición y no lógica de dominio.**

   Se actualizarán pruebas estáticas/Compose para verificar que la barra se declara en el shell, que el CTA queda fuera del bloque scrollable, que los callbacks siguen presentes y que el contenido final permanece accesible. Se cubrirán Light Theme y Dark Theme con los harnesses existentes.

## Risks / Trade-offs

- **[Risk]** Mostrar la barra global en rutas secundarias puede reducir el espacio disponible para formularios, chats o notificaciones. → Aplicar el padding del shell una sola vez y verificar las rutas autenticadas existentes.
- **[Risk]** El callback puede ejecutarse antes de que el cambio de icono sea perceptible durante la navegación. → Actualizar el estado local primero y conservar la navegación inmediatamente después.
- **[Risk]** Un doble uso de `Scaffold` puede duplicar insets. → Mantener el shell externo como dueño de la navegación y el `Scaffold` de Home como dueño exclusivo del CTA contextual.
- **[Risk]** El CTA puede quedar visible cuando el post no permite avistamientos. → Reutilizar exactamente `OwnershipPolicy.canReportSighting` y la condición de estado existente.
- **[Risk]** La superficie sticky puede ocultar el último texto. → Reservar padding inferior con tokens y verificar el último elemento en scroll y captura visual.

## Migration Plan

1. Ajustar el shell autenticado y centralizar la aplicación de `shellPadding`.
2. Reemplazar el `bottomBar` contextual por el botón inline y actualizar la composición de `PetPostCard`.
3. Ajustar etiqueta, proporción de foto y metadatos de ubicación/fecha usando tokens documentados.
4. Actualizar pruebas de presentación y contratos estáticos sin modificar ViewModel ni datos.
5. Ejecutar `openspec validate`, tests unitarios/Compose y `assembleDebug`.
6. Si la validación falla, restaurar la composición anterior del shell y del CTA; no requiere migración de datos ni rollback de backend.

## Visual navigation refinement

The shared authenticated navigation follows the reference order: `Inicio`,
`Perfil`, `Publicar`, `Mensajes`, `Alertas`. Each item has a visible label and
uses the theme primary color when selected, with `onSurfaceVariant` for inactive
items. `Publicar` keeps the centered filled circular action treatment. The
existing unread badge and Notifications destination move to `Alertas`; Home no
longer duplicates that action in its top app bar.

## Visual navigation bank refinement

The navigation surface is a full-width bank rather than a floating card. Its
gesture-area padding stays inside the surface. `Publicar` is placed over a
token-sized circular well with a small upward lift, while the other icons use a
smaller square token to keep them visually delicate without distortion.

The bank uses a compact height token. All destinations share an icon slot so
their labels align on one baseline; the Publicar action is lifted independently
inside that slot. Its well uses the theme surface color and shared elevation to
create the dark extension/shadow visible behind the coral plus button.

The bank itself is an unrounded rectangle that spans the complete width. Rounded
navigation shapes are intentionally excluded from this component.

## Open Questions

Ninguna. El usuario confirmó la referencia visual, el reemplazo del CTA sticky por `La vi`, el comportamiento del ojo y la conservación del flujo de alerta.
