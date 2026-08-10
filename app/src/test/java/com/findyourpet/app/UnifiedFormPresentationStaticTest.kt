package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnifiedFormPresentationStaticTest {
  private val root: File = repoRoot()

  @Test
  fun createPostAndSightingShareStatelessPresentationComponents() {
    val createPost = source("app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt")
    val sighting = source("app/src/main/java/com/findyourpet/app/ui/screens/SightingAlertScreen.kt")
    val shared = source("app/src/main/java/com/findyourpet/app/ui/components/FormPresentationComponents.kt")

    assertTrue(createPost.contains("FormPhotoUploadSurface"))
    assertTrue(createPost.contains("FormSectionTitle"))
    assertTrue(sighting.contains("FormPhotoUploadSurface"))
    assertTrue(sighting.contains("FormSectionTitle"))
    assertTrue(shared.contains("MaterialTheme.colorScheme"))
    assertTrue(shared.contains("AppShapes.content"))
    assertTrue(shared.contains("AppSpacing.mediaHeight"))
    assertFalse(shared.contains("Color("))
    assertFalse(shared.contains(".dp"))
    assertFalse(shared.contains(".sp"))
  }

  @Test
  fun sharedMediaSurfaceKeepsDistinctFunctionalCallbacksAndExistingAlertTags() {
    val createPost = source("app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt")
    val sighting = source("app/src/main/java/com/findyourpet/app/ui/screens/SightingAlertScreen.kt")

    assertTrue(createPost.contains("onSurfaceClick = { showPhotoOptions = true }"))
    assertTrue(sighting.contains("onSurfaceClick = onPhotoSurfaceClick"))
    assertFalse(sighting.contains("onSurfaceClick = onGalleryClick"))
    assertTrue(sighting.contains("testTag = \"sighting-photo-upload-surface\""))
    assertTrue(sighting.contains("var showPhotoOptions by remember { mutableStateOf(false) }"))
    assertTrue(sighting.contains("SightingPhotoOptionsSheet"))
    assertTrue(sighting.contains("ModalBottomSheet"))
    assertTrue(sighting.contains("Elegir de la galería"))
    assertTrue(sighting.contains("Tomar foto con la cámara"))
    assertFalse(sighting.contains("SightingPhotoActions"))
    assertFalse(sighting.contains("sighting-gallery-action"))
    assertFalse(sighting.contains("sighting-camera-action"))
    assertTrue(sighting.contains("Toca para cambiar la foto"))
    assertTrue(sighting.contains("SightingSubmitActionBar"))
    assertTrue(sighting.contains("SightingSubmissionStatus.SUBMITTING"))
    assertTrue(sighting.contains("SightingSubmissionStatus.SUCCESS"))
  }

  @Test
  fun sightingAlertUsesCreatePostTopBarAndPrimaryActionPattern() {
    val sighting = source("app/src/main/java/com/findyourpet/app/ui/screens/SightingAlertScreen.kt")

    assertTrue(sighting.contains("title = { Text(text = \"Alerta de Avistamiento\") }"))
    assertFalse(sighting.contains("title = { Text(text = \"Alerta de Avistamiento\", color = MaterialTheme.colorScheme.error) }"))
    assertTrue(sighting.contains("variant = AppButtonVariant.Primary"))
    assertFalse(sighting.contains("variant = AppButtonVariant.Danger"))
    assertTrue(sighting.contains("color = MaterialTheme.colorScheme.background"))
    assertTrue(sighting.contains("tonalElevation = AppElevation.subtle"))
    assertFalse(sighting.contains("AppSpacing.submitButtonHeight"))
  }

  @Test
  fun visualAlignmentDoesNotMoveBusinessOrPermissionContractsIntoSharedUi() {
    val shared = source("app/src/main/java/com/findyourpet/app/ui/components/FormPresentationComponents.kt")

    listOf(
      "PetViewModel",
      "submitSightingAlert",
      "createNewPetPost",
      "Manifest.permission",
      "LocationSource",
      "MediaSource"
    ).forEach { marker ->
      assertFalse("Shared presentation component must remain stateless: $marker", shared.contains(marker))
    }
  }

  private fun source(relativePath: String): String = File(root, relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
