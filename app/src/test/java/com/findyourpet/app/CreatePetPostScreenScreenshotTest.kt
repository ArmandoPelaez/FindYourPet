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

    composeTestRule.onNodeWithText("Publicar Mascota Perdida").assertIsDisplayed()
    composeTestRule.onNodeWithTag("create-post-photo-upload-surface").assertIsDisplayed()
    composeTestRule.onNodeWithText("Toca para agregar foto").assertIsDisplayed()
    composeTestRule.onNodeWithText("Datos de la mascota").assertIsDisplayed()
    composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
    composeTestRule.onNodeWithText("*").assertIsDisplayed()
    assertNoCurrentLocationAction()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/create-post-compact-top.png"
    )

    composeTestRule.onNodeWithTag(VisualRootTag).performTouchInput { swipeUp() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Ubicacion").assertIsDisplayed()
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

    composeTestRule.onNodeWithText("Publicar Mascota Perdida").assertIsDisplayed()
    composeTestRule.onNodeWithTag("create-post-photo-upload-surface").assertIsDisplayed()
    composeTestRule.onNodeWithText("Toca para agregar foto").assertIsDisplayed()
    composeTestRule.onNodeWithText("Datos de la mascota").assertIsDisplayed()
    composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
    composeTestRule.onNodeWithText("*").assertIsDisplayed()
    assertNoCurrentLocationAction()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/create-post-tall-top.png"
    )

    composeTestRule.onNodeWithTag(VisualRootTag).performTouchInput { swipeUp() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Ubicacion").assertIsDisplayed()
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
            onBackClick = {},
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

  private companion object {
    const val VisualRootTag = "create-post-visual-root"
  }
}
