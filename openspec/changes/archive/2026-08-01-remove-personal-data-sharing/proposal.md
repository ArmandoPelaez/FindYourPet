## Why

FindYourPet debe dejar de gestionar la divulgacion de datos personales como mecanismo de contacto. A partir de este cambio, la app solo facilita el contacto mediante chat privado interno; cualquier intercambio posterior de telefono, email, direccion u otros datos dentro de una conversacion sera una decision voluntaria de las personas usuarias y bajo su propia responsabilidad.

## What Changes

- **BREAKING** Remove the app-managed personal data sharing flow: no contact grants, reveal toggles, owner contact cards, contact-sharing notifications, or "share/revoke contact" actions remain available.
- **BREAKING** Remove persisted contact-disclosure structures from every layer, including Firestore `contactGrants`, Room `contact_grants`, `isContactSharedByOwner`, direct contact fields used for sharing, repository APIs, ViewModel state, mappers, UI components, tests, and static guardrails that expect the old flow.
- Route all owner/reporter communication through participant-only in-app chat, with no app-provided phone, email, address, precise contact location, or equivalent personal-contact disclosure surface.
- Keep authentication email and device permissions that are required for real implemented flows, but prevent those values from being reused or presented as contact information.
- Update privacy policy, public policy HTML, Google Play/release validation notes, README, OpenSpec release deltas, and any `prepare-production-release` publication documents so they no longer mention contact sharing, contact grants, or app-managed disclosure of personal contact data.
- Add user-facing copy in chat/privacy surfaces that explains the app does not request or share personal contact data; if both parties choose to share such data in chat, they do so outside app-managed protection.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `contact-privacy`: replace chat-scoped contact-sharing guarantees with a chat-only contact model that forbids app-managed disclosure of phone, email, address, and equivalent personal data.
- `private-chat`: remove contact-sharing controls and events from chat behavior while keeping participant-only access and message sending.
- `backend-data-model`: retire contact grant collections/documents and direct contact-sharing fields from backend and local models.
- `backend-access-rules`: deny contact-grant writes/reads and prevent direct contact fields or contact-sharing flags from being accepted in production documents.
- `notifications`: remove contact-sharing notification types and keep notification previews free of personal contact data.
- `local-storage`: remove local contact-grant cache and any local storage of contact data whose only purpose is app-managed sharing.
- `release-readiness`: update release/privacy/publication documentation, including artifacts in `prepare-production-release`, so store-facing docs match the new chat-only contact policy.

## Impact

- Android code: entities, DAOs, Room migrations, remote document models/mappers, repository APIs, ViewModel state/actions, Compose screens/components for pet detail, create post, profile, chat list/detail, notifications, and seed/demo data.
- Backend/security: `firestore.rules`, backend collection constants, static rule tests, contact-grant authorization paths, notification type validation, and post/chat payload validation.
- Tests: unit/static tests that currently assert contact grants, `CONTACT_SHARED`, `isContactSharedByOwner`, owner phone/email/address mapping, protected contact card behavior, and release/privacy guardrails.
- Documentation: `README.md`, `docs/privacy-policy.md`, `public/privacy-policy.html`, `docs/google-play-permissions.md`, `docs/release-validation-prepare-production-release.md`, validation guides, and active OpenSpec release artifacts under `openspec/changes/prepare-production-release`.
- Privacy/security: reduces the app's responsibility for handling personal contact disclosure, but chat messages remain sensitive because users may voluntarily type personal data there.
- Users: existing contact-sharing state will stop being honored; chat remains the supported contact path. Legacy contact grant/cache data must be ignored or removed during migration.
- Rollback: restore the previous contact-sharing code/specs from version control only if product direction changes; data written after this change should not require contact grants, so rollback would need a compatibility review before release.
