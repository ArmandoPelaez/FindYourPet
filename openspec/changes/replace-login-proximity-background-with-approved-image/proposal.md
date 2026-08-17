## Why

The Login currently uses a locally drawn Canvas composition for proximity, while the approved visual direction now requires a single bitmap asset that provides the intended map/proximity atmosphere. SCRUM-38 replaces that decorative implementation without changing authentication behavior or introducing a real map.

## What Changes

- Replace the current `LoginProximityBackground` Canvas composition with the approved local bitmap background.
- Use the versioned `imagen_fondo_pantalla_login.png` asset because the requested `.webp` is not present and Jira explicitly permits the PNG as a temporary resource.
- Render the image behind the complete Login content as a non-interactive decorative layer with no accessibility announcement or focus.
- Preserve responsive layout, legibility, Light/Dark support, keyboard behavior, focus, authentication actions, and navigation.
- Remove the obsolete Canvas-based background usage from Login; do not recreate or generate another asset.

## Capabilities

### New Capabilities

- `login-decorative-background`: Approved local bitmap background behavior for the Login screen, including layering, responsiveness, accessibility, and legibility constraints.

### Modified Capabilities

None. Authentication requirements and contracts do not change.

## Impact

- Affected code: `AuthScreen.kt`, the existing `LoginProximityBackground` component and its presentation tests, plus the approved resource reference.
- No new dependencies, permissions, external services, maps, persistence, ViewModel, Firebase, repository, navigation, or domain changes.
- Existing users retain the same Login controls and behavior; only the decorative background implementation changes.
- Rollback: restore the existing `LoginProximityBackground` layer and remove the bitmap reference; no data migration is required.
- Applicable guardrails: stable Jetpack Compose/Material 3, existing Design System tokens, Light/Dark support, no arbitrary visual constants, no Canvas recreation, and no accessibility interference.
