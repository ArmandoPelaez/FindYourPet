# FindYourPet / Mascotas Perdidas

FindYourPet es una aplicacion Android nativa para publicar mascotas perdidas y reportar avistamientos.

El objetivo del proyecto es convertir un prototipo local en un MVP productivo verificable: con autenticacion real, datos compartidos en backend, privacidad por diseno, carga real de imagenes, ubicacion consentida, moderacion y una base preparada para Google Play Internal testing.

## Proposito

La perdida de una mascota suele depender de difusion rapida, datos claros y comunicacion confiable. FindYourPet busca ordenar ese flujo en una app enfocada en:

- Publicar fichas de mascotas perdidas con foto, descripcion, zona de referencia y recompensa opcional.
- Permitir que otros usuarios autenticados reporten avistamientos.
- Mostrar el avistamiento al propietario mediante Alertas y Actividad, con navegacion al Detalle por `sightingId`.
- No publicar ni compartir telefono, email, direccion o datos personales de contacto mediante flujos administrados por la app.
- Evitar que los datos sensibles aparezcan en tarjetas publicas, previews o notificaciones.
- Mantener reglas tecnicas claras para avanzar hacia una publicacion controlada.

## Estado actual

El proyecto esta en transicion desde demo/local hacia MVP productivo.

Ya cuenta con:

- App Android en Kotlin con Jetpack Compose.
- Autenticacion con Firebase Auth cuando `app/google-services.json` esta configurado.
- Email/password y Google Sign-In.
- Cloud Firestore como fuente de verdad para flujos autenticados.
- Room como almacenamiento local/cache y soporte demo cuando no hay backend configurado.
- Publicaciones de mascotas, avistamientos, moderacion, notificaciones internas y perfil.
- Reglas de Firestore en `firestore.rules`.
- Carga de imagenes a Cloudinary mediante unsigned upload preset.
- Captura/uso de ubicacion con consentimiento y fallback manual/coarse.
- Guardrails de privacidad, permisos y release documentados en `docs/` y `openspec/`.

Todavia requiere validacion manual final para release firmado, Play Console, accesibilidad, Crashlytics en build release y smoke tests sobre dispositivo/emulador.

## Funcionalidades principales

- **Autenticacion:** pantalla de acceso obligatoria para usuarios no autenticados; registro, login, Google Sign-In y logout.
- **Feed de publicaciones:** lista de mascotas publicadas desde backend o cache local.
- **Crear publicacion:** formulario para publicar una mascota asociada al `uid` autenticado.
- **Reportar avistamiento:** flujo para enviar foto opcional, zona/ubicacion y notas sobre una mascota vista.
- **Moderacion:** reportar contenido y bloquear usuarios desde el Detalle de Avistamiento.
- **Notificaciones internas:** avisos de avistamientos sin exponer datos sensibles.
- **Perfil:** datos basicos del usuario autenticado.

## Principios de producto y privacidad

El proyecto trata como sensibles:

- Nombre del dueno.
- Email de cuenta.
- Coordenadas GPS.
- Fotos.
- Documentos remotos historicos de Chat retenidos sin acceso activo desde la app.
- Historial de avistamientos.
- Datos legacy de grants/contacto, que la app actual ignora o elimina de caches locales.

Reglas base:

- La app no ofrece mensajeria ni solicita, revela, autoriza, revoca ni notifica telefono, email o direccion como datos de contacto.
- Las notificaciones no deben incluir telefono, email, direccion, coordenadas exactas, URLs privadas, notas completas ni cuerpos completos de mensajes.
- Las acciones de dueno se basan en el `uid` real de Firebase, no en ids hardcodeados.
- Room no otorga autoridad productiva; funciona como cache/local demo.
- No se deben prometer cifrado, anonimato, tiempo real o privacidad productiva si el codigo no lo implementa y valida.

## Stack tecnico

- **Lenguaje:** Kotlin.
- **UI:** Jetpack Compose + Material 3.
- **Arquitectura Android:** `MainActivity`, Navigation Compose, ViewModel, Repository, Flow/StateFlow.
- **Persistencia local:** Room.
- **Backend:** Firebase Authentication + Cloud Firestore.
- **Imagenes:** Cloudinary Android SDK con upload preset unsigned.
- **Ubicacion:** Google Play Services Location.
- **Mapa:** Google Maps SDK for Android y Maps Compose; ver `docs/google-maps-setup.md`.
- **Crash reporting:** Firebase Crashlytics, aplicado cuando existe configuracion Firebase local.
- **Build:** Gradle Kotlin DSL, Gradle Wrapper.
- **Tests:** JUnit, Robolectric, Compose UI tests y Roborazzi.
- **Specs:** OpenSpec para cambios, decisiones y criterios de aceptacion.

Versiones relevantes:

- Android Gradle Plugin: `9.1.1`.
- Kotlin: `2.2.10`.
- Gradle Wrapper: `9.3.1`.
- Java toolchain: `21`.
- `minSdk`: `24`.
- `targetSdk`: `36`.
- `compileSdk`: `36`.
- `applicationId`: `com.findyourpet.app`.

## Estructura del repositorio

```text
.
|-- app/                         # Modulo Android principal
|   |-- src/main/java/com/findyourpet/app/
|   |   |-- data/                # Auth, repositorios, Room, Firestore, modelos remotos
|   |   |-- domain/              # Reglas de negocio y ownership
|   |   |-- ui/                  # Screens, components, theme y ViewModel
|   |   |-- util/                # CrashReporter, NotificationHelper
|   |   `-- MainActivity.kt      # Entry point y navegacion Compose
|   |-- src/test/                # Unit, Robolectric, Compose y guardrail tests
|   `-- build.gradle.kts
|-- buildSrc/                    # Tareas Gradle propias, como validacion de firma release
|-- docs/                        # Guias funcionales, privacidad, Firebase y release
|-- openspec/                    # Specs vivas y cambios historicos/activos
|-- public/                      # Politica de privacidad para Firebase Hosting
|-- firestore.rules              # Reglas de acceso de Firestore
|-- firebase.json                # Configuracion Firestore/Hosting
|-- gradle/                      # Wrapper y catalogo de versiones
`-- README.md
```

## Configuracion local

Requisitos:

- Android Studio compatible con AGP 9.x.
- JDK 21 o permitir que Gradle resuelva el toolchain mediante Foojay.
- Android SDK con API 36.
- Acceso a un proyecto Firebase de desarrollo o testing.

Pasos recomendados:

1. Clonar el repositorio.
2. Abrir el proyecto desde Android Studio.
3. Sincronizar Gradle.
4. Crear o seleccionar un proyecto Firebase.
5. Registrar una app Android con package `com.findyourpet.app`.
6. Descargar `google-services.json` y colocarlo en `app/google-services.json`.
7. Habilitar Firebase Authentication con Email/password y Google.
8. Crear Cloud Firestore y publicar `firestore.rules`.
9. Configurar `firebase_web_client_id` en `app/src/main/res/values/strings.xml` para Google Sign-In.
10. Verificar Cloudinary en `app/build.gradle.kts`:
    - `CLOUDINARY_CLOUD_NAME`
    - `CLOUDINARY_UPLOAD_PRESET`

`app/google-services.json`, keystores, `.env` y credenciales locales no deben commitearse.

## Comandos utiles

En Windows:

```powershell
.\gradlew.bat --version
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat :app:validateReleaseSigning
.\gradlew.bat assembleRelease
.\gradlew.bat bundleRelease
```

En macOS/Linux:

```bash
./gradlew --version
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew :app:validateReleaseSigning
./gradlew assembleRelease
./gradlew bundleRelease
```

Para release firmado se esperan estas variables de entorno:

```text
KEYSTORE_PATH
STORE_PASSWORD
KEY_ALIAS
KEY_PASSWORD
```

Si no estan configuradas, `:app:validateReleaseSigning` debe fallar con un error claro sin imprimir secretos.

## Firebase y backend

La app aplica los plugins de Google Services y Crashlytics solo si existe `app/google-services.json`. Esto permite ejecutar tests locales y CI sin una configuracion Firebase privada.

Colecciones/modelos principales:

- `users/{uid}`: perfil del usuario autenticado.
- `petPosts/{postId}`: publicaciones de mascotas.
- `sightings/{sightingId}`: avistamientos.
- `chatSessions/{chatId}` y `messages/{messageId}`: documentos remotos historicos retenidos; las reglas bloquean nuevos writes y la app no los enruta.
- `users/{uid}/notifications/{notificationId}`: notificaciones internas.

Las reglas viven en `firestore.rules` y deben validarse antes de usar datos reales.

## Testing y calidad

La suite actual cubre:

- Mappers remotos.
- Reglas de ownership.
- Estados del ViewModel.
- Validaciones de producto real.
- Guardrails estaticos de privacidad, permisos, textos y configuracion.
- Componentes Compose clave.
- Smoke tests de `MainActivity` con Robolectric.
- Reglas de Firestore por inspeccion estatica.

Comando base antes de cerrar cambios:

```powershell
.\gradlew.bat testDebugUnitTest
```

Cuando el cambio toca UI, permisos, ubicacion, camara, release o integraciones reales, tambien debe existir validacion manual documentada.

## Documentacion importante

- `docs/firebase-auth-firestore-setup.md`: configuracion Firebase local.
- `docs/firebase-rules-validation.md`: validacion de reglas Firestore.
- `docs/privacy-policy.md`: politica de privacidad en Markdown.
- `public/privacy-policy.html`: version publica para hosting.
- `docs/google-play-permissions.md`: inventario de permisos Android.
- `docs/google-play-data-safety-draft.md`: borrador Data safety para Play Console.
- `docs/release-validation-prepare-production-release.md`: estado de release.
- `docs/guia-funcional-cambios-y-testeos.md`: resumen funcional para QA/producto.
- `docs/GUIA_CAMBIOS_OPENSPEC.md`: guia de evolucion con OpenSpec.

## Flujo de trabajo con OpenSpec

El proyecto usa OpenSpec para ordenar cambios funcionales y tecnicos. Las capabilities principales estan en `openspec/specs/`:

- `auth`
- `pet-posts`
- `sightings`
- `chat-retirement`
- `contact-privacy`
- `notifications`
- `local-storage`
- `media-upload`
- `location`
- `release-readiness`

Cada cambio relevante deberia tener:

- `proposal.md`: problema, alcance, impacto y rollback.
- `design.md`: decisiones tecnicas, backend, cliente, datos y errores.
- `tasks.md`: implementacion, tests y validacion manual.
- `specs/`: escenarios observables Given/When/Then.

## Roadmap cercano

- Completar validacion manual de permisos en dispositivo/emulador.
- Confirmar flujos reales de foto, ubicacion y avistamiento sobre backend.
- Validar reglas Firestore en emulador o proyecto no productivo.
- Generar artifact firmado para Internal testing.
- Confirmar Crashlytics y mapping de release.
- Ejecutar smoke test de release.
- Completar datos finales de Play Console y politica publica.

## Licencia

Licencia pendiente de definir por el responsable del proyecto.
