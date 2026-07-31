## 1. Home Layout Update

- [x] 1.1 Create a bottom primary-action banner composable for profile, create post, and chat actions.
- [x] 1.2 Wire the banner actions to the existing `onNavigateToProfile`, `onNavigateToCreate`, and `onNavigateToChatList` callbacks.
- [x] 1.3 Remove profile and chat actions from the home `TopAppBar` while keeping branding and notifications.
- [x] 1.4 Remove the extended `Publicar Mascota` floating action button from the home scaffold.
- [x] 1.5 Add bottom padding/insets so empty states, feed cards, and in-card buttons are not covered by the floating banner.

## 2. Visual Polish And Accessibility

- [x] 2.1 Style the banner as one floating surface with no visible internal separators.
- [x] 2.2 Make the centered plus action visually primary and balanced between the left profile and right chat actions.
- [x] 2.3 Add clear content descriptions for profile, create post, and chat icon buttons.
- [x] 2.4 Verify the banner sits above Android gesture navigation and respects compact phone widths.

## 3. Tests

- [x] 3.1 Add or update Compose/Robolectric test coverage that verifies the home header no longer exposes profile/chat actions.
- [x] 3.2 Add or update Compose/Robolectric test coverage that verifies the bottom banner exposes profile, create-post, and chat actions.
- [x] 3.3 Run the relevant test suite, such as `gradlew.bat testDebugUnitTest`, and address regressions.

## 4. Create Post Screen Simplification

- [x] 4.1 Remove the current-location button from the create post form.
- [x] 4.2 Remove the optional reward field from the create post form.
- [x] 4.3 Keep manual last-seen location and submit posts with `Sin recompensa` by default.

## 5. Feed Card As Full Pet Sheet

- [x] 5.1 Remove the `Ver Ficha` button from the home pet card.
- [x] 5.2 Show the pet name on the left and breed on the right inside the home pet card.
- [x] 5.3 Show the reported pet information, species, color, characteristics, lost location, and post date in the home pet card.
- [x] 5.4 Keep `¡Lo he visto!` as the main action after the lost-location information.
- [x] 5.5 Remove the detail-screen route from primary navigation so the separate pet sheet screen is no longer reachable.
- [x] 5.6 Delete the obsolete pet detail screen implementation.

- [x] 5.7 Remove the swipe hint and page counter row above the home pet card.
- [x] 5.8 Remove the protected-owner overlay from the pet image.
- [x] 5.9 Remove the `Tipo` card and show `Color` in that first information slot.

## 6. Manual Validation

- [x] 6.1 Build the debug app with `gradlew.bat assembleDebug`.
- [x] 6.2 Manually validate the home screen empty state with the bottom banner visible.
- [x] 6.3 Manually validate the home feed/card state with the bottom banner visible and confirm card actions remain tappable.
- [x] 6.4 Manually tap profile, plus/create post, chat, and notifications to confirm each route still opens the expected screen.
- [x] 6.5 Confirm no privacy, permission, backend, or storage behavior changed as part of the UI-only move.
