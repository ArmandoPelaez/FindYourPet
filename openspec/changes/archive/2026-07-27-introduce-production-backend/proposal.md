## Why

FindYourPet todavia se comporta parcialmente como una demo local: las publicaciones, avistamientos, chats y notificaciones dependen de Room y de datos sembrados, por lo que cada dispositivo puede ver una realidad distinta. Esta etapa convierte esos flujos en una red compartida con persistencia central, autorizacion real y estados claros de carga, error y sincronizacion.

## What Changes

- Introducir un modelo backend de produccion para usuarios, perfiles publicos, publicaciones de mascotas, avistamientos, sesiones de chat, mensajes y notificaciones.
- Usar Firebase Authentication como identidad de produccion y Cloud Firestore como fuente de verdad para datos compartidos.
- Mover el feed y la creacion de publicaciones desde Room local hacia Firestore, conservando Room solo como cache local o soporte offline cuando sea necesario.
- Enrutar avistamientos al dueno correcto usando `ownerId` derivado de la publicacion backend, no strings hardcodeados.
- Habilitar chat real entre los dos participantes autorizados de una sesion: dueno y reportante.
- Crear notificaciones persistentes por usuario para avistamientos, chats y eventos de contacto sin exponer datos sensibles completos fuera de la app.
- Agregar estados observables de carga, error, cache, datos remotos y escrituras pendientes para pantallas que dependan de backend.
- Fortalecer reglas de lectura/escritura en `firestore.rules` y documentar validacion con emulador o entorno Firebase no productivo.
- Mantener compatibilidad de usuario existente migrando o aislando datos demo/locales para que no otorguen permisos de produccion.

## Capabilities

### New Capabilities

- `backend-data-model`: Define las colecciones, documentos, campos sensibles, ownership, timestamps y referencias entre usuarios, publicaciones, avistamientos, chats y notificaciones.
- `pet-posts`: Publicaciones compartidas de mascotas respaldadas por backend y visibles para otros usuarios autenticados.
- `sightings`: Avistamientos persistentes que se crean por reportantes y llegan solo al dueno de la publicacion y al reportante autorizado.
- `private-chat`: Sesiones y mensajes de chat compartidos entre dos usuarios reales con membresia explicita.
- `notifications`: Notificaciones persistentes por usuario para eventos de backend, sin filtrar cuerpos sensibles en notificaciones del sistema.
- `backend-access-rules`: Reglas de autorizacion para leer, crear, actualizar o eliminar datos segun identidad y relacion con el recurso.
- `sync-state`: Estados de UI y repositorio para loading, error, cache, remoto, offline y escrituras pendientes.
- `local-storage-cache`: Limites de Room como cache local, sin autoridad para ownership ni permisos de produccion.

### Modified Capabilities

- `release-readiness`: El cierre de cambios que toquen backend compartido requiere build Android, tests relevantes y validacion documentada de reglas Firestore antes de usar datos reales.

## Impact

- Afecta `app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt`, entidades/mappers de datos, ViewModels y pantallas que consumen publicaciones, avistamientos, chats y notificaciones.
- Afecta `firestore.rules` y la documentacion de validacion en `docs/`.
- Puede requerir nuevas clases de modelo remoto, DTOs Firestore, repositorios de backend, listeners de snapshots y pruebas unitarias/de reglas.
- Aumenta el impacto de privacidad y seguridad: telefono, email, direccion, coordenadas, fotos, mensajes privados e historial de avistamientos quedan sujetos a permisos reales.
- Rollback: mantener Room como lectura local/cache y feature-gatear repositorios remotos permite volver temporalmente a datos locales en builds no productivos si Firestore o reglas bloquean el flujo.
