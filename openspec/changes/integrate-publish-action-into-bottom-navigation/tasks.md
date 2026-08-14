## 1. Contextual action contract

- [x] 1.1 Define the shared bottom-navigation contextual publish action state with label, enabled/busy presentation and callback fields using existing project types/tokens.
- [x] 1.2 Extend `BottomPrimaryActionBanner` with an optional contextual center action while preserving the regular circular `+ Publicar` fallback and the five existing destinations.
- [x] 1.3 Implement the responsive weighted center slot for `Publicar ficha`, keeping the fixed surface, safe-area handling, existing typography/colors/shapes/elevation and no additional navigation row.

## 2. Create-post integration

- [x] 2.1 Register the existing create-post submission callback, validity state and submitting state with the navigation shell, clearing the registration when the create-post route is left or disposed.
- [x] 2.2 Remove the duplicate in-form `Publicar ficha` button while retaining its exact validation and `PetViewModel.createNewPetPost` execution path through the registered action.
- [x] 2.3 Verify that leaving the create-post route restores the regular circular `+ Publicar` action and that Inicio, Perfil, Mensajes and Alertas retain their current callbacks.

## 3. Automated verification

- [x] 3.1 Update or add static/component tests for regular versus contextual center action rendering, disabled/enabled/busy states, single-row layout and the absence of the duplicate form button.
- [x] 3.2 Add or update create-post presentation tests/screenshots for compact and tall supported viewports, scroll visibility, Light Theme and Dark Theme.
- [x] 3.3 Verify the existing publication validation tests still cover missing name, photo and location and that the contextual action does not invoke duplicate submissions.

## 4. Final validation

- [x] 4.1 Run `openspec validate "integrate-publish-action-into-bottom-navigation" --strict`.
- [x] 4.2 Run `openspec instructions apply --change "integrate-publish-action-into-bottom-navigation" --json` and confirm all tasks are complete.
- [x] 4.3 Run `./gradlew.bat testDebugUnitTest`.
- [x] 4.4 Run `./gradlew.bat assembleDebug`.
- [x] 4.5 Review the diff for scope, hardcoded visual values, duplicate publication controls and regressions in the five navigation destinations.
- [x] 4.6 Complete the combined manual and automated validation matrix for compact/tall phone sizes in Light/Dark Theme: open create-post, verify fixed CTA visibility, disabled/enabled/busy states, publication callback coverage, no overlap/clipping, navigation to each secondary destination and restoration of `+ Publicar` after leaving the flow.

Evidence: manual `Small_Phone` and `Medium_Phone` layout/screenshot checks confirmed the fixed contextual CTA, disabled state, complete `Publicar ficha` label, no overlap and restoration of `+ Publicar`; Compose tests cover enabled/busy/click behavior and Light/Dark compact/tall presentation.
