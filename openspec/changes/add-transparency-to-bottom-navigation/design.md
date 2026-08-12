## Context

`SignedInPetAppNavigation` already owns the persistent `BottomPrimaryActionBanner`, and `BottomPrimaryActionBanner` renders a `Surface` using `MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.banner)`. The same `AppOpacity.banner` token is also used by the authentication card, so changing that shared token would unintentionally alter an unrelated screen.

SCRUM-8 is a presentation-only adjustment: the bottom navigation must keep its current color, icon tint, shape, elevation, spacing, safe-area behavior, destinations, and route visibility while gaining a subtle transparent treatment during scroll. The project design system requires Material 3 stable APIs, theme tokens, and Light/Dark Theme support.

## Goals / Non-Goals

**Goals:**

- Give the bottom navigation surface its own semantic opacity token.
- Apply that token only to `BottomPrimaryActionBanner`.
- Preserve the existing `surfaceVariant` color, icon colors, shape, elevation, dimensions, padding, and navigation callbacks.
- Verify the presentation in both Light and Dark themes using focused existing test infrastructure where available.
- Keep the change compatible with the existing navigation shell and system gesture insets.

**Non-Goals:**

- Do not change navigation routes, route visibility, back-stack behavior, or action order.
- Do not change `AuthScreen` or the shared `AppOpacity.banner` behavior used there.
- Do not introduce a new dependency, custom drawing layer, blur effect, animation, or experimental API.
- Do not modify ViewModels, repositories, Firebase, Room, permissions, or business logic.
- Do not redesign the bar or introduce new colors, hardcoded dimensions, or arbitrary screen-level alpha values.

## Decisions

1. **Use a dedicated design-system opacity token.**
   - Add a semantic token such as `AppOpacity.bottomNavigation` alongside the existing opacity tokens.
   - Rationale: the requested transparency belongs to the bottom navigation only; reusing or changing `AppOpacity.banner` would also change the authentication card.
   - Alternative considered: lower `AppOpacity.banner` globally. Rejected because it changes unrelated UI and violates the narrow visual scope.

2. **Keep the existing Material 3 `Surface` and color scheme.**
   - Continue using `MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.bottomNavigation)`.
   - Rationale: this preserves the established color identity and lets the content behind the bar provide the continuity effect without introducing a new color or custom compositing implementation.
   - Alternative considered: add a custom gradient, blur, or scrim. Rejected because it would redesign the component and add unnecessary visual complexity.

3. **Keep component structure and navigation ownership unchanged.**
   - Modify only the banner's presentation token usage and add focused presentation assertions/tests if required.
   - Rationale: the current shell already centralizes the bar and handles safe-area padding; changing navigation would exceed SCRUM-8.
   - Alternative considered: move or recompose the bar in the navigation shell. Rejected because ownership was already established by the existing primary-navigation change.

4. **Validate theme compatibility without changing theme definitions.**
   - Exercise the existing component under Light and Dark themes and confirm that the surface remains visible, icons retain their current tint, and the surface is not fully opaque.
   - Rationale: opacity composition depends on the active Material color scheme, so both themes must remain legible.

## Risks / Trade-offs

- [Risk] The opacity may reduce contrast against some feed content. → Use the existing color-scheme surface and validate icon/content legibility in both themes; keep the value as a semantic token for adjustment.
- [Risk] A shared token change could regress the authentication screen. → Introduce a dedicated bottom-navigation token and assert the banner uses it without changing `AppOpacity.banner`.
- [Risk] A visual test may be brittle across renderers. → Prefer deterministic static/component assertions already used by the project and add screenshot coverage only if the existing harness is available and stable.
- [Risk] The bar could overlap gesture-navigation content if its structure changes. → Do not alter `navigationBarsPadding`, shell padding, height, or inset tokens.

## Migration Plan

1. Add the dedicated opacity token to `DesignTokens.kt` using the existing design-token pattern.
2. Update only `BottomPrimaryActionBanner` to consume the dedicated token.
3. Add or update focused tests for token usage and Light/Dark presentation without changing navigation behavior.
4. Run OpenSpec validation, unit tests, and the debug build.

Rollback is limited to restoring the banner's prior opacity token usage and removing the focused presentation assertions; no data or persisted-state migration is required.

## Open Questions

- The Scrum does not specify a numeric alpha. The implementer should select a subtle non-opaque value consistent with the existing design language and validate it against both themes; the exact value must remain centralized in the design token.
