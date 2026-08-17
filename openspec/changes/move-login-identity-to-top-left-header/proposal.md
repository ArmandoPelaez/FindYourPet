## Why

La identidad de FindYourPet todavía ocupa el centro del Login mediante un avatar circular grande, compitiendo con el headline y consumiendo espacio vertical. SCRUM-40 busca convertirla en una firma de marca discreta que contextualice la pantalla sin desplazar el foco de la autenticación.

## What Changes

- Eliminar la composición vertical centrada del logo/avatar grande.
- Mostrar el recurso de marca oficial existente junto al texto `FindYourPet` en una composición horizontal.
- Ubicar la firma en la zona superior izquierda, integrada directamente con el fondo continuo.
- Mantener al headline como elemento visual dominante y conservar supporting text, formulario y acciones de autenticación.
- Reutilizar tokens existentes para tamaño, separación y límites responsive; no crear una superficie, logo, color o estilo tipográfico nuevo.
- Validar posición, jerarquía, modo oscuro, tamaños de pantalla y funcionamiento del formulario.

## Capabilities

### New Capabilities

- `login-brand-signature`: Define la presentación compacta, horizontal y superior izquierda de la identidad de marca en el Login.

### Modified Capabilities

- Ninguna. La capability funcional `auth` no cambia; el alcance es exclusivamente la presentación visual de la identidad.

## Impact

- Código afectado: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt` y pruebas estáticas de presentación si requieren nuevas aserciones.
- Recursos considerados: reutilización de recursos de marca existentes; no se crea ni modifica un asset de logo.
- No se modifican Firebase, ViewModels, repositorios, navegación, textos funcionales, campos, botones ni asset de fondo.
- No hay impacto de privacidad, seguridad, datos o permisos.
- Rollback: restaurar el bloque visual centrado anterior sin migraciones ni cambios persistentes.
- Guardrails aplicables: `docs/design-system.md`, Material 3 estable, tokens de Design System, Light/Dark y ausencia de valores visuales hardcodeados.
