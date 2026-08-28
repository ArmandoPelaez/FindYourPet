## Context

`AuthScreen` ya compone un `Box` de pantalla completa con insets del sistema, soporte para teclado, una capa de fondo y una imagen decorativa detrás de la columna desplazable de login. Actualmente la imagen usa `R.drawable.imagen_fondo_pantalla_login`; el nuevo recurso local disponible es `R.drawable.imagen_nuevo_fondo_pantalla`.

El cambio es exclusivamente de presentación. Debe respetar `docs/design-system.md`: Jetpack Compose y Material 3 estable, colores/opacidades y dimensiones tokenizados, Light/Dark Theme y ningún cambio en ViewModel, repositorios, Firebase, navegación o contratos de autenticación.

## Goals / Non-Goals

**Goals:**

- Sustituir el recurso visual del fondo únicamente en `AuthScreen`.
- Mantener la imagen como una capa decorativa posterior al contenido y conservar la capa de legibilidad existente.
- Mantener accesibles y operables los textbox, botones, mensajes, foco, scroll y acciones de login/registro/Google.
- Conservar el comportamiento responsive actual en teléfonos, tablets, alturas reducidas y teclado visible.
- Mantener la compatibilidad con Light Theme y Dark Theme usando el esquema de colores y tokens existentes.
- Probar que el cambio visual no altera el comportamiento funcional.

**Non-Goals:**

- No modificar autenticación, validaciones, estados, callbacks, textos, ViewModel, repositorios, Firebase ni navegación.
- No agregar controles, overlays interactivos, permisos, dependencias, red, almacenamiento ni APIs nuevas.
- No rediseñar la estructura del formulario ni introducir tamaños, colores, paddings o radios hardcodeados.
- No modificar otras pantallas que compartan recursos o componentes.

## Decisions

### 1. Reemplazar solo el painter del fondo de `AuthScreen`

La implementación mantendrá el `Image` existente en la capa posterior del `Box` y cambiará únicamente su `painterResource` al nuevo drawable. El recurso seguirá teniendo `contentDescription = null`, porque es decorativo, y no tendrá `clickable`, `pointerInput` ni semántica que pueda interceptar controles.

Alternativas descartadas:

- Crear un componente o pantalla nueva: ampliaría el alcance y podría duplicar la lógica de autenticación.
- Colocar la imagen dentro de la columna desplazable: haría que el fondo se mueva con el formulario y reduciría el área útil de los controles.
- Usar una imagen remota: agregaría dependencia de red y un estado de carga innecesario.

### 2. Conservar el orden de capas y la legibilidad

El orden seguirá siendo: superficie/gradiente base del tema, imagen decorativa, overlay de legibilidad basado en `MaterialTheme.colorScheme`, y contenido interactivo. El overlay se conserva para que el texto y los campos mantengan contraste en Light/Dark Theme; cualquier ajuste, si fuese estrictamente necesario durante implementación, debe reutilizar `AppOpacity` u otros tokens existentes.

Alternativas descartadas:

- Eliminar el overlay: el contenido podría perder contraste sobre las áreas brillantes de la imagen.
- Añadir un color o alpha literal: incumpliría el Design System y podría romper la adaptación entre temas.

### 3. Mantener el sizing responsive existente

La imagen continuará usando `Modifier.fillMaxSize()`, `ContentScale.Crop` y la alineación actual, salvo que la validación visual demuestre una obstrucción en un tamaño soportado. La columna conservará `widthIn(max = AppSpacing.authMaxWidth)`, `verticalScroll`, `safeDrawing` e `imePadding`; no se introducirán offsets ni dimensiones específicas por dispositivo.

En pantallas angostas o bajas, el formulario seguirá siendo alcanzable mediante scroll. En tablets, el ancho máximo y los márgenes tokenizados mantendrán los controles centrados y legibles mientras el fondo cubre la ventana sin deformarse.

Alternativas descartadas:

- `ContentScale.FillBounds`: puede deformar la composición de la imagen.
- Dimensiones o posiciones distintas por modelo: producirían comportamiento frágil y valores device-specific.

### 4. Verificación limitada a presentación y regresión de auth

Se actualizarán o agregarán aserciones de presentación solo si el test existente fija el nombre del recurso anterior. La validación debe confirmar el nuevo recurso, el orden posterior del `Image`, la permanencia del overlay y la ausencia de cambios en las llamadas de autenticación. La revisión manual cubrirá temas, tamaños, teclado, foco y acciones principales.

No hay backend ni almacenamiento local involucrados. No existen reglas de autorización, permisos runtime ni datos sensibles nuevos; los datos de autenticación siguen manejándose por el flujo existente.

## Risks / Trade-offs

- [La imagen puede recortarse de forma diferente en tablets o ventanas muy anchas] → Mantener `ContentScale.Crop` y validar teléfonos y tablets; ajustar solo con APIs/tokens ya existentes si el recorte oculta contenido interactivo.
- [El contenido puede perder contraste en una zona clara] → Conservar el overlay del tema y verificar Light/Dark Theme con los controles completos visibles.
- [Una capa de imagen mal ordenada puede interceptar interacción] → Mantenerla antes del overlay y del contenedor interactivo, sin modificadores de interacción ni semántica.
- [La sustitución visual puede romper tests estáticos existentes] → Actualizar únicamente las expectativas del recurso de fondo y preservar las aserciones de autenticación y accesibilidad.

## Migration Plan

1. Confirmar que el drawable nuevo está incluido en `drawable-nodpi` y que su nombre de recurso es válido.
2. Sustituir la referencia del painter en `AuthScreen` sin cambiar la composición interactiva.
3. Ejecutar tests unitarios/presentación y `assembleDebug`.
4. Validar manualmente teléfonos compactos y amplios, tablet, Light/Dark Theme y teclado abierto.
5. Si la validación falla, revertir la referencia del painter al recurso anterior; no se requieren migraciones ni limpieza de datos.

## Open Questions

- Ninguna para iniciar la implementación. Si la composición del nuevo recurso afecta el contraste o la visibilidad en un dispositivo concreto, detener la implementación y consultar antes de modificar la estructura o los tokens visuales.
