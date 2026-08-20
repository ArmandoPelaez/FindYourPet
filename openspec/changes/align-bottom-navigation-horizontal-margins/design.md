## Context

`BottomPrimaryActionBanner` ya se renderiza como una superficie flotante centrada y aplica `navigationBarsPadding()`. Su ancho se calcula dentro de `BoxWithConstraints` con márgenes responsive (`bottomNavigationSmallBreakpoint`, `bottomNavigationLargeBreakpoint`) y un ancho máximo tokenizado. SCRUM-46 requiere validar y ajustar esa relación para que los bordes de la barra coincidan con el contenedor principal en teléfonos de distintos anchos, sin cambiar los cinco destinos, el CTA `Reportar` ni la navegación.

Restricciones: Jetpack Compose + Material 3 estable, tokens de `DesignTokens.kt`, Light/Dark Theme, accesibilidad y ausencia de valores `dp` nuevos hardcodeados. El cambio no toca ViewModels, repositorios, Firebase, Room ni permisos.

## Goals / Non-Goals

**Goals:**

- Definir una única regla tokenizada para el margen horizontal compartido por el contenedor principal y la superficie de navegación inferior.
- Conservar el centrado, el ancho máximo responsive, la posición fija y el área segura del sistema.
- Mantener intactos destinos, labels, iconos, semantics, áreas táctiles y el offset vertical del botón `Reportar`.
- Agregar o actualizar pruebas que verifiquen la regla de márgenes y las invariantes de navegación.

**Non-Goals:**

- Rediseñar la navegación inferior o cambiar su forma, colores, elevación, transparencia o iconografía.
- Modificar la lógica de navegación, el contenido de las pantallas o el espaciado interno de los ítems.
- Agregar dependencias, permisos, cambios de datos o migraciones.

## Decisions

1. **Reutilizar el token de inset principal.** El margen de la superficie se derivará del token existente que representa el inset horizontal del contenido (`AppSpacing.contentInset` o el token equivalente que el código ya use), en lugar de introducir un `dp` específico para SCRUM-46. Si las variantes por tamaño son necesarias, se conservarán como tokens existentes y se centralizará la selección en una sola función.

   Alternativa descartada: mantener una fórmula independiente solo para la barra, porque puede dejar sus bordes desalineados cuando cambie el contenedor principal.

2. **Conservar `BoxWithConstraints` y el ancho máximo.** La navegación continuará calculando su ancho disponible a partir del ancho de la ventana, márgenes simétricos y `bottomNavigationMaxWidth`, manteniendo el centrado de la superficie en teléfonos y tablets.

   Alternativa descartada: usar una anchura fija o `fillMaxWidth()` sin margen, porque rompe la adaptación y el requisito visual de alineación.

3. **Validar la presentación sin alterar contratos funcionales.** Las pruebas estáticas/Compose existentes se extenderán para cubrir el token de margen, `navigationBarsPadding()`, las cinco etiquetas y el content description de `Reportar`. La validación manual cubrirá Light/Dark, teléfono compacto, teléfono mediano y una ventana ancha.

   Alternativa descartada: probar únicamente el screenshot de una dimensión, porque no demostraría el comportamiento responsive ni la conservación de insets.

## Risks / Trade-offs

- [Risk] Unificar el margen puede reducir ligeramente el ancho útil en una variante compacta. → Mitigación: conservar los breakpoints y `bottomNavigationMaxWidth`, y verificar que los targets táctiles sigan siendo accesibles.
- [Risk] Un cambio de padding externo podría afectar el espacio reservado del contenido inferior. → Mitigación: mantener `navigationBarsPadding()` y comprobar que el contenido y el estado vacío no queden cubiertos.
- [Risk] Un screenshot puede ocultar problemas de TalkBack o semántica. → Mitigación: conservar los semantics actuales y ejecutar pruebas Compose/estáticas de content descriptions y labels.

## Migration Plan

1. Ajustar la regla de margen en la capa de presentación y actualizar las pruebas del componente.
2. Ejecutar `openspec validate`, tests unitarios y `assembleDebug`.
3. Realizar revisión visual en Light/Dark y tamaños compacto/mediano/ancho cuando haya emulador disponible.
4. Rollback: revertir el commit del cambio; no hay migración de datos ni despliegue backend.

## Open Questions

- Confirmar durante la implementación cuál es el token de contenido principal que ya consume la pantalla host y si puede compartirse directamente con `BottomPrimaryActionBanner` sin introducir una dependencia visual entre pantallas.
