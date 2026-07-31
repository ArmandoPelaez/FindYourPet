## 1. Home Feed UI Cleanup

- [x] 1.1 Inspect `HomeScreen.kt` for the current home card identity row, attribute blocks, and share summary generation.
- [x] 1.2 Remove the visible `Especie` attribute block from the reported-pet card on the home screen.
- [x] 1.3 Remove the duplicate visible `Raza` attribute block from the reported-pet card on the home screen.
- [x] 1.4 Preserve the compact purple breed chip when `post.breed` is not blank.
- [x] 1.5 Keep color, reported features, reported information, lost location, status pill, image loading, and in-card actions working as before.
- [x] 1.6 Remove `Especie` from home feed share text or any other home feed user-facing copy introduced by the card.

## 2. Project Text Audit

- [x] 2.1 Run `rg -n "Especie|especie" .` and review every match.
- [x] 2.2 Remove or update affected UI, tests, specs, and docs that reference `Especie` as visible home feed card content.
- [x] 2.3 Document in the implementation summary any retained internal `species` data-contract references that are intentionally out of scope for this presentation-only change.

## 3. Tests

- [x] 3.1 Update home feed Compose tests that currently assert the `Especie` attribute is displayed.
- [x] 3.2 Add or update assertions that the home feed card does not show labels `Especie` or duplicate `Raza` in the attribute section.
- [x] 3.3 Add or update assertions that the purple breed chip remains visible when breed data exists.
- [x] 3.4 Add or update share-content coverage so generated home feed share text does not include `Especie` or a species-only line.

## 4. Validation

- [x] 4.1 Run `openspec validate "remove-unwanted-information-from-main-screen" --strict`.
- [x] 4.2 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 4.3 Run `.\gradlew.bat assembleDebug`.
- [x] 4.4 Manually review the home screen on a compact phone viewport to confirm the card has no `Especie` block, no duplicate `Raza` block, and still shows the purple breed chip.
