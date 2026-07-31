## ADDED Requirements

### Requirement: Privacy Policy Preserves Contact Privacy Guarantees
The privacy policy and release documentation SHALL preserve the app requirements that direct contact data, precise location and private messages remain protected by authenticated, scoped flows.

#### Scenario: Contact data disclosure is described accurately
- **WHEN** the privacy policy is reviewed
- **THEN** it states that phone, email, address and precise location are not public by default and are disclosed only through implemented authorized flows

#### Scenario: Private message handling is described accurately
- **WHEN** the privacy policy is reviewed
- **THEN** it describes private messages and chat metadata without implying public visibility or unsupported encryption guarantees

#### Scenario: Notification privacy is reflected in release docs
- **WHEN** release documentation covers notifications and crash reporting
- **THEN** it states that notifications and crash metadata must not include phone, email, address, precise coordinates, photo URLs, full notes or private message bodies

### Requirement: Monitoring Does Not Weaken Contact Privacy
Crash reporting and release diagnostics SHALL NOT collect or transmit sensitive contact, location, media or private chat contents as diagnostic metadata.

#### Scenario: Crash keys avoid direct contact fields
- **WHEN** crash reporting custom keys and logs are reviewed
- **THEN** they do not include owner phone, email, address, precise coordinates or active contact grant contents

#### Scenario: Diagnostic errors use bounded context
- **WHEN** repository, ViewModel or UI errors are logged for diagnostics
- **THEN** logs identify bounded technical context such as flow name, state or document type without including full user-entered sensitive values
