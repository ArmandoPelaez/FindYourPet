## Why

Las pantallas de creación de publicaciones de mascotas perdidas y de creación de alertas resuelven flujos relacionados, pero actualmente presentan jerarquías, superficies, acciones y espaciados diferentes. Esto aumenta la carga cognitiva y hace que una misma aplicación parezca tener dos lenguajes visuales; el SCRUM-4 prioriza alinear ambos flujos usando la pantalla de publicación existente como referencia.

## What Changes

- Alinear la jerarquía visual, estructura de secciones, superficies de carga de fotos, campos, acciones primarias y estados de feedback de ambas pantallas.
- Reutilizar componentes Compose y tokens del Design System cuando exista una equivalencia, evitando duplicación visual.
- Mantener las diferencias funcionales necesarias: la publicación conserva sus datos y validaciones; la alerta conserva ubicación, evidencia opcional y envío de alerta.
- Mantener el comportamiento responsive, edge-to-edge, Light Theme y Dark Theme existente.
- Agregar o ajustar pruebas de presentación para verificar consistencia visual y continuidad de ambos flujos.
- No cambiar modelos, repositorios, Firebase, ViewModels ni reglas de negocio salvo una necesidad técnica explícita para sostener la presentación.

## Capabilities

### New Capabilities

Ninguna. El cambio refina capacidades existentes.

### Modified Capabilities

- `pet-posts`: ajustar los requisitos de presentación del flujo de creación de publicaciones para compartir el lenguaje visual común sin alterar la creación, validación ni el mapeo de datos.
- `sightings`: ajustar los requisitos de presentación del flujo de creación de alertas para alinearlo con la pantalla de publicación, preservando ubicación, foto opcional, elegibilidad y envío existentes.

## Impact

- Código afectado: `CreatePetPostScreen.kt`, `SightingAlertScreen.kt`, componentes Compose compartidos y sus pruebas de UI/presentación.
- APIs, backend, Room, Firebase, permisos y contratos de datos: sin cambios previstos.
- Diseño: se aplican `MaterialTheme`, `AppColors`, `AppTypography`, `AppShapes`, `AppSpacing` y demás tokens existentes según `docs/design-system.md`; no se agregan colores, tamaños ni radios arbitrarios.
- Usuarios existentes: los flujos conservan sus capacidades y validaciones; solo cambia la presentación y la ubicación/forma de controles cuando sea necesario para la unificación.
- Privacidad y seguridad: no se amplía la recolección ni exposición de datos; se mantienen los permisos y el consentimiento de ubicación ya existentes.
- Rollback: revertir el commit del change restaura las pantallas y pruebas anteriores sin migración de datos ni cambios de backend.
