## Context

`CreatePetPostScreen` se muestra como destino principal de la navegación autenticada y actualmente declara un `Scaffold` propio con `WindowInsets.safeDrawing` y una `TopAppBar` que contiene el título y una flecha que ejecuta `onBackClick`. El shell de `MainActivity` ya aporta la Bottom Navigation fija mediante un `Scaffold` externo, por lo que la cabecera superior no es necesaria para acceder al destino.

El primer contenido actual es `FormPhotoUploadSurface`, seguido por las secciones del formulario. La pantalla ya utiliza `AppSpacing`, `AppTypography`/`AppFormTypography`, `AppShapes` y colores de `MaterialTheme`; el cambio debe conservar ese lenguaje visual y no tocar el estado del formulario, la carga de medios, la ubicación, la validación ni la publicación.

## Goals / Non-Goals

**Goals:**

- Quitar la AppBar y la flecha únicamente de la pantalla de creación de publicación.
- Mantener la Status Bar visible con la superficie de la pantalla y respetar el safe area.
- Introducir una cabecera de contenido con el título antes de la carga de foto.
- Usar la separación `AppSpacing.md` después del inset superior y un estilo tipográfico de título existente equivalente al rango solicitado.
- Conservar el shell externo y la Bottom Navigation sin cambiar sus destinos, selección, posición o comportamiento.
- Mantener la presentación correcta en Light Theme, Dark Theme, tamaños de teléfono soportados y con IME visible.

**Non-Goals:**

- No cambiar ViewModels, repositories, Firebase, modelos, permisos, validaciones ni contratos de publicación.
- No rediseñar el componente de carga de foto ni el resto del formulario.
- No modificar otras pantallas que usan `TopAppBar`.
- No agregar dependencias, APIs experimentales, navegación alternativa ni nuevos valores visuales hardcodeados.

## Decisions

### 1. Integrar el título en el contenido de `CreatePetPostScreen`

Se eliminará el bloque `topBar` del `Scaffold` de la pantalla y se conservará el `Scaffold` para administrar los insets de contenido. Dentro de la `Column` desplazable, el título se ubicará antes de `FormPhotoUploadSurface`.

**Alternativa considerada:** conservar una AppBar sin navegación. Se descarta porque seguiría reservando altura superior y mantendría la jerarquía que SCRUM-16 busca eliminar.

### 2. Reutilizar tokens para insets, espaciado y tipografía

Se mantendrá `WindowInsets.safeDrawing` para que la Status Bar siga protegida y se reutilizará `AppSpacing.md` para la separación superior y el ritmo horizontal existente. El título usará un estilo de `MaterialTheme.typography`/`AppTypography` que ya esté dentro del rango visual solicitado, evitando `fontSize`, `fontWeight` o colores declarados dentro de la pantalla.

**Alternativa considerada:** introducir `22.sp`/`FontWeight.SemiBold` directamente en la pantalla. Se descarta porque contradice las Design Rules y duplica tokens tipográficos.

### 3. Retirar el callback de navegación superior sin alterar el back del sistema

Como `onBackClick` solo se utiliza para la flecha eliminada, se quitará de la firma de `CreatePetPostScreen`, de su llamada en `MainActivity` y de los harnesses de presentación que construyan la pantalla. El destino seguirá participando en `NavController`, por lo que el back del sistema y la navegación inferior conservan sus responsabilidades existentes.

**Alternativa considerada:** mantener un parámetro sin uso para compatibilidad. Se descarta porque dejaría una API de UI engañosa y una referencia de navegación que ya no existe visualmente.

### 4. Validar la interacción con el shell externo

La pantalla conservará el `Scaffold` externo de `MainActivity`, su `shellPadding` y `BottomPrimaryActionBanner`. La validación comprobará que el último contenido siga siendo alcanzable, que la Status Bar no cubra el título y que el teclado no tape campos o acciones.

## Risks / Trade-offs

- [Riesgo] Quitar la AppBar puede dejar el contenido demasiado próximo a la Status Bar. → Mantener `WindowInsets.safeDrawing` y `AppSpacing.md` antes del título; validar capturas en Light/Dark.
- [Riesgo] Eliminar `onBackClick` puede romper un harness o llamada residual. → Actualizar todas las referencias encontradas y ejecutar los tests de presentación y compilación.
- [Riesgo] El contenido puede quedar cubierto por la Bottom Navigation si se cambia el contrato de insets. → No modificar el shell externo y verificar el final del scroll en viewport compacto y alto.
- [Riesgo] El peso tipográfico disponible puede no ser un Semibold exacto. → Usar el token de título existente más cercano dentro del Design System y documentar la decisión sin introducir una fuente o peso arbitrario.

## Migration Plan

1. Aplicar el cambio únicamente en la rama `ops/optimize-publish-lost-pet-header`.
2. Actualizar `CreatePetPostScreen`, el punto de composición en `MainActivity` y las pruebas/harnesses afectados.
3. Ejecutar validación OpenSpec, tests unitarios y `assembleDebug`.
4. Ejecutar validación visual de la pantalla en Light/Dark y tamaños soportados si el arnés disponible lo permite.
5. Rollback: revertir el commit de la rama; no hay migración de datos ni cambios de backend.

## Open Questions

- Ninguna para iniciar la implementación. Si el token tipográfico existente no cubre el rango solicitado sin una nueva decisión de Design System, el implementador debe detenerse y reportarlo antes de crear un token.
