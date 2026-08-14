## Why

La pantalla principal de publicaciones de mascotas perdidas todavía presenta la foto, la acción de avistamiento y la navegación inferior con proporciones y tratamientos distintos a la referencia visual aprobada. Esto dificulta escanear la publicación y deja el CTA `Lo vi` ambiguo; el cambio busca cerrar esa brecha visual sin alterar el flujo funcional ni los datos de las publicaciones.

## What Changes

- Ajustar la foto de cada publicación para que use la proporción, el tamaño relativo y los bordes redondeados de la referencia, reutilizando `AppSpacing.cardImageAspectRatio`, `AppShapes` y los tokens de estado existentes.
- Mantener la etiqueta `PERDIDO` superpuesta en la esquina superior izquierda de la foto y conservar su color semántico mediante `PetStatusColors`.
- Cambiar la leyenda `Lo vi` por `He visto a esta mascota` en la acción inline de avistamiento.
- Adaptar el contenedor de `He visto a esta mascota` para que sea una etiqueta accionable compacta, con un tratamiento naranja coherente con el color primario de la aplicación, sin cambiar su callback, elegibilidad ni flujo de alerta.
- Reducir ligeramente el tamaño del icono de agregar publicación `Publicar` en la barra inferior mediante tokens del Design System.
- Extender la línea o borde de separación de la barra inferior por todo el contorno horizontal de la superficie, manteniendo la barra como una superficie de ancho completo y no como una tarjeta flotante.
- Actualizar specs, pruebas Compose/estáticas y capturas de presentación para cubrir el nuevo texto, la geometría visual, los estados claro/oscuro y la accesibilidad.
- No modificar modelos, ViewModels, repositorios, Firebase, Room, permisos, navegación funcional ni reglas de negocio.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `home-feed-presentation`: cambia la presentación de la imagen de la publicación y del control inline para reportar un avistamiento, manteniendo su comportamiento funcional y reglas de elegibilidad.
- `primary-navigation`: ajusta el tamaño tokenizado de la acción `Publicar` y hace continua la línea de separación de la barra inferior en todo el ancho de la superficie.

## Impact

- Código potencialmente afectado: `HomeScreen.kt`, `CommonComponents.kt`, tokens de `DesignTokens.kt` y componentes de tema/forma si no existe un token equivalente.
- Pruebas potencialmente afectadas: `HomeFeedPresentationTest`, pruebas de screenshot del feed, pruebas de `BottomPrimaryActionBanner` y guardrails estáticos que validen el texto o los tamaños actuales.
- No se agregan APIs, dependencias, permisos ni cambios de persistencia.
- Privacidad y seguridad: impacto nulo; no se exponen datos nuevos ni se modifican contactos, ubicaciones, mensajes o reglas de acceso.
- Usuarios existentes: conservarán la misma acción, destino, callback y reglas de elegibilidad; solo cambiarán el texto y la apariencia de los controles.
- Rollback: restaurar el texto `Lo vi`, los tokens/tamaños anteriores, la geometría previa de la foto y la línea de separación original, sin migración de datos ni reversión de backend.
- Goals relacionados: mantener una interfaz Android consistente con el Design System y avanzar hacia un MVP usable sin introducir cambios de dominio o de privacidad.
- Guardrails aplicables: usar Jetpack Compose + Material 3 estable, tokens centralizados, soporte Light/Dark Theme, sin valores visuales hardcodeados, sin APIs experimentales y sin inventar datos de mascotas.
