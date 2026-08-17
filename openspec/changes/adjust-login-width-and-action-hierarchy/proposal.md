## Why

El Login todavía puede percibirse demasiado ancho y sus acciones compiten visualmente entre sí. SCRUM-42 busca una columna principal más contenida y una jerarquía inmediata entre `Entrar`, Google y `Crear una cuenta`, manteniendo intacta la autenticación.

## What Changes

- Unificar headline, supporting text, título, campos y acciones bajo una referencia horizontal común.
- Ajustar la distribución vertical tomando de la pantalla de referencia únicamente su intención de ritmo: hero más arriba y compacto, menor separación entre hero, título, campos y acciones, controles agrupados al centro y menor espacio inferior.
- Aplicar márgenes laterales y límites de ancho usando exclusivamente tokens existentes del Design System.
- Mantener `Entrar` como CTA primario con el estilo primario existente.
- Mantener `Continuar con Google` como acción secundaria, con branding oficial y menor protagonismo visual.
- Presentar `Crear una cuenta` como acción terciaria, sin competir con `Entrar`.
- Conservar responsive, accesibilidad, foco, teclado, Light/Dark Theme y todos los callbacks actuales.
- Preservar `verticalScroll` e `imePadding()` para pantallas pequeñas y teclado abierto.
- Excluir explícitamente elementos de la referencia que no existen en el alcance: Recordarme, recuperación de contraseña y textos informativos adicionales.
- No modificar lógica de autenticación, textos del hero, fondo, navegación, backend ni dependencias.

## Capabilities

### New Capabilities

- `login-action-hierarchy`: Define la columna visual contenida y los niveles primario, secundario y terciario de las acciones de autenticación.

### Modified Capabilities

- Ninguna. El contrato funcional de autenticación no cambia; la nueva capacidad documenta requisitos de presentación.

## Impact

- Código afectado: composición de `AuthScreen.kt` y pruebas estáticas o visuales de presentación.
- Componentes reutilizados: `AppButton`, `AppButtonVariant`, `AppSpacing`, `MaterialTheme` y asset oficial de Google.
- No se modifican APIs, ViewModels, repositorios, Firebase, navegación, datos, permisos o backend.
- Usuarios existentes conservan los mismos métodos, textos funcionales y navegación; cambia únicamente la contención visual y la prominencia relativa de las acciones.
- No hay impacto de privacidad o seguridad.
- Rollback: restaurar la composición y pruebas previas de `AuthScreen.kt`; no requiere migraciones ni cambios remotos.
- Guardrails: Material 3 estable, Design System como fuente de verdad, Light/Dark Theme, sin valores visuales hardcodeados ni APIs experimentales.
