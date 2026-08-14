## 1. Revisión de tokens y componentes compartidos

- [x] 1.1 Revisar `docs/design-system.md`, `DesignTokens.kt`, `PetPostCard`, `InlineSightingButton` y `BottomNavigationTopDivider` para identificar tokens reutilizables y referencias visuales actuales.
- [x] 1.2 Definir o ajustar tokens semánticos de forma, proporción, padding, altura y tamaño de iconos solo cuando no exista un token equivalente; evitar valores visuales hardcodeados en las pantallas.
- [x] 1.3 Confirmar que los cambios quedan limitados a presentación y no requieren modificar ViewModels, repositorios, Firebase, Room, permisos ni reglas de negocio.

## 2. Marco visual de la publicación

- [x] 2.1 Ajustar el contenedor de la imagen de `PetPostCard` para usar `AppSpacing.cardImageAspectRatio`, el inset de contenido y una forma redondeada compartida de `AppShapes`.
- [x] 2.2 Mantener `post.photoUri`, el cargador actual y `ContentScale.Crop`; asegurar que la imagen de referencia no se agregue como asset ni fallback.
- [x] 2.3 Mantener `PetStatusChip` dentro del marco de foto, alineado arriba a la izquierda y usando `PetStatusColors` para `PERDIDO` y los demás estados.
- [x] 2.4 Verificar que la nueva geometría no solape el nombre, ubicación, fecha, información reportada ni las acciones en anchos compactos y tamaños de fuente accesibles.

## 3. Acción de avistamiento

- [x] 3.1 Cambiar el texto visible `Lo vi` por `He visto a esta mascota` y actualizar el content description, tests y referencias de presentación relacionadas.
- [x] 3.2 Adaptar `InlineSightingButton` o `AppButton` a una variante compacta con apariencia de etiqueta, fondo naranja/primario del tema y contenido con contraste suficiente en Light/Dark Theme.
- [x] 3.3 Conservar el icono de visibilidad, su alternancia, la elegibilidad actual y el callback que abre el flujo existente de alerta de avistamiento.
- [x] 3.4 Verificar que el texto largo se mantenga legible, no desborde y conserve un área táctil accesible en teléfonos compactos y con font scale aumentado.

## 4. Barra de navegación inferior

- [x] 4.1 Reducir mediante tokens el tamaño visual del círculo y/o icono de `Publicar`, manteniendo su centrado, elevación, pozo y área táctil accesible.
- [x] 4.2 Ajustar `BottomNavigationTopDivider` para que la línea de separación cubra continuamente todo el ancho de la superficie, sin introducir divisores entre destinos ni esquinas redondeadas.
- [x] 4.3 Confirmar que la barra conserva sus cinco destinos, labels, estados activo/inactivo, badge de alertas, safe areas, gesto del sistema y destinos existentes.
- [x] 4.4 Mantener el padding inferior tokenizado para que la ficha, el estado vacío y las acciones sigan siendo visibles y táctiles por encima de la barra.

## 5. Pruebas de presentación

- [x] 5.1 Actualizar `HomeFeedPresentationTest` y pruebas estáticas para comprobar la ausencia de `Lo vi`, la presencia de `He visto a esta mascota`, la semántica accesible y la conservación de callbacks/elegibilidad.
- [x] 5.2 Actualizar o agregar pruebas Compose/screenshot para la proporción, bordes redondeados, overlay `PERDIDO`, layout compacto y contenido largo de la imagen.
- [x] 5.3 Actualizar `BottomPrimaryActionBannerComposeTest` y guardrails estáticos para verificar los tokens reducidos de `Publicar`, el área táctil y la línea de separación de ancho completo.
- [x] 5.4 Ejecutar una búsqueda de referencias antiguas (`rg -n "Lo vi|La vi|bottomNavigationCreate|BottomNavigationTopDivider|cardImageAspectRatio" app/src/main app/src/test docs openspec`) y revisar que cada coincidencia sea intencional o esté actualizada.

## 6. Validación final

- [x] 6.1 Ejecutar `openspec validate "refine-lost-pet-card-visuals" --strict`.
- [x] 6.2 Ejecutar `./gradlew.bat testDebugUnitTest --console=plain`.
- [x] 6.3 Ejecutar `./gradlew.bat assembleDebug`.
- [x] 6.4 Realizar validación manual en Home con una publicación, varias publicaciones, estado vacío y contenido largo, en Light Theme y Dark Theme.
- [x] 6.5 Revisar manualmente teléfono compacto, font scale aumentado, navegación por gestos y la interacción de `He visto a esta mascota` y `Publicar`, confirmando que no cambió ningún flujo funcional ni se expusieron datos sensibles.
