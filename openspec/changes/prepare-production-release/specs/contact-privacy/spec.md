## ADDED Requirements

### Requirement: Privacy Policy Follows Chat-Only Contact Policy
The privacy policy and release documentation SHALL describe owner/reporter contact as in-app chat only and SHALL NOT claim app-managed disclosure of phone, email, address, or external contact availability.

#### Scenario: Contact path is described accurately
- **WHEN** the privacy policy is reviewed
- **THEN** it states that communication between owner and reporter happens through in-app chat and that the app does not request or share phone, email or address as contact data

#### Scenario: Private message handling is described accurately
- **WHEN** the privacy policy is reviewed
- **THEN** it describes private messages and chat metadata without implying public visibility, external contact availability or unsupported encryption guarantees

#### Scenario: Notification privacy is reflected in release docs
- **WHEN** release documentation covers notifications and crash reporting
- **THEN** it states that notifications and crash metadata must not include phone, email, address, precise coordinates, photo URLs, full notes or private message bodies

### Requirement: Monitoring Does Not Weaken Contact Privacy
Crash reporting and release diagnostics SHALL NOT collect or transmit sensitive contact, location, media or private chat contents as diagnostic metadata.

#### Scenario: Crash keys avoid direct contact and grant fields
- **WHEN** crash reporting custom keys and logs are reviewed
- **THEN** they do not include owner phone, email, address, precise coordinates or external contact availability state

#### Scenario: Diagnostic errors use bounded context
- **WHEN** repository, ViewModel or UI errors are logged for diagnostics
- **THEN** logs identify bounded technical context such as flow name, state or document type without including full user-entered sensitive values
