## Why

SCRUM-7 removes a share action that is not currently needed and avoids exposing a platform sharing surface from the application. The change is intentionally narrow: the visible Share control and its current post-sharing flow must no longer be available.

## What Changes

- **BREAKING** Remove the Share button and its content description from lost-pet post cards.
- **BREAKING** Remove the current platform share intent and post share-text generation used by that control.
- Preserve the existing sighting action, post presentation, navigation, data model, privacy rules, and other screen behavior.
- Update affected presentation tests and screenshot assertions so they verify that the Share control and implementation are absent.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `home-feed-presentation`: remove the secondary share control and the associated platform-share behavior from the home feed contract.

## Impact

- Affected UI source: `app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt`.
- Affected tests: home-feed presentation and screenshot tests that currently assert the Share control or share-text helper.
- No new dependencies, permissions, backend APIs, persistence changes, or migrations.
- Existing users will no longer see or invoke Share from lost-pet cards; sighting reporting and the remaining card hierarchy are unchanged.
- Rollback is limited to restoring the removed UI/action and tests if the product decision is reverted.
- Applicable guardrails: preserve the existing Material 3 design system, Light/Dark theme behavior, privacy boundaries, and avoid unrelated business-logic changes.
