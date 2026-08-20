## Why

El elemento central `Reportar` todavía se percibe como un FAB elevado por su well circular, offsets y arco superior, mientras los otros cuatro destinos forman una fila uniforme. Este cambio alinea visualmente la acción principal con la navegación existente, conservando su énfasis mediante el color `primary` y evitando alterar su destino o comportamiento.

## What Changes

- Reestructurar `Reportar` para compartir con `Inicio`, `Perfil`, `Actividad` y `Alertas` la misma altura, slot de icono, eje vertical y línea base del label.
- Eliminar el well de `60.dp`, los offsets de elevación, el arco superior y la lógica específica de composición tipo FAB.
- Mantener un círculo visual `primary` de `40.dp` con la huella de `22.dp` y un área táctil mínima de `48.dp`.
- Mantener la barra de `60.dp`, el espaciado horizontal, los destinos de navegación, los labels existentes y la compatibilidad Light/Dark.
- Añadir cobertura estática y/o de presentación para comprobar la estructura, dimensiones y ausencia de elevación de `Reportar`.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `primary-navigation`: cambia el requisito visual de la navegación inferior para que `Reportar` comparta la estructura vertical de los cuatro destinos restantes, manteniendo su destino y área táctil accesible.

## Impact

- Código afectado: `CommonComponents.kt`, tokens de navegación inferior en `DesignTokens.kt` y pruebas de presentación de navegación primaria.
- No se modifican APIs externas, persistencia, autenticación, permisos, backend ni lógica de negocio.
- Es un cambio visual; respeta Material 3 estable, tokens existentes, Light/Dark y las restricciones de `docs/design-system.md`.
- Rollback: revertir la rama o el commit del change restaura la composición anterior sin migraciones ni cambios de datos.
