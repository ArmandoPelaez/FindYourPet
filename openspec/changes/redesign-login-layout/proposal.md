## Why

La pantalla actual de autenticación presenta el formulario dentro de una gran tarjeta exterior y una cabecera anidada, lo que hace que el acceso se perciba como un formulario aislado y no como parte integrada de la aplicación. SCRUM-31 busca modernizar únicamente la composición visual del login, manteniendo intactos los flujos de autenticación existentes.

## What Changes

- Eliminar la tarjeta exterior que contiene toda la pantalla de autenticación.
- Reorganizar el login como una composición vertical que use el viewport disponible.
- Mantener visibles y utilizables la identidad FindYourPet, el área visual de proximidad, el mensaje principal, el formulario, Google Sign-In y el acceso a creación de cuenta.
- Conservar el soporte para Light Theme y Dark Theme.
- Reutilizar tokens y componentes existentes del Design System, sin valores visuales hardcodeados ni APIs experimentales.
- Mantener sin cambios la lógica de autenticación, los estados del `ViewModel`, Firebase y los contratos de datos.

## Capabilities

### New Capabilities

- `login-screen-presentation`: composición visual responsive de la pantalla de autenticación sin alterar su comportamiento de autenticación.

### Modified Capabilities

- Ninguna. Los requisitos funcionales de `auth` no cambian; este change modifica únicamente la presentación de la pantalla.

## Impact

- Código afectado: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt` y, únicamente si fuera necesario para reutilizar tokens existentes, archivos del Design System.
- No se agregan dependencias, APIs, permisos, endpoints ni migraciones de datos.
- No cambia la identidad ni los datos de usuarios existentes.
- Validación: OpenSpec estricto, tests unitarios, build debug y revisión manual de login/signup, Google Sign-In, mensajes de error y ambos temas.
- Rollback: revertir los cambios de la rama `ops/redesign-login-layout`; no requiere migración ni recuperación de datos.
- Guardrails aplicables: Material 3 estable, tokens centralizados, Light/Dark Theme, no modificar autenticación ni lógica de dominio.
