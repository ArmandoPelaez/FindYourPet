## ADDED Requirements

### Requirement: Backend Changes Require Rules Validation
Changes that introduce or modify production backend access SHALL validate Firestore rules before using real user data.

#### Scenario: Backend rules are validated
- **GIVEN** a change touches pet posts, sightings, chats, notifications or user-owned backend documents
- **WHEN** the change is prepared for completion
- **THEN** Firestore rules validation is run in the emulator or a documented non-production Firebase project

### Requirement: Backend Changes Require Android Build And Tests
Changes that introduce backend repositories or sync state SHALL keep the Android debug build and relevant unit tests passing.

#### Scenario: Backend repository change is completed
- **GIVEN** backend-backed repositories, ViewModels or rules-sensitive mappers were changed
- **WHEN** the change is prepared for completion
- **THEN** the debug build and relevant local tests are run and their result is documented
