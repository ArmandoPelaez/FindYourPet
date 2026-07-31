## 1. Home Card Structure

- [x] 1.1 Refactor `PetPostCard` into clear sections for image/status, identity, attributes, reported information, lost location/date, and actions.
- [x] 1.2 Keep `AsyncImage` loading from each post's existing `photoUri` and avoid adding the reference dog image as an asset, fallback, or hardcoded URI.
- [x] 1.3 Style the image area as the top visual focus and place the status pill so it is readable without hiding the pet subject.
- [x] 1.4 Update the identity area so pet name is primary and breed is supporting text or a compact chip.

## 2. Information Presentation

- [x] 2.1 Present existing attributes such as color, species, breed, and characteristics in compact, scannable sections.
- [x] 2.2 Omit unavailable mockup-only attributes such as age or gender unless the current post model already provides them.
- [x] 2.3 Move reported information into a distinct readable panel with stable spacing and wrapping.
- [x] 2.4 Group lost location and formatted date near the lower part of the card without exposing exact coordinates.

## 3. In-Card Controls

- [x] 3.1 Keep `Lo he visto` as the primary action when the post is not reunited and `OwnershipPolicy.canReportSighting` allows reporting.
- [x] 3.2 Hide or disable the sighting action when the post is reunited or the current user cannot report a sighting.
- [x] 3.3 Add a secondary `Compartir` control only after defining an allowlisted share summary that excludes owner phone, owner email, exact coordinates, private messages, and hidden contact data.
- [x] 3.4 Ensure both action controls have content descriptions and touch targets appropriate for mobile use.

## 4. Header And Spacing

- [x] 4.1 Keep home header branding, subtitle, and notifications visible without reintroducing profile/chat/create controls into the header.
- [x] 4.2 Adjust pager/card bottom padding so content and actions remain tappable above the bottom action banner and Android gesture navigation.
- [x] 4.3 Validate compact phone widths so text does not overlap or escape buttons, chips, or panels.

## 5. Tests And Validation

- [x] 5.1 Add or update Compose/Robolectric coverage for the card hierarchy and primary action visibility.
- [x] 5.2 Add or update tests/static checks confirming the reference dog image is not bundled or referenced by the app.
- [x] 5.3 Add coverage for privacy-safe share text if the share control is implemented.
- [x] 5.4 Run `gradlew.bat testDebugUnitTest` and address regressions.
- [x] 5.5 Run `gradlew.bat assembleDebug`.
- [x] 5.6 Manually validate the home feed with several posts on compact and typical mobile sizes.
- [x] 5.7 Manually verify `Lo he visto`, `Compartir`, notifications, and bottom banner controls remain reachable and route to the expected flows.
