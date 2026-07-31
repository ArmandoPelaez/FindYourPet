## Why

The home feed currently repeats pet classification details in a way that makes each reported-pet card feel crowded: breed appears both as the purple identity chip and again as an attribute block, while species is shown as a separate attribute that is no longer desired on the main screen. This change tightens the main feed presentation and removes user-facing references to `Especie` so the card focuses on the pet name, retained breed chip, color, reported information, and location context.

## What Changes

- Remove the `Especie` attribute from reported-pet cards on the home screen.
- Remove the duplicated `Raza` attribute block from reported-pet cards on the home screen.
- Keep the existing purple breed chip visible when a post has breed data.
- Remove or update existing user-facing/project-text references to `Especie` that describe or test the home feed presentation.
- Update home feed tests and specs so they assert the absence of the `Especie` label and the duplicate `Raza` attribute while still asserting that the breed chip remains visible.
- No privacy, permissions, backend access, or personal-data exposure behavior is intentionally changed.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `home-feed-presentation`: Home feed reported-pet cards no longer show species or a duplicate breed attribute block, and they preserve breed only as the compact purple identity chip.

## Impact

- Affected UI: `app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt`.
- Affected tests: home feed Compose tests that currently expect `Especie` or duplicate `Raza` attribute labels.
- Affected specs/docs: OpenSpec home feed presentation requirements and any project text that still references `Especie` for the main feed.
- User impact: reported-pet cards become less repetitive while retaining the breed chip users already see beside the pet name.
- Rollback: restore the previous home feed attribute rows and the prior home-feed-presentation spec/tests if the removed fields are later required again.
- Applicable guardrails: do not remove the purple breed chip; do not invent replacement attributes; do not change backend fields, persistence schema, permissions, or contact/privacy behavior as part of this presentation-only change.
