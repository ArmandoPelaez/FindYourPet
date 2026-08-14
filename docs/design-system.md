# FindYourPet Design System

## Principios

El diseño visual existente de FindYourPet es la fuente de verdad.

No cambiar la identidad visual salvo indicación explícita.

## Tecnología

- Kotlin
- Jetpack Compose
- Material 3 estable
- Usá los skills adaptive, navigation-3 y edge-to-edge que estén disponibles.

## Tokens

Centralizar:

- AppColors
- AppTypography
- AppShapes
- AppSpacing
- AppElevation
- PetStatusColors

The authenticated bottom navigation uses five shared destinations in this order:
`Inicio`, `Perfil`, `Reportar`, `Mensajes`, `Alertas`. Active items use the theme
primary color, inactive items use `onSurfaceVariant`, and `Reportar` uses the
filled circular action treatment. Alert badges belong to the bottom navigation;
screens must not duplicate the alert action in their top bars.

The bottom navigation uses a responsive, centered floating surface. It keeps
tokenized horizontal margins on small, medium, and large phones, and uses a
centered maximum width on tablets. The surface uses the shared rounded button
shape rather than a full-width card. The `Reportar` action uses
`AppSpacing.bottomNavigationWellSize` and `AppSpacing.bottomNavigationActionLift`
(18 dp)
for its circular emphasis; secondary navigation content uses its native slot
position without an additional vertical offset. Secondary icons use
`AppSpacing.bottomNavigationIcon` so their proportions stay consistent across
themes and window sizes.

The compact navigation uses `AppSpacing.bannerHeight` (60 dp) and
`AppSpacing.bottomNavigationIconSlotHeight` for shared alignment. The create
action uses `AppSpacing.bottomNavigationCreateActionSize` (52 dp) and
`AppSpacing.bottomNavigationCreateIconSize`; its dark elevated well uses the
shared surface and `AppElevation.bottomNavigation` (6 dp).

For lost-pet cards, `AppSpacing.cardImageAspectRatio` is the shared aspect-ratio
token for the photo frame. The status label uses `PetStatusColors` and is placed
in the top-left photo overlay; screens must not introduce per-screen status colors.

## Colores

No utilizar `Color(...)` directamente en pantallas.

Usar:

- MaterialTheme.colorScheme
- AppColors
- PetStatusColors

## Tipografía

No utilizar tamaños `sp` arbitrarios en pantallas.

Usar:

- MaterialTheme.typography
- AppTypography
- AppFormTypography

### Tipografia de campos de formulario

Todas las pantallas que utilicen campos de entrada deben respetar estos tokens:

| Elemento | Tamano | Peso | Familia | Color |
|---|---:|---|---|---|
| Label | 14sp | Medium | Poppins | `MaterialTheme.colorScheme.onSurface` |
| Placeholder | 14sp | Normal | Poppins | `MaterialTheme.colorScheme.onSurfaceVariant` |
| Texto ingresado | 16sp | Normal | Poppins | `MaterialTheme.colorScheme.onSurface` |

En Compose se deben usar `AppFormTypography.label`, `AppFormTypography.placeholder` y
`AppFormTypography.input`, junto con `FormFieldLabel` y `FormFieldPlaceholder` cuando corresponda.
No se deben declarar estilos tipograficos independientes dentro de cada pantalla.

## Espaciado

No utilizar valores `dp` repetidos directamente.

Usar AppSpacing:

- xs
- sm
- md
- lg
- xl
- cardImageAspectRatio

## Componentes

Priorizar componentes reutilizables:

- AppButton
- PetCard
- StatusChip
- EmptyState

## Tema

Todas las pantallas deben funcionar correctamente en:

- Light Theme
- Dark Theme

## Refactorización

Al encontrar valores hardcodeados:

1. Verificar si ya existe un token equivalente.
2. Reutilizarlo si existe.
3. Si no existe, crear un token coherente con el diseño actual.
4. No modificar visualmente la pantalla innecesariamente.

## Restricciones

No usar:

- APIs alpha
- APIs beta
- APIs experimentales
- Librerías visuales innecesarias

No modificar:

- ViewModels
- repositories
- Firebase
- lógica de dominio

salvo necesidad técnica explícita.
