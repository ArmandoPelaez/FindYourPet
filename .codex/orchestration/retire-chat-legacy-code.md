state: BLOCKED
phase: VERIFICATION_BLOCKED
issue: SCRUM-26
change: retire-chat-legacy-code
branch: ops/retire-chat-legacy-code
base_branch: main
base_commit: ed763f059d5d5c71adfa3a52dc78d2455e960891
remote_base_commit: ed763f059d5d5c71adfa3a52dc78d2455e960891
integration_status: PENDING
integrated_commit:
integration_evidence:
delegation_status: COMPLETED_BLOCKED
handoff_mode: SUBAGENT
delegation_required: true
agent_id: 01a00746-d12f-7b92-91fe-312f1af62b5d
agent_role: findyourpet-implementer
delegation_error:

## Estado actual

- `openspec status --change "retire-chat-legacy-code" --json`: proposal, design, specs y tasks completos.
- `openspec validate "retire-chat-legacy-code" --strict`: PASSED.
- `openspec instructions apply --change "retire-chat-legacy-code" --json`: 23 tareas pendientes listas para implementar.
- Delegacion obligatoria al agente `findyourpet-implementer`; el orquestador no implementa Kotlin ni reglas.
- Agente delegado: `Kierkegaard` (`01a00746-d12f-7b92-91fe-312f1af62b5d`).

# Orquestacion de SCRUM-26

## Autorizacion y Scrum normalizado

- Autorizacion explicita de trabajo paralelo recibida del usuario el 2026-08-15.
- Issue Jira: `SCRUM-26`.
- Titulo: `retirar UI y codigo legacy del Chat.`
- Estado: `To Do`.
- Prioridad: `Medium`.
- Sprint: `SCRUM Sprint 1`.
- Vencimiento: `2026-08-15`.
- Referencia: https://pelaezarmando.atlassian.net/browse/SCRUM-26

## Alcance funcional

- Retirar la UI visible de Chat, la ruta `chat/{chatId}`, la navegacion por `chatId`, el listado de conversaciones, el detalle y el envio de mensajes.
- Retirar del codigo activo las operaciones, listeners, ViewModel, repository, mappers, documentos, entidades y DAOs de Chat que queden sin consumidores.
- Revisar y eliminar referencias legacy de Chat en alertas/notificaciones, manteniendo `sightingId` para Alertas, Actividad y Detalle.
- Actualizar Room con migracion no destructiva desde la version actual `9` si se eliminan tablas Chat locales.
- Evitar nuevos writes de Chat y conservar temporalmente los documentos historicos remotos de `chatSessions` y `messages`; no borrar datos remotos.
- Mantener sin cambios funcionales el formulario de avistamiento, Alertas, Actividad, Detalle, Reportar contenido y Bloquear usuario.
- Actualizar tests y contratos para justificar o eliminar cada referencia restante.

## Fuera de alcance

- Borrar documentos historicos de Firestore o migrar conversaciones.
- Crear mensajeria nueva, reemplazar Chat por otra comunicacion o agregar panel administrativo.
- Rediseñar Inicio, Perfil, Reportar, Alertas, Actividad o Detalle.
- Cambiar la logica funcional de creacion de avistamientos, moderacion, bloqueo o navegacion `sightingId`.
- Modificar changes OpenSpec paralelos no relacionados.

## Preflight y sincronizacion

- `git status --short --branch` inicial: `main...origin/main`.
- `git status --porcelain=v1` inicial: vacio.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: `Already up to date`.
- `git rev-parse main`: `ed763f059d5d5c71adfa3a52dc78d2455e960891`.
- `git rev-parse origin/main`: `ed763f059d5d5c71adfa3a52dc78d2455e960891`.
- Rama creada desde `main`: `ops/retire-chat-legacy-code`.
- `git rev-parse HEAD` de la nueva rama: `ed763f059d5d5c71adfa3a52dc78d2455e960891`.
- Ramas/changes paralelos: `navigate-sighting-alert-to-detail`, `sticky-lost-pet-detail-actions`, `optimize-sighting-messaging-flow` y otros; el usuario autorizo paralelo.

## Contraste tecnico

- `MainActivity` aun declara `ROUTE_CHATS`, `ROUTE_CHAT_DETAIL`, callbacks de Chat y fallback de notificaciones por `chatId`.
- `ChatListScreen` y `ChatDetailScreen` exponen conversaciones, mensajes y envio.
- `PetViewModel` mantiene estados, seleccion y envio de Chat.
- `PetRepository` mantiene lectura de sesiones/mensajes y `sendChatMessage`; el fan-out nuevo de sightings ya fue desacoplado, por lo que SCRUM-26 debe retirar solo la infraestructura legacy restante.
- Room esta en version 9 y aun registra `ChatMessageEntity`, `ChatSessionEntity` y sus tablas; la migracion debe conservar otras tablas y no usar destructive migration.
- `RemoteMappers`, `RemoteDocuments`, `BackendCollections` y `firestore.rules` contienen contratos de Chat.
- Los tests incluyen contratos de Chat que deberan eliminarse o convertirse en regresiones de ausencia del Chat; los tests de sightings, Activity, Alertas y moderacion deben conservarse.
- `docs/design-system.md` fue leido; cualquier ajuste visual debe conservar Material 3 estable, tokens existentes y Light/Dark. No se rediseñara la identidad.

## Inventario SCRUM-26 y referencias restantes justificadas

- Eliminado de codigo activo: `ChatListScreen`, `ChatDetailScreen`, rutas y builders Chat, callbacks, estados/listeners ViewModel, lecturas/escrituras repository, entidades Room, DAO Chat, mappers y documentos remotos Chat, helpers de coleccion y fixtures seed Chat.
- Retenido por compatibilidad historica: `AppNotificationEntity.chatId`, `AppNotificationDocument.chatId` y `RemoteMappers.toNotificationEntity` pueden decodificar notificaciones antiguas; `chatId` no se serializa en nuevas notificaciones, no autoriza y no enruta.
- Retenido por migracion historica: SQL Chat dentro de migraciones anteriores a Room 10 permite actualizar bases antiguas; `MIGRATION_9_10` elimina unicamente `chat_messages` y `chat_sessions`. No se usa fallback destructivo.
- Retenido en reglas/documentacion: `chatSessions/{chatId}` y `messages/{messageId}` mantienen lecturas participant-only explicitas para documentos remotos historicos; create/update/delete estan denegados y no se ejecuta limpieza remota.
- Retenido en tests: contratos estaticos nombran simbolos Chat para demostrar su ausencia, validar la migracion, validar el bloqueo de writes y validar decodificacion historica. No son consumidores de runtime.
- No quedaron referencias activas a `ChatScreen`, `ChatViewModel`, `ChatRepository`, `ChatSessionEntity`, `ChatMessageEntity`, `sendChatMessage`, `ROUTE_CHATS` o `ROUTE_CHAT_DETAIL` en `app/src/main`.

## Implementacion y evidencia parcial

- Tareas 1.1-5.4 marcadas completas despues de inspeccion de diff, auditoria global y tests contractuales.
- `:app:compileDebugKotlin`: BUILD SUCCESSFUL.
- `:app:testDebugUnitTest`: BUILD SUCCESSFUL; 145 tests completados.
- `ChatRetirementContractTest` y `RoomMigrationContractTest` agregados; contratos Chat-only eliminados o adaptados.
- `firestore.rules` bloquea nuevos writes Chat y conserva lecturas historicas participant-only; no se borran documentos remotos.

## Verificacion final del implementador

- `openspec validate "retire-chat-legacy-code" --strict`: PASSED.
- `openspec instructions apply --change "retire-chat-legacy-code" --json`: 22/23 tareas completas; solo 6.4 queda pendiente por verificacion externa.
- `.\gradlew.bat :app:testDebugUnitTest --tests com.findyourpet.app.ChatRetirementContractTest --tests com.findyourpet.app.RoomMigrationContractTest --tests com.findyourpet.app.FirestoreRulesStaticTest --tests com.findyourpet.app.SightingFanOutContractTest --tests com.findyourpet.app.NotificationRoutingContractTest --tests com.findyourpet.app.ModerationContractTest --no-daemon --console=plain`: BUILD SUCCESSFUL.
- `.\gradlew.bat :app:testDebugUnitTest --no-daemon --console=plain`: BUILD SUCCESSFUL; 145 tests completados.
- `.\gradlew.bat :app:assembleDebug --no-daemon --console=plain`: BUILD SUCCESSFUL.
- `git diff --check`: PASSED.
- Auditoria de simbolos activos: no encontro `ChatScreen`, `ChatViewModel`, `ChatRepository`, `ChatSessionEntity`, `ChatMessageEntity`, `sendChatMessage`, `ROUTE_CHATS` ni `ROUTE_CHAT_DETAIL` en `app/src/main`.

## Bloqueo externo

- Tarea 6.4 no se marca completa: `adb` no esta disponible en el entorno, por lo que no puede ejecutarse la verificacion manual/emulador de navegacion y ausencia de Chat.
- Firebase CLI tampoco esta disponible; las reglas fueron cubiertas por contratos estaticos, pero no se valido contra Firebase Emulator o backend real.
- Estado del implementador: `BLOCKED`.

## Cierre del orquestador

- Verificacion local adicional: `openspec validate "retire-chat-legacy-code" --strict` => PASSED.
- Verificacion local adicional: `openspec instructions apply --change "retire-chat-legacy-code" --json` => 22/23 tareas completas.
- Verificacion local adicional: `git diff --check` => PASSED.
- `adb` y `firebase` => NOT_FOUND; no se crea commit ni PR mientras 6.4 permanezca pendiente.

## OpenSpec

Los artefactos se generaran con el CLI de OpenSpec siguiendo el orden indicado por `openspec status` e instrucciones de cada artefacto.

## Integracion

Despues de implementar y verificar, el change quedara `PASSED_PENDING_INTEGRATION` hasta contar con merge autorizado a `main` y sincronizacion con `origin/main`.
