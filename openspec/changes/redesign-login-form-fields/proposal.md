## Why

Los campos Email y Contraseña actuales usan una presentación funcional básica y no exponen de forma clara todos los estados ni acciones esperados para un Login moderno. SCRUM-35 busca hacerlos más livianos e integrados con el diseño, manteniendo intactos la validación existente, el contrato con Authentication/ViewModel y las acciones de acceso.

## What Changes

- Rediseñar visualmente los inputs Email y Contraseña dentro de `AuthScreen`.
- Mantener iconos y agregar placeholders claros.
- Mantener estados focused y error visualmente distinguibles, con el error asociado al campo correspondiente.
- Incorporar mostrar/ocultar contraseña sin modificar el valor enviado a autenticación.
- Configurar acciones de teclado adecuadas: avanzar desde Email y completar desde Contraseña.
- Conservar la posibilidad de accionar Entrar desde el teclado.
- Mantener los campos visualmente secundarios frente al CTA principal.
- Preservar validaciones existentes, callbacks, Firebase Auth y ViewModel.
- Usar únicamente tokens del Design System y soportar Light Theme y Dark Theme.

## Capabilities

### New Capabilities

- `login-form-fields`: presentación, estados y comportamiento de entrada de los campos Email y Contraseña.

### Modified Capabilities

<!-- No se modifica el contrato funcional de auth; los cambios son de presentación e interacción local del formulario. -->

## Impact

- Código afectado: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt` y pruebas focalizadas de presentación/entrada.
- No se modifican Authentication, `PetViewModel`, repositorios, Firebase, persistencia, permisos ni datos.
- No se agregan dependencias ni APIs experimentales.
- El cambio no altera datos existentes; solo mejora la entrada en Login/Registro.
- Rollback: restaurar la composición actual de los `OutlinedTextField` y sus estados locales.
- Guardrails: `AppFormTypography`, `FormFieldLabel`, `FormFieldPlaceholder`, `AppShapes`, `AppSpacing`, `MaterialTheme.colorScheme`, Material 3 estable y soporte Light/Dark.
- Jira: la búsqueda por “Work Item 4” encontró `SCRUM-35`; `SCRUM-34` no existe o no está visible. El issue encontrado declara prioridad Alta en la descripción y devuelve prioridad estructurada Medium.
- Decisión de alcance actualizada en Jira: autofill queda diferido a un change separado de actualización de Compose/API.
