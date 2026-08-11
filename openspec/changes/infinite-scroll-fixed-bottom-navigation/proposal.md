## Why

Las pantallas de la aplicación deben conservar una navegación inferior estable mientras el contenido se desplaza de forma continua. Hoy el cambio solicitado en SCRUM-6 requiere que el contenido pueda pasar visualmente por detrás de esa barra, manteniendo sus límites de desplazamiento y la identidad visual existente.

## What Changes

- Ajustar las pantallas con contenido desplazable para que permitan desplazamiento continuo hacia arriba y hacia abajo hasta el inicio y el final del contenido.
- Mantener la barra de navegación inferior fija dentro del shell de navegación.
- Permitir que el contenido pase visualmente por detrás de la barra y que esta use la transparencia parcial ya definida por el sistema visual.
- Revisar insets y espaciado para que el comportamiento no oculte contenido accionable ni interfiera con el área de gestos del sistema.
- Conservar colores, tipografía, formas, jerarquías, navegación, datos y lógica de negocio existentes.

## Capabilities

### New Capabilities

- Ninguna.

### Modified Capabilities

- `primary-navigation`: ajusta la presentación de la barra inferior fija y su transparencia parcial sobre el contenido desplazable.
- `home-feed-presentation`: ajusta el comportamiento de desplazamiento y la visibilidad del contenido del feed bajo la barra inferior.

## Impact

- Código potencialmente afectado: shell de navegación firmado, `HomeScreen`, componentes de contenido desplazable y pruebas visuales/estáticas relacionadas.
- No se esperan cambios de APIs, dependencias, backend, almacenamiento, permisos, autenticación ni datos.
- El cambio es exclusivamente visual y debe usar Jetpack Compose, Material 3 estable y tokens existentes del Design System en Light y Dark Theme.
- La estrategia de rollback consiste en restaurar la configuración previa de insets, scroll y presentación de la barra inferior sin modificar las fuentes de datos ni la navegación funcional.
- Guardrails aplicables: no cambiar lógica de dominio, no introducir valores visuales hardcodeados, no usar APIs alpha/beta/experimentales y no alterar datos sensibles.
