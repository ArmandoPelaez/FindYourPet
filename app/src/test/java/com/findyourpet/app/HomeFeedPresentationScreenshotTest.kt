package com.findyourpet.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.ui.screens.PetPostCard
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
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
class HomeFeedPresentationScreenshotTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone, sdk = [36])
  fun homeFeed_compactPhone_lightTheme_hasContinuousTopAndScrolledContent() {
    renderHomeFeed(width = 360, height = 640, darkTheme = false)

    composeTestRule.onNodeWithText("REX").assertIsDisplayed()
    composeTestRule.onNodeWithText("Cerca del Parque Central").assertIsDisplayed()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/home-feed-compact-light-top.png"
    )

    composeTestRule.onNodeWithTag(VisualRootTag).performTouchInput { swipeUp() }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Compartir").assertIsDisplayed()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/home-feed-compact-light-scrolled.png"
    )
  }

  @Test
  @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
  fun homeFeed_tallPhone_darkTheme_hasContinuousTopAndScrolledContent() {
    renderHomeFeed(
      width = 411,
      height = 914,
      darkTheme = true,
      features = "Visto por ultima vez cerca del Parque Central. " +
        "Es asustadizo, llevaba collar azul y suele esconderse en zonas tranquilas. " +
        "La familia busca ayuda para encontrarlo y agradece cualquier aviso."
    )

    composeTestRule.onNodeWithText("REX").assertIsDisplayed()
    composeTestRule.onNodeWithText("Cerca del Parque Central").assertIsDisplayed()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/home-feed-tall-dark-top.png"
    )

    composeTestRule.onNodeWithTag(VisualRootTag).performTouchInput { swipeUp() }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Compartir").assertIsDisplayed()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/home-feed-tall-dark-scrolled.png"
    )
  }

  private fun renderHomeFeed(
    width: Int,
    height: Int,
    darkTheme: Boolean,
    features: String = "Visto por ultima vez cerca del Parque Central. Es asustadizo y llevaba collar azul."
  ) {
    composeTestRule.setContent {
      MascotasPerdidasTheme(darkTheme = darkTheme) {
        Surface {
          Box(
            modifier = Modifier
              .width(width.dp)
              .height(height.dp)
              .testTag(VisualRootTag)
          ) {
            PetPostCard(
              post = samplePost(features),
              canReportSighting = true,
              onAlertClick = {}
            )
          }
        }
      }
    }
  }

  private fun samplePost(features: String = "Visto por ultima vez cerca del Parque Central. Es asustadizo y llevaba collar azul.") = PetPostEntity(
    id = "post_1",
    petName = "REX",
    species = "Perro",
    breed = "Boxer",
    color = "Marron",
    features = features,
    status = "PERDIDO",
    photoUri = "https://example.com/rex.jpg",
    dateLost = 1785207600000L,
    lastSeenLocation = "Cerca del Parque Central",
    latitude = -32.95,
    longitude = -60.66,
    rewardAmount = "Sin recompensa",
    ownerId = "owner_1",
    ownerName = "Persona Responsable"
  )

  private companion object {
    const val VisualRootTag = "home-feed-visual-root"
  }
}
