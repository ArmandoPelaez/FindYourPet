## 1. Shell de navegación sticky

- [x] 1.1 Revisar `MainActivity.kt`, `BottomPrimaryActionBanner` y las rutas autenticadas para identificar la aplicación actual de `bottomBar` e insets, sin modificar callbacks ni destinos.
- [x] 1.2 Mantener `BottomPrimaryActionBanner` como `bottomBar` del shell autenticado en todas las rutas firmadas, conservando sus tokens visuales y acciones existentes.
- [x] 1.3 Aplicar el `PaddingValues` del `Scaffold` externo al contenido del `NavHost` una sola vez y eliminar reservas duplicadas de `shellPadding` en las rutas secundarias.
- [x] 1.4 Verificar que la barra permanezca fija sobre contenido desplazable y sobre el área de gestos en Home, Profile, Chats, Create Post, Notifications, Sighting Alert y Chat Detail.

## 2. Acción inline de avistamiento

- [x] 2.1 Eliminar el CTA contextual sticky `¡Lo he visto!` de `HomeScreen` y conservar la navegación global como único `bottomBar` de la pantalla.
- [x] 2.2 Renderizar `La vi` alineado a la derecha del nombre dentro de `PetPostCard`, únicamente para publicaciones elegibles según `OwnershipPolicy` y estado.
- [x] 2.3 Mostrar `VisibilityOff` inicialmente y `Visibility` después del click, invocando el callback existente para seleccionar la publicación y abrir la alerta de avistamiento.
- [x] 2.4 Mantener espacio inferior tokenizado dentro del contenido desplazable para que la barra global no oculte el final de la publicación.

## 3. Presentación de ficha y diseño

- [x] 3.1 Mover la etiqueta de estado al extremo superior izquierdo de la foto, usar tratamiento rojo semántico para `PERDIDO` y conservar soporte de temas claro/oscuro mediante `PetStatusColors`.
- [x] 3.2 Ajustar la foto al token compartido `AppSpacing.cardImageAspectRatio`, sin tamaños visuales hardcodeados en la pantalla.
- [x] 3.3 Mostrar debajo de la ubicación el icono de calendario, `Última vez visto` y la fecha en la misma fila.
- [x] 3.4 Actualizar pruebas Compose, estáticas y screenshots para cubrir elegibilidad, alternancia del ojo, ausencia del CTA eliminado, posición semántica del estado, proporción de foto y metadatos.
- [x] 3.5 Actualizar la barra global con el orden `Inicio`, `Perfil`, `Publicar`, `Mensajes`, `Alertas`, labels visibles, estados activo/inactivo, badge de alertas y eliminación del icono duplicado del top app bar.
- [x] 3.6 Cambiar la barra de tarjeta flotante a superficie de ancho completo, agregar el semicírculo sutil detrás de Publicar y reducir los iconos secundarios mediante tokens de tamaño cuadrado.
- [x] 3.7 Reducir la altura útil de la barra, alinear el label de Publicar con los demás y ajustar el pozo oscuro/sombra y tamaños de iconos mediante tokens.
- [x] 3.8 Eliminar completamente las esquinas redondeadas para que la barra sea un rectángulo continuo de ancho completo.

## 4. Validación final

- [x] 4.1 Ejecutar `openspec validate "sticky-lost-pet-detail-actions" --strict`.
- [x] 4.2 Ejecutar `./gradlew testDebugUnitTest`.
- [x] 4.3 Ejecutar `./gradlew assembleDebug`.
- [x] 4.4 Ejecutar `openspec instructions apply --change "sticky-lost-pet-detail-actions" --json` y confirmar el progreso y la única tarea manual pendiente.
- [ ] 4.5 Realizar validación manual en las rutas autenticadas: navegar, desplazar contenido largo, comprobar la barra siempre sticky, revisar safe areas y confirmar que el último contenido se puede tocar.
