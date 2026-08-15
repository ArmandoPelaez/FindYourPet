## Why

La navegación principal todavía presenta `Mensajes` como si los avistamientos recibidos fueran conversaciones, aunque el flujo actual debe permitir consultar esos reportes de forma independiente del Chat. SCRUM-23 necesita una bandeja de `Actividad` que muestre al propietario los avistamientos relacionados con sus publicaciones y reduzca la dependencia del sistema legacy de mensajería.

## What Changes

- Reemplazar la etiqueta, icono y destino de `Mensajes` por `Actividad` en la Bottom Navigation autenticada.
- Crear o adaptar la pantalla de Actividad para listar los avistamientos recibidos del usuario.
- Obtener los datos desde `SightingAlertEntity` y la fuente remota/local de avistamientos, con acceso limitado al propietario de las publicaciones.
- Ordenar los elementos del más reciente al más antiguo y conservar `sightingId` en cada item.
- Mostrar nombre de mascota, indicador de avistamiento, ubicación, fecha/hora e imagen cuando estén disponibles y permitidos por el Design System.
- Implementar estados loading, success, empty y error sin crash, usando tokens y componentes existentes.
- Mantener el Chat legacy disponible en su código y rutas, pero no usarlo como fuente de datos ni mostrar previews, participantes, mensajes, `lastMessage`, respuestas o `chatId` en Actividad.
- Mantener sin cambios Inicio, Perfil, Reportar, Alertas, la creación de avistamientos y la navegación Alerta → Detalle de Avistamiento.

## Capabilities

### New Capabilities

- `activity`: bandeja informativa de avistamientos recibidos, independiente del Chat.

### Modified Capabilities

- `primary-navigation`: el cuarto destino autenticado pasa de `Mensajes` a `Actividad`, preservando el orden y el comportamiento de los demás destinos.

## Impact

- Android Compose: `MainActivity`, `BottomPrimaryActionBanner`, navegación de la sección, nueva pantalla de Actividad y pruebas de presentación/routing.
- Datos: consulta owner-scoped de `SightingAlertEntity` en repository/DAO/ViewModel cuando sea necesario; no se modifica el contrato de creación de avistamientos.
- Privacidad: la lista solo debe exponer avistamientos autorizados al propietario correspondiente y no debe presentar datos de Chat ni información de contacto personal.
- Diseño: cambio visual sujeto a `docs/design-system.md`, Material 3 estable, tokens existentes, Light Theme y Dark Theme.
- Usuarios existentes: verán `Actividad` en lugar de `Mensajes`; las rutas y entidades legacy de Chat se conservan para compatibilidad.
- Rollback: restaurar el destino/etiqueta de `Mensajes` y retirar la pantalla de Actividad sin modificar datos persistidos ni el flujo de avistamientos.

El cambio contribuye al objetivo de desacoplar avistamientos del Chat y respeta los guardrails de privacidad, sin agregar permisos ni dependencias externas.
