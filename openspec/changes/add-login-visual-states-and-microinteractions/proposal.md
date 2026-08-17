## Why

The Login currently exposes authentication actions and outcomes without a complete, consistent visual response for focus, loading, errors, and successful authentication. Work Item 6 adds restrained feedback so users can understand what the app is doing and cannot accidentally submit authentication operations multiple times.

## What Changes

- Add stable, non-blocking visual transitions for input focus and password visibility changes.
- Add loading feedback for email/password login and Google authentication while preventing simultaneous attempts.
- Add recoverable visual feedback for authentication errors and a transition for successful login.
- Keep any proximity-point animation optional, extremely subtle, and non-infinite.
- Preserve existing authentication behavior, action hierarchy, design tokens, Light/Dark support, and screen identity.
- Respect reduced-motion behavior where the platform provides it.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `auth`: Extend authentication UI requirements with observable loading, error, success, focus, password-visibility, and duplicate-submit behavior without changing authentication contracts.

## Impact

- Affected area: Login Compose UI and its presentation-state handling/tests.
- No changes to ViewModels, Firebase Auth, Credential Manager, repositories, navigation, permissions, or domain contracts.
- No new dependencies, permissions, stored data, or sensitive-data flows.
- Existing users retain the current sign-in, sign-up, and Google authentication behavior; only visual feedback and interaction guarding change.
- Rollback: revert the change branch; authentication contracts and backend state remain unaffected.
- Applicable guardrails: stable Material 3/Compose APIs, existing design-system tokens, Light/Dark support, no arbitrary visual constants, and no resource-consuming infinite animations.
