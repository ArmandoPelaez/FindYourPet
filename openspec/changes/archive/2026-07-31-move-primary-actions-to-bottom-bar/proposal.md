## Why

The home screen currently splits primary actions between the top app bar and an extended floating button, which makes the header visually crowded and places frequent actions far from thumb reach on mobile. Moving profile, create-post, and chat into a single floating bottom banner improves discoverability and gives the feed more visual focus while preserving the existing notification entry in the header.

## What Changes

- Add a floating bottom primary-action banner on the home screen with three actions:
  - Left: profile icon, navigates to the user profile.
  - Center: prominent plus icon, navigates to create a pet post.
  - Right: chat icon, navigates to private chats.
- Remove profile and chat from the home top app bar actions.
- Replace the current extended "Publicar Mascota" floating action button with the centered plus action in the bottom banner.
- Keep notifications in the top app bar so alerts remain visually separate from navigation/actions.
- Ensure the bottom banner has no visible internal dividers and floats above the screen content with enough bottom inset for gesture navigation.
- Add bottom content padding on the feed/empty state so the floating banner does not cover content.
- No breaking changes to post creation, profile, chat, authentication, backend data, permissions, or privacy behavior.

## Capabilities

### New Capabilities
- `primary-navigation`: Defines the home screen primary action/navigation surface, including the floating bottom banner layout, destinations, visibility, and content-safe spacing.

### Modified Capabilities
- None.

## Impact

- Affected UI code: `HomeScreen.kt` and, if useful, a reusable Compose component under `ui/components`.
- Affected navigation callbacks: existing `onNavigateToCreate`, `onNavigateToChatList`, and `onNavigateToProfile` remain the destinations for the new banner actions.
- Affected tests: add or update Compose/Robolectric coverage for the home action layout where feasible, plus manual visual validation on a mobile-sized viewport/device.
- Privacy/security impact: none expected; this change only relocates existing authenticated navigation actions and does not reveal additional personal data.
- User impact: authenticated users will access profile, post creation, and chats from the bottom banner instead of the header/FAB.
- Rollback strategy: restore the prior top app bar profile/chat actions and extended publish FAB if usability validation shows the bottom banner causes confusion or content obstruction.
