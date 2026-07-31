## Context

`HomeScreen` already renders a top app bar, a feed state banner, a horizontal pager of pet posts, and `PetPostCard` using `AsyncImage` from each post's `photoUri`. The reference image suggests a stronger alert-card hierarchy: large image, status pill, pet identity, compact attributes, reported information, location/date, and large action controls.

This change is intentionally presentation-focused. It should preserve the existing post data source, navigation callbacks, ownership policy, and image-loading path. The mockup dog image is a visual reference only and must not become an app asset, fallback, or replacement for `photoUri`.

## Goals / Non-Goals

**Goals:**
- Redesign the home feed card so each post reads like a complete lost-pet alert.
- Keep the pet image visually dominant while preserving the current `photoUri` loading behavior.
- Make status, name, breed, color, species, characteristics, reported information, location, date, and actions easier to scan.
- Keep "Lo he visto" as the primary in-card action when reporting is allowed.
- Add or expose a secondary "Compartir" control only with privacy-safe share content.
- Keep header branding and notifications clear.
- Ensure the card content and controls are not covered by the bottom action surface from `move-primary-actions-to-bottom-bar` or by gesture navigation.

**Non-Goals:**
- Do not change backend schema, Room schema, remote mappers, repositories, or sync behavior.
- Do not add age or gender fields unless a separate data-model change introduces them.
- Do not use, bundle, edit, or replace app images with the provided dog image.
- Do not change profile, chat, notifications, create-post, or sighting alert destinations.
- Do not expose owner phone, owner email, exact coordinates, private messages, or hidden contact data in the card or share payload.

## Decisions

1. Keep the redesigned card inside `HomeScreen` or a small Compose component extracted from it.
   - Rationale: the behavior is local to the home feed and should remain easy to test.
   - Alternative considered: create a new feature module. Rejected because this is a UI-only refinement with no new domain boundary.

2. Preserve `AsyncImage` and the existing `ImageRequest` data source.
   - Rationale: the user's explicit constraint is that the image behavior must not change.
   - Alternative considered: add the reference image as a placeholder. Rejected because it would blur the distinction between visual inspiration and app data.

3. Use a top image area with an overlaid or adjacent status pill.
   - Rationale: status must be visible immediately, but the photo remains the main recognition element.
   - Alternative considered: place status only below the image. Rejected because urgent state is less discoverable while swiping cards.

4. Render attributes from existing post fields only.
   - Rationale: the current `PetPostEntity` has species, breed, color, features, status, date, and location, but no age or gender. The UI must not hardcode fake values from the mockup.
   - Alternative considered: add age/gender to the data model in this change. Rejected because it expands scope into persistence, forms, backend mapping, and validation.

5. Treat share as a privacy-sensitive UI action.
   - Rationale: sharing can leak data if it includes owner contact or exact coordinates. The share summary should be limited to public-facing post details such as pet name, species/breed/color, broad location text already visible on the card, and app context.
   - Alternative considered: include all post fields in the share text. Rejected because owner and coordinate fields are sensitive.

6. Keep layout stable on compact phones.
   - Rationale: the card lives in a pager and may coexist with a bottom action banner. Fixed visual regions, bounded text, and bottom padding reduce overlap and accidental taps.
   - Alternative considered: make the whole card unconstrained and rely on natural scroll height. Rejected because controls can become hard to reach or hidden behind bottom overlays.

## Risks / Trade-offs

- Richer cards can become visually dense -> Use compact sections, clear hierarchy, and only show available fields.
- Large photos can reduce space for details on small screens -> Keep the card vertically scrollable or use predictable height constraints with enough bottom padding.
- Share payload could expose sensitive data -> Build share text from an allowlist of safe fields and add tests/static checks where feasible.
- Active bottom-bar work may still be in flight -> Keep this change compatible by adding bottom-safe padding without owning the bottom navigation contract.
- Optional fields from the mockup may be unavailable -> Omit unavailable attributes instead of adding placeholder values.

## Migration Plan

1. Refactor `PetPostCard` into clearer sections: image/status, identity, attributes, reported info, location/date, and actions.
2. Add the secondary share control using a privacy-safe share summary if product scope accepts the control.
3. Adjust `HomeScreen` spacing so the redesigned card remains usable with the bottom action banner.
4. Add/update tests for visible hierarchy, action availability, privacy-safe sharing, and absence of the reference image asset.
5. Run the relevant unit/UI tests and build the debug app before closing implementation.

Rollback is limited to restoring the previous `PetPostCard` layout and removing the share control, with no data or backend migration.

## Open Questions

- Should the share action be implemented in this change, or only reserve the UI placement until a separate sharing change?
