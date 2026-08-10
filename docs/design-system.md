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

## Espaciado

No utilizar valores `dp` repetidos directamente.

Usar AppSpacing:

- xs
- sm
- md
- lg
- xl

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