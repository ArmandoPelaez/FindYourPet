## Why

The pet detail screen needs to remain readable and tappable across phones, tablets, landscape, notches, and gesture-navigation devices. A fixed mobile-only layout risks cropped media, hidden actions, and truncated information just when users need to inspect a lost-pet alert quickly.

## What Changes

- Introduce responsive layout behavior for the pet detail screen:
  - Compact screens below 600dp use a single vertical column inside a scrollable content area.
  - Medium and expanded screens at 600dp and above use a two-column master-detail layout, with the media header on the left and scrollable information/actions on the right.
  - Tablet single-column fallback may center content with a 640dp maximum width when a two-column layout is not appropriate.
- Keep top app bar and bottom action/navigation surfaces fixed at screen edges, respecting system safe-area insets, notches, and gesture bars.
- Make the main photo header adaptive:
  - Use a 4:3 ratio in portrait.
  - Limit height to 45vh in landscape or compact horizontal presentations.
  - Use center-crop image scaling without distorting photos.
  - Keep overlaid badges positioned independently from the image bounds with dp margins.
- Make primary action controls fluid within their parent container, capped at 400dp per button and using 56dp touch height.
- Make information cards flexible-height surfaces so long labels, owner notes, locations, and pet attributes wrap instead of truncating.
- Use density-independent units for layout/padding/borders and scalable text units for typography.
- No breaking changes to post creation, sightings, chat, authentication, backend data, permissions, or privacy behavior.

## Capabilities

### New Capabilities
- `adaptive-pet-detail-ui`: Defines responsive pet detail presentation, including layout breakpoints, media header behavior, action sizing, fixed bars, safe-area handling, flexible cards, and accessible typography.

### Modified Capabilities
- None.

## Impact

- Affected UI code: pet detail screen composables and any reusable media header, info-card, or action-control components used by that screen.
- Affected design system usage: responsive sizing, window size class handling, safe drawing/window inset handling, and adaptive spacing rules.
- Affected tests: add or update Compose UI coverage where feasible for compact and expanded layout decisions, plus manual validation on phone portrait, phone landscape, tablet width, and large font settings.
- Privacy/security impact: none expected; this change reorganizes already-visible pet detail information and must not expose additional contact, exact address, coordinate, owner, or message data.
- User impact: existing users see the same pet data and actions arranged more reliably across device sizes and orientations.
- Rollback strategy: restore the previous single-layout pet detail composition and remove the adaptive layout branch without changing data, repositories, navigation routes, or permissions.
