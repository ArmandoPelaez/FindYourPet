## Context

`PetAppNavigation` currently creates one `NavHost` after sign-in and each destination owns its own screen chrome. `HomeScreen` owns both its top app bar and the `BottomPrimaryActionBanner`; `ProfileScreen` and `ChatListScreen` are separate destinations with back arrows and no primary bar.

The requested behavior is the standard primary-navigation model: Home, Profile, and Chats remain inside the same signed-in shell and keep the bottom bar visible. Secondary flows such as Create Post, Notifications, Sighting Alert, and Chat Detail should remain focused task/detail screens and should not show the primary bar.

Navigation Compose supports placing app chrome beside the `NavHost` in a parent composable. Material 3 `Scaffold` is the natural fit for this app because the existing bottom bar is a Compose/Material surface and the scaffold content padding can keep screen content clear of the bar.

## Goals / Non-Goals

**Goals:**

- Move the bottom primary bar outside individual screens and into the signed-in navigation shell.
- Keep the bar visible on Home, Profile, and Chats.
- Hide the bar on secondary routes.
- Avoid duplicate back-stack entries when users repeatedly tap Home, Profile, or Chats.
- Preserve the floating bar style while adding an explicit Home action for the main publications feed.
- Remove top-level back affordances from Profile and Chats when they are opened as primary destinations.
- Keep build/test validation focused on navigation behavior and UI visibility.

**Non-Goals:**

- Redesign the visual style of the bottom bar.
- Introduce multiple independent back stacks per tab.
- Add adaptive tablet navigation, navigation rail, or a new dependency.
- Change authentication, repositories, backend rules, private chat data, notifications data, permissions, or local storage.
- Make Create Post a selected tab; it remains a central action that opens a secondary flow.

## Decisions

1. Use a signed-in app shell `Scaffold` around the `NavHost`.
   - Rationale: The bar needs to live at the same compositional level as the `NavHost`, not inside `HomeScreen`. A parent `Scaffold(bottomBar = ...)` expresses that ownership directly and supplies content padding for destinations under the bar.
   - Alternative considered: Keep the bar inside each primary screen. Rejected because it duplicates UI ownership, makes selected state/navigation policy harder to centralize, and risks inconsistent spacing.
   - Alternative considered: Build a custom `Box` overlay around the `NavHost` instead of `Scaffold`. Possible, but weaker here because `Scaffold` already solves bottom-bar insets and content padding using APIs the app already uses.

2. Determine bar visibility from the current route.
   - Rationale: `currentBackStackEntryAsState()` can identify whether the active destination is one of the primary routes: `home`, `profile`, or `chats`.
   - Alternative considered: Pass visibility flags down into each screen. Rejected because it keeps navigation chrome coupled to destination internals.

3. Centralize primary navigation in a helper.
   - Rationale: The bar should call one helper for Home, Profile, and Chats that applies bounded stack behavior consistently.
   - Expected policy: `navController.navigate(primaryRoute) { popUpTo(navController.graph.startDestinationId or findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true }`.
   - `launchSingleTop` prevents repeated taps on the selected item from pushing duplicate copies.
   - `popUpTo` keeps the primary stack anchored at the start destination instead of creating an ever-growing sequence like Home > Profile > Chats > Profile > Chats.
   - `saveState`/`restoreState` may preserve UI state such as scroll position where Navigation Compose can support it.
   - Alternative considered: Use bare `navigate(route)`. Rejected because it can create duplicate destinations with repeated taps.

4. Keep Create Post as a secondary action route, but do not let it replace Home navigation.
   - Rationale: The plus opens a creation task, not a persistent destination. Home, Profile, and Chats are the primary destinations and must remain directly reachable from the persistent bar.
   - Navigation should at least use `launchSingleTop = true` for create navigation so accidental double taps do not add duplicate create screens.
   - Alternative considered: Add Create Post as a fourth top-level destination. Rejected because the requested persistent set is Home, Profile, and Chats, and the existing bar visually treats create as a prominent action rather than a tab.

5. Remove top-level back icons from Profile and Chats when the shell owns navigation.
   - Rationale: Top-level destinations should not present themselves as child screens of Home. The shared bar and system back behavior are enough for primary switching.
   - Implementation can either make `onBackClick` optional for those composables or pass a flag such as `showBackNavigation = false` from the shell.
   - Alternative considered: Keep the back arrows. Rejected because it conflicts with the standard top-level model and suggests Profile/Chats are temporary child routes.

6. Keep secondary destinations unchanged except for content padding from the shell.
   - Rationale: Create, Notifications, Sighting Alert, and Chat Detail should continue to own their own back affordances and should not show the primary bar.
   - If the outer scaffold only supplies a bottom bar for primary routes, secondary destinations receive no extra bottom-bar padding from it.

## Risks / Trade-offs

- Nested scaffold padding could double-space Home if the old `HomeScreen` bottom bar is not removed -> Remove the `bottomBar` from `HomeScreen` and verify compact layouts.
- Profile or Chats content could be covered by the shell bar -> Apply the outer scaffold content padding to the `NavHost` and verify list bottom content remains reachable.
- Route matching can fail for parameterized routes such as `chat/{chatId}` -> Use exact primary route matching and keep parameterized/detail routes out of the visible-bar set.
- `popUpTo` can accidentally discard too much state if configured too broadly -> Anchor to the graph start destination and use `saveState`/`restoreState` for top-level switching.
- Removing back arrows changes learned behavior -> Only remove them from primary rendering; secondary detail/task screens keep explicit back controls.
- Navigation helpers can become stringly typed -> Define route constants or a small sealed/enum-like model for primary destinations if it fits the existing code style.

## Migration Plan

1. Introduce route constants and a primary-destination list/model near `PetAppNavigation`.
2. Wrap the signed-in `NavHost` in an outer `Scaffold` whose `bottomBar` renders `BottomPrimaryActionBanner` only for Home, Profile, and Chats.
3. Move the banner wiring from `HomeScreen` to the outer shell and remove the `HomeScreen` bottom bar.
4. Apply scaffold content padding to the `NavHost` so primary screen content clears the shell bar.
5. Add centralized navigation helpers for primary routes and create action navigation, using `popUpTo`, `launchSingleTop`, and state restoration where appropriate.
6. Adjust Profile and Chats top bars so primary rendering does not show a back arrow.
7. Update/add tests for bar placement, visibility rules, navigation options, and absence of duplicate home-owned bottom bars.
8. Run the relevant Android test/build commands and perform manual validation on Home, Profile, Chats, Create Post, Notifications, Sighting Alert, and Chat Detail.

Rollback: remove the outer shell bottom bar, return `BottomPrimaryActionBanner` to `HomeScreen`, restore Profile/Chats back-arrow behavior, and replace helper-based primary navigation with the prior direct `navigate(...)` calls.

## Open Questions

- None. If implementation reveals that outer `Scaffold` padding conflicts with an existing inner scaffold in a way that cannot be resolved cleanly, a custom parent `Box` overlay can be reconsidered, but `Scaffold` is the preferred starting point.
