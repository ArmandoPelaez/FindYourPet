## 1. Remove the Share implementation

- [x] 1.1 Remove the Share `AppButton`, its Android chooser callback, and Share-specific state from `HomeScreen.kt` while preserving the sighting action and existing card layout.
- [x] 1.2 Remove `buildPetPostShareText` and any imports that become unused; confirm no production Kotlin source still contains the post Share control or `ACTION_SEND` flow.

## 2. Update presentation coverage

- [x] 2.1 Update `HomeFeedPresentationTest.kt` to remove obsolete Share visibility/payload expectations and add assertions that the Share label/content description and share implementation are absent.
- [x] 2.2 Update `HomeFeedPresentationScreenshotTest.kt` so compact/tall and Light/Dark scenarios validate the remaining card content without requiring Share.
- [x] 2.3 Keep or add coverage proving the existing sighting action remains available or hidden according to its current eligibility rules.

## 3. Validate the change

- [x] 3.1 Run `openspec validate "remove-share-button" --strict` and confirm all task/spec requirements are satisfied.
- [x] 3.2 Run `openspec instructions apply --change "remove-share-button" --json` and verify all tasks are complete.
- [x] 3.3 Run `.\gradlew.bat testDebugUnitTest` and `.\gradlew.bat assembleDebug`.
- [x] 3.4 Review the diff and run a focused search for `ACTION_SEND`, `createChooser`, `buildPetPostShareText`, Share icons, and visible Share labels to ensure no out-of-scope sharing code remains.
