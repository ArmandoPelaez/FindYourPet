package com.findyourpet.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.ui.screens.SightingAlertAdaptiveContent
import com.findyourpet.app.ui.screens.SightingSubmitActionBar
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class SightingAlertAdaptiveLayoutTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun adaptiveContent_usesCompactSingleColumnBelow600dp() {
    renderAdaptiveContent(width = 360, height = 720)

    composeTestRule.onNodeWithTag("sighting-layout-compact").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sighting-media-header").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sighting-info-card").assertIsDisplayed()
    composeTestRule.onNodeWithText("Reportando avistamiento de: REX").assertIsDisplayed()
  }

  @Test
  fun adaptiveContent_usesExpandedTwoColumnAtTabletWidth() {
    renderAdaptiveContent(width = 840, height = 720)

    composeTestRule.onAllNodesWithTag("sighting-layout-expanded").assertCountEquals(1)
    composeTestRule.onAllNodesWithTag("sighting-media-column").assertCountEquals(1)
    composeTestRule.onAllNodesWithTag("sighting-detail-column").assertCountEquals(1)
  }

  @Test
  fun adaptiveContent_usesCenteredFallbackWhenWideHeightIsConstrained() {
    renderAdaptiveContent(width = 840, height = 460)

    composeTestRule.onAllNodesWithTag("sighting-layout-centered").assertCountEquals(1)
    composeTestRule.onAllNodesWithTag("sighting-media-header").assertCountEquals(1)
  }

  @Test
  fun submitActionBar_isAccessibleAndTappable() {
    var clicks = 0

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        SightingSubmitActionBar(
          isSubmitting = false,
          enabled = true,
          onSubmit = { clicks++ }
        )
      }
    }

    composeTestRule.onNodeWithTag("sighting-bottom-action-bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sighting-primary-action").assertIsEnabled()
    composeTestRule.onNodeWithText("ENVIAR ALERTA").performClick()

    assertEquals(1, clicks)
  }

  @Test
  fun adaptiveContent_doesNotExposeProtectedContactOrExactCoordinates() {
    renderAdaptiveContent(width = 840, height = 720)

    composeTestRule.onAllNodesWithText("3415551234").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("owner@example.com").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Calle Privada 123").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("-34.6037").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("-58.3816").assertCountEquals(0)
  }

  private fun renderAdaptiveContent(width: Int, height: Int) {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(modifier = Modifier.width(width.dp).height(height.dp)) {
          SightingAlertAdaptiveContent(
            pet = samplePost(),
            selectedPhotoUri = "",
            locationName = "Parque Central",
            locationSource = LocationSource.MANUAL_COARSE,
            notes = "Lo vi caminando hacia una calle lateral.",
            authMessage = null,
            formMessage = null,
            onGalleryClick = {},
            onCameraClick = {},
            onLocationClick = {},
            onLocationNameChange = {},
            onNotesChange = {},
            modifier = Modifier.fillMaxSize(),
            availableWidthOverride = width.dp,
            availableHeightOverride = height.dp
          )
        }
      }
    }
  }

  private fun samplePost() = PetPostEntity(
    id = "post_1",
    petName = "REX",
    species = "Perro",
    breed = "Boxer",
    color = "Marron",
    features = "Es asustadizo y llevaba collar azul.",
    status = "PERDIDO",
    photoUri = "https://example.com/rex.jpg",
    dateLost = 1785207600000L,
    lastSeenLocation = "Cerca del Parque Central",
    latitude = -34.6037,
    longitude = -58.3816,
    rewardAmount = "Sin recompensa",
    ownerId = "owner_1",
    ownerName = "Persona Responsable"
  )
}
