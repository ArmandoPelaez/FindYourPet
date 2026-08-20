## 1. Data and visibility rules

- [x] 1.1 Separar la fuente de publicaciones propias de la fuente de descubrimiento publico, conservando `REUNIDO` para el dueno y excluyendolo del feed y busquedas de otros usuarios.
- [x] 1.2 Reforzar en ViewModel/repositorio la transicion unica `PERDIDO -> REUNIDO`, rechazando reactivaciones y preservando la autorizacion basada en `ownerId`.
- [x] 1.3 Mantener el filtrado defensivo para resultados remotos, Room/cache y estados de busqueda sin exponer publicaciones `REUNIDO` en la UI publica.

## 2. Perfil y gestion de publicaciones

- [x] 2.1 Redisenar `ProfileScreen` con avatar, nombre y `Colaborador`, eliminando email, tarjeta de comunidad y logout del encabezado sin cambiar la navegacion inferior.
- [x] 2.2 Renderizar publicaciones propias como tarjetas compactas sin foto, con `Marcar reunida` solo para `PERDIDO` y estado-only para `REUNIDO`.
- [x] 2.3 Agregar el dialogo de confirmacion con el aviso de perdida de visibilidad publica y mover `Cerrar sesion` al final manteniendo la logica existente.
- [x] 2.4 Implementar estados de carga, cancelacion y error sin cambios optimistas permanentes cuando la actualizacion sea rechazada.
- [x] 2.5 Aplicar unicamente tokens y componentes del design system, verificando Light Theme y Dark Theme sin APIs experimentales ni valores visuales hardcodeados.
- [x] 2.6 Eliminar el AppBar/TopAppBar y el titulo `Perfil`, dejando la card compacta del usuario como primer elemento y preservando exactamente la posicion actual de Bottom Navigation.
- [x] 2.7 Reutilizar el mismo token semantico de superficie de Bottom Navigation para la card de usuario y usar chip de estado mas accion secundaria compacta para las cards de mascotas.
- [x] 2.8 Cambiar el encabezado `Mis Mascotas Publicadas` por `Mis publicaciones`.
- [x] 2.9 Implementar la limpieza fisica owner-only al marcar `REUNIDO`: borrar avistamientos por `postId` y notificaciones relacionadas por `postId`/`sightingId` en Firestore y Room/cache.
- [x] 2.10 Actualizar reglas y tests para autorizar el borrado solo al propietario y reportar errores si la limpieza no completa.

## 3. Tests

- [x] 3.1 Crear o actualizar tests de ViewModel/repositorio para visibilidad publica, publicaciones propias, transicion `PERDIDO -> REUNIDO`, terminalidad y rechazo de no dueno.
- [x] 3.2 Crear o actualizar tests de presentacion para acciones disponibles en `PERDIDO`/`REUNIDO`, confirmacion, cancelacion, logout inferior y ausencia de cambios en navegacion.
- [x] 3.3 Ejecutar `openspec validate "redesign-user-profile-and-reunited-pets" --strict` y corregir cualquier incumplimiento de specs o tareas.

## 4. Verificacion

- [x] 4.1 Ejecutar `./gradlew.bat testDebugUnitTest` y conservar el resultado en el reporte del agente.
- [x] 4.2 Ejecutar `./gradlew.bat assembleDebug` y conservar el resultado en el reporte del agente.
- [ ] 4.3 Realizar validacion manual del perfil y feed con usuario dueno/no dueno, publicaciones `PERDIDO`/`REUNIDO`, confirmacion/cancelacion, Light/Dark y navegacion inferior.
- [x] 4.4 Documentar archivos modificados, comandos ejecutados, resultados, limitaciones y cualquier indice/configuracion remota requerida.
