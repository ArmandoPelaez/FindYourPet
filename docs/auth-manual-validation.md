# Auth Manual Validation

Use a non-production Firebase project on the Spark plan.

## Setup

- Place the project `google-services.json` at `app/google-services.json`.
- Set `firebase_web_client_id` in `app/src/main/res/values/strings.xml` to the Web client id used for Google Sign-In.
- Enable Email/password and Google providers in Firebase Authentication.
- Publish `firestore.rules` to the test Firebase project.

## Checklist

- Launch the app with no signed-in user and verify the auth screen appears before pet data.
- Create an account with email/password and verify the app opens authenticated content.
- Sign out from Profile and verify the auth screen returns and stale profile data is not displayed.
- Sign back in with the same email/password account and verify the profile is loaded from `users/{uid}`.
- Sign in with Google and verify Firebase Authentication shows the Google provider on the account.
- Create a pet post and verify Firestore stores `petPosts/{postId}.ownerId` equal to the signed-in Firebase `uid`.
- Open the same post as a different user and verify edit/close/contact-sharing controls are not shown.
- Attempt a non-owner write directly against Firestore and verify rules deny it.
- Open a chat as owner and reporter and verify only participants can read/send messages.
- Toggle contact sharing as owner and verify the update succeeds.
- Attempt contact sharing as non-owner and verify rules deny it.
- Confirm Room seed posts remain demo/cache records and do not give owner controls to a signed-in user unless imported with the signed-in `uid`.
