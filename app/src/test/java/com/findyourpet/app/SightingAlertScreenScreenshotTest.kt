package com.findyourpet.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.ui.components.FormPhotoUploadSurface
import com.findyourpet.app.ui.screens.SightingAlertAdaptiveContent
import com.findyourpet.app.ui.screens.SightingPhotoOptionsSheet
import com.findyourpet.app.ui.screens.SightingSubmitActionBar
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
class SightingAlertScreenScreenshotTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun sightingPhotoSurface_opensCreatePostSelectionSheetWithoutInlineActions() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        var showPhotoOptions by remember { mutableStateOf(false) }

        Box(
          modifier = Modifier
            .width(360.dp)
            .height(280.dp)
        ) {
          FormPhotoUploadSurface(
            selectedPhotoUri = "",
            emptyTitle = "Toca para agregar foto opcional",
            photoContentDescription = "Foto del avistamiento",
            onSurfaceClick = { showPhotoOptions = true },
            testTag = "sighting-photo-upload-surface"
          )
          if (showPhotoOptions) {
            SightingPhotoOptionsSheet(
              onDismissRequest = { showPhotoOptions = false },
              onGalleryClick = { showPhotoOptions = false },
              onCameraClick = { showPhotoOptions = false }
            )
          }
        }
      }
    }

    composeTestRule.onNodeWithTag("sighting-photo-upload-surface").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Agregar foto").assertIsDisplayed()
    composeTestRule.onNodeWithText("Elegir de la galería").assertIsDisplayed()
    composeTestRule.onNodeWithText("Tomar foto con la cámara").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Galeria").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Camara").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("sighting-gallery-action").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("sighting-camera-action").assertCountEquals(0)
  }

  @Test
  @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone, sdk = [36])
  fun sightingAlert_compactPhone_hasReportFirstVisuals() {
    renderSightingAlert(width = 360, height = 640)

    assertReportFirstContent()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/sighting-alert-compact-top.png"
    )

    composeTestRule.onNodeWithTag("sighting-detail-column").performTouchInput { swipeUp() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Detalles adicionales").assertIsDisplayed()
    composeTestRule.onNodeWithText("ENVIAR ALERTA").assertIsDisplayed()
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/sighting-alert-compact-scrolled.png"
    )
  }

  @Test
  @Config(qualifiers = "w840dp-h860dp-xhdpi", sdk = [36])
  fun sightingAlert_wideLayout_hasReportFirstVisuals() {
    renderSightingAlert(width = 840, height = 860)

    assertReportFirstContent()
    composeTestRule.onAllNodesWithTag("sighting-layout-expanded").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("ENVIAR ALERTA").assertCountEquals(1)
    composeTestRule.onNodeWithTag(VisualRootTag).captureRoboImage(
      filePath = "src/test/screenshots/sighting-alert-wide.png"
    )
  }

  @OptIn(ExperimentalMaterial3Api::class)
  private fun renderSightingAlert(width: Int, height: Int) {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(
          modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .testTag(VisualRootTag)
        ) {
          Scaffold(
            topBar = {
              TopAppBar(
                title = {
                  Text(
                    text = "Alerta de Avistamiento"
                  )
                },
                navigationIcon = {
                  IconButton(onClick = {}) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                  }
                }
              )
            },
            bottomBar = {
              SightingSubmitActionBar(
                isSubmitting = false,
                enabled = true,
                onSubmit = {}
              )
            }
          ) { padding ->
            SightingAlertAdaptiveContent(
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
              modifier = Modifier
                .fillMaxSize()
                .padding(padding),
              availableWidthOverride = width.dp,
              availableHeightOverride = height.dp
            )
          }
        }
      }
    }
  }

  private fun assertReportFirstContent() {
    composeTestRule.onNodeWithTag("sighting-photo-upload-surface").assertIsDisplayed()
    composeTestRule.onNodeWithText("Toca para agregar foto opcional").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Galeria").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Camara").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("sighting-gallery-action").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("sighting-camera-action").assertCountEquals(0)
    composeTestRule.onNodeWithText("Ubicacion del avistamiento").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Usar ubicacion actual").assertCountEquals(1)
    composeTestRule.onAllNodesWithTag("sighting-media-header").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("sighting-info-card").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Reportando avistamiento de:").assertCountEquals(0)
  }

  private companion object {
    const val VisualRootTag = "sighting-alert-visual-root"
  }
}
