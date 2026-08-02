## Why

The current primary action bar is owned by `HomeScreen`, so it disappears when users navigate to Profile or Chats even though those destinations are primary areas of the signed-in app. This change standardizes the navigation model by moving the bar to the signed-in app shell, preventing visual churn and avoiding accidental back-stack growth from repeated taps.

## What Changes

- Move the existing three-action bottom bar out of `HomeScreen` and into a signed-in navigation shell at the same level as `NavHost`.
- Keep the bar visible on the primary destinations: Home, Profile, and Chats.
- Hide the bar on secondary/task destinations such as Create Post, Notifications, Sighting Alert, and Chat Detail.
- Treat Home, Profile, and Chats as top-level destinations rather than back-stack-only child screens.
- Centralize primary bar navigation so repeated taps use appropriate Navigation Compose options such as `launchSingleTop` and `popUpTo` instead of creating duplicate destination stacks.
- Update the bar actions so Home, Profile, and Chats are directly reachable as primary destinations while Create Post remains a prominent secondary action.
- Keep Create Post as an action launched from the bar, not as a persistent selected tab.
- No changes to authentication, backend data, privacy rules, permissions, chat contents, notifications data, or local storage.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `primary-navigation`: Updates primary navigation from a home-only banner to an app-level shell that persists across Home, Profile, and Chats and applies bounded back-stack behavior for repeated primary navigation taps.

## Impact

- Affected UI/navigation code: `MainActivity.kt` / `PetAppNavigation`, `HomeScreen.kt`, `ProfileScreen.kt`, `ChatListScreen.kt`, and the existing `BottomPrimaryActionBanner` component if small API adjustments are useful.
- Affected behavior: Home, Profile, and Chats become primary destinations with the shared bottom bar visible; secondary flows continue to use their own top bars and back behavior without the primary bar.
- Affected tests: add or update Compose/Robolectric/static coverage for app-shell bar visibility, top-level navigation options, and removal of the home-owned bottom bar.
- Dependencies/APIs: no new dependency expected; implementation should use existing Navigation Compose and Material 3 APIs.
- Privacy/security/data/permissions: no expected impact. Existing guardrails around personal data, chat content, notifications, location, and permissions continue to apply because this change only moves navigation chrome and navigation options.
- User impact: signed-in users get a stable primary navigation surface across the core app areas and repeated taps do not build a deep stack of duplicate screens.
- Rollback strategy: move the bottom bar ownership back into `HomeScreen`, restore Profile/Chats as secondary screens with back arrows, and remove the centralized top-level navigation helper.
