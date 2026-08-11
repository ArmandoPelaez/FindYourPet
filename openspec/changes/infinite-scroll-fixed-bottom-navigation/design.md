## Context

El shell autenticado de `MainActivity.kt` ya posee un `Scaffold` con `BottomPrimaryActionBanner`, y el componente usa `navigationBarsPadding`, `AppShapes`, `AppElevation`, `AppOpacity.banner` y `AppSpacing`. `HomeScreen` mantiene su propio `Scaffold`; el contenido del feed usa `HorizontalPager` y cada tarjeta usa `verticalScroll`, mientras otras pantallas usan `LazyColumn`.

SCRUM-6 solicita una mejora visual sobre ese modelo existente: el desplazamiento debe continuar hasta los límites del contenido, la navegación inferior debe permanecer fija y el contenido debe poder pasar visualmente por detrás de una superficie parcialmente transparente. La solución no debe introducir paginación, carga adicional, cambios de navegación ni cambios de datos.

## Goals / Non-Goals

**Goals:**

- Mantener la barra inferior en el shell firmado, fija respecto del viewport y por encima del contenido desplazable.
- Permitir que el contenido desplazable llegue de forma verificable al inicio y al final en ambas direcciones.
- Hacer visible el paso del contenido por detrás de la barra mediante la transparencia parcial ya representada por los tokens del sistema.
- Conservar el área de gestos del sistema y la accesibilidad de las acciones inferiores.
- Mantener compatibilidad con Light Theme y Dark Theme sin cambiar la identidad visual.
- Cubrir el comportamiento con pruebas Compose, estáticas o visuales existentes, según el alcance real de los archivos tocados.

**Non-Goals:**

- No implementar paginación, carga infinita de datos, nuevos estados de red ni cambios de repositorio/ViewModel.
- No rediseñar la barra, sus colores, íconos, tipografía, formas o jerarquía.
- No cambiar rutas, back stack, autenticación, Firebase, Room, permisos ni datos sensibles.
- No agregar APIs experimentales, alpha o beta, ni nuevas dependencias.

## Decisions

1. **Conservar el shell `Scaffold` como dueño de la barra inferior.**
   - La barra ya está colocada junto al `NavHost`, que es el nivel correcto para que permanezca fija al cambiar de destino.
   - No se duplicará la barra dentro de cada pantalla.
   - Alternativa descartada: ponerla dentro de cada `LazyColumn` o `verticalScroll`, porque se desplazaría con el contenido y duplicaría insets.

2. **Separar la capa fija de navegación de la capa desplazable.**
   - El contenido de cada destino seguirá usando sus contenedores de scroll actuales (`verticalScroll`, `LazyColumn` o pager) y la barra se renderizará como chrome del shell.
   - Se revisará la aplicación de `shellPadding` en destinos primarios y el padding inferior basado en tokens para que el contenido pueda pasar detrás de la superficie sin quedar permanentemente oculto.
   - La distancia de seguridad para acciones al final del contenido debe provenir de `AppSpacing` y de los insets del sistema, no de valores nuevos en las pantallas.
   - Alternativa descartada: usar offsets o alturas literales específicos por pantalla, porque romperían compact phones, pantallas altas y temas.

3. **Reutilizar la transparencia y el lenguaje visual existentes.**
   - `BottomPrimaryActionBanner` conservará `MaterialTheme.colorScheme`, `AppShapes.card`, `AppElevation` y `AppOpacity.banner`; cualquier ajuste permitido por el criterio de aceptación debe expresarse en tokens existentes o en un token coherente del Design System.
   - La transparencia no se aplicará directamente al contenido ni a los colores de los íconos; solo debe permitir percibir el contenido que pasa por detrás de la superficie.
   - Alternativa descartada: agregar un color o un gradiente nuevo en la pantalla.

4. **Validar límites de scroll sin cambiar el modelo de datos.**
   - Las pruebas deben comprobar que el primer y último contenido permanecen alcanzables y que las acciones finales son visibles/tocables con la barra presente.
   - En el feed, la validación se hará sobre el desplazamiento vertical dentro de la tarjeta y sobre el cambio horizontal de publicaciones existente, sin convertirlo en una fuente paginada.
   - En listas, se conservarán `LazyColumn` y su estado; solo se corregirán insets o padding de presentación cuando sea necesario.

5. **Mantener la solución dentro de APIs estables de Compose y Material 3.**
   - Se usarán APIs estables ya presentes en el proyecto, como `Scaffold`, `WindowInsets`, `navigationBarsPadding`, `verticalScroll` y `LazyColumn`.
   - No se incorporará una dependencia ni se adoptará una API experimental para lograr el efecto.

## Risks / Trade-offs

- **[Riesgo]** El padding del `Scaffold` externo y los `Scaffold` internos puede duplicar espacio o impedir que el contenido pase bajo la barra. → Revisar rutas primarias y secundarias por separado y verificar compact phone y pantalla alta.
- **[Riesgo]** El contenido o una acción final puede quedar cubierto por la superficie transparente. → Mantener un inset inferior semántico basado en `AppSpacing` y cubrir el último elemento con pruebas de scroll y captura visual.
- **[Riesgo]** La barra podría perder contraste en Light o Dark Theme cuando el contenido pasa detrás. → Validar ambos temas y conservar colores de contenido del `MaterialTheme.colorScheme`.
- **[Riesgo]** Interpretar “scroll infinito” como carga de más datos ampliaría el alcance. → Limitar explícitamente la implementación al desplazamiento del contenido existente hasta sus límites.
- **[Riesgo]** Un ajuste visual puede afectar pantallas secundarias que no muestran la barra. → Verificar Create Post, Notifications, Sighting Alert y Chat Detail sin cambiar su lógica de navegación.

## Migration Plan

1. Revisar el shell y cada destino desplazable afectado contra los requisitos de `primary-navigation` y `home-feed-presentation`.
2. Ajustar únicamente la composición de insets, padding de scroll y capas visuales necesarias para mantener la barra fija y parcialmente transparente.
3. Añadir o actualizar pruebas de límites de desplazamiento, visibilidad de acciones y barra fija en Light/Dark Theme.
4. Ejecutar `openspec validate`, tests unitarios/Compose disponibles y `assembleDebug`.
5. Si la validación falla, revertir los cambios de presentación y conservar el comportamiento previo de navegación y datos.

## Open Questions

- Ninguna para iniciar la implementación. Si una pantalla no puede cumplir simultáneamente con el solapamiento visual y la accesibilidad del último control usando los tokens existentes, el implementador debe reportarlo antes de ampliar el Design System.
