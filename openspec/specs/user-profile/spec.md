# user-profile Specification

## Purpose
Define authenticated user profile persistence and access control.

## Requirements
### Requirement: Authenticated User Profile
The app SHALL create and load a user profile document tied to the authenticated Firebase `uid`.

#### Scenario: Profile is created after first authentication
- **WHEN** a user authenticates and no `users/{uid}` profile exists
- **THEN** the app creates `users/{uid}` using the authenticated Firebase `uid` as the document id

#### Scenario: Existing profile is loaded
- **WHEN** a user authenticates and `users/{uid}` exists
- **THEN** the app loads that profile as the active user profile

### Requirement: User Profile Access Control
Firestore rules SHALL allow a user to read and modify only their own `users/{uid}` profile document.

#### Scenario: User reads own profile
- **WHEN** an authenticated user reads `users/{uid}` where `uid` equals `request.auth.uid`
- **THEN** Firestore allows the read

#### Scenario: User reads another profile
- **WHEN** an authenticated user reads `users/{otherUid}` where `otherUid` differs from `request.auth.uid`
- **THEN** Firestore denies the read

#### Scenario: User updates another profile
- **WHEN** an authenticated user updates `users/{otherUid}` where `otherUid` differs from `request.auth.uid`
- **THEN** Firestore denies the write

### Requirement: Profile Data Source
The app SHALL treat Firestore `users/{uid}` as the source of truth for authenticated user profile fields.

#### Scenario: Cached profile conflicts with Firestore
- **WHEN** a locally cached profile differs from Firestore for the authenticated `uid`
- **THEN** the app resolves the active profile from Firestore, not from the stale cache

#### Scenario: User signs out
- **WHEN** the user signs out
- **THEN** cached profile data is no longer exposed as an authenticated profile
