## Why

La pantalla de autenticación mezcla contenido semántico —identidad, slogan, hero y textos descriptivos— dentro de una imagen de fondo fija con controles desplazables por encima. Al abrirse el teclado o reducirse la altura disponible, los campos y acciones pueden superponerse con ese contenido y perder legibilidad. SCRUM-51 solicita corregir la estructura para que Login y Crear cuenta sigan siendo utilizables sin alterar la autenticación.

## What Changes

- Separar el fondo visual decorativo del contenido semántico adaptable de autenticación.
- Renderizar como contenido de la interfaz el logo/branding, slogan, hero y textos descriptivos que hoy están incrustados en el fondo.
- Mantener una apariencia y jerarquía equivalentes con espacio suficiente y teclado cerrado.
- Permitir que el contenido se reorganice o desplace cuando disminuya la altura disponible.
- Dar prioridad al formulario, al campo editado y a las acciones necesarias cuando el teclado esté abierto.
- Mantener el fondo gráfico estable y evitar superposiciones entre hero, textos, campos y botones.
- Aplicar el comportamiento a Login y Crear cuenta.
- Conservar los flujos existentes de email/contraseña y Google sin cambios funcionales.
- No cambiar identidad, logo, slogan, textos, navegación, validaciones, Firebase ni Google Sign-In.

## Capabilities

### New Capabilities

- `adaptive-login-presentation`: Define la separación entre fondo decorativo y contenido semántico de autenticación, junto con su comportamiento adaptable ante cambios de altura y teclado.

### Modified Capabilities

- Ninguna. La capability funcional `auth` conserva sus proveedores, callbacks, estados y navegación; este cambio agrega un contrato de presentación adaptable.

## Impact

- Código afectado: composición de la pantalla de autenticación y sus pruebas de presentación/adaptabilidad.
- Se deberán revisar los recursos de fondo para que no contengan contenido semántico incrustado y conservar solo su tratamiento decorativo.
- No se modifican APIs, ViewModels, repositorios, Firebase, persistencia, permisos ni dependencias.
- No hay impacto de privacidad o seguridad; no se incorporan datos, permisos ni transmisión nueva.
- Los usuarios existentes conservarán los mismos flujos y acciones, con mejor legibilidad y acceso en pantallas bajas o con teclado.
- Rollback: revertir la composición de autenticación, los recursos visuales y las pruebas asociadas; no requiere migraciones ni cambios remotos.
- Guardrails aplicables: Jetpack Compose, Material 3 estable, tokens de `docs/design-system.md`, soporte Light/Dark, sin valores visuales hardcodeados ni APIs experimentales.
