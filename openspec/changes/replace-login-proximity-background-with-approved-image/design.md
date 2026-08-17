## Context

The integrated SCRUM-32 implementation renders `LoginProximityBackground` with a local Canvas. SCRUM-38 supplies an approved vertical bitmap that must become the Login's decorative background. The existing Login content, authentication state, navigation, and design tokens are already established and must remain unchanged.

Constraints:

- Use the tracked `app/src/main/res/drawable-nodpi/imagen_fondo_pantalla_login.png` because the requested `.webp` is absent and Jira explicitly permits the PNG temporarily.
- Use stable Jetpack Compose/Material 3 APIs and existing Design System tokens only.
- Do not create another bitmap, infer colors or dimensions from the image, or recreate it with Canvas.
- Keep the image decorative, non-focusable, non-clickable, and absent from TalkBack semantics.
- Preserve Login behavior in Light/Dark themes, with keyboard open, across screen sizes, and for all existing authentication actions.

## Goals / Non-Goals

**Goals:**

- Place the approved bitmap behind the full Login content.
- Preserve the asset's upper composition while adapting it without distortion to available bounds.
- Maintain sufficient content legibility using existing theme-aware surfaces/opacity tokens if an overlay is needed.
- Remove the Login dependency on the old Canvas proximity component and update focused presentation tests.

**Non-Goals:**

- Change authentication, Firebase, ViewModel, repository, navigation, permissions, or domain behavior.
- Add Maps SDK, Places API, network calls, geolocation, dynamic Canvas drawing, background animation, or a new visual identity.
- Redesign Login fields, actions, header, typography, spacing, or shapes.

## Decisions

1. **Use the approved local resource with `painterResource`.**
   - Reference the existing PNG from `drawable-nodpi`; do not generate or convert an alternate asset in this change.
   - Rationale: this is the exact repository asset Jira identifies as the permitted temporary fallback.
   - Alternative rejected: keeping the Canvas or reconstructing the image, because SCRUM-38 explicitly requires the approved image and forbids Canvas recreation.

2. **Use explicit background/content layering in the existing Login root.**
   - Keep the root `Box` as the stacking container, render the bitmap as a `matchParentSize` child first, then an optional theme-aware tokenized scrim, then the existing scrollable Login content.
   - Rationale: the functional content remains above the asset and the image receives no interaction handlers or focus modifiers.
   - Alternative rejected: placing the image in the scrollable column, which would make the decorative layer move with form content and complicate keyboard behavior.

3. **Preserve the image without distortion using top-centered crop behavior.**
   - Use a stable Compose content-scale/alignment strategy that fills the viewport, preserves aspect ratio, and prioritizes the asset's upper visual composition.
   - Rationale: the source is a tall background and the acceptance criteria require responsive behavior without deformation or unintended empty space.
   - Alternative rejected: `FillBounds`, because it can stretch the map/proximity geometry.

4. **Protect legibility with existing theme tokens only.**
   - Retain the existing theme-aware root treatment and, if visual verification requires additional separation, use an existing surface/opacity token such as the established media overlay treatment; do not introduce a new alpha or color literal.
   - Rationale: the same asset must remain readable in both themes and the Design System is the source of truth.
   - Alternative rejected: sampling or inventing colors from the bitmap.

5. **Retire only the obsolete visual implementation.**
   - Remove the `LoginProximityBackground` call and delete its component only if repository search confirms it has no other consumers; retain unrelated shared Canvas code.
   - Rationale: the change should remove the old Login background without broad cleanup outside SCRUM-38.
   - Alternative rejected: deleting all Canvas utilities, which would exceed scope.

## Risks / Trade-offs

- [The dark bitmap reduces Light Theme contrast] → Preserve theme-aware content surfaces and verify Light/Dark manually; adjust only through existing Design System tokens.
- [Crop removes important image content on large/aspect-ratio-diverse screens] → Use top-centered aspect-ratio-preserving scaling and verify small phone, tall phone, and larger window layouts.
- [Image enters accessibility or interaction flow] → Use decorative semantics (`contentDescription = null`), no focusability, no click/pointer handlers, and test the functional controls above it.
- [Old Canvas component remains accidentally in use] → Search all references and add a focused static test rejecting the old Login background call.
- [PNG and requested WEBP diverge] → Keep the tracked PNG as the documented temporary asset; do not create a replacement without a new approved asset.

## Migration Plan

1. Replace the Login background layer and remove the obsolete Login Canvas reference/component when unused.
2. Extend presentation tests for resource reference, layering, decorative semantics, no Canvas usage, and preserved authentication controls.
3. Run strict OpenSpec validation, unit tests, debug assembly, diff checks, and manual visual/accessibility checks.
4. Roll back by restoring the previous `LoginProximityBackground` reference; no data or backend migration is needed.

## Open Questions

- Jira names the desired resource as `.webp` but explicitly permits the tracked `.png` temporarily. This change uses the PNG until an approved WEBP is supplied.
- The exact scrim token/strength must be selected from existing Design System tokens during implementation and validated in both themes; no new token is approved by this Scrum.
