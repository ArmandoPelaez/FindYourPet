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
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.ui.screens.PetPostCard
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
    composeTestRule.onNodeWithText("Cerca del Parque Central").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Boxer").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Color").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Marron").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Señas").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Especie").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Raza").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Perro").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Información reportada").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("Ubicación en la que se perdió").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("¡Lo he visto!").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Reportar avistamiento de REX").assertCountEquals(1)
    composeTestRule.onAllNodesWithText("Compartir").assertCountEquals(0)
    composeTestRule.onAllNodesWithContentDescription("Compartir publicacion de REX").assertCountEquals(0)
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
    composeTestRule.onAllNodesWithText("Compartir").assertCountEquals(0)
    composeTestRule.onAllNodesWithContentDescription("Compartir publicacion de REX").assertCountEquals(0)
  }

  @Test
  fun petPostCard_compactPhoneViewport_keepsTitlelessLocationReadableWithoutRemovedAttributes() {
    val longLocation = "Avenida Siempre Viva y Pasaje de los Aromos, frente a la plaza principal"

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(
          modifier = Modifier
            .width(360.dp)
            .height(740.dp)
        ) {
          PetPostCard(
            post = samplePost(lastSeenLocation = longLocation),
            canReportSighting = true,
            onAlertClick = {}
          )
        }
      }
    }

    composeTestRule.onNodeWithText(longLocation).assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Boxer").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Especie").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Raza").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Color").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Señas").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Ubicación en la que se perdió").assertCountEquals(0)
  }

  @Test
  fun petPostCard_compactPhoneViewport_canScrollFinalActionsIntoView() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(
          modifier = Modifier
            .width(360.dp)
            .height(640.dp)
        ) {
          PetPostCard(
            post = samplePost(),
            canReportSighting = true,
            onAlertClick = {}
          )
        }
      }
    }

    composeTestRule.onNodeWithText("¡Lo he visto!")
      .performScrollTo()
      .assertIsDisplayed()
  }

  @Test
  fun petPostCard_hasNoOuterFloatingCardSurface() {
    val homeSource = File(repoRoot(), "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()
    val cardSource = homeSource
      .substringAfter("fun PetPostCard(")
      .substringBefore("@Composable\nprivate fun PetIdentitySection")

    assertFalse(cardSource.contains("AppElevation.card"))
    assertFalse(cardSource.contains("shape = AppShapes.card"))
    assertTrue(cardSource.contains("modifier = Modifier.fillMaxSize()"))
    assertTrue(cardSource.contains("verticalScroll(rememberScrollState())"))
  }

  @Test
  fun petPostCard_doesNotExposeShareControlOrImplementation() {
    val appSource = File(repoRoot(), "app/src/main")
    val forbiddenMarkers = listOf(
      "ACTION_SEND",
      "createChooser",
      "buildPetPostShareText",
      "Icons.Outlined.Share",
      "Compartir publicacion",
      "text = \"Compartir\""
    )

    val matches = appSource.walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .flatMap { file ->
        val source = file.readText()
        forbiddenMarkers.filter { marker -> marker in source }
          .map { marker -> "${file.relativeTo(repoRoot()).path}: $marker" }
      }
      .toList()

    assertTrue("Share implementation must be absent from production Kotlin: $matches", matches.isEmpty())
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
    longitude: Double = -60.66,
    lastSeenLocation: String = "Cerca del Parque Central"
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
    lastSeenLocation = lastSeenLocation,
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
