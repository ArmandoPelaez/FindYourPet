# Guia funcional de cambios y testeos

Este documento resume los cambios aplicados en FindYourPet desde una mirada funcional, pensada para producto y QA. La guia se debe actualizar cada vez que se cierre un nuevo cambio relevante, agregando que se modifico, que mejora se logro y que pruebas funcionales conviene realizar.

## Como usar esta guia

- Leer primero el resumen del cambio para entender el objetivo funcional.
- Ejecutar los testeos sugeridos sobre una build actualizada.
- Registrar observaciones, errores encontrados o dudas de producto en la seccion de notas del cambio.
- Agregar nuevos cambios al final del documento usando la misma estructura.

## Estado general actual

La app se encuentra en una etapa de transicion desde demo/local hacia flujos productivos. Ya incorpora autenticacion real con Firebase Auth y datos autenticados en Cloud Firestore cuando el proyecto Firebase esta configurado, pero todavia no incorpora carga real de fotos, GPS real ni notificaciones push reales. Room puede seguir existiendo como demo/cache, pero no debe otorgar permisos productivos de dueno.

## Resumen de cambios

| Cambio | Objetivo funcional | Mejora lograda | Foco principal de QA |
| --- | --- | --- | --- |
| `stabilize-android-build` | Dejar la app compilable, limpia y estable para futuras iteraciones. | Base Android mas confiable para probar y construir producto. | Apertura de app, navegacion principal, textos visibles y estados basicos. |
| `harden-local-privacy` | Reducir exposicion innecesaria de datos sensibles en la demo local. | Backups locales desactivados, menos permisos innecesarios, chat interno y mensajes de privacidad mas honestos. | Permisos, backup local, chat, previews, exposicion de datos sensibles y textos de privacidad. |
| `add-user-authentication` | Incorporar identidad real y reglas de propiedad para usuarios autenticados. | Login con Firebase, perfiles en Firestore, acciones de dueno basadas en `uid` y reglas de acceso del backend. | Login/logout, perfil, propiedad de publicaciones, reglas Firestore, chat interno y frontera Room/demo. |

## Cambio: `stabilize-android-build`

### En que consistio

Este cambio ordeno la base del proyecto Android para que la app pueda compilarse, probarse y abrirse de forma confiable. Tambien se limpiaron restos de plantillas anteriores, nombres de paquete incorrectos, dependencias que no correspondian al estado actual y textos visibles con problemas de codificacion.

Desde producto, el cambio no incorpora una funcionalidad nueva para usuarios finales. Su valor esta en dejar una base mas estable para seguir desarrollando y testeando sin errores heredados del prototipo.

### Mejora lograda

- La app queda en mejores condiciones para generar builds de prueba.
- Los testeos parten de una version mas consistente y menos propensa a fallar por configuracion.
- Los textos principales en espanol son mas legibles.
- Se reduce la confusion entre comportamiento real y comportamiento demo.
- Se agregan controles para detectar regresiones basicas en estados visuales y arranque de la app.

### Testeos funcionales sugeridos

- Abrir la app en un dispositivo o emulador y validar que no crashee al iniciar.
- Recorrer las pantallas principales disponibles.
- Verificar que los estados de mascotas se muestren correctamente: perdido, avistado, reunido y estados no esperados.
- Revisar que los textos visibles esten claros y sin caracteres rotos.
- Confirmar que la app no muestre promesas de funciones productivas que aun son demo, por ejemplo privacidad avanzada, GPS real o tiempo real.
- Revisar que las tarjetas de contacto tengan estado oculto y visible de manera coherente.

### Criterios funcionales de aceptacion

- La app abre correctamente.
- Las pantallas principales no presentan errores bloqueantes.
- Los textos visibles son entendibles para usuarios hispanohablantes.
- Los estados principales de mascotas se ven correctamente.
- El comportamiento demo/local esta claro y no se presenta como funcionalidad productiva completa.

### Notas de QA

- Registrar aca errores visuales, pantallas que no carguen, textos confusos o comportamientos que parezcan prometer funcionalidad no implementada.

## Cambio: `harden-local-privacy`

### En que consistio

Este cambio reforzo la privacidad local de la app. Se ajusto la configuracion Android para evitar respaldos automaticos de datos sensibles, se limitaron los permisos declarados a lo que la app realmente usa hoy y se revisaron textos o pantallas que podian dar a entender garantias de privacidad todavia no implementadas.

Tambien se reviso la exposicion de datos sensibles como telefono, email, direccion, coordenadas, mensajes y notas privadas. El foco actual es que esos datos no aparezcan en espacios publicos, previews ni flujos administrados por la app; la comunicacion entre dueno y reportero queda limitada al chat interno.

En concreto, el cambio dejo `android:allowBackup` desactivado, mantuvo reglas explicitas para excluir bases de datos, preferencias, archivos privados, cache y archivos externos propios de la app, y documento que la informacion local de la demo no cuenta todavia con cifrado propio de FindYourPet. La app conserva solo el permiso de internet y no pide permisos reales para flujos que siguen siendo simulados, como foto, ubicacion, chat o notificaciones.

### Mejora lograda

- La app pide menos permisos innecesarios.
- Los datos sensibles tienen menor riesgo de quedar expuestos en la experiencia local o en respaldos automaticos del dispositivo.
- La app no ofrece controles para revelar telefono, email o direccion del dueno.
- Las pantallas publicas evitan mostrar telefono, email, direccion o coordenadas exactas como reemplazo del chat interno.
- Los previews de chat y notificaciones locales evitan incluir telefono, email, coordenadas exactas o contenido privado completo.
- La app evita prometer cifrado, privacidad productiva, autorizacion real, verificacion o tiempo real si todavia no existe esa implementacion.
- La base queda mejor preparada para futuras funciones de autenticacion, backend, GPS y carga real de fotos.

### Testeos funcionales sugeridos

- Instalar y abrir la app validando que no pida permisos innecesarios como camara, ubicacion, contactos, almacenamiento, microfono, telefono, SMS o notificaciones.
- Revisar el manifiesto Android y confirmar que solo declare `android.permission.INTERNET`.
- Revisar que `android:allowBackup` este desactivado y que las reglas de backup/extraccion excluyan bases de datos, preferencias, archivos privados, cache y archivos externos propios de la app.
- Ingresar a una ficha de mascota y verificar que telefono, email, direccion o coordenadas exactas no aparezcan expuestos publicamente.
- Confirmar que no existe flujo de revelar contacto, compartir contacto ni revocar contacto.
- Revisar pantallas de detalle, perfil, chat y avistamientos para confirmar que no muestren datos sensibles fuera de contexto.
- Validar que previews de chat o notificaciones locales no incluyan telefono, email, coordenadas exactas ni contenido privado completo.
- Revisar textos de privacidad para confirmar que no prometan encriptacion, verificacion, autorizacion real o tiempo real.
- Confirmar que las funciones simuladas de foto o ubicacion no disparen pedidos de permisos reales.
- Ejecutar la suite de unit tests de debug y una build debug para confirmar que las guardas estaticas siguen pasando.

### Criterios funcionales de aceptacion

- La app solo solicita permisos necesarios para lo que realmente funciona hoy.
- El backup automatico de Android queda desactivado y los archivos de reglas excluyen los dominios sensibles definidos.
- Los datos personales de contacto no se muestran ni se administran mediante acciones de revelado.
- No se muestran coordenadas exactas ni datos privados en pantallas publicas o previews.
- Los textos de privacidad son claros y no exageran las capacidades actuales: no prometen cifrado propio, autorizacion productiva, verificacion real ni tiempo real.
- Las funciones simuladas de foto, ubicacion, chat y notificacion no piden permisos runtime.
- La navegacion y consulta de mascotas siguen funcionando despues de los ajustes de privacidad.
- Las validaciones `testDebugUnitTest` y `assembleDebug` pasan antes de cerrar el cambio.

### Notas de QA

- Registrar aca cualquier caso donde aparezcan datos sensibles sin accion del usuario, permisos inesperados, datos privados en previews o textos que generen expectativas de seguridad/producto que aun no estan implementadas.
- Queda como seguimiento de producto definir en una etapa posterior si los datos locales de Room seran cache cifrada, datos sincronizados con backend o estado local de corta duracion cuando existan autenticacion y reglas productivas.

## Cambio: `add-user-authentication`

### En que consistio

Este cambio incorporo autenticacion real para FindYourPet usando Firebase Authentication y Cloud Firestore. La app deja de depender de identidades demo como `user_1` u `owner_1` para validar acciones sensibles, y pasa a usar el `uid` real del usuario autenticado como identidad productiva.

El alcance incluye registro e inicio de sesion con email/password, inicio de sesion con Google, cierre de sesion, estados de carga/error recuperables y bloqueo de la experiencia principal cuando no hay una sesion autenticada. Tambien se agrego la creacion y carga de perfiles en `users/{uid}` para que la pantalla de perfil no muestre datos viejos o ajenos despues de cerrar sesion.

Ademas, las publicaciones, avistamientos, chats y acciones controladas por dueno quedan asociadas al `uid` autenticado. Firestore pasa a ser la fuente de verdad para datos productivos autenticados, mientras que Room queda limitado a datos demo/cache y no puede otorgar permisos reales de propietario. Las reglas de Firestore mantienen denegacion por defecto, acceso propio a `users/{uid}`, propiedad inmutable en publicaciones y restricciones para chats, avistamientos y campos/grants de contacto retirados.

### Mejora lograda

- La app ya cuenta con un flujo real de autenticacion para email/password y Google Sign-In.
- El perfil del usuario queda vinculado al `uid` de Firebase y se carga desde Firestore.
- Las acciones de dueno dejan de depender de ids hardcodeados o datos locales no confiables.
- El cierre de sesion limpia el estado autenticado y evita mostrar perfil o datos sensibles obsoletos.
- Las publicaciones productivas se crean con `ownerId` igual al `uid` del usuario autenticado.
- Las reglas de Firestore protegen perfiles, publicaciones, avistamientos, chats, mensajes y niegan campos/grants de contacto retirados.
- Room queda documentado y tratado como demo/cache, sin autoridad para conceder permisos productivos.
- La configuracion Firebase queda documentada sin commitear `google-services.json`.

### Testeos funcionales sugeridos

- Configurar un proyecto Firebase no productivo en plan Spark, colocar `app/google-services.json`, cargar `firebase_web_client_id` y habilitar proveedores Email/password y Google.
- Publicar o validar `firestore.rules` en un proyecto Firebase de prueba antes de usar cuentas reales.
- Abrir la app sin sesion iniciada y verificar que aparezca la pantalla de autenticacion antes de ver datos de mascotas.
- Crear una cuenta con email/password y confirmar que la app ingrese al contenido autenticado.
- Cerrar sesion desde Perfil y validar que vuelva la pantalla de autenticacion sin mostrar datos de perfil anteriores.
- Volver a iniciar sesion con la misma cuenta y confirmar que el perfil se cargue desde `users/{uid}`.
- Iniciar sesion con Google y verificar que Firebase Authentication registre el proveedor Google en la cuenta.
- Crear una publicacion de mascota y confirmar en Firestore que `petPosts/{postId}.ownerId` sea igual al `uid` autenticado.
- Abrir una publicacion como otro usuario y validar que no aparezcan acciones de editar, cerrar/reabrir o compartir contacto.
- Intentar una escritura directa en Firestore como no dueno y confirmar que las reglas la rechacen.
- Abrir un chat como dueno y reportante, y validar que solo participantes puedan leer o enviar mensajes.
- Verificar que el chat no muestre telefono, email, direccion ni controles de revelar/compartir/revocar contacto.
- Intentar escribir `contactGrants`, `isContactSharedByOwner`, `ownerPhone`, `ownerEmail` u `ownerAddress` y confirmar que las reglas lo rechacen.
- Confirmar que las publicaciones seed de Room sigan siendo demo/cache y no den controles de dueno a un usuario autenticado salvo importacion explicita con su `uid`.
- Ejecutar `gradlew.bat testDebugUnitTest` y `gradlew.bat assembleDebug` antes de cerrar el cambio.

### Criterios funcionales de aceptacion

- La app muestra autenticacion cuando no hay usuario firmado y solo permite ingresar al contenido principal con sesion valida.
- El registro, login, Google Sign-In y logout tienen estados recuperables de carga/error.
- El perfil autenticado se crea o carga desde `users/{uid}` y no queda visible despues de cerrar sesion.
- Las acciones de dueno se basan en `currentUid == ownerId` y no en strings hardcodeados.
- Un usuario no dueno no puede editar, cerrar/reabrir, borrar ni escribir campos/grants de contacto en una publicacion o chat ajenos.
- Firestore es la fuente de verdad para perfiles y datos productivos autenticados introducidos por este cambio.
- Room no concede permisos productivos y los datos demo solo pasan a produccion con asignacion explicita del `uid` autenticado.
- Las reglas de Firestore niegan por defecto y cubren usuarios, publicaciones, avistamientos, chats, mensajes y campos de contacto.
- `google-services.json` queda fuera del repositorio y documentado como configuracion local.
- Las validaciones funcionales, reglas y build/tests pasan antes de considerar cerrado el cambio.

### Notas de QA

- Registrar aca fallas de login, mensajes de error confusos, cuentas que no carguen perfil, acciones de dueno visibles para no duenos o datos autenticados que queden visibles luego de cerrar sesion.
- Para pruebas reales, usar siempre un proyecto Firebase no productivo y revisar `docs/auth-manual-validation.md`, `docs/firebase-auth-firestore-setup.md` y `docs/firebase-rules-validation.md`.
- Queda como seguimiento definir los campos obligatorios de perfil, la politica final de importacion de datos demo y cualquier migracion posterior de Room a cache sincronizada.

## Checklist general por cada nuevo cambio

Antes de cerrar un nuevo cambio funcional, agregar una entrada en esta guia y revisar:

- Que cambio se aplico y por que importa para el usuario o para el equipo de producto.
- Que mejora concreta se logro.
- Que pantallas o flujos debe revisar QA.
- Que datos sensibles, permisos o textos de privacidad se ven afectados.
- Que comportamiento esperado debe cumplirse para aceptar el cambio.
- Que validaciones quedaron pendientes o requieren dispositivo/emulador.

## Plantilla para proximos cambios

Copiar esta seccion al final del documento cuando se aplique un nuevo cambio.

```md
## Cambio: `nombre-del-cambio`

### En que consistio

Resumen funcional del cambio, sin detalle tecnico innecesario.

### Mejora lograda

- Mejora 1.
- Mejora 2.
- Mejora 3.

### Testeos funcionales sugeridos

- Prueba funcional 1.
- Prueba funcional 2.
- Prueba funcional 3.

### Criterios funcionales de aceptacion

- Criterio 1.
- Criterio 2.
- Criterio 3.

### Notas de QA

- Registrar observaciones, bugs o dudas funcionales.
```
