## 1. Release Build Configuration

- [x] 1.1 Enable release minification in `app/build.gradle.kts` and keep optimized default ProGuard/R8 rules plus `app/proguard-rules.pro`.
- [ ] 1.2 Review release shrinker impact and add only evidence-backed keep rules for Firebase, Room, Cloudinary, Compose or app models that fail in release.
- [x] 1.3 Add or document a release signing preflight that verifies keystore path, store password, key alias and key password without storing secrets in the repo.
- [ ] 1.4 Generate a signed release artifact with `.\gradlew.bat assembleRelease` or `.\gradlew.bat bundleRelease` and record artifact path, versionCode and versionName.

## 2. Crash Reporting And Monitoring

- [x] 2.1 Add Firebase Crashlytics plugin/dependency using Gradle Kotlin DSL and existing Firebase configuration patterns.
- [x] 2.2 Configure release crash reporting so mapping files or symbols are uploaded when the release artifact is built from an authorized environment.
- [x] 2.3 Add a small crash reporting wrapper or usage convention that blocks phone, email, address, precise coordinates, private message bodies, full sighting notes, photo URLs and secrets from custom keys/logs.
- [ ] 2.4 Validate in a non-production Firebase project that a controlled test crash or documented equivalent appears with versionCode/versionName and no sensitive metadata.

## 3. Test Coverage

- [x] 3.1 Add repository/mapper tests for publication, sighting, chat-only contact, profile and empty-state production data paths.
- [ ] 3.2 Add ViewModel tests for loading, success, empty, validation error and backend error states in the critical flows.
- [x] 3.3 Add static or unit tests that verify release minification, signing preflight behavior, Crashlytics configuration and sensitive-data logging guardrails.
- [x] 3.4 Run `.\gradlew.bat testDebugUnitTest` and document the result in release validation notes.

## 4. Accessibility Review

- [x] 4.1 Review primary Compose screens for accessible labels on icon buttons, image/media actions, permission actions, navigation, forms and chat controls.
- [x] 4.2 Add tests or static checks for critical missing content descriptions where practical.
- [ ] 4.3 Manually validate that primary flows remain reachable and understandable with basic Android accessibility settings and record findings.

## 5. Privacy Policy And Google Play Permissions

- [x] 5.1 Create an initial privacy policy document covering collected data, purpose, permissions, storage, third-party processors, retention, user choices and contact channel.
- [x] 5.2 Verify the privacy policy does not claim encryption, anonymity, realtime guarantees or privacy protections beyond implemented behavior.
- [x] 5.3 Build a permission inventory from `app/src/main/AndroidManifest.xml` mapping each permission to user-visible flow, trigger, data accessed, Play justification and validation evidence.
- [x] 5.4 Confirm manifest permissions, privacy policy and Google Play data safety/store declarations use the same data categories and permission rationale.
- [x] 5.5 Recheck backup and data extraction XML so sensitive local data remains excluded from backup/transfer.

## 6. Manual Release Validation And Rollback

- [ ] 6.1 Install the signed release build on a supported device or emulator and validate feed, detail, create post, report sighting, auth, chat-only contact, notification and profile flows.
- [ ] 6.2 Validate granted, denied, permanently denied and unavailable states for each runtime permission touched by the release.
- [ ] 6.3 Create release validation notes with build artifact, versionCode, versionName, test output, manual validation, accessibility review, permissions review, privacy policy location and Crashlytics status.
- [x] 6.4 Document rollback or corrective-release steps, including the previous valid build or required versionCode bump and the Crashlytics signal used to decide rollback.
- [ ] 6.5 Run final OpenSpec status/validation for `prepare-production-release` and resolve any remaining checklist gaps before archiving.
