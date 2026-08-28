## Why

La pantalla de login necesita incorporar el nuevo fondo visual provisto para reforzar la identidad de FindYourPet. El cambio debe conservar la composición actual de la pantalla: el fondo será decorativo y los campos, botones, mensajes y acciones de autenticación seguirán disponibles y utilizables en teléfonos y tablets.

## What Changes

- Usar `app/src/main/res/drawable-nodpi/imagen_nuevo_fondo_pantalla.png` como fondo exclusivo de `AuthScreen`.
- Mantener la imagen detrás de la composición existente, sin convertirla en un elemento interactivo ni bloquear los eventos de los controles.
- Conservar la capa de legibilidad basada en el tema y todos los tokens actuales del Design System.
- Mantener el comportamiento responsive existente, incluyendo ancho máximo tokenizado, desplazamiento vertical, insets del sistema y `imePadding()` cuando corresponda.
- Preservar sin cambios la lógica, estados, validaciones, callbacks, ViewModel, repositorios, Firebase Auth, navegación y dependencias.
- Validar la presentación en Light Theme, Dark Theme, teléfonos compactos y amplios, y tablets, incluyendo teclado abierto.

## Capabilities

### New Capabilities

- `login-background-presentation`: fondo decorativo local de la pantalla de login, con composición responsive y controles accesibles.

### Modified Capabilities

- Ninguna. Los requisitos funcionales de `auth` no cambian.

## Impact

- Código afectado: composición visual de `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt` y sus pruebas de presentación, si requieren actualizarse.
- Recurso visual utilizado: `app/src/main/res/drawable-nodpi/imagen_nuevo_fondo_pantalla.png`.
- No se agregan APIs, permisos, dependencias, servicios, almacenamiento ni llamadas de red.
- No hay impacto en privacidad, seguridad, datos o autenticación; la imagen se incluye localmente en el APK.
- Los usuarios existentes solo observarán un cambio visual en el login; sus flujos y credenciales no se alteran.
- Rollback: restaurar la referencia al recurso de fondo anterior o revertir el commit del change, sin migraciones ni efectos persistentes.
- El objetivo general relacionado es mantener una experiencia Android usable y verificable mientras se evoluciona el MVP, sin ampliar la superficie funcional.
