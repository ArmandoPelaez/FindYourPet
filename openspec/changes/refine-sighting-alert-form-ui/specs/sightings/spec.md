## ADDED Requirements

### Requirement: Simplified Sighting Alert Form Presentation
The app SHALL present eligible sighting reports as a report-first form that removes duplicated lost-pet profile content while preserving existing sighting submission behavior, reporter eligibility, location handling and optional real photo evidence.

#### Scenario: Reporter opens compact sighting form
- **GIVEN** a signed-in user is eligible to report a sighting for another user's lost-pet post
- **WHEN** the user opens the sighting alert screen on a supported phone viewport
- **THEN** the main report form does not render the referenced pet media/status header or the duplicated "Reportando avistamiento de:" summary card before the report controls
- **AND** the first report controls prioritize optional evidence photo, sighting location, current-location action, additional details and the existing send alert action

#### Scenario: Optional sighting photo uses publication upload pattern
- **GIVEN** the user has not selected a sighting photo
- **WHEN** the optional photo section is rendered
- **THEN** the section is presented as a single upload surface with camera and gallery choices consistent with the lost-pet publication photo surface
- **AND** the form keeps the photo explicitly optional

#### Scenario: Selected sighting photo remains real media evidence
- **GIVEN** the user chooses a photo through the existing camera or gallery flow
- **WHEN** the sighting alert form displays the chosen photo
- **THEN** the upload surface shows the selected real media preview and keeps camera/gallery replacement actions available
- **AND** the form stores the selected media through the existing sighting photo state without introducing preset demo media or a new media source

#### Scenario: Sighting submission behavior is unchanged
- **GIVEN** an eligible reporter completes a valid sighting location and optional details
- **WHEN** the reporter sends the alert with or without optional photo evidence
- **THEN** the app submits the sighting through the existing validated submission path with the same `postId`, derived `ownerId`, `reporterId`, location, notes, optional photo and fan-out behavior

#### Scenario: Ineligible reporter remains blocked
- **GIVEN** the referenced lost-pet post belongs to the signed-in user
- **WHEN** the user reaches the sighting alert screen directly
- **THEN** the app keeps the existing self-report blocked state and does not show the simplified report form as an enabled submission path

#### Scenario: Responsive sighting layout remains stable
- **GIVEN** the app renders the sighting alert screen on supported compact, centered or expanded viewport sizes
- **WHEN** the simplified form is displayed and scrolled
- **THEN** no report text, upload controls, location controls or bottom submit action overlap or clip
- **AND** the removed referenced-pet media/header and summary card are absent from all layout variants
