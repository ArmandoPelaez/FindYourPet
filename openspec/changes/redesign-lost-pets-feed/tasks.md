## 1. Home Feed Card Redesign

- [x] 1.1 Inspect `HomeScreen.kt` for the current `PetPostCard`, `PetIdentitySection`, `PetAttributeGrid`, `InfoPill`, and `buildPetPostShareText` usage.
- [x] 1.2 Move the last-seen location row so it renders directly below the pet name with only `Icons.Outlined.LocationOn` and `post.lastSeenLocation`.
- [x] 1.3 Remove the separate `Ubicación en la que se perdió` title and its lower location row from the card body.
- [x] 1.4 Remove the breed chip from the pet identity row.
- [x] 1.5 Remove the `Color` and `Señas` attribute blocks from the home card.
- [x] 1.6 Preserve the existing photo, status chip, date display, horizontal pager, bottom action banner, report-sighting action, share action, and ownership gating behavior.

## 2. Presentation Reference Cleanup

- [x] 2.1 Update `buildPetPostShareText` so home-feed sharing does not include breed, color, signs/features, owner contact data, exact coordinates, private messages, or hidden contact data.
- [x] 2.2 Run `rg -n "Color|Señas|Raza|breed|Ubicación en la que se perdió|buildPetPostShareText" app/src/main app/src/test openspec docs` and review matches tied to home-feed presentation.
- [x] 2.3 Remove or update affected UI, tests, specs, and docs that still describe the deleted home-feed breed chip, color block, signs block, or titled location section.
- [x] 2.4 Leave storage/model/backend references intact when they are data-contract fields rather than home-feed presentation references.

## 3. Tests

- [x] 3.1 Update `HomeFeedPresentationTest` to assert that the breed chip, `Color` block, `Señas` block, and `Ubicación en la que se perdió` title are absent.
- [x] 3.2 Add or update assertions that the last-seen location is still displayed in the card after the pet name and remains readable on a compact phone viewport.
- [x] 3.3 Update share-text tests so the generated summary excludes breed, color, signs/features, direct owner contact, exact coordinates, private messages, and hidden contact data.
- [x] 3.4 Update static guardrail tests that currently expect the removed home-feed labels or helper references.

## 4. Validation

- [x] 4.1 Run `openspec validate "redesign-lost-pets-feed" --strict`.
- [x] 4.2 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 4.3 Run `.\gradlew.bat assembleDebug`.
- [x] 4.4 Manually review the home screen on a compact phone viewport to confirm the card keeps existing behavior while showing pet name, titleless last-seen location, image, status, date, and actions without the removed elements.
