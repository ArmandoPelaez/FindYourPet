## Why

La pantalla de Login presenta Entrar, Google y Crear cuenta con una jerarquía visual demasiado similar, lo que dificulta reconocer la acción principal. Este cambio ordena las acciones sin alterar los flujos de autenticación existentes y agrega estados de feedback y bloqueo para evitar submits simultáneos.

## What Changes

- Presentar `Entrar` como la única acción primaria.
- Presentar `Continuar con Google` como acción secundaria sobre una superficie neutra.
- Presentar `Crear una cuenta` como acción terciaria mediante un enlace de texto.
- Mantener los callbacks actuales de email/password, Google y cambio entre login y registro.
- Mostrar feedback visual durante autenticación y deshabilitar las acciones mientras una autenticación está en curso.
- Usar un asset oficial o preaprobado de Sign in with Google para la acción de Google, sin sustituirlo por un icono genérico ni alterar el logotipo.
- Permitir que el contenedor se integre visualmente con FindYourPet, dando precedencia a las reglas de marca de Google sobre las Design Rules locales dentro de la acción.
- No modificar ViewModel, Firebase Auth, repositorios ni contratos de dominio.

## Capabilities

### New Capabilities

- Ninguna. El cambio reorganiza una capacidad de autenticación existente.

### Modified Capabilities

- `auth`: la interfaz de autenticación debe exponer una jerarquía primaria/secundaria/terciaria clara y estados observables de carga sin cambiar el comportamiento de autenticación.

## Impact

- Código afectado: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt` y sus pruebas de presentación.
- Dependencias: no se requieren nuevas dependencias; se reutilizan Compose Material 3 estable, componentes `AppButton` y tokens existentes.
- Privacidad y seguridad: no se agregan datos, permisos ni superficies de autenticación; se conservan los flujos Firebase actuales.
- Usuarios existentes: los callbacks y resultados de autenticación permanecen sin cambios; solo cambia la jerarquía visual y el feedback de la pantalla.
- Rollback: revertir el cambio de presentación de `AuthScreen` restaura la disposición anterior sin migraciones de datos.
- Guardrails aplicables: Design Rules del proyecto, autenticación Firebase como fuente de identidad y ausencia de submits simultáneos.
