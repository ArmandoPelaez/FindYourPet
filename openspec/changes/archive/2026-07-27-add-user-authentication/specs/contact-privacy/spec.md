## ADDED Requirements

### Requirement: Authenticated Contact Sharing Control
The app SHALL allow only the authenticated owner of a pet post to change contact-sharing state for that post or its chat session.

#### Scenario: Owner shares contact
- **WHEN** the authenticated user's Firebase `uid` equals the post `ownerId` and they enable contact sharing
- **THEN** Firestore allows the contact-sharing update

#### Scenario: Non-owner shares contact
- **WHEN** an authenticated user's Firebase `uid` differs from the post `ownerId` and they attempt to enable contact sharing
- **THEN** Firestore denies the update

### Requirement: Contact Data Remains Hidden By Default
The app SHALL keep owner phone, email, address, and precise location hidden unless authenticated contact-sharing rules allow disclosure.

#### Scenario: Contact sharing is disabled
- **WHEN** a post or chat has no owner-approved contact-sharing state
- **THEN** the UI hides phone, email, address, and precise location from non-owners

#### Scenario: Contact sharing is enabled for an authorized participant
- **WHEN** the owner has enabled contact sharing for an authenticated participant or allowed surface
- **THEN** the UI may show the approved contact fields for that authorized context

### Requirement: Contact Privacy Uses Backend Rules
Contact-sharing behavior SHALL NOT rely only on local UI state for production authorization.

#### Scenario: Local UI attempts unauthorized reveal
- **WHEN** a non-owner manipulates local state to reveal contact information
- **THEN** Firestore rules still deny unauthorized contact field writes and restricted reads
