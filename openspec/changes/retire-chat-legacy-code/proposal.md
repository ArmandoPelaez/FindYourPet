## Why

SCRUM-26 elimina la superficie de Chat bidireccional que ya no pertenece al flujo funcional de FindYourPet. Aunque los nuevos avistamientos ya no crean sesiones ni mensajes, el APK aun expone pantallas, rutas, operaciones, modelos y almacenamiento local de Chat, lo que permite reactivar accidentalmente una comunicacion retirada y mantiene deuda tecnica activa.

## What Changes

- Eliminar la UI de listado/detalle de Chat, el composer, la accion de enviar y toda navegacion visible o interna por `chatId`.
- Retirar del codigo activo los metodos, estados, listeners, mappers, documentos, entidades, DAOs y recursos de Chat sin consumidores validos.
- Mantener el flujo `Avistamiento -> Alerta/Actividad -> Detalle` basado en `sightingId`; no modificar funcionalmente el formulario, Alertas, Actividad, Detalle, Reportar contenido ni Bloquear usuario.
- Retirar las tablas locales de Chat mediante una migracion Room no destructiva desde la version actual, sin `fallbackToDestructiveMigration`.
- Evitar nuevos writes de `chatSessions` y `messages`; conservar temporalmente los documentos historicos remotos y no ejecutar borrado o migracion de datos.
- Conservar solo compatibilidad de lectura/almacenamiento que tenga una justificacion explicita para datos historicos, sin dejar rutas activas hacia conversaciones.
- **BREAKING**: se elimina la capacidad de abrir conversaciones, enviar mensajes o crear nuevas sesiones/mensajes desde el cliente.

## Capabilities

### New Capabilities

- `chat-retirement`: contrato de retiro verificable para UI, navegacion, runtime, almacenamiento y writes de Chat legacy.

### Modified Capabilities

- `private-chat`: el Chat deja de estar disponible en el producto activo; los datos remotos historicos no se borran.
- `primary-navigation`: la navegacion autenticada conserva Inicio, Perfil, Reportar, Actividad y Alertas, sin Mensajes/Chat.
- `local-storage`: se eliminan tablas y acceso Room de Chat con migracion segura.
- `backend-data-model`: no se generan nuevos documentos de Chat y se conserva la estrategia de retencion historica remota.
- `backend-access-rules`: las reglas no deben permitir nuevos writes de Chat desde el cliente retirado, sin borrar datos historicos.
- `notifications`: las alertas nuevas y existentes usan `sightingId`; no se agrega una ruta activa basada en `chatId`.
- `sightings`: el camino de avistamientos permanece independiente de Chat y no expone infraestructura legacy.

## Impact

- Android: `MainActivity`, pantallas Compose de Chat, `PetViewModel`, `PetRepository`, entidades/DAO Room, `AppDatabase`, mappers y documentos remotos.
- Backend: `firestore.rules` y contratos de colecciones; no se eliminan documentos historicos de `chatSessions` ni `messages`.
- Tests: eliminacion o adaptacion de contratos exclusivamente Chat y nuevas regresiones de ausencia de UI, rutas, writes, listeners y referencias ejecutables.
- Usuarios existentes: dejan de poder abrir o enviar conversaciones desde la app; sus datos remotos historicos permanecen sin migracion destructiva.
- Privacidad y seguridad: se reduce la superficie de mensajes privados y se mantienen reglas de acceso para datos historicos mientras no exista una politica aprobada de borrado.
- Rollback: revertir el commit de cleanup y la migracion si se requiere restaurar el cliente legacy; no se requiere restaurar datos remotos.
