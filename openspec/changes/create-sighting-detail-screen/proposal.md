## Why

Las alertas nuevas ya identifican el avistamiento mediante `sightingId`, pero el producto todavía no tiene una superficie de consulta independiente del Chat para leer ese reporte. Esta pantalla permitirá al propietario y al reportante consultar la información autorizada del avistamiento sin crear ni depender de una conversación.

## What Changes

- Crear una pantalla de detalle de avistamiento en modo solo lectura.
- Cargar el detalle mediante `sightingId` usando `SightingAlertEntity` como fuente de verdad.
- Mostrar mascota, ubicación autorizada, fecha/hora, notas y fotografía cuando estén disponibles.
- Gestionar estados de carga, éxito, error y ausencia de datos opcionales.
- Ofrecer `Ver ubicación` cuando existan datos de localización, reutilizando el mecanismo actual.
- Excluir caja de respuesta, botón Enviar, burbujas, historial y creación de Chat.
- Mantener fuera de alcance la navegación desde Alertas y las pantallas legacy de Chat.

## Capabilities

### New Capabilities

- `sighting-detail`: pantalla autenticada de consulta de un avistamiento autorizado, independiente del Chat.

### Modified Capabilities

<!-- No se modifican requisitos existentes de navegación, notificaciones o Chat en esta task. -->

## Impact

- Android: nueva pantalla Compose, ruta/estado de detalle y acceso al repositorio existente para leer `sightings/{sightingId}`.
- Datos: no se agregan colecciones ni se modifican documentos; `SightingAlertEntity` continúa siendo la fuente de verdad.
- Privacidad: ubicación, foto, notas e historial de avistamiento se muestran únicamente en la superficie autorizada; no se agregan a notificaciones.
- UI: debe respetar Material 3 estable, Light/Dark Theme y los tokens de `docs/design-system.md`.
- Rollback: retirar la pantalla y su ruta sin migración de datos ni cambios destructivos en Chat o Firestore.
