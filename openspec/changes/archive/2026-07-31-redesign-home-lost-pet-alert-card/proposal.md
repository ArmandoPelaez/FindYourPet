## Why

The home screen should communicate a lost-pet alert quickly, with the pet photo, status, identity, reported details, location, date, and actions arranged in one scannable surface. The provided mockup highlights a clearer hierarchy for urgent pet information, but it must be used only as layout inspiration and must not change the current image-loading behavior.

## What Changes

- Redesign the main home pet card into a richer lost-pet alert presentation inspired by the reference image:
  - Keep the current app behavior for showing pet images; do not add, bundle, replace, crop-source, or transform the example dog image.
  - Place the pet image as the primary visual area at the top of the card.
  - Overlay a visible status pill on the image area for lost/found state when that status is available.
  - Present pet name and breed as the main identity row below the image.
  - Present key attributes such as color, age, gender, species, and characteristics in compact, readable sections.
  - Present reported information in a distinct text panel.
  - Present lost location and post date together near the lower portion of the card.
  - Keep "I saw it" as the primary in-card action and add/share a secondary "Compartir" control when it can use a non-sensitive public post summary.
- Refresh the home header area so branding, subtitle, and notifications remain clear without competing with the pet alert card.
- Preserve existing home navigation and action destinations, including any active bottom action banner work from `move-primary-actions-to-bottom-bar`.
- Add spacing rules so the redesigned card remains readable and its controls remain tappable above bottom overlays or gesture navigation.
- No breaking changes to post creation, profile, chat, authentication, backend data, permissions, privacy, or media storage behavior.

## Capabilities

### New Capabilities
- `home-feed-presentation`: Defines how the home feed presents pet alerts, including the main card information hierarchy, in-card controls, image treatment constraints, and safe spacing.

### Modified Capabilities
- None.

## Impact

- Affected UI code: `HomeScreen.kt` and likely reusable Compose components under `ui/components`.
- Affected data usage: existing visible pet post fields are reorganized only; no schema or repository behavior changes are expected.
- Affected actions: existing sighting/report callback remains the destination for the primary in-card control; a secondary share control may use Android sharing with a privacy-safe summary.
- Affected tests: add or update Compose/Robolectric coverage for card content hierarchy and actions where feasible, plus manual visual validation on compact and typical mobile sizes.
- Privacy/security impact: none expected; the change reorganizes already-visible post data and must not expose extra contact, address, coordinate, owner, or message data.
- User impact: users can scan each lost-pet post faster and reach the main "I saw it" and share actions from the same card.
- Rollback strategy: restore the previous home card composition while leaving navigation, data, and backend behavior untouched.
