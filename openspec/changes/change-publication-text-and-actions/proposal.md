## Why

La pantalla de publicación todavía comunica la acción con un título y CTA genéricos (`Publicar mascota perdida` y `Publicar ficha`). SCRUM-50 busca que el flujo explique mejor que el usuario está creando un aviso y que la acción principal sea reconocible como publicación de ese aviso, sin modificar el comportamiento existente.

## What Changes

- Cambiar el título visible de la pantalla a `Crea un aviso para ayudar a encontrarla`.
- Mantener para ese texto el mismo peso visual que `Toca para agregar una foto`.
- Cambiar la etiqueta visible y la descripción de accesibilidad del CTA de `Publicar ficha` a `Publicar aviso`.
- Cambiar el ícono del CTA a un avión de papel usando un ícono Material 3 estable disponible.
- Conservar validación, estado de carga, callback de publicación, navegación, campos y persistencia actuales.
- Mantener soporte Light/Dark, tokens del Design System y sin valores visuales hardcodeados nuevos.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `pet-posts`: actualiza los requisitos de presentación y rotulado del flujo de creación de avisos, manteniendo intacto el contrato de publicación.

## Impact

- Código afectado: `CreatePetPostScreen.kt` y las pruebas Compose/estáticas que verifican sus textos e ícono.
- No se modifican APIs, modelos, ViewModels, repositorios, Firebase, navegación ni permisos.
- No hay impacto de privacidad, seguridad, datos o permisos.
- Usuarios existentes verán textos e ícono más claros; el flujo y sus resultados no cambian.
- Rollback: revertir los cambios de presentación y actualizar las aserciones de pruebas a los textos/ícono anteriores.
- Goals relacionados: mejorar la claridad del MVP y mantener la calidad visual verificable.
- Guardrails aplicables: Material 3 estable, tokens existentes, Light/Dark Theme, accesibilidad y no modificación de lógica de negocio.
