## Why

FindYourPet todavia conserva puntos criticos de demo: fotos preset, ubicacion simulada, alertas locales y seed data que pueden hacer parecer productivo un flujo que no lo es. Esta etapa reemplaza esas simulaciones por funciones reales de producto para que una publicacion o avistamiento use evidencia, ubicacion y notificaciones reales sin exponer datos sensibles fuera de contexto.

## What Changes

- Permitir que el usuario adjunte fotos reales desde camara o galeria con permisos Android y estados de denegacion manejados.
- Subir imagenes de publicaciones y avistamientos a Cloudinary mediante un unsigned upload preset restringido, dejando Firebase Storage fuera del alcance hasta pasar a Blaze.
- Capturar ubicacion real con consentimiento explicito, permitiendo publicar solo ubicacion aproximada cuando el contexto sea publico.
- Reemplazar alertas demo/locales por notificaciones in-app persistentes de backend para eventos relevantes, sin prometer push en esta etapa.
- Remover o aislar `seedInitialDataIfNeeded` para que los flujos autenticados funcionen sin datos demo.
- Agregar validaciones de formulario para impedir publicaciones o avistamientos incompletos, simulados o inconsistentes.
- Mantener rollback operativo deshabilitando camara o ubicacion por feature flag/configuracion y volviendo a estados manuales o in-app no productivos sin reactivar datos demo como produccion.

## Capabilities

### New Capabilities

- `media-upload`: Captura o seleccion de fotos reales, carga a Cloudinary con preset unsigned, validacion de archivos y asociacion autorizada con publicaciones y avistamientos.
- `location`: Captura de ubicacion real con consentimiento, manejo de estados de permiso y separacion entre ubicacion precisa autorizada y ubicacion aproximada publica.

### Modified Capabilities

- `pet-posts`: La creacion de publicaciones debe exigir datos reales validados y foto real cuando el flujo de produccion este activo.
- `sightings`: Los avistamientos deben poder incluir ubicacion real consentida y foto real opcional/subida en vez de valores preset.
- `notifications`: Las alertas al dueno deben quedar disponibles como notificaciones in-app persistentes y privacidad-seguras; push real queda fuera de esta etapa.
- `device-permissions`: El manifest y los permisos runtime deben cubrir camara, galeria/media y ubicacion solo donde exista flujo real, con estados granted, denied, permanently denied y unavailable.
- `local-storage`: Los datos demo sembrados deben quedar removidos o aislados de los flujos autenticados y las fotos/ubicaciones locales no deben convertirse en autoridad de produccion.
- `backend-data-model`: Los documentos backend deben registrar referencias Cloudinary de media subido, metadatos minimos de ubicacion y clasificacion sensible.
- `backend-access-rules`: Las reglas deben limitar escritura de referencias de media, ubicacion precisa y eventos sensibles al usuario o rol autorizado.
- `release-readiness`: El cierre del cambio requiere build Android, tests relevantes, validacion manual de permisos y evidencia de que la app funciona sin seed data demo.

## Impact

- Afecta pantallas de crear publicacion y reportar avistamiento, `PetViewModel`, repositorios, mappers remotos/locales, helper de notificaciones, Android manifest, reglas de backup, reglas Firebase y documentacion de validacion.
- Requiere dependencias Android para camara/photo picker, ubicacion de Google Play Services y Cloudinary Android SDK; Firebase Storage queda fuera del alcance por requerir Blaze.
- Impacto alto de privacidad y seguridad: fotos, coordenadas GPS, nombres, notas de avistamiento y relaciones owner/reporter son datos sensibles.
- Usuarios existentes dejaran de depender de fotos preset y ubicaciones sembradas en flujos autenticados; si no conceden permisos, la app debe ofrecer estados claros y alternativas permitidas.
- Goals vinculados: reemplazar simulaciones por camara, galeria, GPS y push reales; proteger datos personales; centralizar datos compartidos en backend; llegar a un MVP usable por usuarios reales.
- Guardrails aplicables: no exponer telefono, email, direccion o coordenadas sin consentimiento; no prometer push o tiempo real fuera de la app; no pedir permisos sin flujo real; no usar ids hardcodeados como autoridad; no exponer `api_secret` de Cloudinary en la app; no cerrar sin build, tests y validacion manual documentada.
