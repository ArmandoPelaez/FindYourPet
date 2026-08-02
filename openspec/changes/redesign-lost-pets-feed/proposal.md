## Why

The home feed currently shows classification and descriptive blocks that are either visually redundant or can come from default/internal values instead of explicit user input. This makes the main lost-pet card look crowded and risks presenting non-verified details as if they were reported facts.

## What Changes

- Redesign the lost-pet home card so the pet name remains the primary identity element and the last-seen location moves directly underneath it as an icon plus location text only, without a section title.
- Remove from the home card the elements marked for deletion in the reference: the breed chip, the `Color` attribute block, and the `Señas` attribute block.
- Remove the standalone location title `Ubicación en la que se perdió` from the home card.
- Remove home-feed user-facing text, share text, tests, specs, and helper-layer references that reintroduce the deleted breed/color/signs presentation for this screen.
- Keep existing behavior for post loading, image rendering, status badge, horizontal paging, bottom navigation, report-sighting action, share action, ownership restrictions, and privacy-safe sharing.
- Do not introduce new fields, mock values, fallback facts, generated descriptions, or claims that are not present in the post information submitted by the user.
- Do not change backend schema, Room schema, create-post validation, auth, permissions, chat, notifications, or location capture behavior as part of this presentation change.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `home-feed-presentation`: The home feed card hierarchy changes to show pet name followed immediately by titleless last-seen location, and removes the breed chip plus color/signs attribute blocks from the main feed and its associated presentation outputs.

## Impact

- Affected UI: `app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt`.
- Affected tests: `HomeFeedPresentationTest` and static guardrail tests that currently assert breed, color, signs, reported-information headings, or titled location content in the home card.
- Affected specs/docs: `home-feed-presentation` requirements and any OpenSpec/project text describing the removed home-feed elements.
- Affected sharing helper: home-feed share text must not add removed breed/color/signs facts or default values from this screen.
- User impact: the main feed becomes cleaner and more trustworthy, emphasizing photo, status, pet name, last-seen location, and actions without changing how users navigate or report sightings.
- Privacy/security impact: no new sensitive data is exposed; the change reduces public-facing detail and keeps exact coordinates, owner contact, private messages, and hidden contact data excluded.
- Rollback: restore the prior home card identity chip, attribute blocks, titled location section, share-text lines, and tests if product later decides those fields should return.
- Applicable guardrails: do not invent values; do not expose precise location or contact data; do not change permissions; do not alter backend access rules or data ownership; preserve existing action behavior.
