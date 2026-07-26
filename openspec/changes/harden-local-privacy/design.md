## Context

FindYourPet is an Android native prototype using Kotlin, Jetpack Compose, Room, and local seeded data. The current app already displays or stores sensitive demo fields such as owner names, phone numbers, email addresses, addresses, coordinates, photos, private chat text, and sightings. It also has Android backup enabled through sample rules, while encryption and production authorization are not implemented yet.

This change establishes a local privacy baseline before adding real authentication, backend sync, camera, gallery, GPS, push notifications, or production release workflows. The scope is Android client configuration, local data/copy guardrails, and validation. No backend behavior is introduced.

## Goals / Non-Goals

**Goals:**
- Prevent Android cloud backup or device-transfer rules from copying Room databases and sensitive local files.
- Keep local data handling honest: sensitive fields are either avoided, minimized, or clearly treated as local demo/cache data.
- Remove unsupported privacy/encryption claims from user-facing text and source strings.
- Keep Android permissions limited to currently implemented flows.
- Add checks that make privacy regressions visible in tests or manual validation.

**Non-Goals:**
- Implement end-to-end encryption or SQLCipher in this stage.
- Add production authentication, authorization, backend sync, or server-side privacy rules.
- Replace simulated camera, gallery, GPS, push notifications, or chat with real integrations.
- Redesign the full contact-sharing product flow; this change only prevents unsafe local exposure and misleading claims.

## Decisions

1. Disable backup at the application boundary, and keep explicit exclusion files as defense in depth.
   - Decision: set `android:allowBackup` to `false` for the app while also making `backup_rules.xml` and `data_extraction_rules.xml` exclude databases, shared preferences, files, cache, and external app-owned files that may contain sensitive data.
   - Rationale: the safest local baseline is to prevent Android from copying sensitive demo data until the app has a production data-retention model.
   - Alternative considered: keep backup enabled with only path exclusions. This is weaker because future local files or databases can be missed.

2. Do not introduce encryption in this stage unless it is implemented end to end.
   - Decision: document local storage as not encrypted beyond platform defaults, remove copy that says or implies encryption, and leave a future change to decide SQLCipher/EncryptedSharedPreferences.
   - Rationale: privacy copy must match the actual code. A false encryption claim is worse than a conservative local-only claim.
   - Alternative considered: add an encryption dependency now. This increases migration and testing surface before auth/backend boundaries are known.

3. Treat sensitive contact and location fields as hidden by default in public UI.
   - Decision: public pet details must not show phone, email, address, or precise coordinates unless a local explicit reveal/contact-sharing state says they can be shown.
   - Rationale: this preserves the current privacy direction while preventing accidental exposure through public cards or seeded data.
   - Alternative considered: remove contact fields entirely from local demo data. That would reduce risk but break current contact-sharing screens and tests.

4. Keep declared permissions aligned with real implemented flows.
   - Decision: the manifest must only declare permissions used by a working flow in this app stage. Camera, precise/coarse location, notification runtime helpers, and similar permissions stay out until their real flows are added.
   - Rationale: unused sensitive permissions reduce user trust and create unnecessary store-review/privacy obligations.
   - Alternative considered: predeclare future permissions. This is rejected because the app still uses simulated photo/location behavior.

5. Add static guardrails before broader product work.
   - Decision: extend or add tests that inspect manifest permissions, backup/data extraction rules, and unsupported privacy/encryption/realtime claims.
   - Rationale: these checks are cheap, deterministic, and catch the most likely regressions during future feature work.
   - Alternative considered: rely on manual review only. Manual review remains required, but static checks reduce repeated mistakes.

## Risks / Trade-offs

- [Risk] Disabling Android backup may surprise testers who expect local demo data to survive reinstall or device migration. -> Mitigation: document this as intentional until production retention and account sync exist.
- [Risk] Removing privacy/encryption claims can make the app sound less polished. -> Mitigation: use accurate local-demo language and avoid overpromising before the implementation exists.
- [Risk] Backup exclusion XML differs by Android API level. -> Mitigation: cover both `fullBackupContent` and `dataExtractionRules`, and validate the XML files directly.
- [Risk] Contact fields remain in seeded Room data for demo flows. -> Mitigation: require hidden-by-default UI, explicit reveal state, and no Android backup of local databases.
- [Risk] Future camera/location work may need permissions removed here. -> Mitigation: reintroduce permissions only in the dedicated feature changes with runtime UX and validation.

## Migration Plan

1. Update Android manifest backup settings and backup/data extraction XML.
2. Audit current source text for unsupported encryption/privacy/realtime claims and replace them with accurate local-demo wording.
3. Audit current permissions and keep only permissions required by implemented flows.
4. Add or update static tests for manifest permissions, backup rules, data extraction rules, and unsupported privacy copy.
5. Run debug unit tests and, where feasible, a debug build.
6. Roll back by restoring manifest/XML/copy changes if the app cannot launch, while keeping an explicit follow-up task for the privacy gap.

## Open Questions

- Should the next production stage encrypt Room with SQLCipher, move sensitive data out of local storage, or wait until backend/auth defines the final local cache model?
- Which exact local fields should survive app restart in the MVP once backend sync exists?
- Should demo seed contact data be anonymized further, or is hidden-by-default UI plus backup prevention enough for this stage?
