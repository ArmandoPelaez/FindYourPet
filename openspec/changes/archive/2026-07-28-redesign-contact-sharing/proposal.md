## Why

FindYourPet currently has two contact-sharing concepts, public reveal on the pet post and owner sharing inside a chat, that can contradict each other and expose phone or email on the wrong surface. This change resolves that privacy risk before contact, chat, notifications and backend access rules become part of the MVP production path.

## What Changes

- Choose private, per-chat contact sharing as the canonical product behavior.
- Remove public contact reveal from pet detail/public pet-card behavior; owner phone, email, address and precise location remain hidden outside authorized contexts.
- Store owner consent on the specific chat session where contact was shared or revoked.
- Show owner contact data only to chat participants authorized by that chat-level consent.
- Record contact sharing and revocation as auditable in-app chat/system events without embedding phone or email in notification previews.
- Ensure revocation hides contact immediately in chat, detail and notification-driven entry points.
- **BREAKING**: `isContactRevealedToAll` and any public post-level reveal behavior are retired in favor of chat-scoped contact consent.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `contact-privacy`: Contact visibility becomes chat-scoped only; public pet surfaces must never use local/public reveal state to show direct contact data.
- `private-chat`: Chat sessions become the consent boundary for sharing and revoking owner contact details, including audit/system events.
- `backend-access-rules`: Firestore rules must authorize contact sharing, revocation and restricted contact reads by chat participant role.
- `backend-data-model`: The canonical data model must retire public reveal state and represent chat-scoped contact consent with timestamps/audit metadata.
- `notifications`: Notification records and push payloads must never include phone, email, address, precise coordinates or full contact values when contact is shared or revoked.

## Impact

- Affected Android code includes Room entities/DAO migrations, remote Firestore mappers, repositories, `PetViewModel`, pet detail UI, chat detail UI, chat list UI and notification rendering.
- Affected backend surfaces include Firestore `chatSessions`, `chatMessages`/system events, `notifications`, security rules and related tests.
- Privacy and security impact is high: phone, email, address and precise location remain sensitive, access must be authenticated, and the owner controls each chat-specific disclosure.
- Existing users or demo data with `isContactRevealedToAll = true` must migrate to hidden contact by default; no public contact reveal is preserved.
- Rollback strategy: re-disable the chat contact controls and render all direct contact fields hidden while preserving chat messages and non-sensitive notification records.
- Applicable guardrails: no direct contact exposure without explicit consent, no hardcoded ownership ids for authorization, no sensitive data in push notification payloads, and no backend contact access without server-enforced rules.
