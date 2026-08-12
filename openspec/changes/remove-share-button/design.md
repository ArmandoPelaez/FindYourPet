## Context

The home feed currently renders each lost-pet post with a secondary `AppButton` labeled `Compartir`. `HomeScreen.kt` builds a privacy-safe text payload, creates an `Intent.ACTION_SEND`, and launches the Android chooser. The current Compose tests and screenshot tests assert that this control is visible. Repository inspection found no other post-sharing flow in Kotlin source.

The change is a client-only removal. It must preserve the existing Material 3 presentation, theme behavior, sighting action, feed data, navigation, and privacy/backend rules. `docs/design-system.md` was reviewed before planning.

## Goals / Non-Goals

**Goals:**

- Remove the Share control from the lost-pet post card.
- Remove the Android share intent and the helper used only to construct its payload.
- Remove or replace stale tests that expect Share to be available.
- Keep the remaining card actions and layout within the existing design tokens and themes.

**Non-Goals:**

- Do not change the sighting-report flow or any ViewModel/repository/domain behavior.
- Do not change post fields, Room, Firestore, permissions, manifest, notifications, or privacy rules.
- Do not redesign the card or introduce new colors, dimensions, spacing, shapes, dependencies, or APIs.
- Do not remove unrelated uses of the word “share” referring to contact privacy documentation or Android backup storage.

## Decisions

### Remove the control and implementation, rather than disable it

Delete the Share `AppButton`, its `Intent` callback, its `LocalContext` dependency, and `buildPetPostShareText`. This satisfies Jira's requirement to remove both the button and the current functionality. Hiding only the button would leave dead behavior and stale production code.

**Alternative considered:** keep the helper and intent for a future feature while hiding the button. Rejected because Jira explicitly requires removing the current Share functionality and because it would retain an unreachable platform action.

### Keep the card structure and remaining action unchanged

Remove only the Share-specific action block and its test expectations. Preserve the existing sighting action, spacing tokens, status, identity, location, reported information, date, image loading, and bottom-safe layout. No replacement action is added.

**Alternative considered:** replace Share with another action or reflow the entire card. Rejected because no replacement is specified by Jira and it would expand the visual scope.

### Verify absence through focused presentation tests

Update `HomeFeedPresentationTest.kt` and `HomeFeedPresentationScreenshotTest.kt` to stop asserting Share visibility and to assert that the Share label/content description and implementation markers are absent where appropriate. Keep the existing tests for card hierarchy and sighting reporting.

**Alternative considered:** remove all tests mentioning Share without adding absence coverage. Rejected because the acceptance criteria require that the control and functionality remain removed.

## Risks / Trade-offs

- [Risk] A future or less-visible screen may still expose a Share action → Mitigation: search Kotlin production and test sources for `ACTION_SEND`, `createChooser`, `Icons.*.Share`, `buildPetPostShareText`, and the visible Share labels during verification.
- [Risk] Removing the action changes the vertical rhythm of a card → Mitigation: preserve existing design tokens and validate compact/tall, Light/Dark presentation tests; do not introduce a redesign.
- [Risk] Existing users may rely on the system share flow → Mitigation: this is the explicit product decision in SCRUM-7; rollback is limited to restoring the removed control and helper if the decision changes.

## Migration Plan

No data or persistence migration is required. Remove the client code, update focused tests, run OpenSpec validation, unit tests, and the debug build. Rollback consists of reverting the change branch if Jira's decision is reversed.

## Open Questions

None blocking. Jira says “screens”, and repository inspection found the only post Share UI/implementation in `HomeScreen.kt`; the implementer should report any additional production occurrence discovered while applying the change.
