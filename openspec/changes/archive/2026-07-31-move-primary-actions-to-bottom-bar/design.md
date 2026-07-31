## Context

`HomeScreen` currently uses a `TopAppBar` for branding plus notifications, chat, and profile actions, and an `ExtendedFloatingActionButton` for post creation. The requested visual change moves profile, create-post, and chat into a floating bottom banner with no visible internal separation: profile on the left, plus/create post in the center, and chat on the right.

The app already gates `HomeScreen` behind signed-in navigation in `PetAppNavigation`, and the existing callbacks cover all three destinations. This change should therefore be UI-only: no new routes, repositories, permissions, backend calls, or data models are needed.

## Goals / Non-Goals

**Goals:**
- Move profile, create-post, and chat into one floating bottom action banner on the home screen.
- Keep notifications in the top app bar.
- Preserve existing navigation behavior for profile, create post, and chat.
- Prevent the bottom banner from covering feed cards, empty states, or gesture navigation.
- Keep the implementation readable and reusable enough to test in isolation if needed.

**Non-Goals:**
- Redesign profile, chat, post creation, notification, or pet detail screens.
- Add bottom navigation across every route in the app.
- Change authentication, ownership, privacy, backend, or local-storage behavior.
- Add new Android permissions or dependencies.

## Decisions

1. Implement the banner as a Compose component used by `HomeScreen`.
   - Rationale: the banner is a presentational surface with existing callbacks, so a small composable keeps the home scaffold readable.
   - Alternative considered: use Material `NavigationBar`. Rejected because the requested design is a floating, separator-free action banner with a prominent central plus, not a standard destination bar.

2. Keep notifications in the `TopAppBar`, remove chat/profile from header actions, and remove the extended publish FAB.
   - Rationale: notifications are status/alert-driven, while profile, create, and chat are primary navigation/action shortcuts. Separating them reduces header crowding without hiding alerts.
   - Alternative considered: move notifications to the bottom banner too. Rejected because it would add a fourth action and conflict with the user's requested three-icon layout.

3. Use `Scaffold` bottom inset/padding plus extra content padding for the floating banner height.
   - Rationale: feed cards, horizontal pager content, and empty states must remain tappable and readable above the banner.
   - Alternative considered: overlay the banner without adjusting content padding. Rejected because it could cover the bottom of cards or text, especially on gesture-navigation devices.

4. Keep the center plus action icon-only or mostly icon-led, with accessibility labels and visual prominence.
   - Rationale: the mockup request emphasizes a centered `+` icon. The previous "Publicar Mascota" text can be removed from the visible primary control while preserving the content description for accessibility.
   - Alternative considered: retain the long extended button label inside the banner. Rejected because it would unbalance the three-action layout and recreate the crowding problem at the bottom.

5. Render the center plus action as a circular button inside the banner.
   - Rationale: the selected visual direction keeps the three actions unified in one floating banner while making the create-post action clearly primary.
   - Alternative considered: use a larger elevated plus button overlapping the banner. Rejected because the desired layout is a single integrated banner without a separate overlapping control.

## Risks / Trade-offs

- Bottom banner may obscure content on small screens -> Add explicit bottom padding and validate on compact mobile dimensions.
- Icon-only actions may be less obvious for first-time users -> Use familiar Material icons and clear content descriptions; keep the center plus visually dominant.
- Banner styling could look like a standard nav bar if it spans edge-to-edge -> Use a floating surface with horizontal margins, rounded shape, elevation, and no internal dividers.
- Repositioning actions changes user muscle memory -> Keep destinations unchanged and make rollback limited to `HomeScreen`/component edits.

## Migration Plan

1. Add the floating bottom banner component and wire it to the existing home callbacks.
2. Remove chat/profile icon buttons from the top app bar and remove the extended publish FAB.
3. Add content bottom padding so the feed and empty state remain unobstructed.
4. Run Android tests/build relevant to Compose UI and perform manual visual validation.

Rollback is straightforward: restore the prior top app bar actions and `ExtendedFloatingActionButton`, then remove the bottom banner component usage.

## Open Questions

- None.
