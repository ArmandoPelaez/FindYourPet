## 1. Scope and design-token preparation

- [x] 1.1 Confirm the current `BottomPrimaryActionBanner` surface, `AppOpacity.banner` usages, and existing presentation test harness before editing.
- [x] 1.2 Add a dedicated `AppOpacity.bottomNavigation` token in `DesignTokens.kt` without changing the value or behavior of shared tokens used by other screens.

## 2. Bottom navigation presentation

- [x] 2.1 Update only `BottomPrimaryActionBanner` to apply the dedicated non-opaque token while retaining `surfaceVariant`, icon tints, shape, elevation, dimensions, spacing, safe-area padding, and action order.
- [x] 2.2 Verify that the change does not modify navigation callbacks, route visibility, back-stack behavior, ViewModels, repositories, Firebase, Room, permissions, or business logic.

## 3. Automated validation

- [x] 3.1 Add or update focused component/static coverage proving the banner uses the dedicated token and the existing shared banner opacity behavior remains unchanged elsewhere.
- [x] 3.2 Validate the bottom banner presentation in Light Theme and Dark Theme, including icon legibility and non-opaque surface behavior, using the existing deterministic test harness where available.
- [x] 3.3 Run `openspec validate "add-transparency-to-bottom-navigation" --strict`.
- [x] 3.4 Run `\.\gradlew.bat testDebugUnitTest`.
- [x] 3.5 Run `\.\gradlew.bat assembleDebug`.

## 4. Manual UI verification

- [x] 4.1 On a signed-in compact phone layout, inspect Home while scrolling behind the bottom banner and confirm continuity without a visible redesign or loss of contrast.
- [x] 4.2 Verify the existing Home, Profile, Create Post, and Chats actions still navigate exactly as before and the bar remains above the system gesture area.
- [x] 4.3 Repeat the visual check in Light Theme and Dark Theme and record the evidence in the orchestration state file.
