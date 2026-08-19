## Why

El bloque de autenticación del Login quedó demasiado próximo al hero y la pantalla pierde equilibrio vertical en alturas estándar. SCRUM-43 busca separar el formulario del hero y centrarlo aproximadamente en el espacio disponible, manteniendo la composición interna y el comportamiento existente.

## What Changes

- Reubicar como una única unidad visual el título del formulario, Email, Contraseña, Entrar, el divisor, Google y Crear una cuenta.
- Usar distribución vertical flexible y responsive para aumentar el espacio entre hero y autenticación sin offsets fijos ni márgenes por dispositivo.
- Preservar el spacing interno, orden, anchos, tipografías, colores, estilos, fondo, hero, scroll, IME y comportamiento de autenticación.
- Validar alturas estándar, pantallas pequeñas y teclado abierto.

## Capabilities

### New Capabilities

- `login-vertical-auth-layout`: distribución vertical adaptable del bloque de autenticación del Login.

### Modified Capabilities

- Ninguna. Los requisitos funcionales existentes de `auth` no cambian; el change agrega una capacidad de composición visual del Login.

## Impact

- Código afectado: composición de `AuthScreen` y sus pruebas de presentación.
- No se modifican ViewModel, Firebase Auth, repositorios, navegación, permisos, contratos de dominio, dependencias ni backend.
- No hay impacto de privacidad, seguridad, datos o permisos.
- Rollback: revertir el commit del change restaura la distribución vertical previa sin cambios de contrato.
- Objetivo relacionado: modernizar la pantalla de inicio sin alterar la identidad visual existente.
- Guardrails aplicables: usar tokens y componentes existentes, soportar Light/Dark Theme, no introducir valores visuales hardcodeados ni APIs experimentales.
