# Firebase Auth And Firestore Setup

This change uses Firebase Authentication and Cloud Firestore on the Firebase Spark plan.

## Firebase Console

1. Create or select a Firebase project on the Spark plan.
2. Add an Android app with package name `com.findyourpet.app`.
3. Register the debug SHA-1 and SHA-256 fingerprints for Google Sign-In.
4. Register release SHA fingerprints before publishing a signed release build.
5. Enable Authentication providers:
   - Email/password
   - Google
6. Create a Cloud Firestore database.
7. Publish the repository `firestore.rules` rules before testing with real accounts.

## Local Android Config

1. Download `google-services.json` from the Firebase console.
2. Place it at `app/google-services.json`.
3. Do not commit it. The file is ignored by `.gitignore`.

The Android build applies the Google Services plugin only when `app/google-services.json` exists. This keeps local unit tests and CI able to run without project-specific Firebase configuration, while real auth builds use the Firebase config when present.

## Room And Firestore Boundary

Firestore is the source of truth for authenticated production profiles, pet posts, sightings, chats, and ownership decisions. Room may remain only for demo seed data or local cache behavior. A Room record must not grant production owner permissions, and seeded demo records must receive the signed-in Firebase `uid` before they become Firestore production records.
