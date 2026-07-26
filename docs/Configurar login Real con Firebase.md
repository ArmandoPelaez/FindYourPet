# Configurar login Real con Firebase

Esta guia deja ordenados los pasos para configurar Firebase Authentication y poder probar login real en emulador o celular. La idea es avanzar de lo minimo funcional a lo mas completo.

## Nivel 0: Compilar sin Firebase real

Estado actual del repo:

- La app puede compilar sin `app/google-services.json`.
- Si falta configuracion Firebase, el login real no funciona.
- Esto sirve para validar build, UI y tests locales, pero no para crear usuarios reales.

Comandos utiles:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

## Nivel 1: Login basico con Email/Password

Este es el minimo recomendado para probar usuarios reales.

### 1. Crear proyecto Firebase

1. Entrar a Firebase Console.
2. Crear un proyecto en plan gratuito Spark.
3. Agregar una app Android.
4. Usar este package name:

```text
com.findyourpet.app
```

### 2. Descargar `google-services.json`

En Firebase Console:

1. Ir a Project settings.
2. Entrar a la app Android registrada.
3. Descargar `google-services.json`.
4. Guardarlo en:

```text
app/google-services.json
```

No subir este archivo a git. Ya esta incluido en `.gitignore`.

### 3. Activar Email/Password

En Firebase Console:

1. Ir a Authentication.
2. Entrar a Sign-in method.
3. Activar Email/Password.

Con esto ya deberias poder:

- Instalar la app en emulador o celular.
- Crear una cuenta con email/password.
- Iniciar sesion.
- Cerrar sesion.
- Ver que el perfil queda asociado al `uid` real de Firebase.

## Nivel 2: Probar en emulador o celular

### Emulador

1. Abrir Android Studio.
2. Crear o iniciar un emulador con Google Play Services.
3. Ejecutar:

```powershell
.\gradlew.bat installDebug
```

O correr la app desde Android Studio.

### Celular fisico

1. Activar Developer Options en el telefono.
2. Activar USB debugging.
3. Conectar por USB.
4. Ejecutar:

```powershell
.\gradlew.bat installDebug
```

Tambien puedes usar Android Studio y elegir el dispositivo conectado.

## Nivel 3: Google Sign-In

Google Sign-In requiere pasos extra. Email/password puede funcionar antes de hacer esto.

### 1. Obtener SHA-1 y SHA-256

Ejecutar:

```powershell
.\gradlew.bat signingReport
```

Buscar el variant `debug` y copiar:

- SHA1
- SHA-256

### 2. Registrar fingerprints en Firebase

En Firebase Console:

1. Project settings.
2. App Android `com.findyourpet.app`.
3. Add fingerprint.
4. Pegar SHA-1.
5. Repetir con SHA-256.

Luego descargar nuevamente `google-services.json` y reemplazar el archivo local en:

```text
app/google-services.json
```

### 3. Activar proveedor Google

En Firebase Console:

1. Authentication.
2. Sign-in method.
3. Activar Google.

### 4. Configurar Web Client ID

Para Credential Manager/Firebase Google Sign-In se usa el client id web, no el Android client id.

Buscarlo en Google Cloud Console:

1. APIs & Services.
2. Credentials.
3. OAuth 2.0 Client IDs.
4. Copiar el Client ID de tipo Web application.

Tiene una forma parecida a:

```text
1234567890-abcxyz.apps.googleusercontent.com
```

Pegar ese valor en:

```xml
<!-- app/src/main/res/values/strings.xml -->
<string name="firebase_web_client_id">1234567890-abcxyz.apps.googleusercontent.com</string>
```

Con esto deberias poder probar el boton `Continuar con Google`.

## Nivel 4: Firestore y ownership real

Para que perfil, publicaciones y reglas de dueno funcionen con datos reales, tambien hay que configurar Cloud Firestore.

### 1. Crear base Firestore

En Firebase Console:

1. Ir a Firestore Database.
2. Crear database.
3. Usar modo bloqueado/produccion si Firebase lo ofrece.
4. Elegir una region.

### 2. Publicar reglas

El repo incluye:

```text
firestore.rules
```

Estas reglas usan `request.auth.uid` para:

- Permitir `users/{uid}` solo al usuario con ese `uid`.
- Permitir crear publicaciones solo con `ownerId == request.auth.uid`.
- Impedir cambiar `ownerId`.
- Restringir chats y mensajes a participantes.
- Permitir cambios de contacto solo al dueno.

Publicar estas reglas desde Firebase Console o Firebase CLI antes de probar con datos reales.

## Checklist de prueba basica

1. Abrir la app sin sesion.
2. Ver pantalla de login.
3. Crear cuenta con email/password.
4. Confirmar que entra a la app.
5. Ir a Perfil.
6. Cerrar sesion.
7. Volver a iniciar sesion.
8. Crear una publicacion.
9. Verificar en Firestore que `ownerId` coincide con el `uid` del usuario.
10. Probar con otro usuario y confirmar que no puede editar/cerrar esa publicacion.

## Checklist de prueba con Google

1. Confirmar que `google-services.json` fue descargado despues de registrar SHA-1/SHA-256.
2. Confirmar que `firebase_web_client_id` no tiene el valor `REPLACE_WITH_WEB_CLIENT_ID`.
3. Abrir la app en emulador/celular con Google Play Services.
4. Tocar `Continuar con Google`.
5. Elegir una cuenta.
6. Confirmar que entra a la app.
7. Confirmar en Firebase Authentication que el usuario aparece con proveedor Google.

## Problemas comunes

### La app compila, pero no inicia sesion

Revisar que exista:

```text
app/google-services.json
```

Y que Email/Password este activado en Firebase Authentication.

### Google Sign-In no funciona

Revisar:

- SHA-1 registrado.
- SHA-256 registrado.
- `google-services.json` descargado despues de registrar fingerprints.
- `firebase_web_client_id` configurado con el Web application client id.
- Emulador/dispositivo con Google Play Services.

### El boton de Google dice que falta configuracion

Revisar:

```xml
<string name="firebase_web_client_id">REPLACE_WITH_WEB_CLIENT_ID</string>
```

Si sigue con ese valor, reemplazarlo por el Web client ID real.

### Un usuario puede ver UI pero no escribir en Firestore

Revisar que las reglas `firestore.rules` esten publicadas y que el documento que intenta escribir use:

```text
ownerId == uid del usuario autenticado
```

## Orden recomendado

1. Primero hacer funcionar Email/Password.
2. Luego crear y leer perfil desde Firestore.
3. Despues validar crear publicaciones con `ownerId` real.
4. Luego agregar Google Sign-In.
5. Finalmente probar reglas de Firestore con dos usuarios distintos.
