package com.findyourpet.app

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.test.core.app.ApplicationProvider
import com.findyourpet.app.ui.screens.CreatePetPostScreen
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import com.findyourpet.app.ui.viewmodel.PetViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class CreatePetPostScreenScreenshotTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone, sdk = [36])
  fun createPostScreen_compactPhone_hasStableTopAndScrolledVisuals() {
    renderCreatePostScreen(width = 360, height = 640, darkTheme = false)

    assertTitlePrecedesPhotoUpload()
    composeTestRule.onNodeWithTag("create-post-photo-upload-surface").assertIsDisplayed()
    composeTestRule.onNodeWithText("Toca para agregar foto").assertIsDisplayed()
    composeTestRule.onNodeWithText("Datos de la mascota").assertIsDisplayed()
    composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Características").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Señas particulares").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Descripcion adicional").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("*").assertCountEquals(2)
    assertNoCurrentLocationAction()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/create-post-compact-top.png"
    )

    composeTestRule.onNodeWithText("Descripcion adicional").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Contanos cómo reconocerla...").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("0/500").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(VisualRootTag).performTouchInput { swipeUp() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("¿Dónde fue vista por última vez?").assertIsDisplayed()
    composeTestRule.onNodeWithText("Publicar ficha").assertIsDisplayed()
    assertNoCurrentLocationAction()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/create-post-compact-scrolled.png"
    )
  }

  @Test
  @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
  fun createPostScreen_tallPhone_hasStableTopAndScrolledVisuals() {
    renderCreatePostScreen(width = 411, height = 914, darkTheme = true)

    assertTitlePrecedesPhotoUpload()
    composeTestRule.onNodeWithTag("create-post-photo-upload-surface").assertIsDisplayed()
    composeTestRule.onNodeWithText("Toca para agregar foto").assertIsDisplayed()
    composeTestRule.onNodeWithText("Datos de la mascota").assertIsDisplayed()
    composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Características").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Señas particulares").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Descripcion adicional").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("*").assertCountEquals(2)
    assertNoCurrentLocationAction()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/create-post-tall-top.png"
    )

    composeTestRule.onNodeWithText("Descripcion adicional").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Contanos cómo reconocerla...").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("0/500").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(VisualRootTag).performTouchInput { swipeUp() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("¿Dónde fue vista por última vez?").assertIsDisplayed()
    composeTestRule.onNodeWithText("Publicar ficha").assertIsDisplayed()
    assertNoCurrentLocationAction()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/create-post-tall-scrolled.png"
    )
  }

  private fun renderCreatePostScreen(width: Int, height: Int, darkTheme: Boolean) {
    val viewModel = PetViewModel(ApplicationProvider.getApplicationContext<Application>())

    composeTestRule.setContent {
      MascotasPerdidasTheme(darkTheme = darkTheme) {
        Box(
          modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .testTag(VisualRootTag)
        ) {
          CreatePetPostScreen(
            viewModel = viewModel,
            onPostCreated = {}
          )
        }
      }
    }
  }

  private fun assertNoCurrentLocationAction() {
    composeTestRule.onAllNodesWithText("Usar ubicacion actual").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Ubicacion GPS capturada").assertCountEquals(0)
  }

  private fun assertTitlePrecedesPhotoUpload() {
    composeTestRule.onNodeWithText("Publicar mascota perdida").assertIsDisplayed()
    composeTestRule.onNodeWithTag("create-post-photo-upload-surface").assertIsDisplayed()
    val titleTop = composeTestRule
      .onNodeWithText("Publicar mascota perdida")
      .fetchSemanticsNode()
      .boundsInRoot
      .top
    val photoTop = composeTestRule
      .onNodeWithTag("create-post-photo-upload-surface")
      .fetchSemanticsNode()
      .boundsInRoot
      .top
    assertTrue("The integrated title must precede the photo upload surface", titleTop < photoTop)
  }

  private companion object {
    const val VisualRootTag = "create-post-visual-root"
  }
}
