## Context

`BottomPrimaryActionBanner` renderiza cinco destinos con un helper compartido, pero `Reportar` toma una ruta especial: un `Surface` circular de `bottomNavigationWellSize`, un `FilledIconButton` elevado mediante `bottomNavigationActionLift` y un label anclado al borde inferior. `BottomNavigationTopDivider` también dibuja un arco asociado al well. Los otros destinos usan una columna con slot de icono, separación y label.

El issue exige conservar la barra de `60.dp`, la navegación, el espaciado horizontal, la huella de `22.dp` y el color semántico `primary`, pero quitar la percepción de FAB y alinear los cinco elementos. `docs/design-system.md` requiere Material 3 estable, tokens existentes o nuevos tokens del sistema, soporte Light/Dark y ausencia de valores visuales arbitrarios en los composables.

## Goals / Non-Goals

**Goals:**

- Hacer que los cinco destinos recorran la misma estructura vertical y compartan el eje del icono y la línea base del label.
- Mantener una superficie circular visible de `40.dp` detrás de `Reportar`, con icono de `22.dp`, `primary`/`onPrimary` y área interactiva mínima de `48.dp`.
- Eliminar la elevación estructural de `Reportar`: well de `60.dp`, offsets, arco y uso de `bottomNavigationActionLift`.
- Mantener destinos, callbacks, labels, altura de barra, espaciado horizontal, badges y comportamiento de navegación.
- Cubrir las decisiones estructurales con pruebas estáticas o de presentación deterministas.

**Non-Goals:**

- Cambiar la navegación, ViewModels, repositorios, persistencia, backend o contratos de datos.
- Rediseñar la superficie general, transparencia, forma o elevación global de la barra.
- Cambiar iconos, labels o destinos de `Inicio`, `Perfil`, `Actividad` o `Alertas`.
- Añadir dependencias o APIs Compose experimentales.

## Decisions

### 1. Unificar el árbol de composición, no compensar con offsets

`BottomNavigationItem` deberá usar la misma ruta de interacción para los cinco destinos y `BottomNavigationItemContent` deberá renderizar una columna común con un slot de icono compartido, separación y label. `Reportar` diferenciará únicamente el contenido del slot mediante un círculo de `40.dp`; no tendrá una rama que eleve el componente ni un label con alineación distinta.

Alternativa descartada: conservar el árbol especial y ajustar offsets. Eso mantiene la causa del desalineamiento y contradice el requisito de eliminar la composición tipo FAB.

### 2. Expresar las dimensiones mediante tokens del Design System

Se conservará `AppSpacing.bottomNavigationIcon` para la huella de `22.dp` y se agregarán o ajustarán tokens semánticos para el slot común, el círculo visual y el touch target de `Reportar`. Los composables no recibirán literales de tamaño. `bannerHeight` seguirá siendo el token de `60.dp`.

Alternativa descartada: insertar `40.dp` o `48.dp` directamente en `CommonComponents.kt`, porque rompe las reglas visuales del proyecto y dificulta Light/Dark o futuros ajustes de plataforma.

### 3. Mantener una sola superficie de barra y retirar solo el arco asociado al FAB

La superficie principal, su forma, color, elevación, padding de sistema y ancho máximo permanecen sin cambios. `BottomNavigationTopDivider` dejará de dibujar el arco central y se eliminará la dependencia del tamaño del well; si se conserva una separación superior, será una línea uniforme basada en tokens existentes y no una forma alrededor de `Reportar`.

Alternativa descartada: rediseñar toda la superficie o cambiar su elevación/transparencia, porque queda fuera de alcance y mezcla este issue con cambios anteriores de navegación.

### 4. Preservar accesibilidad con el contenedor interactivo

El contenedor clickable de `Reportar` conservará al menos el token de `48.dp` de touch target aunque el círculo visible sea de `40.dp`. Se evitarán clickables anidados; el callback `onCreatePostClick` seguirá ejecutándose una sola vez y los content descriptions permanecerán iguales.

### 5. Validar estructura y regresiones de navegación sin dispositivo obligatorio

Se ampliará la prueba estática existente de `PrimaryNavigationShellStaticTest` para verificar que el helper compartido no contenga elevación para `Reportar`, que existan los tokens/dimensiones semánticas y que callbacks, labels y destinos sigan presentes. Las validaciones de runtime Light/Dark y touch target se documentarán como manuales si no hay emulador disponible.

## Risks / Trade-offs

- [Cambio de alineación vertical] → Al ampliar el slot común para alojar el círculo de `40.dp`, los iconos no centrales pueden desplazarse dentro de la barra; se verificará que los cinco centros y labels compartan las mismas referencias y que la barra conserve `60.dp`.
- [Ancho reducido] → Cinco labels pueden competir en ventanas estrechas; se conservarán pesos y espaciado actuales y se revisará la presentación en los breakpoints existentes.
- [Touch target menor al esperado por una modificación futura] → La prueba comprobará el token mínimo de `48.dp` y se evitarán tamaños interactivos dependientes del círculo visible.
- [Regresión del borde superior] → Se eliminará únicamente el arco central y se mantendrá la superficie general; la prueba y revisión visual comprobarán que no aparezca un notch o cutout.

## Migration Plan

No hay migración de datos ni despliegue especial. Implementar en la rama del change, ejecutar OpenSpec, tests unitarios y `assembleDebug`, y revisar Light/Dark si hay emulador. El rollback consiste en revertir los commits de la rama; no requiere cambios en Room, Firestore ni configuración de release.

## Open Questions

- Confirmar durante implementación si el divisor superior debe conservarse como línea uniforme o eliminarse completamente; en ambos casos no debe existir un arco alrededor de `Reportar`.
- Confirmar manualmente en una ventana estrecha y en Light/Dark que el label `Reportar` no se recorte; no bloquea la planificación porque la estructura y tokens ya están definidos.
