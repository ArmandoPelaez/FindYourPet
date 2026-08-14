## Context

`HomeScreen` usa un `Scaffold` con `WindowInsets.safeDrawing` y una `topBar` propia que contiene un `Surface`, el espacio de la Status Bar, un logo circular y los textos de identidad. El shell autenticado de `MainActivity` es propietario de la Bottom Navigation, por lo que la eliminación de la cabecera debe quedar aislada en Home.

El código actual de Home ya renderiza las publicaciones mediante `HorizontalPager` y conserva sus acciones dentro de `PetPostCard`. `CreatePetPostScreen` es una ruta distinta y no debe recibir cambios por la referencia de Jira a un componente de carga de foto.

## Goals / Non-Goals

**Goals:**

- Eliminar la `topBar` visual de `HomeScreen`.
- Mantener el contenido de publicaciones dentro del área segura y comenzar con un margen tokenizado equivalente a 16 dp.
- Mantener la Status Bar visible, con el fondo de Home y el contraste provistos por el tema.
- Conservar `Scaffold` y `WindowInsets.safeDrawing` para que el contenido respete los insets del sistema.
- Preservar la Bottom Navigation del shell, las acciones del feed, estados vacíos, scroll y soporte Light/Dark.

**Non-Goals:**

- No mover ni recrear acciones de notificaciones, perfil, reportes, chats o creación de publicaciones.
- No cambiar `MainActivity`, rutas, ViewModels, repositorios, persistencia, Firebase, permisos o modelos.
- No modificar el formulario de `CreatePetPostScreen` ni agregar una carga de foto a Home.
- No rediseñar colores, tipografía, shapes, elevaciones ni la Bottom Navigation.

## Decisions

1. **Eliminar la cabecera desde `HomeScreen`**

   Se quitará la implementación de `topBar` y sus elementos hijos del `Scaffold` de Home. Esto elimina el espacio vertical y la identidad duplicada en el punto donde se presenta el feed, sin cambiar el shell de navegación.

   Alternativa considerada: conservar una cabecera reducida solo con el logo. Se descarta porque SCRUM-19 solicita eliminar completamente la cabecera.

2. **Conservar `WindowInsets.safeDrawing` y añadir separación tokenizada al contenido**

   El `Scaffold` seguirá usando los insets seguros. El contenido aplicará el margen superior requerido después del safe area mediante `AppSpacing.md` o el token equivalente confirmado durante la implementación, nunca con un literal `16.dp`.

   Alternativa considerada: usar un `Spacer` manual con `WindowInsets.statusBars`. Se descarta porque duplicaría el manejo de insets y puede producir separación distinta entre dispositivos.

3. **Mantener la propiedad de la Bottom Navigation en el shell autenticado**

   No se cambiará el `Scaffold` de `MainActivity` ni `BottomPrimaryActionBanner`. El padding inferior seguirá llegando desde el `shellPadding` del shell para no cubrir cards, empty states ni acciones del feed.

   Alternativa considerada: mover la barra a `HomeScreen` para compensar la eliminación del header. Se descarta porque rompería la navegación persistente entre destinos y excedería SCRUM-19.

4. **Actualizar contratos de presentación y pruebas, no lógica de dominio**

   Se ajustarán las especificaciones delta y las pruebas que verifiquen la cabecera de Home. La validación cubrirá ausencia de textos/logo de cabecera, contenido visible debajo del safe area, preservación del feed y Bottom Navigation en Light/Dark cuando exista cobertura.

## Risks / Trade-offs

- [Riesgo] El contenido puede quedar demasiado cerca de la Status Bar en algún viewport → Mitigación: conservar `WindowInsets.safeDrawing`, usar el token de espaciado existente y verificar compact/tall phone en Light/Dark.
- [Riesgo] Tests o documentación existentes pueden exigir branding en la cabecera → Mitigación: actualizar solo las expectativas relacionadas con la cabecera, sin eliminar contratos del feed ni de navegación.
- [Riesgo] El cambio puede confundirse con la eliminación de la AppBar de publicación → Mitigación: limitar el diff a `HomeScreen` y pruebas de Home; no tocar `CreatePetPostScreen`.
- [Riesgo] El feed puede perder área útil por insets duplicados → Mitigación: inspeccionar el padding entregado por `Scaffold` y validar que la separación superior se aplique una sola vez.

## Migration Plan

No hay migración de datos ni cambios de backend. Implementar la eliminación en la rama del change, actualizar pruebas y validar OpenSpec, tests unitarios y `assembleDebug`. El rollback consiste en restaurar el `topBar` de `HomeScreen` y sus expectativas de presentación.

## Open Questions

- Confirmar durante la implementación cuál token existente representa exactamente el margen superior de 16 dp; `AppSpacing.md` es la opción documentada actualmente.
- Confirmar si alguna prueba de screenshot de Home está fuera del patrón detectado por la búsqueda inicial y requiere actualizar su referencia visual.
