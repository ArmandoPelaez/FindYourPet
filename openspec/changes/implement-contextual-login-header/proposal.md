## Why

El encabezado actual del Login presenta primero la acción de autenticación y no establece una jerarquía contextual clara para la pantalla. Este cambio incorpora un mensaje principal y un texto de apoyo que expliquen el propósito de FindYourPet antes de las opciones de acceso, manteniendo la legibilidad sobre el background existente y la composición usable en pantallas pequeñas.

## What Changes

- Reorganizar el contenido superior de `AuthScreen` para mostrar un headline dominante.
- Agregar un supporting text secundario con el propósito de la aplicación.
- Mantener la identidad `FindYourPet` visible de forma más discreta que el headline.
- Usar los estilos existentes de `AppTypography` y los tokens del Design System, sin tamaños visuales hardcodeados.
- Mantener intactos los flujos de email/password, Google Sign-In, registro, mensajes de error y lógica de autenticación.
- Validar el resultado en temas claro y oscuro y en pantallas pequeñas.

## Capabilities

### New Capabilities

- `contextual-login-header`: jerarquía visual y contenido contextual del encabezado de la pantalla de Login.

### Modified Capabilities

<!-- No se modifica el contrato funcional de autenticación; el cambio afecta únicamente la presentación del encabezado. -->

## Impact

- Código afectado: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt` y, si fuera necesario, reutilización de tokens ya existentes en `ui/theme`.
- No se modifican APIs, Firebase Auth, ViewModels, repositorios, datos, permisos ni privacidad.
- No se esperan cambios para usuarios existentes fuera de la presentación del Login.
- El rollback consiste en restaurar la composición previa del encabezado sin alterar los flujos de autenticación.
- Guardrails aplicables: Design System vigente, Material 3 estable, `AppTypography`, soporte Light/Dark y ausencia de valores visuales hardcodeados.
- Jira: `SCRUM-33`. Jira muestra prioridad `Medium`, mientras la descripción del issue declara `Prioridad: Alta`; queda registrado como discrepancia pendiente, sin cambiar el alcance.
