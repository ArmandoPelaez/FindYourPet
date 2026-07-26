## 1. Android Backup And Local Storage

- [x] 1.1 Set `android:allowBackup` to `false` in `app/src/main/AndroidManifest.xml`.
- [x] 1.2 Replace sample `backup_rules.xml` content with explicit exclusions for databases, shared preferences, files, cache, and app-owned external files.
- [x] 1.3 Replace sample `data_extraction_rules.xml` content with cloud backup and device-transfer exclusions for the same sensitive domains.
- [x] 1.4 Add or update local-storage documentation/source notes that identify sensitive local fields and clarify that app-level encryption is not implemented in this stage.

## 2. Privacy Copy And Sensitive Data Exposure

- [x] 2.1 Audit Kotlin and XML user-facing text for unsupported privacy, encryption, realtime, or authorization claims.
- [x] 2.2 Replace misleading privacy/encryption claims with accurate local-demo wording where needed.
- [x] 2.3 Verify public pet detail, pet creation, profile, chat, and sighting screens do not expose phone, email, address, or precise coordinates without explicit contact-reveal state.
- [x] 2.4 Ensure notification/chat preview text does not include phone, email, exact coordinates, or full private-note content outside protected in-app surfaces.

## 3. Permission Surface

- [x] 3.1 Confirm the manifest declares only permissions required by implemented flows, currently `android.permission.INTERNET`.
- [x] 3.2 Remove any camera, location, contacts, storage, microphone, SMS, phone, or notification permission declarations if present before their real feature flow exists.
- [x] 3.3 Confirm simulated photo and location flows do not request runtime permissions.

## 4. Tests And Static Guardrails

- [x] 4.1 Extend static guardrail tests to assert `android:allowBackup="false"`.
- [x] 4.2 Add static tests that parse `backup_rules.xml` and `data_extraction_rules.xml` for sensitive-domain exclusions.
- [x] 4.3 Keep or extend manifest permission tests so only implemented permissions are allowed.
- [x] 4.4 Keep or extend source-text tests rejecting mojibake and unsupported encryption, privacy, realtime, or authorization claims.
- [x] 4.5 Update contact/privacy UI tests if copy changes affect protected contact card behavior.

## 5. Validation And Closure

- [x] 5.1 Run `./gradlew.bat testDebugUnitTest` and fix regressions.
- [x] 5.2 Run `./gradlew.bat assembleDebug` when the unit suite is passing.
- [x] 5.3 Manually review manifest, backup rules, data extraction rules, and privacy-sensitive screens.
- [x] 5.4 Document validation results and any remaining encryption/local-data follow-up before closing the change.
