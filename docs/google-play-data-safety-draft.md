# Borrador Data safety para Google Play

Ultima actualizacion: 2026-07-31

Este borrador alinea la declaracion de Google Play con `app/src/main/AndroidManifest.xml`, `docs/privacy-policy.md` y `docs/google-play-permissions.md`. Debe copiarse y revisarse en Play Console durante Internal testing.

## Categorias de datos

| Categoria Play | Datos en FindYourPet | Finalidad | Compartido con terceros/proveedores |
| --- | --- | --- | --- |
| Informacion personal | Nombre visible y email de cuenta | Cuenta y perfil autenticado | Firebase Authentication, Cloud Firestore |
| Fotos y videos | Fotos de mascotas y avistamientos elegidas por el usuario | Publicaciones, avistamientos, evidencia visual | Cloudinary, Cloud Firestore para referencias |
| Ubicacion | Zona aproximada, ubicacion GPS si el usuario acepta, o punto elegido en el mapa por la persona | Publicaciones y avistamientos | Cloud Firestore, Google Play Services Location y Google Maps SDK for Android |
| Mensajes | Conversaciones privadas y metadata de chat | Comunicacion por chat interno entre participantes autorizados | Cloud Firestore |
| Contenido generado por usuarios | Publicaciones, notas de avistamiento, estados y reportes | Operacion principal de la app | Cloud Firestore, Cloudinary cuando incluye imagen |
| Diagnostico | Version de app, estado tecnico acotado y crash reports | Diagnostico de fallos | Firebase Crashlytics |

## Permisos declarados

- `android.permission.INTERNET`: requerido para Firebase, Cloudinary y diagnostico.
- `android.permission.CAMERA`: requerido solo cuando el usuario toma una foto.
- `android.permission.ACCESS_COARSE_LOCATION`: requerido solo cuando el usuario usa ubicacion aproximada.
- `android.permission.ACCESS_FINE_LOCATION`: requerido solo cuando el usuario acepta ubicacion precisa.
- `android.permission.POST_NOTIFICATIONS`: requerido para mostrar avisos locales de avistamientos y mensajes; si se deniega, la app continua sin esos avisos.

## Guardrails para completar Play Console

- No declarar datos sensibles como publicos por defecto.
- No declarar que la app solicita, comparte o administra telefono, email o direccion como canal de contacto entre usuarios.
- No declarar notificaciones o crash reports como canal para telefono, email, direccion, coordenadas exactas, URLs privadas de fotos, notas completas ni cuerpos completos de mensajes.
- No marcar promesas de cifrado, anonimato o tiempo real si no estan implementadas como requisito tecnico verificable.
- Si se agrega un permiso nuevo al manifest, actualizar este borrador, el inventario de permisos y la politica de privacidad antes de subir un build.
