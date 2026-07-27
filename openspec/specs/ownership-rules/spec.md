# ownership-rules Specification

## Purpose
Define Firebase uid based ownership and participant access for production records.

## Requirements
### Requirement: Owner Identity Uses Firebase Uid
The app SHALL use Firebase `uid` values for production ownership decisions.

#### Scenario: Owner views own post
- **WHEN** an authenticated user's Firebase `uid` equals a post's `ownerId`
- **THEN** the app may display owner-only controls for that post

#### Scenario: Non-owner views a post
- **WHEN** an authenticated user's Firebase `uid` differs from a post's `ownerId`
- **THEN** the app hides owner-only controls for that post

### Requirement: No Hardcoded Owner Permissions
The app SHALL NOT grant owner privileges based on hardcoded strings, id prefixes, demo owner ids, or local-only user ids.

#### Scenario: Source is scanned for hardcoded owner grants
- **WHEN** the validation suite scans Kotlin sources
- **THEN** it fails if owner privileges depend on values such as `user_1`, `owner_1`, or `id.startsWith("owner")`

### Requirement: Owner-Only Pet Post Writes
Firestore rules SHALL allow pet post creation, update, close/reopen, and deletion only for the authenticated owner.

#### Scenario: Owner creates post
- **WHEN** an authenticated user creates a pet post with `ownerId` equal to `request.auth.uid`
- **THEN** Firestore allows the create

#### Scenario: User creates post for another owner
- **WHEN** an authenticated user creates a pet post with `ownerId` different from `request.auth.uid`
- **THEN** Firestore denies the create

#### Scenario: Owner updates post without changing owner
- **WHEN** the existing post `ownerId` equals `request.auth.uid` and the update keeps the same `ownerId`
- **THEN** Firestore allows the update

#### Scenario: Owner reassignment is attempted
- **WHEN** an update changes an existing post's `ownerId`
- **THEN** Firestore denies the update

#### Scenario: Non-owner closes post
- **WHEN** an authenticated user whose `uid` differs from the post `ownerId` attempts to close or edit the post
- **THEN** Firestore denies the write

### Requirement: Participant-Only Private Records
Firestore rules SHALL restrict private sightings, chat sessions, and chat messages to authenticated participants.

#### Scenario: Chat participant reads session
- **WHEN** an authenticated user is the `ownerId` or `reporterId` of a chat session
- **THEN** Firestore allows the session read

#### Scenario: Non-participant reads session
- **WHEN** an authenticated user is neither the `ownerId` nor the `reporterId` of a chat session
- **THEN** Firestore denies the session read

#### Scenario: Message sender is not participant
- **WHEN** a user attempts to create a chat message in a session where they are not a participant
- **THEN** Firestore denies the create
