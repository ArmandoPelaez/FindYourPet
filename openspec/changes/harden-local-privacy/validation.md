# Validation

## Automated checks

- `.\gradlew.bat testDebugUnitTest`: passed.
- `.\gradlew.bat assembleDebug`: passed.

## Manual review

- Reviewed `app/src/main/AndroidManifest.xml`: `android:allowBackup` is `false`, backup/data extraction XML files are referenced, and only `android.permission.INTERNET` is declared.
- Reviewed `app/src/main/res/xml/backup_rules.xml`: databases, shared preferences, private files, root private storage, and app-owned external files are excluded.
- Reviewed `app/src/main/res/xml/data_extraction_rules.xml`: the same sensitive domains are excluded for both cloud backup and device transfer.
- Scanned main app sources for runtime permission requests and sensitive permission constants; no camera, location, contacts, storage, microphone, SMS, phone, media, or notification runtime request flow is present.
- Scanned main app sources for unsupported encryption, production privacy, realtime, verification, or authorization claims; no matches remain.
- Reviewed privacy-sensitive UI source paths for public contact/location exposure. Public pet details no longer display exact coordinates, contact values remain hidden until contact reveal, and chat/notification previews no longer include full message text, phone, email, or exact coordinates.

## Follow-up

- Decide in a later production-stage change whether Room data should become encrypted cache, backend-synced account data, or short-lived local state after authentication and backend access rules exist.
