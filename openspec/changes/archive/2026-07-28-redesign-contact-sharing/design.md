## Context

FindYourPet now has authentication, Firestore collections and initial rules, but contact privacy is still split across two states: `isContactRevealedToAll` on pet posts and `isContactSharedByOwner` on chat sessions. The public reveal flag is unsafe for the production direction because pet posts are shared feed documents; if direct contact fields travel with those documents, any signed-in reader or cached local copy can receive phone/email even when the UI hides them.

Firestore rules do not provide field-level filtering for a readable document. Therefore privacy cannot depend on storing owner phone, email or address on public `petPosts` and trusting screens not to render them. Contact disclosure must be modeled as a separate chat-scoped authorization record that is only read by the owner and the authorized reporter for that chat.

## Goals / Non-Goals

**Goals:**

- Make chat-scoped owner consent the only way direct contact data is shown to another user.
- Retire public/post-level contact reveal state from the canonical model, mappers, UI and rules.
- Keep public pet cards and pet detail screens free of phone, email, address and precise location for non-owners.
- Persist share/revoke state and audit metadata per chat session.
- Ensure revocation removes visible contact immediately from chat UI and cache-backed surfaces.
- Keep notification and push preview text generic, even for contact-share events.

**Non-Goals:**

- End-to-end encryption for chat messages or contact fields.
- A general user-blocking/reporting system.
- Expiring contact grants automatically by time.
- Sharing contact outside the existing owner/reporter chat flow.
- Adding Android contacts, phone-call or SMS permissions.

## Decisions

### Android client

Use the active chat session plus a chat contact grant as the UI authorization source. Pet detail and feed screens SHALL always render direct contact as hidden for non-owners and offer chat/reporting entry points instead of public reveal controls. Chat detail may render owner contact only when the signed-in user is a participant and the current chat contact grant is active.

Alternative considered: keep `isContactRevealedToAll` and patch screens to agree. Rejected because a global flag increases exposure and still leaves sensitive values in shared documents.

### Backend data model

Remove direct owner contact fields and `isContactRevealedToAll` from shared `petPosts`. Store owner contact values in the authenticated owner's profile/private backend data, and copy only the approved contact fields into a chat-scoped grant document or equivalent restricted chat child record when the owner explicitly shares contact in that chat. The grant stores `chatId`, `postId`, `ownerId`, `reporterId`, `sharedBy`, `sharedAt`, `revokedAt` when applicable, `isActive`, and the approved phone/email fields only while active.

Alternative considered: store phone/email on `chatSessions` gated by `isContactSharedByOwner`. This is simpler but risks stale sensitive fields remaining on a participant-readable session after revocation or mapper bugs. A separate grant document makes deletion/revocation and listener-driven UI hiding easier to validate.

### Backend authorization

Only the chat owner participant may create, update or revoke a contact grant. Only chat participants may read an active grant for their chat. Non-participants and the reporter cannot create, update or re-enable contact grants. Chat participant fields remain immutable.

Alternative considered: allow any participant to request or toggle contact state. Rejected because consent must belong to the owner whose contact data is being disclosed.

### Audit and notifications

Share/revoke actions create generic chat system events and notification records. These records may say that contact availability changed, but they SHALL NOT contain phone, email, address, exact coordinates or full contact values. Push payloads follow the same rule and deep-link to the chat instead of carrying sensitive values.

Alternative considered: send phone/email in the contact-share message for convenience. Rejected because messages and notifications are broader persistence/preview surfaces and cannot be reliably revoked.

### Local storage and migration

Room schema and cache mappers should remove `isContactRevealedToAll` from pet posts and add chat-scoped contact grant/cache support if offline display of granted contact is needed. Existing local or remote posts with `isContactRevealedToAll = true` migrate to hidden contact by default. Any cached contact grant is removed or marked inactive immediately when the backend revocation is observed.

Alternative considered: keep the old local column for backward compatibility. Rejected for product logic, though a migration can read old rows and drop the value during schema upgrade.

## Risks / Trade-offs

- [Risk] Existing remote `petPosts` documents may already contain `ownerPhone`, `ownerEmail` or `ownerAddress` → Mitigation: migration/backfill removes these fields or stops writing them before production use; rules/tests block future public contact writes.
- [Risk] Offline cache may briefly show previously shared contact after revocation → Mitigation: UI keys visibility from current grant state, listener updates delete inactive grants, and logout/private-cache clearing removes chat contact data.
- [Risk] Separate grant reads add implementation complexity → Mitigation: keep the grant shape small, scoped to one chat, and covered by mapper/repository/rules tests.
- [Risk] Owners may expect contact to appear on the pet card → Mitigation: copy and UI consistently explain that contact is shared only inside conversations.
- [Risk] Push notification providers can display payloads outside the app → Mitigation: payloads remain generic and only include notification id/type/target chat id.

## Migration Plan

1. Add/adjust Firestore rules for chat contact grants and remove allowances for post-level public contact reveal.
2. Update remote mappers so new pet posts do not write `isContactRevealedToAll`, phone, email or address into shared post documents.
3. Add Room migration and entities/DAO support for chat-scoped contact grant state if the UI caches grants locally.
4. Replace public reveal UI/actions with hidden public contact copy and chat-only owner share/revoke controls.
5. Write share/revoke through repository batch operations that update grant state, append generic system messages, and create generic notifications.
6. Add tests for mappers, ViewModel authorization, Firestore rules static checks, notification text, and Compose hidden/visible/revoked states.
7. Manually validate owner/reporter/non-participant flows before closing the change.

Rollback: disable the chat share/revoke control and treat all grants as inactive in UI while preserving chat sessions/messages. If needed, remove contact grant documents and keep all public contact hidden.

## Open Questions

None. This proposal adopts Option A: contact is shared only inside a specific chat.
