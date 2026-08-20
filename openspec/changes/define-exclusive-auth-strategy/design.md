## Context

FindYourPet usa Firebase Authentication como fuente de verdad para la identidad. La implementación actual permite Email/Password y Google Sign-In, pero `FirebaseAuthRepository` retiene una credencial Google ante `FirebaseAuthUserCollisionException` y luego ejecuta `linkWithCredential` después de un login con contraseña. SCRUM-45 exige lo contrario: una cuenta, un proveedor original, sin vinculación ni conversión.

Firebase documenta que una colisión entre proveedores puede producir `account-exists-with-different-credential` y que `fetchSignInMethodsForEmail` sirve para identificar proveedores, pero puede devolver una lista vacía cuando está activa Email Enumeration Protection. Por eso la lista de proveedores será una señal explícita cuando esté disponible, no la única fuente de verdad.

## Goals / Non-Goals

**Goals:**

- Mantener una única identidad Firebase por correo y conservar su UID y datos.
- Definir los cuatro escenarios de usuario nuevo/existente para Email/Password y Google.
- Detectar conflictos de proveedor de forma segura y mapearlos a mensajes funcionales recuperables.
- Eliminar el almacenamiento de credenciales pendientes y cualquier llamada de `linkWithCredential`.
- Mantener la sesión sin autenticar cuando el usuario intenta un método distinto al original.
- Cubrir la política con pruebas de contrato y errores sin depender de cuentas Firebase reales en unit tests.

**Non-Goals:**

- Vincular proveedores, migrar cuentas o permitir un segundo método de acceso.
- Cambiar la configuración de Firebase, agregar proveedores OAuth o crear funciones backend.
- Rediseñar la pantalla de Login, cambiar textos no relacionados o modificar flujos de logout.
- Administrar proveedores desde Perfil/Seguridad.

## Decisions

### 1. Firebase mantiene la identidad y la regla de una cuenta por email

Se conserva Firebase Auth como autoridad. La aplicación no crea una segunda cuenta ni intenta reemplazar el proveedor original. El UID existente y los datos Firestore asociados permanecen sin cambios.

Alternativa descartada: vincular credenciales para simplificar el acceso. Contradice SCRUM-45 y transforma una cuenta de proveedor único en una cuenta multi-proveedor.

### 2. La aplicación nunca conserva una credencial pendiente

Ante una colisión Google sobre una cuenta Email/Password, se descarta la credencial Google y se devuelve un resultado de dominio `EmailPasswordRequired`. No se llama a `linkWithCredential`, no se autentica al usuario existente automáticamente y el estado permanece recuperable.

Alternativa descartada: conservar la credencial hasta que el usuario introduzca su contraseña. Ese flujo es precisamente account linking y puede alterar el contrato de identidad.

### 3. Detección de proveedor con degradación segura

Para un intento Email/Password, se consulta el proveedor asociado solo cuando Firebase devuelve una identificación explícita. Si la respuesta contiene `password`, se continúa con el flujo normal; si contiene `google.com`, se muestra el mensaje para usar Google y no se intenta crear ni convertir una cuenta.

Si Firebase devuelve una lista vacía o no concluyente —incluido el caso de Email Enumeration Protection, que permanece activa— no se debe inferir el proveedor. Se ejecuta la autenticación solicitada y, ante fallo, se muestra el mensaje recuperable “No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.”. La redacción orienta a probar Google sin confirmar la existencia ni el proveedor de un correo.

Para un intento Google, la colisión `account-exists-with-different-credential` es la señal explícita para indicar que debe usarse Email/Password. Otros errores se mapean al mismo fallback recuperable aprobado, sin exponer el mensaje crudo de Firebase.

Alternativa descartada: basar toda la política en `fetchSignInMethodsForEmail`. Puede devolver una lista vacía con protección contra enumeración y no debe convertirse en un canal de descubrimiento de cuentas.

### 4. Los errores se traducen en el dominio antes de llegar a Compose

El repositorio devuelve resultados o excepciones de dominio estables, por ejemplo `EmailPasswordRequired`, `GoogleRequired` y `AuthenticationFailed`. El ViewModel/estado de autenticación transforma esos resultados en mensajes funcionales localizables. No se propaga `Throwable.message`, códigos ni detalles de Firebase a la UI.

Mensajes conceptuales requeridos:

- Cuenta Email/Password usada con Google: “Esta cuenta fue creada con correo y contraseña. Iniciá sesión utilizando tu contraseña.”
- Cuenta Google usada con Email/Password, cuando Firebase confirma el proveedor: “Esta cuenta fue creada utilizando Google. Iniciá sesión con Google.”
- Proveedor no determinable: “No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.”, sin confirmar si el correo existe.

Cuando `AuthenticationFailed` se muestra como fallback orientado a Google después de un intento Email/Password en Login (`isSignUp == false`), `AuthScreen` limpia Email y Contraseña y conserva visible el mensaje. El modo sign-up y los intentos Google no limpian esos campos.

### 5. Las pruebas verifican invariantes, no datos reales de Firebase

Se actualizarán los contratos estáticos/unitarios para comprobar que no existen `linkWithCredential`, credenciales pendientes ni cuentas duplicadas, y que los conflictos mantienen el estado no autenticado. La validación manual posterior cubrirá cuentas reales Email/Password y Google, usuario nuevo, cancelación, errores y logout.

## Risks / Trade-offs

- **[Firebase oculta el proveedor]** → Email Enumeration Protection permanece activa; no inferir el proveedor y usar el mensaje aprobado, permitiendo reintento con Google sin confirmar la existencia de la cuenta.
- **[El código actual ya puede tener la ruta de linking]** → Retirar el estado/credencial pendiente y actualizar las pruebas que actualmente la exigen antes de marcar el change como verificado.
- **[Mensajes distintos entre proveedores]** → Centralizar el mapeo en el dominio y cubrir cada resultado con pruebas.
- **[Usuarios acostumbrados a vincular]** → El cambio es deliberadamente incompatible con linking; conservar UID/datos y explicar el método original reduce el impacto.
- **[Configuración Firebase permite más de una cuenta por email]** → Verificar la configuración “One account per email address” como prerrequisito operativo; la app no puede garantizar unicidad si Firebase permite duplicados.

## Migration Plan

1. Actualizar el contrato `auth` y los tipos de resultado/mensaje.
2. Eliminar la ruta de credencial pendiente y vinculación del repositorio.
3. Implementar la detección explícita, el fallback por enumeración y las pruebas.
4. Ejecutar tests, build y validación manual con cuentas existentes y nuevas.
5. No ejecutar migraciones de datos: las cuentas y UID existentes permanecen intactos.

Rollback: revertir el artefacto de aplicación a la versión validada anterior. No se requieren cambios ni migraciones en Firebase; no se debe reactivar linking como operación de recuperación sobre cuentas existentes.

## Confirmed Decisions

- Email Enumeration Protection permanece activa.
- El fallback aprobado es: “No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.”

## Open Questions
- Confirmar las traducciones finales y el canal de presentación de los mensajes funcionales en la pantalla de Login.
