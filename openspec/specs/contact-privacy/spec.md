# contact-privacy Specification

## Purpose
Define how FindYourPet limits exposure of owner contact details, precise location data, and sensitive notification text.
## Requirements
### Requirement: Public Pet UI Hides Direct Contact Data By Default
The app SHALL hide and avoid app-managed disclosure of owner phone, email, address, and precise personal contact location from public pet cards, pet detail surfaces, chat surfaces, profile-derived contact surfaces, and notification entry points. Public surfaces SHALL NOT show direct contact data based on local state, post-level reveal flags, chat-scoped grants, or profile fields.

#### Scenario: Public details hide contact fields
- **GIVEN** a pet post has owner phone and email values in legacy local or remote data
- **WHEN** the public pet detail screen is rendered for a non-owner
- **THEN** the phone and email values are not displayed

#### Scenario: Public reveal flag does not disclose contact
- **GIVEN** a pet post has a legacy public contact reveal flag enabled
- **WHEN** a non-owner opens the pet detail screen
- **THEN** the app hides owner phone, email, address and precise personal contact location

#### Scenario: Profile data is not reused as contact data
- **GIVEN** the authenticated profile contains an email or display name for account operation
- **WHEN** a post, chat, notification, or public detail surface renders
- **THEN** the app does not present that account field as a direct contact method

### Requirement: Contact Sharing Copy Matches Actual Behavior
The app SHALL describe contact between owner and reporter as in-app chat only and SHALL NOT imply that the app can reveal, authorize, revoke, unlock, or otherwise manage direct personal contact details.

#### Scenario: Copy describes chat-only contact
- **GIVEN** direct contact data is hidden on a public pet detail screen
- **WHEN** users read contact-related copy
- **THEN** the copy directs them to continue in the in-app chat without mentioning app-managed phone, email, or address sharing

#### Scenario: Voluntary personal data warning is bounded
- **GIVEN** users are in a private chat
- **WHEN** chat privacy copy is shown
- **THEN** it states that any voluntary exchange of phone, email, address, or similar personal data inside messages is the users' responsibility and does not claim automatic app protection

#### Scenario: Retired reveal states are not shown
- **GIVEN** a legacy contact grant or reveal flag exists in local or remote data
- **WHEN** the related screen renders
- **THEN** visible copy does not say contact is available, revoked, unlocked, shared, or hidden behind a grant

### Requirement: Sensitive Contact Data Is Not Sent In Notifications
The app SHALL NOT include phone, email, address, precise coordinates, direct personal contact values, or full private-message content in local notification text, backend notification records, or push notification payloads. The app SHALL NOT emit contact-sharing notifications because the contact-sharing flow is retired.

#### Scenario: Sighting notification avoids precise sensitive data
- **GIVEN** a sighting includes a reporter name, location name, latitude, longitude, and notes
- **WHEN** notification text is generated
- **THEN** the notification avoids phone, email, address, exact coordinates, full private note content, and direct personal contact values

#### Scenario: Chat notification avoids message and contact values
- **GIVEN** a participant sends a private chat message
- **WHEN** the recipient notification or push payload is created
- **THEN** it uses generic chat text and does not expose phone, email, address, exact coordinates, full message content, or other user-entered contact values

#### Scenario: Contact-sharing notification is not created
- **GIVEN** legacy code or data attempts to represent a contact-sharing state change
- **WHEN** notification generation runs
- **THEN** no `CONTACT_SHARED` notification is created or displayed

### Requirement: Authenticated Contact Sharing Control
The app SHALL NOT allow any authenticated user, including the post owner, to create, update, revoke, or view an app-managed contact-sharing grant for a chat.

#### Scenario: Owner attempts to share contact through retired control
- **GIVEN** the authenticated user's Firebase `uid` equals the chat `ownerId`
- **WHEN** the app renders the chat or pet detail actions
- **THEN** no control exists to enable owner phone, email, address, or personal contact sharing

#### Scenario: Client attempts contact grant write
- **WHEN** any authenticated client attempts to create or update a contact-sharing grant
- **THEN** Firestore denies the write and the app does not display the grant data

#### Scenario: Legacy contact grant exists
- **GIVEN** a chat has a legacy active contact grant
- **WHEN** either participant opens the chat
- **THEN** the app ignores the grant and does not show owner phone, email, address, or direct personal contact values

### Requirement: Contact Data Remains Hidden By Default
The app SHALL keep owner phone, email, address, and precise personal contact location hidden in all app-managed surfaces regardless of authentication, ownership, chat membership, legacy grants, or cached reveal state.

#### Scenario: Chat has no contact grant
- **WHEN** a post or chat has no contact grant
- **THEN** the UI uses chat-only communication and shows no phone, email, address, or direct personal contact values

#### Scenario: Chat has active legacy contact grant
- **GIVEN** the owner previously enabled contact sharing before this change
- **WHEN** the authenticated reporter opens the current chat
- **THEN** the UI does not show the approved contact fields and does not offer a replacement reveal action

#### Scenario: Owner views own post
- **GIVEN** an owner opens their own pet post or chat
- **WHEN** the app renders owner actions
- **THEN** the owner can manage allowed post/chat actions but cannot publish or grant direct personal contact values through the app

### Requirement: Contact Privacy Uses Backend Rules
Contact privacy SHALL be enforced by removing and denying app-managed contact-sharing backend paths, not by relying only on local UI state. Shared pet post and chat session reads SHALL NOT provide direct owner contact values to non-owners or reporters.

#### Scenario: Local UI attempts unauthorized reveal
- **WHEN** a non-owner manipulates local state to reveal contact information
- **THEN** Firestore rules and mappers still prevent contact grant reads and direct contact values from becoming rendered app state

#### Scenario: Shared pet post is read
- **GIVEN** a signed-in non-owner reads a shared pet post
- **WHEN** the backend returns the post document
- **THEN** the document does not expose owner phone, email, address or precise personal contact coordinates

#### Scenario: Contact grant collection is requested
- **WHEN** any client attempts to read a retired contact grant document
- **THEN** the backend denies the read or the client ignores the document without rendering personal contact values

### Requirement: Public Contact Reveal State Is Retired
The app SHALL NOT expose or honor any post-level or chat-level contact reveal state for owner phone, email, address or precise personal contact location.

#### Scenario: Legacy public reveal flag is present
- **GIVEN** a local or remote pet post contains `isContactRevealedToAll = true`
- **WHEN** the app renders a public pet card or pet detail surface for a non-owner
- **THEN** the app hides owner phone, email, address and precise personal contact location

#### Scenario: Legacy chat share flag is present
- **GIVEN** a local or remote chat session contains `isContactSharedByOwner = true`
- **WHEN** either participant opens chat list or chat detail
- **THEN** the app does not show "contact available" state and does not expose owner contact values

### Requirement: Contact Revocation Hides Shared Data Immediately
The app SHALL treat all legacy shared contact data as unavailable immediately after this change, regardless of whether an explicit revoke action has occurred.

#### Scenario: Previously shared contact is cached
- **GIVEN** local cache contains a previous contact grant for a chat
- **WHEN** the app starts or the chat screen observes local state
- **THEN** the app treats contact as unavailable and does not render cached phone, email, address, or personal contact values

#### Scenario: Remote grant still exists
- **GIVEN** a remote contact grant remains in Firestore from a previous build
- **WHEN** the updated app syncs the chat
- **THEN** the app ignores or deletes the local copy and continues to show only the chat conversation

