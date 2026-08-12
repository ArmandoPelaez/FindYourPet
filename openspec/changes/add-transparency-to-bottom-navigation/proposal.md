## Why

The persistent bottom navigation surface currently reads as an opaque block over scrolling content. SCRUM-8 requests a subtle transparent treatment so the bar feels visually continuous with the scroll while preserving the existing FindYourPet navigation identity.

## What Changes

- Adjust the existing bottom primary navigation surface to use a subtle, design-system-backed transparency treatment.
- Preserve the current surface color, icon tint, shape, elevation, spacing, navigation destinations, and action order.
- Keep the bar's safe-area handling and route visibility unchanged.
- Add focused UI/static coverage for the transparent presentation in Light and Dark themes where the existing test harness supports it.
- Keep all navigation, ViewModel, repository, Firebase, data, and business behavior unchanged.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `primary-navigation`: The persistent bottom primary navigation surface must preserve its current visual identity while rendering with a subtle transparency that supports continuity during scroll.

## Impact

- Affected UI: `BottomPrimaryActionBanner` and its existing theme/design-token usage; implementation may also touch the signed-in navigation shell only if required to preserve the current surface behavior.
- Affected tests: focused presentation/static or screenshot coverage for the bottom navigation surface.
- No new dependencies, APIs, permissions, backend calls, or data changes.
- No privacy or security impact is expected.
- Rollback: restore the previous opaque surface treatment and remove only the focused presentation assertions.
- User impact: authenticated users see the same bottom navigation actions and interactions with a more continuous visual surface while scrolling.
