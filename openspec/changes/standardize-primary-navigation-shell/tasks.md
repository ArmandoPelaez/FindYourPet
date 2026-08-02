## 1. Navigation Shell

- [x] 1.1 Define route constants and the primary destination set for `home`, `profile`, and `chats` in or near `PetAppNavigation`.
- [x] 1.2 Wrap the signed-in `NavHost` in an outer `Scaffold` that owns the bottom primary action bar.
- [x] 1.3 Use the current back stack entry to show `BottomPrimaryActionBanner` only for Home, Profile, and Chats.
- [x] 1.4 Apply the outer scaffold content padding to the `NavHost` so primary screen content clears the shell bar.

## 2. Navigation Behavior

- [x] 2.1 Add a centralized helper for primary destination taps that uses `launchSingleTop`, `popUpTo`, and state save/restore where supported.
- [x] 2.2 Wire the Home, Profile, and Chats bar actions through the primary navigation helper.
- [x] 2.3 Wire the center create-post action as a secondary route with duplicate-tap protection such as `launchSingleTop`.
- [x] 2.4 Keep secondary route navigation for notifications, sighting alerts, chat details, post creation completion, and back actions compatible with the existing flows.
- [x] 2.5 Scope the signed-in `NavController` to the authenticated session so login starts from Home instead of restoring a previous primary destination.

## 3. Screen Chrome Updates

- [x] 3.1 Remove `BottomPrimaryActionBanner` ownership and bottom-bar wiring from `HomeScreen`.
- [x] 3.2 Adjust Home content bottom spacing so feed cards and empty states remain unobstructed under the shell-owned bar.
- [x] 3.3 Adjust `ProfileScreen` so primary rendering does not show an in-screen back arrow while preserving sign-out.
- [x] 3.4 Adjust `ChatListScreen` so primary rendering does not show an in-screen back arrow while preserving chat selection.
- [x] 3.5 Preserve explicit back controls on Create Post, Notifications, Sighting Alert, and Chat Detail.

## 4. Tests

- [x] 4.1 Update or add Compose/Robolectric coverage proving the bottom bar is not owned by `HomeScreen` and remains available from the shell.
- [x] 4.2 Add coverage for bar visibility on Home, Profile, and Chats and hidden state on Create Post, Notifications, Sighting Alert, and Chat Detail where feasible.
- [x] 4.3 Add coverage or a static guardrail for primary navigation options: `launchSingleTop`, `popUpTo`, and state save/restore.
- [x] 4.4 Update existing `BottomPrimaryActionBanner` tests only if the component API changes.
- [x] 4.5 Add coverage that the persistent bar exposes a Home action and that signed-in navigation is created after the auth gate.

## 5. Validation

- [x] 5.1 Run the relevant JVM/Compose test command for the touched app tests.
- [x] 5.2 Run an Android debug build before closing the implementation.
- [x] 5.3 Manually validate Home -> Profile -> Chats -> Home repeated taps do not create a long back stack and the bar stays visible.
- [x] 5.4 Manually validate Create Post, Notifications, Sighting Alert, and Chat Detail hide the bar and return correctly through existing back/completion flows.
- [x] 5.5 Manually validate Profile and Chats bottom content remains reachable above the bar on a compact mobile viewport or device.
