## ADDED Requirements

### Requirement: Public Contact Reveal State Is Retired
The app SHALL NOT expose or honor any post-level public contact reveal state for owner phone, email, address or precise location.

#### Scenario: Legacy public reveal flag is present
- **GIVEN** a local or remote pet post contains `isContactRevealedToAll = true`
- **WHEN** the app renders a public pet card or pet detail surface for a non-owner
- **THEN** the app hides owner phone, email, address and precise location

#### Scenario: User enters from a notification
- **GIVEN** a notification links to a pet post or chat
- **WHEN** the recipient opens the target without an active chat contact grant
- **THEN** owner phone, email, address and precise location remain hidden

### Requirement: Contact Revocation Hides Shared Data Immediately
The app SHALL hide previously shared owner contact data as soon as a chat contact grant is revoked or becomes inactive.

#### Scenario: Owner revokes contact in a chat
- **GIVEN** a reporter is viewing owner contact in an authorized chat
- **WHEN** the owner revokes contact sharing for that chat
- **THEN** the reporter's chat UI hides phone, email, address and precise location after the current grant state is observed

#### Scenario: Cached grant is inactive
- **GIVEN** local cache contains a previous contact grant for a chat
- **WHEN** the current backend grant is missing or inactive
- **THEN** the app treats contact as hidden and does not render cached phone or email values

## MODIFIED Requirements

### Requirement: Public Pet UI Hides Direct Contact Data By Default
The app SHALL hide owner phone, email, address, and precise location from public pet cards and pet detail surfaces. Public surfaces SHALL NOT show direct contact data based on local state or post-level reveal flags.

#### Scenario: Public details hide contact fields
- **GIVEN** a pet post has owner phone and email values in legacy local data
- **WHEN** the public pet detail screen is rendered for a non-owner
- **THEN** the phone and email values are not displayed

#### Scenario: Public reveal flag does not disclose contact
- **GIVEN** a pet post has a legacy public contact reveal flag enabled
- **WHEN** a non-owner opens the pet detail screen
- **THEN** the app still hides owner phone, email, address and precise location

#### Scenario: Address and coordinates are not exposed as direct contact
- **GIVEN** a pet or sighting contains an address, location name, latitude, or longitude
- **WHEN** a public-facing screen renders the data
- **THEN** it does not expose precise contact/location data as a substitute for direct contact permission

### Requirement: Contact Sharing Copy Matches Actual Behavior
The app SHALL describe contact sharing as a private chat-scoped owner consent flow and SHALL NOT imply that contact is public, globally revealed, encrypted, or visible outside the authorized chat.

#### Scenario: Copy describes chat-scoped sharing
- **GIVEN** direct contact data is hidden on a public pet detail screen
- **WHEN** users read contact-sharing copy
- **THEN** the copy states that direct contact can be shared only by the owner inside a conversation

#### Scenario: Hidden state explains limited exposure
- **GIVEN** contact data is hidden
- **WHEN** the protected contact component is rendered
- **THEN** the visible text explains that direct contact details are not shown in the public card

#### Scenario: Revoked state explains unavailability
- **GIVEN** a chat contact grant was revoked
- **WHEN** the chat contact area renders
- **THEN** the visible text does not show contact values and indicates that contact is no longer available in that conversation

### Requirement: Sensitive Contact Data Is Not Sent In Notifications
The app SHALL NOT include phone, email, address, precise coordinates, or full private-message content in local notification text, backend notification records, or push notification payloads.

#### Scenario: Sighting notification avoids precise sensitive data
- **GIVEN** a sighting includes a reporter name, location name, latitude, longitude, and notes
- **WHEN** notification text is generated
- **THEN** the notification avoids phone, email, address, exact coordinates, and full private note content

#### Scenario: Contact-sharing notification avoids direct contact values
- **GIVEN** contact sharing changes state
- **WHEN** a notification, push payload, or chat preview is generated
- **THEN** it does not expose phone or email values outside the protected in-app contact surface

#### Scenario: Revocation notification avoids previous contact values
- **GIVEN** contact sharing is revoked for a chat
- **WHEN** the recipient notification is created
- **THEN** the notification uses generic text and does not include the previously shared phone or email

### Requirement: Authenticated Contact Sharing Control
The app SHALL allow only the authenticated owner participant of a chat to create, update, or revoke contact sharing for that chat.

#### Scenario: Owner shares contact in a chat
- **WHEN** the authenticated user's Firebase `uid` equals the chat `ownerId` and they enable contact sharing for that chat
- **THEN** Firestore allows the chat-scoped contact-sharing update

#### Scenario: Non-owner shares contact
- **WHEN** an authenticated user's Firebase `uid` differs from the chat `ownerId` and they attempt to enable contact sharing
- **THEN** Firestore denies the update

#### Scenario: Owner revokes contact in a chat
- **GIVEN** contact sharing is active for a chat
- **WHEN** the authenticated chat owner revokes sharing
- **THEN** Firestore allows the revocation and the app stops showing contact values to the reporter

### Requirement: Contact Data Remains Hidden By Default
The app SHALL keep owner phone, email, address, and precise location hidden unless an authenticated active contact grant for the current chat allows disclosure to that chat participant.

#### Scenario: Contact sharing is disabled
- **WHEN** a post or chat has no owner-approved active chat contact grant
- **THEN** the UI hides phone, email, address, and precise location from non-owners

#### Scenario: Contact sharing is enabled for an authorized participant
- **WHEN** the owner has enabled contact sharing for the authenticated reporter in the current chat
- **THEN** the UI may show the approved contact fields only inside that chat context

#### Scenario: Contact sharing is enabled in another chat
- **GIVEN** the owner shared contact in chat A
- **WHEN** the same reporter or another user opens chat B without an active grant
- **THEN** the app hides owner contact data in chat B

### Requirement: Contact Privacy Uses Backend Rules
Contact-sharing behavior SHALL NOT rely only on local UI state for production authorization, and shared pet post reads SHALL NOT provide direct owner contact values to non-owners.

#### Scenario: Local UI attempts unauthorized reveal
- **WHEN** a non-owner manipulates local state to reveal contact information
- **THEN** Firestore rules still deny unauthorized contact grant writes and restricted reads

#### Scenario: Shared pet post is read
- **GIVEN** a signed-in non-owner reads a shared pet post
- **WHEN** the backend returns the post document
- **THEN** the document does not expose owner phone, email, address or precise contact coordinates
