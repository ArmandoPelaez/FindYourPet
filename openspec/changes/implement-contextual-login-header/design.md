## Context

`AuthScreen` ya contiene un `Surface` para el encabezado, un icono, un título de autenticación y una descripción, además de `LoginProximityBackground`. La pantalla usa Compose Material 3, `AppTypography`, `AppSpacing`, `AppShapes`, `AppElevation` y colores del tema. El cambio de `SCRUM-33` es exclusivamente visual: debe introducir una jerarquía contextual superior sin alterar los flujos de autenticación.

## Goals / Non-Goals

**Goals:**

- Presentar el mensaje contextual de FindYourPet como headline dominante dentro del encabezado.
- Mostrar un supporting text secundario y la identidad `FindYourPet` con menor énfasis.
- Mantener legibilidad sobre el background actual en Light Theme y Dark Theme.
- Reutilizar tokens y componentes existentes para responder correctamente en pantallas pequeñas.

**Non-Goals:**

- No cambiar Firebase Auth, ViewModel, navegación, validaciones ni mensajes de error.
- No agregar dependencias, permisos, recursos de red, persistencia ni componentes visuales nuevos.
- No modificar el background de proximidad ni introducir colores, tamaños, paddings o radios nuevos.

## Decisions

1. **Modificar la composición existente del encabezado en `AuthScreen`.**
   - La propuesta conserva el `Surface`, la forma, elevación, espaciado e iconografía ya usados por Login.
   - Alternativa descartada: crear una pantalla o componente de autenticación nuevo, porque ampliaría el alcance y duplicaría la estructura existente.

2. **Usar una jerarquía de texto basada únicamente en `AppTypography`.**
   - El headline contextual usará un estilo headline existente; el supporting text usará un estilo body existente; la marca usará un estilo de menor énfasis existente.
   - La elección concreta debe respetar la escala ya definida en `AppTypography` y mantener el headline claramente dominante.
   - Alternativa descartada: declarar `TextStyle` o tamaños `sp` dentro de `AuthScreen`, prohibido por el Design System y el criterio de aceptación.

3. **Mantener el copy funcional existente debajo del contexto.**
   - El contexto superior podrá usar el ejemplo de Jira: `Conectá con avisos cerca tuyo.` y `Reportá, buscá y ayudá a reencontrar mascotas.`.
   - La identidad `FindYourPet` se renderizará como texto discreto, sin competir con el headline.
   - Los textos que explican `Iniciar sesión` / `Crear cuenta` y las acciones de autenticación permanecen disponibles para conservar la comprensión del formulario.
   - Alternativa descartada: reemplazar toda la información del formulario por el mensaje contextual, porque reduciría la claridad de la acción de acceso y registro.

4. **Conservar el layout adaptable existente.**
   - El encabezado seguirá dentro de la columna desplazable, con ancho máximo y alineación centrada existentes; el wrapping natural permitirá que el copy responda a pantallas pequeñas.
   - No se añadirán offsets ni dimensiones específicas por dispositivo.
   - La legibilidad se resolverá con `MaterialTheme.colorScheme` y los estilos de tema existentes, sin colores directos.

## Risks / Trade-offs

- [El copy contextual puede aumentar la altura del encabezado] → Mantener la columna desplazable, el ancho máximo y los espaciados existentes; validar una pantalla pequeña.
- [La marca puede competir visualmente con el headline] → Aplicar un estilo tipográfico de menor jerarquía y conservar el orden headline, supporting text, identidad.
- [El background decorativo puede reducir el contraste] → Usar colores del esquema Material y verificar Light/Dark; no introducir overlays ni colores arbitrarios.
- [La prioridad de Jira es inconsistente] → La descripción indica `Alta` y el campo estructurado indica `Medium`; no afecta el diseño ni se modifica Jira desde este change.

## Migration Plan

No hay migración de datos ni compatibilidad de API. La implementación se limita a la composición de `AuthScreen`. El rollback consiste en restaurar la composición anterior del `Surface` del encabezado. La validación incluye inspección estática de tokens, tests unitarios disponibles, build debug y revisión manual o equivalente de Light/Dark y pantallas pequeñas.

## Open Questions

- Confirmar durante la implementación si el headline contextual debe permanecer idéntico entre Login y Registro o si el modo de registro requiere una variante; Jira no define una variante específica.
