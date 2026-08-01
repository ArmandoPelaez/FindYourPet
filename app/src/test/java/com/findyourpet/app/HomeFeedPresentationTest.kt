package com.findyourpet.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.ui.screens.PetPostCard
import com.findyourpet.app.ui.screens.buildPetPostShareText
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class HomeFeedPresentationTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun petPostCard_displaysAlertHierarchyAndActions() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        PetPostCard(
          post = samplePost(),
          canReportSighting = true,
          onAlertClick = {}
        )
      }
    }

    composeTestRule.onNodeWithText("REX").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Boxer").assertCountEquals(1)
    composeTestRule.onNodeWithText("Color").assertIsDisplayed()
    composeTestRule.onNodeWithText("Marron").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Especie").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Raza").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Perro").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Información reportada").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("Ubicación en la que se perdió").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("¡Lo he visto!").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("Compartir").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Reportar avistamiento de REX").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Compartir publicacion de REX").assertCountEquals(1)
  }

  @Test
  fun petPostCard_hidesSightingActionWhenUnavailable() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        PetPostCard(
          post = samplePost(status = "REUNIDO"),
          canReportSighting = false,
          onAlertClick = {}
        )
      }
    }

    composeTestRule.onAllNodesWithText("¡Lo he visto!").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Compartir").assertCountEquals(1)
  }

  @Test
  fun petPostCard_compactPhoneViewport_keepsBreedChipWithoutRemovedAttributes() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(
          modifier = Modifier
            .width(360.dp)
            .height(740.dp)
        ) {
          PetPostCard(
            post = samplePost(),
            canReportSighting = true,
            onAlertClick = {}
          )
        }
      }
    }

    composeTestRule.onAllNodesWithText("Boxer").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("Especie").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Raza").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Color").assertCountEquals(1)
  }

  @Test
  fun shareText_usesOnlyPublicPostSummary() {
    val post = samplePost(
      latitude = -34.6037,
      longitude = -58.3816
    )

    val shareText = buildPetPostShareText(post)

    assertTrue(shareText.contains("REX"))
    assertTrue(shareText.contains("Boxer"))
    assertTrue(shareText.contains("Marron"))
    assertTrue(shareText.contains("Parque Central"))
    assertFalse(shareText.contains("Especie"))
    assertFalse(shareText.contains("Perro"))
    assertFalse(shareText.lines().any { it.startsWith("Especie:") })
    assertFalse(shareText.contains("3415551234"))
    assertFalse(shareText.contains("owner@example.com"))
    assertFalse(shareText.contains("Calle Privada"))
    assertFalse(shareText.contains("-34.6037"))
    assertFalse(shareText.contains("-58.3816"))
  }

  @Test
  fun appDoesNotBundleReferenceDogImage() {
    val appSource = File(repoRoot(), "app/src/main")
    val forbiddenMarkers = listOf(
      "Gemini_Generated_Image_pam55fpam55fpam5",
      "pam55fpam55fpam5"
    )

    val matches = appSource.walkTopDown()
      .filter { it.isFile }
      .flatMap { file ->
        val fileNameMatches = forbiddenMarkers.filter { marker -> marker in file.name }
        val textMatches = if (file.extension in setOf("kt", "xml", "gradle", "kts")) {
          val text = file.readText()
          forbiddenMarkers.filter { marker -> marker in text }
        } else {
          emptyList()
        }
        (fileNameMatches + textMatches).map { marker -> "${file.relativeTo(repoRoot()).path}: $marker" }
      }
      .toList()

    assertTrue("Reference dog image must not be bundled or referenced: $matches", matches.isEmpty())
  }

  private fun samplePost(
    status: String = "PERDIDO",
    latitude: Double = -32.95,
    longitude: Double = -60.66
  ) = PetPostEntity(
    id = "post_1",
    petName = "REX",
    species = "Perro",
    breed = "Boxer",
    color = "Marron",
    features = "Visto por ultima vez cerca del Parque Central. Es asustadizo y llevaba collar azul.",
    status = status,
    photoUri = "https://example.com/rex.jpg",
    dateLost = 1785207600000L,
    lastSeenLocation = "Cerca del Parque Central",
    latitude = latitude,
    longitude = longitude,
    rewardAmount = "Sin recompensa",
    ownerId = "owner_1",
    ownerName = "Persona Responsable"
  )

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
