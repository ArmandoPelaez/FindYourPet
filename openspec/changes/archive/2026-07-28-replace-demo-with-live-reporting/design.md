## Context

FindYourPet ya tiene Firebase Authentication, Firestore, reglas iniciales, cache local y flujos principales de publicaciones, avistamientos, chat y notificaciones. Sin embargo, la experiencia de producto todavia mezcla datos reales con simulaciones: fotos preset en formularios, ubicacion typed/demo, notificaciones del sistema generadas localmente y `seedInitialDataIfNeeded` activado desde el ViewModel.

Este cambio convierte esos puntos en funciones reales de MVP. La identidad de produccion sigue siendo Firebase Auth; Firestore conserva los documentos compartidos; Cloudinary alojara fotos reales mediante unsigned upload preset; las alertas al dueno se mantendran como notificaciones in-app persistentes; y Room quedara limitado a cache o demo aislada. Firebase Storage y push notifications quedan para etapas posteriores.

## Goals / Non-Goals

**Goals:**

- Permitir publicar mascotas con una foto real capturada o elegida por el usuario.
- Permitir reportar avistamientos con ubicacion real consentida y foto real opcional.
- Subir fotos a Cloudinary con preset unsigned y guardar en Firestore las referencias vinculadas al usuario y al recurso autorizado.
- Crear notificaciones in-app persistentes para el dueno cuando se registra un avistamiento o evento relevante.
- Remover o aislar seed data para que la app autenticada funcione con feed vacio y datos backend reales.
- Validar formularios antes de crear publicaciones, avistamientos, media, ubicacion o notificaciones.
- Mantener estados claros para permisos concedidos, denegados, denegados permanentemente y servicios no disponibles.

**Non-Goals:**

- Implementar cifrado extremo a extremo para fotos, mensajes o ubicaciones.
- Prometer entrega instantanea garantizada de push notifications.
- Implementar Firebase Cloud Messaging o Cloud Functions para envio push.
- Migrar automaticamente publicaciones demo a cuentas productivas.
- Exponer coordenadas precisas o contacto del dueno en superficies publicas.
- Crear un backend custom fuera del stack Firebase existente.
- Usar Firebase Storage antes de pasar el proyecto a Blaze.

## Decisions

### Decision: Use Android Photo Picker plus camera capture for media input

The app SHALL prefer the Android Photo Picker for gallery selection because it minimizes storage permission scope on supported Android versions. Camera capture SHALL use a scoped app-owned URI through `FileProvider` or equivalent and SHALL handle permission denial before launch.

Alternatives considered:

- Request broad storage permissions: rejected because it expands sensitive access beyond the selected media.
- Keep preset image selection as fallback in production: rejected because it preserves demo behavior in the production path.

### Decision: Store production photos in Cloudinary for this stage

Uploaded images SHALL be sent from the Android app to Cloudinary using the configured unsigned upload preset. Firestore SHALL store the Cloudinary secure URL, provider and public ID as media metadata linked to the authenticated post or sighting. Upload completion SHALL be required before a production post or sighting is committed, unless the form marks the photo optional. The app SHALL NOT include the Cloudinary API secret.

Alternatives considered:

- Store base64 images in Firestore: rejected because it is inefficient and complicates rules, caching and document size.
- Keep local file URIs only: rejected because other users and devices cannot access the evidence.
- Use Firebase Storage now: deferred because the current Firebase project is on Spark and Storage requires Blaze for this setup.

### Decision: Capture location only after explicit user action

Location SHALL be requested from a clear user action in the create/report flow. The app SHALL support unavailable, denied and permanently denied states and SHALL allow non-public precise coordinates to be stored only when rules and UI visibility protect them. Public UI can show a coarse location label or approximate area.

Alternatives considered:

- Auto-capture location on screen open: rejected because it asks for sensitive data before intent is clear.
- Require exact GPS for every sighting: rejected because users may deny permission or need to submit a useful manual location.

### Decision: Keep alerts in-app for this stage

The app SHALL create persistent, per-user backend notification records and update badges/chat entry points while the app is open or when the owner returns. Sensitive fields such as phone, email, address, exact coordinates, photo URLs and full notes SHALL stay in authenticated app reads, not notification preview text. Push delivery is intentionally deferred to a future `add-push-notifications` change that can add Cloud Functions/FCM safely.

Alternatives considered:

- Add FCM now: deferred because real push delivery requires trusted backend fan-out and broader infrastructure validation.
- Continue local notifications only: rejected as the durable path because local notifications do not survive across devices or account changes.

### Decision: Authenticated production flows do not seed demo data

`seedInitialDataIfNeeded` SHALL be removed from authenticated startup or gated behind an explicit demo/development mode. Empty backend state SHALL render a proper empty UI instead of silently injecting fake pets, sightings, chats or notifications.

Alternatives considered:

- Keep seed data until launch: rejected because it hides broken backend, media, location and notification flows.
- Auto-import seed data for every user: rejected because demo owners and photos cannot establish production consent or ownership.

### Decision: Validate before side effects

Form validation SHALL run before requesting uploads, location commits or backend writes. Required fields, media selection/upload status, location consent state and authenticated user identity SHALL be checked before the app creates remote documents or notification events.

Alternatives considered:

- Write partial documents and patch later: rejected because it can create orphan media, incomplete posts or misleading alerts.

## Risks / Trade-offs

- [Risk] Owners will not receive an immediate system alert while the app is closed -> Mitigation: keep persistent in-app notifications and chat updates as the durable source of truth, and track push as a separate future change.
- [Risk] Unsigned Cloudinary presets can be abused if unrestricted -> Mitigation: use an unsigned preset with generated public IDs, no API secret in app code, image-only settings where available, and Firestore validation for Cloudinary media metadata.
- [Risk] Location permission denial can block reporting -> Mitigation: support a manual/coarse location fallback where product rules allow it.
- [Risk] Camera and upload failures can leave temporary files -> Mitigation: clean failed captures and do not commit Firestore documents until upload succeeds.
- [Risk] Removing seed data can make screens appear empty during development -> Mitigation: add explicit empty states and keep demo mode only behind a non-production gate.

## Migration Plan

1. Add media picker/camera abstractions, upload status models and Cloudinary media metadata helpers.
2. Add location provider abstraction and permission state handling for create post and sighting flows.
3. Keep sighting fan-out creating privacy-safe in-app notification records and chat updates.
4. Replace preset photo and simulated location UI with real controls, validation and denial states.
5. Gate or remove `seedInitialDataIfNeeded` from authenticated startup and verify empty backend behavior.
6. Update Firestore rules and static tests for Cloudinary media metadata and precise location fields.
7. Run debug build, unit tests, rules validation and manual permission validation on supported Android versions.

Rollback strategy: disable media upload or location capture through configuration while preserving backend documents and in-app notifications. Production builds MUST NOT re-enable seed data as a substitute for real flows.

## Open Questions

No blocking open questions. Push notification delivery is intentionally deferred to a future change that can introduce trusted backend fan-out.
