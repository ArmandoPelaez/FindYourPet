## Why

SCRUM-23 dejó la lista de Actividad desacoplada de Chat, pero sus elementos todavía no ofrecen acceso al avistamiento recibido. SCRUM-24 completa ese flujo permitiendo abrir el detalle correcto mediante el `sightingId` que ya conserva cada item.

## What Changes

- Hacer seleccionable cada elemento de Actividad.
- Validar que el `sightingId` del elemento no sea nulo, vacío o inválido antes de navegar.
- Reutilizar la ruta y la pantalla existentes de Detalle de Avistamiento.
- Mantener el flujo `Actividad → sightingId → Detalle de Avistamiento → Back → Actividad` sin depender de Chat.
- Conservar la lista, sus datos, el estado de scroll y los destinos de Inicio, Perfil, Reportar y Alertas.
- Manejar identificadores inválidos sin crash, sin abrir Chat y con diagnóstico útil según los patrones existentes.
- Mantener pressed state, touch target, accesibilidad y legibilidad Light/Dark mediante los tokens del Design System.

## Capabilities

### New Capabilities

- `activity-sighting-navigation`: navegación segura desde un item de Actividad al detalle del avistamiento correspondiente.

### Modified Capabilities

No se modifican los requisitos existentes de `sightings` ni `notifications`; se reutiliza el contrato vigente de detalle y de navegación por `sightingId`.

## Impact

- Android Compose: `ActivityScreen` y la integración de navegación en `MainActivity`.
- Pruebas: contratos estáticos/Compose de selección, ruta, back stack, identificadores inválidos y ausencia de dependencias de Chat.
- Datos y backend: sin cambios en Room, Firestore, reglas, creación de avistamientos o entidades.
- Privacidad: la navegación usa únicamente el identificador autorizado del avistamiento y no agrega datos de contacto ni contenido de Chat.
- Dependencias y permisos: sin cambios.
- Usuarios existentes: los items de Actividad pasan a ser interactivos; los demás destinos y el Chat legacy conservan su comportamiento.
- Rollback: retirar el callback y las aserciones de navegación, dejando la lista de Actividad como lectura informativa sin modificar datos persistidos.

Este change sigue los guardrails de `docs/design-system.md`: Material 3 estable, tokens existentes, soporte Light/Dark y ninguna API experimental.
