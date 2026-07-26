# Guia funcional de cambios y testeos

Este documento resume los cambios aplicados en FindYourPet desde una mirada funcional, pensada para producto y QA. La guia se debe actualizar cada vez que se cierre un nuevo cambio relevante, agregando que se modifico, que mejora se logro y que pruebas funcionales conviene realizar.

## Como usar esta guia

- Leer primero el resumen del cambio para entender el objetivo funcional.
- Ejecutar los testeos sugeridos sobre una build actualizada.
- Registrar observaciones, errores encontrados o dudas de producto en la seccion de notas del cambio.
- Agregar nuevos cambios al final del documento usando la misma estructura.

## Estado general actual

La app se encuentra en una etapa demo/local. Todavia no incorpora autenticacion real, backend productivo, carga real de fotos, GPS real ni notificaciones push reales. Los cambios documentados hasta ahora buscan estabilizar la base Android y reducir riesgos de privacidad antes de avanzar hacia funciones productivas.

## Resumen de cambios

| Cambio | Objetivo funcional | Mejora lograda | Foco principal de QA |
| --- | --- | --- | --- |
| `stabilize-android-build` | Dejar la app compilable, limpia y estable para futuras iteraciones. | Base Android mas confiable para probar y construir producto. | Apertura de app, navegacion principal, textos visibles y estados basicos. |
| `harden-local-privacy` | Reducir exposicion innecesaria de datos sensibles en la demo local. | Menos permisos innecesarios, datos de contacto mas protegidos y mensajes de privacidad mas honestos. | Permisos, visibilidad de contacto, exposicion de datos sensibles y textos de privacidad. |

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

Tambien se reviso la exposicion de datos sensibles como telefono, email, direccion, coordenadas, mensajes y notas privadas. El foco fue que esos datos no aparezcan en espacios publicos o previews sin una accion explicita de revelado dentro de la app.

### Mejora lograda

- La app pide menos permisos innecesarios.
- Los datos sensibles tienen menor riesgo de quedar expuestos en la experiencia local.
- El contacto del dueno permanece protegido hasta que corresponde mostrarlo.
- La app evita prometer encriptacion, privacidad productiva o autorizacion real si todavia no existe esa implementacion.
- La base queda mejor preparada para futuras funciones de autenticacion, backend, GPS y carga real de fotos.

### Testeos funcionales sugeridos

- Instalar y abrir la app validando que no pida permisos innecesarios como camara, ubicacion, contactos, almacenamiento, microfono, telefono, SMS o notificaciones.
- Ingresar a una ficha de mascota y verificar que telefono, email, direccion o coordenadas exactas no aparezcan expuestos publicamente.
- Probar el flujo de revelar contacto y confirmar que el dato solo aparece cuando el usuario realiza esa accion.
- Revisar pantallas de detalle, perfil, chat y avistamientos para confirmar que no muestren datos sensibles fuera de contexto.
- Validar que previews de chat o notificaciones locales no incluyan telefono, email, coordenadas exactas ni contenido privado completo.
- Revisar textos de privacidad para confirmar que no prometan encriptacion, verificacion, autorizacion real o tiempo real.
- Confirmar que las funciones simuladas de foto o ubicacion no disparen pedidos de permisos reales.

### Criterios funcionales de aceptacion

- La app solo solicita permisos necesarios para lo que realmente funciona hoy.
- Los datos de contacto se mantienen ocultos hasta una accion explicita de revelado.
- No se muestran coordenadas exactas ni datos privados en pantallas publicas o previews.
- Los textos de privacidad son claros y no exageran las capacidades actuales.
- La navegacion y consulta de mascotas siguen funcionando despues de los ajustes de privacidad.

### Notas de QA

- Registrar aca cualquier caso donde aparezcan datos sensibles sin accion del usuario, permisos inesperados o textos que generen expectativas de seguridad/producto que aun no estan implementadas.

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
