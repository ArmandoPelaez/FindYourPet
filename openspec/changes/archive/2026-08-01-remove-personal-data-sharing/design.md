## Context

FindYourPet currently contains a privacy model where direct owner contact data can be shared inside a chat through app-managed contact grants. That model appears in Firestore rules, backend collection constants, Room entities and migrations, repository methods, ViewModel state, Compose surfaces, notification types, tests, privacy copy, and release documentation.

The new product rule is simpler and stricter: the app connects the owner and reporter only through the in-app private chat. The app must not request, store, reveal, toggle, grant, revoke, notify, or document any app-managed sharing of phone, email, address, or other personal contact details. If users voluntarily type personal data into chat messages, that is user-provided message content and is not an app-managed disclosure flow.

## Goals / Non-Goals

**Goals:**

- Remove all app-managed contact sharing behavior from Android UI, app state, local persistence, backend documents, Firestore rules, notifications, tests, and documentation.
- Keep participant-only chat as the only app-provided contact mechanism between owner and reporter.
- Ensure legacy `contactGrants`, `isContactSharedByOwner`, `CONTACT_SHARED`, owner phone/email/address fields, and reveal flags are ignored, denied, or removed.
- Update `prepare-production-release` artifacts and publication docs so no store-facing document claims the app shares contact data through permissions or grants.
- Preserve authentication, ownership, chat membership, camera/media/location flows, and release permission review where they remain tied to real app functionality.

**Non-Goals:**

- Do not scan, block, redact, or moderate arbitrary personal data that users voluntarily type inside chat messages.
- Do not remove Firebase Authentication email or profile identity fields required for account operation, as long as they are not surfaced as contact data.
- Do not remove camera, gallery/media, notification, or location permissions that still support implemented pet post or sighting flows.
- Do not change the ownership model, self-sighting rules, chat participant authorization, media upload, or sighting location capture except where they referenced contact sharing.
- Do not promise encryption, anonymity, realtime guarantees, or automatic protection for user-written personal data inside messages.

## Decisions

### Android client

- Remove contact-sharing UI rather than hiding it behind disabled states.
  - Rationale: the product no longer supports this path, and disabled controls would keep obsolete concepts visible.
  - Alternatives considered: keep the card with explanatory hidden-state copy. Rejected because it still trains users to expect an app-managed contact reveal flow.
- Replace contact copy with chat-only copy.
  - Rationale: users need one clear path: continue the conversation in app chat. Copy may say the app does not request or share phone, email, or address, and that voluntary sharing inside chat is the users' responsibility.
  - Alternatives considered: omit privacy copy entirely. Rejected because the previous UI mentioned contact sharing, so a short transition-safe statement prevents ambiguity.
- Keep account email internal to auth/profile flows.
  - Rationale: email/password auth and Firebase profile records can require an email, but that value must not be mapped into pet posts, grants, contact cards, chat headers, notifications, or public surfaces.
- Remove ViewModel and repository contact-grant state/actions.
  - Rationale: there should be no `activeContactGrant`, `toggleChatContactSharing`, "share contact", "revoke contact", or equivalent source of truth.

### Backend and rules

- Retire `chatSessions/{chatId}/contactGrants/{grantId}` as a production contract.
  - Rationale: the app should not create or read documents whose purpose is personal contact disclosure.
  - Alternatives considered: leave the collection readable for legacy users. Rejected because the new policy requires no app-managed sharing; legacy grants must be ignored or removed.
- Deny direct contact fields in production shared documents.
  - Rationale: Firestore rules must reject `ownerPhone`, `ownerEmail`, `ownerAddress`, `isContactRevealedToAll`, `isContactSharedByOwner`, and contact-grant fields in post/chat/contact paths.
- Remove `CONTACT_SHARED` as a notification type.
  - Rationale: contact availability changes no longer exist, and notification schemas should not preserve obsolete privacy events.

### Local storage and migration

- Drop or ignore `contact_grants` local cache and contact-sharing flags.
  - Rationale: local cache cannot imply access to data the backend no longer exposes. If a Room destructive migration is unacceptable for surrounding data, add a schema migration that deletes the obsolete table/columns and leaves pet, sighting, chat, and message records intact.
- Remove seeded/demo personal contact values where they only exist to demonstrate sharing.
  - Rationale: demo values can leak into tests, UI, docs, and screenshots, causing false confidence about a feature that no longer exists.
- Treat legacy backend data as unsupported for display.
  - Rationale: if old documents contain grants or contact fields, mappers and rules must omit them from app state and tests must assert they are not rendered.

### Documentation and release artifacts

- Update both main docs and active `prepare-production-release` artifacts.
  - Rationale: the release change currently references chat/contact validation, contact privacy guarantees, and publication documents. Those references must move to the new chat-only contact policy before release evidence is considered consistent.
- Keep permission documentation only for device/runtime permissions.
  - Rationale: "permission" should refer to Android permissions such as camera/media/location/notifications or backend authorization, not personal contact-sharing grants.

## Risks / Trade-offs

- [Risk] Legacy users or QA data contain contact grants that the new UI ignores -> Mitigation: delete local caches, deny/ignore remote grants, and document cleanup in validation notes.
- [Risk] Tests still assert the old contact-sharing model and give false failures -> Mitigation: replace those assertions with negative tests that prove no contact-sharing path remains.
- [Risk] Removing owner phone/email/address from models can require Room migration care -> Mitigation: choose a versioned migration that preserves non-contact records or document a controlled cache reset if acceptable for current release state.
- [Risk] Users can still type personal data into chat -> Mitigation: copy clearly states this is voluntary and outside app-managed disclosure; do not claim automatic redaction or moderation.
- [Risk] Store-facing docs drift from implementation -> Mitigation: include release tasks that update `prepare-production-release`, privacy policy, public HTML, README, and permission/release validation docs in the same implementation.

## Migration Plan

1. Remove contact-sharing UI/state/actions from Compose screens and ViewModels, replacing user-facing text with chat-only communication copy.
2. Remove repository, mapper, remote document, backend collection, and notification type support for contact grants and contact-sharing flags.
3. Remove or migrate Room `contact_grants` and contact-sharing columns/fields used only by the retired flow.
4. Harden Firestore rules and static tests so contact grants, direct contact fields, public reveal flags, and `CONTACT_SHARED` notification writes are denied.
5. Update tests to verify chat remains participant-only and no app-managed personal contact disclosure is available.
6. Update documentation, including `prepare-production-release`, privacy policy, public policy HTML, README, release validation notes, and Google Play permission docs.
7. Run debug unit tests, build validation, Firestore rules validation, and manual chat/privacy release checks.

Rollback should be treated as a product rollback, not a simple feature flag. Restoring contact sharing would require reintroducing UI, models, rules, docs, privacy policy language, store declarations, migration compatibility, and explicit approval of the older privacy model.

## Open Questions

None.
