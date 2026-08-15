## MODIFIED Requirements

### Requirement: Sighting Delivery To Owner
The system SHALL make each sighting visible to the post owner and the reporter, SHALL deny it to unrelated users, and SHALL expose the post owner's notification target as the corresponding sighting detail destination.

#### Scenario: Owner receives sighting
- **GIVEN** user B submits a sighting for user A's post
- **WHEN** user A opens the notification target for that sighting
- **THEN** the app opens the sighting detail screen using the notification's `sightingId`
- **AND** user A can read the authorized sighting details

#### Scenario: Unrelated user cannot read sighting
- **GIVEN** user C is neither the owner nor reporter of a sighting
- **WHEN** user C attempts to read the sighting
- **THEN** the backend denies access

#### Scenario: Invalid owner notification target is controlled
- **GIVEN** a sighting notification for user A has no non-blank `sightingId`
- **WHEN** user A selects the notification
- **THEN** the app does not crash, does not open Chat, and does not resolve a replacement `chatId`
