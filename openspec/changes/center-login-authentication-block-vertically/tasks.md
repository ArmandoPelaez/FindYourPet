## 1. Scope and baseline

- [x] 1.1 Capture baseline coordinates for IdentityHeader, Hero, and AuthenticationBlock from the current emulator.
- [x] 1.2 Read `docs/design-system.md` and identify existing tokens/components for region-local responsive positioning.
- [x] 1.3 Confirm AuthenticationBlock subtree, current coordinate, spacing, callbacks, semantics, scroll, and IME behavior.

## 2. Independent region layout

- [x] 2.1 Split IdentityHeader from Hero into separate direct layout boundaries without changing their contents.
- [x] 2.2 Preserve IdentityHeader at its current vertical coordinate using its own layout boundary.
- [x] 2.3 Move only Hero headline/supporting text to the marked lower composition.
- [x] 2.4 Preserve AuthenticationBlock coordinate, subtree, internal spacing, order, widths, styles, callbacks, and states.
- [x] 2.5 Preserve root `verticalScroll()` and `imePadding()` for small screens and keyboard-open states.
- [x] 2.6 Ensure no shared-parent padding, spacer, or offset displaces AuthenticationBlock.
- [x] 2.7 Apply reference-only start alignment and preserve the original Hero typography tokens (`headlineSmall`/`bodyMedium`).
- [x] 2.8 Start-align the visible `Iniciar sesion` authentication label without changing the auth controls.

## 3. Presentation tests

- [x] 3.1 Add static assertions for independent IdentityHeader, Hero, and AuthenticationBlock boundaries.
- [x] 3.2 Assert header/Hero-only positioning and reject shared-parent displacement or device-specific hardcoded vertical values.
- [x] 3.3 Assert preservation of AuthenticationBlock controls, tokens, callbacks, semantics, scroll, and IME behavior.
- [x] 3.4 Add static assertions for Hero text alignment/typography and authentication-label alignment.

## 4. Verification

- [x] 4.1 Run `openspec validate "center-login-authentication-block-vertically" --strict`.
- [x] 4.2 Run `./gradlew.bat testDebugUnitTest --no-daemon --console=plain` and `./gradlew.bat assembleDebug --no-daemon --console=plain`.
- [x] 4.3 Install a freshly rebuilt APK and compare fixed IdentityHeader, shifted Hero, and AuthenticationBlock coordinates with the reference/current baseline.
- [ ] 4.4 Validate small screen, Light/Dark Theme, keyboard-open behavior, and interactions with all authentication controls.
- [x] 4.5 Run `git diff --check` and document emulator limitations.
