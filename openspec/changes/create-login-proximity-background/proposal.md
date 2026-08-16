## Why

La pantalla de Login necesita comunicar visualmente que FindYourPet conecta avisos dentro de una zona de proximidad. Hoy no cuenta con un recurso gráfico dedicado para esa idea; SCRUM-32 agrega una representación abstracta local que mejora el contexto visual sin convertirla en un mapa real ni afectar la autenticación.

## What Changes

- Agregar un gráfico abstracto de proximidad compuesto por líneas, nodos, zonas circulares, un marcador principal y conexiones discretas.
- Integrar el gráfico en la pantalla de Login sin interferir con la legibilidad ni con los controles existentes.
- Generar todo localmente, sin Google Maps, Maps SDK, Places API, ubicaciones reales ni conexión a Internet.
- Mantener adaptación a distintas resoluciones, Light Theme y Dark Theme usando el Design System existente.
- Agregar validaciones estáticas o de presentación para comprobar la presencia del recurso y la conservación del contenido de autenticación.

## Capabilities

### New Capabilities

- `login-proximity-background`: representación gráfica local y abstracta de proximidad para la pantalla de autenticación.

### Modified Capabilities

- Ninguna. El cambio es exclusivamente visual y no modifica requisitos funcionales de autenticación.

## Impact

- Código afectado: `app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt` y, si corresponde, un componente visual reutilizable o tokens del Design System.
- No se agregan dependencias, permisos, APIs externas, datos persistidos ni cambios en ViewModels, repositories, Firebase o lógica de dominio.
- No afecta datos ni usuarios existentes; el rollback consiste en revertir la integración visual del gráfico.
- Validación: OpenSpec estricto, tests unitarios, build debug y revisión manual en Light/Dark Theme y distintas resoluciones.
- Guardrails: Material 3 estable, valores visuales tokenizados, sin APIs experimentales, sin mapas reales y sin interferencia con el contenido de Login.
