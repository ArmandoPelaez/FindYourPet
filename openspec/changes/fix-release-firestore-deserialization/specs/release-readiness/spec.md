# release-readiness Specification Delta

## MODIFIED Requirements

### Requirement: Backend Changes Require Android Build And Tests

Changes that introduce or modify backend repositories, Firestore reflection-based DTOs, or sync state SHALL keep the Android debug build and relevant unit tests passing, and SHALL verify the minified release path when reflective deserialization is involved.

#### Scenario: Backend repository change is completed

- **GIVEN** backend-backed repositories, ViewModels, rules-sensitive mappers, or reflective Firestore DTOs were changed
- **WHEN** the change is prepared for completion
- **THEN** the debug build and relevant local tests are run and their result is documented

#### Scenario: Reflective Firestore DTO survives minification

- **GIVEN** a release code path calls Firestore `toObject` with an application DTO
- **WHEN** the minified release artifact is built and its R8 output is inspected
- **THEN** the DTO class, no-argument construction path, and required Firestore properties remain available for deserialization

#### Scenario: Existing profile document remains readable

- **GIVEN** a user profile document already exists with `uid`, `displayName`, `email`, `createdAt`, and `updatedAt` fields
- **WHEN** an authenticated user starts the minified release candidate
- **THEN** profile initialization succeeds without a no-argument-constructor or obfuscated-class deserialization error

## ADDED Requirements

### Requirement: Profile load failures use a controlled user message

The app SHALL keep Firestore/R8 implementation details out of the user-facing profile-load error while retaining diagnostic information for development or crash reporting.

#### Scenario: Profile deserialization fails

- **WHEN** profile loading fails because Firestore cannot deserialize the DTO
- **THEN** the UI shows a controlled Spanish message and does not expose class names, constructor details, R8 names, or raw Firebase exception text

#### Scenario: Profile deserialization succeeds

- **WHEN** Firestore returns a valid profile document in debug or minified release
- **THEN** no profile-load error is shown and the authenticated user can continue to Reportar/Publicar
