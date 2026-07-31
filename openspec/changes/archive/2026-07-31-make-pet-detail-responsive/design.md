## Context

The current Android app is a Jetpack Compose prototype with edge-to-edge enabled and several screen-specific composables. The responsive request targets the pet detail experience: a primary media header, status badges, information cards, and primary actions must adapt across compact phones, landscape, tablets, and accessibility text scaling.

This change is UI-only. It must preserve the current data sources, navigation destinations, contact privacy behavior, sighting/reporting rules, and permission prompts. Sensitive information such as exact coordinates, contact data, addresses, and private messages must remain governed by the existing privacy capabilities; responsive layout must not reveal any additional fields.

## Goals / Non-Goals

**Goals:**
- Define a responsive pet detail layout for compact, medium, and expanded widths.
- Keep top and bottom fixed surfaces usable with safe-area/system-bar insets.
- Make the media header preserve photo quality and page readability in portrait and landscape.
- Ensure badges, cards, and action controls remain readable, tappable, and non-overlapping.
- Preserve accessibility text scaling by using scalable text units and flexible-height content.
- Provide validation steps for phone portrait, phone landscape, tablet width, and larger font settings.

**Non-Goals:**
- Add a new backend, repository, Room entity, or data migration.
- Change authentication, ownership, sighting submission, chat, contact privacy, or notification behavior.
- Add new Android runtime permissions.
- Replace the app theme or redesign unrelated screens.
- Promise encryption, privacy, or real-time behavior beyond what the existing app implements.

## Decisions

1. Use width-driven adaptive layout branching.
   - Rationale: the requested breakpoint is based on available width: compact below 600dp and medium/expanded at 600dp and above.
   - Approach: derive the available width in Compose and choose between single-column, two-column, or centered constrained single-column variants.
   - Alternative considered: maintain one scroll column for every device. Rejected because tablets and landscape screens would waste horizontal space while still forcing excessive vertical scrolling.

2. Keep fixed chrome outside the scrolling detail body.
   - Rationale: back/navigation and primary actions should remain reachable while the user scrolls long pet information.
   - Approach: keep the top app bar in the scaffold top slot and put bottom actions in the bottom slot or a bottom-aligned surface, applying system-bar/safe-area padding. The scrollable body reserves top/bottom padding from scaffold insets and the bottom action height.
   - Alternative considered: place all actions inline at the end of the scroll. Rejected because critical report/contact actions can become hard to reach on long content.

3. Use a single-column vertical scroll for compact widths.
   - Rationale: narrow phones need predictable reading order and full-width media/actions.
   - Approach: media header first, then badges/identity, information cards, and action controls with 16dp horizontal margins.
   - Alternative considered: squeeze a two-column layout into landscape compact height. Rejected because it would create cramped cards and smaller touch targets.

4. Use a two-column master-detail layout for widths at or above 600dp when height allows.
   - Rationale: tablets and wide landscape screens can keep the pet photo visible while details scroll independently.
   - Approach: left column hosts the media header as fixed or sticky content; right column hosts a scrollable stack of cards and actions with 24dp gutters. If the available height is too constrained, use the centered single-column fallback.
   - Alternative considered: make both columns independently scrollable. Rejected because it increases coordination complexity and can make the main photo disappear unpredictably.

5. Constrain the media header by aspect ratio and viewport height.
   - Rationale: photos need to remain visually useful without consuming the entire screen in landscape.
   - Approach: use a 4:3 portrait-oriented aspect ratio by default, center-crop the image, and cap landscape/compact-horizontal height at 45% of the viewport height.
   - Alternative considered: fixed dp image height. Rejected because it does not adapt across phones, tablets, and split-screen sizes.

6. Size actions for touch and readability.
   - Rationale: primary actions must stay ergonomic and comply with expected mobile touch areas.
   - Approach: each button fills its parent width up to 400dp, uses 56dp height, and remains horizontally centered when the parent is wider than the cap.
   - Alternative considered: stretch buttons across full tablet width. Rejected because overly long buttons reduce scanability and visual polish.

7. Allow information cards to grow with content and font scale.
   - Rationale: pet notes, locations, and accessibility text settings can be longer than the design baseline.
   - Approach: avoid fixed card heights, avoid hard text truncation for core pet data, and use wrap-content/auto-height card bodies.
   - Alternative considered: fixed-height cards with ellipsized text. Rejected because truncation can hide important pet-identification details.

## Risks / Trade-offs

- Two-column layout can crowd short landscape screens -> fall back to centered single-column when available height cannot support readable columns.
- Bottom fixed actions can cover content -> reserve explicit bottom padding in the scroll content and validate with gesture navigation.
- Center-cropped photos can hide edge details -> keep image centered and avoid distorting aspect ratio; do not crop source assets or mutate uploaded media.
- Larger font settings can stretch cards significantly -> prefer flexible vertical spacing and manual validation with increased system font scale.
- Introducing adaptive helpers can add UI complexity -> keep helpers local to the screen or reusable only where the same layout contract is needed.

## Migration Plan

1. Identify the pet detail/sighting-detail composable that owns the media header, badges, info cards, and actions.
2. Extract small presentational pieces only when it keeps the responsive branches readable.
3. Add width and height-aware layout branching for compact, expanded two-column, and centered single-column fallback.
4. Move action surfaces into a fixed bottom area or bottom-aligned scaffold surface with safe-area padding.
5. Update or add Compose tests for layout selection and action accessibility where feasible.
6. Run the Android debug build and perform manual validation across phone portrait, phone landscape, tablet width, and large font settings.

Rollback is limited to the affected UI composables: restore the previous single-column composition and inline actions while leaving navigation, data, permissions, and privacy behavior unchanged.

## Open Questions

- Should the implementation apply first to the existing `SightingAlertScreen`, a future dedicated pet detail screen, or both if the UI pieces are shared?
