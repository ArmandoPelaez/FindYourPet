## 1. Data Model And Persistence Cleanup

- [x] 1.1 Remove `ContactGrantEntity`, `contact_grants` DAO queries, Room entity registration, and local migration/table creation from `Entities.kt`, `PetDao.kt`, and `AppDatabase.kt`.
- [x] 1.2 Remove or migrate `isContactSharedByOwner` and owner phone/email/address fields from chat/post local models where their only purpose is app-managed contact disclosure.
- [x] 1.3 Update Room schema version/migration so legacy contact grant rows and reveal flags are deleted or ignored without breaking retained pet, sighting, chat, message, and notification data.
- [x] 1.4 Remove demo/seed owner phone, email, and address values used only to exercise contact reveal behavior.

## 2. Backend, Repository, And Rules

- [x] 2.1 Remove `CONTACT_GRANTS`, `OWNER_CONTACT_GRANT`, contact grant remote documents, mappers, and repository read/write/toggle APIs.
- [x] 2.2 Update chat session and notification remote/local mappers so they no longer persist or consume `isContactSharedByOwner`, contact grant ids, or `CONTACT_SHARED`.
- [x] 2.3 Update `firestore.rules` to deny contact grant reads/writes, direct contact fields, chat contact-sharing flags, and `CONTACT_SHARED` notification writes.
- [x] 2.4 Ensure post, sighting, chat, message, notification, auth profile, media, and location rules continue to allow valid non-contact-sharing flows.

## 3. Android UI And Copy

- [x] 3.1 Remove contact reveal cards, share/revoke contact controls, active grant banners, contact availability icons, and owner phone/email display from Compose screens/components.
- [x] 3.2 Update chat detail/list, pet detail, create post, profile, and notification copy to present in-app chat as the only app-mediated contact path.
- [x] 3.3 Add bounded chat privacy copy stating that voluntary sharing of phone, email, address, or similar data inside messages is the users' responsibility.
- [x] 3.4 Keep auth email visible only where needed for account/profile operation, not as owner/reporter contact data.

## 4. Documentation And Active Release Artifacts

- [x] 4.1 Update `README.md`, `docs/privacy-policy.md`, `public/privacy-policy.html`, and local privacy/storage docs to remove app-managed contact sharing language.
- [x] 4.2 Update `docs/google-play-permissions.md`, release validation docs, and validation guides so permissions refer only to Android/runtime permissions and backend authorization, not contact-sharing grants.
- [x] 4.3 Update `openspec/changes/prepare-production-release` proposal, design, tasks, and specs to remove references to contact sharing, contact controls, contact grants, `CONTACT_SHARED`, and chat/contact validation wording.
- [x] 4.4 Add release notes/evidence guidance for validating the new chat-only contact policy.

## 5. Tests And Guardrails

- [x] 5.1 Replace unit/static tests that expect contact grants, `ProtectedContactCard`, `isContactSharedByOwner`, owner phone/email/address mapping, or `CONTACT_SHARED` with negative tests for their absence.
- [x] 5.2 Add mapper/repository/ViewModel tests proving legacy contact fields and grants are ignored and no renderable contact disclosure state is produced.
- [x] 5.3 Add Firestore rules/static tests proving contact grant reads/writes, direct contact fields, chat share flags, and `CONTACT_SHARED` notification writes are denied.
- [x] 5.4 Add source/document guardrails that fail if release/public docs or user-facing strings reintroduce app-managed personal contact sharing claims.

## 6. Validation

- [x] 6.1 Run `.\gradlew.bat testDebugUnitTest` and document the result.
- [x] 6.2 Run `.\gradlew.bat assembleDebug` and document the result.
- [x] 6.3 Validate Firestore rules with emulator or documented non-production Firebase validation for post, sighting, chat, notification, profile, and denied contact-sharing paths.
- [x] 6.4 Manually validate owner/reporter chat flow on a device or emulator: report sighting, open chat from both roles, send messages, and verify no contact reveal controls or personal contact values appear.
- [x] 6.5 Manually review privacy policy, public policy HTML, Google Play permission docs, and `prepare-production-release` artifacts for obsolete contact-sharing language before closing the change.
