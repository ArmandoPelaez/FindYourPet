## 1. Additional-details field

- [x] 1.1 Inspect the existing `recognitionDetails` field and reuse the current `AppFormTypography`, `FormFieldLabel`, `FormFieldPlaceholder`, `AppShapes` and `AppSpacing` tokens.
- [x] 1.2 Change the visible label to `Descripcion adicional` and add the placeholder `Contanos cómo reconocerla...` without changing the existing field state or persistence path.
- [x] 1.3 Keep the field multiline with the established form-field height and enforce a maximum of 500 characters in the screen input handler.
- [x] 1.4 Add a discreet `actual/500` supporting counter that updates as the user types and remains readable in Light Theme and Dark Theme.

## 2. Persistence and presentation tests

- [x] 2.1 Update `CreatePetPostFormStaticTest` and any affected presentation assertions for the new label, placeholder, multiline configuration, counter and 500-character limit.
- [x] 2.2 Preserve and test the existing mapping from `recognitionDetails` to `features`, including the empty-value fallback and the independence of `characteristics` and `particularMarks`.
- [x] 2.3 Update `CreatePetPostScreenScreenshotTest` or the existing screenshot references so compact/tall and Light/Dark form states show the new field without clipping or overlap.

## 3. Validation

- [x] 3.1 Run `openspec validate "convert-additional-details-to-multiline" --strict`.
- [x] 3.2 Run `openspec instructions apply --change "convert-additional-details-to-multiline" --json` and confirm all tasks are complete.
- [x] 3.3 Run `.\gradlew.bat testDebugUnitTest` and `.\gradlew.bat assembleDebug`.
- [x] 3.4 Manually review `CreatePetPostScreen` on compact and tall phone viewports in Light Theme and Dark Theme, confirming the label, placeholder, counter, multiline editing, 500-character cap, scroll behavior and unchanged publish flow.
- [x] 3.5 Review `git diff --check` and confirm no ViewModel, repository, backend, permission or unrelated screen changes were introduced.
