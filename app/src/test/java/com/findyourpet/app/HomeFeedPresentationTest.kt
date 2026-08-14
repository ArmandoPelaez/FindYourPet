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
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.ui.screens.PetPostCard
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import java.io.File
import org.junit.Assert.assertEquals
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
        PetPostCard(post = samplePost(), canReportSighting = true, onAlertClick = {})
      }
    }

    composeTestRule.onNodeWithText("REX").assertIsDisplayed()
    composeTestRule.onNodeWithText("Cerca del Parque Central").assertIsDisplayed()
    composeTestRule.onNodeWithText("Última vez visto").assertIsDisplayed()
    composeTestRule.onNodeWithText("He visto a esta mascota").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Boxer").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Cómo reconocerla").assertCountEquals(1)
    composeTestRule.onNodeWithText(
      "Visto por ultima vez cerca del Parque Central.",
      substring = true
    ).assertIsDisplayed()
    composeTestRule.onAllNodesWithText("¡Lo he visto!").assertCountEquals(0)
    composeTestRule.onNodeWithContentDescription("He visto a esta mascota: reportar avistamiento de REX").assertIsDisplayed()
    composeTestRule.onAllNodesWithContentDescription("Reportar avistamiento de REX").assertCountEquals(0)
  }

  @Test
  fun inlineSightingButton_displaysEligibleActionAndInvokesCallback() {
    var clicks = 0
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        PetPostCard(
          post = samplePost(),
          canReportSighting = true,
          onAlertClick = { clicks++ }
        )
      }
    }

    composeTestRule.onNodeWithText("He visto a esta mascota").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("He visto a esta mascota: reportar avistamiento de REX").performClick()
    assertEquals(1, clicks)
  }

  @Test
  fun inlineSightingButton_hidesSightingActionWhenUnavailable() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        PetPostCard(post = samplePost(status = "REUNIDO"), canReportSighting = false, onAlertClick = {})
      }
    }

    composeTestRule.onAllNodesWithText("¡Lo he visto!").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("He visto a esta mascota").assertCountEquals(0)
  }

  @Test
  fun petPostCard_compactPhoneViewport_keepsTitlelessLocationReadableWithoutRemovedAttributes() {
    val longLocation = "Avenida Siempre Viva y Pasaje de los Aromos, frente a la plaza principal"
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(modifier = Modifier.width(360.dp).height(740.dp)) {
          PetPostCard(post = samplePost(lastSeenLocation = longLocation), canReportSighting = false)
        }
      }
    }

    composeTestRule.onNodeWithText(longLocation).assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Boxer").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Especie").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Raza").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Color").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("Señas").assertCountEquals(0)
  }

  @Test
  fun petPostCard_compactPhoneViewport_keepsFinalContentScrollable() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(modifier = Modifier.width(360.dp).height(640.dp)) {
          PetPostCard(post = samplePost(), canReportSighting = true)
        }
      }
    }

    composeTestRule.onNodeWithText("Cómo reconocerla").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun petPostCard_hasNoOuterFloatingCardSurface() {
    val homeSource = File(repoRoot(), "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()
    val cardSource = homeSource.substringAfter("fun PetPostCard(")
      .substringBefore("@Composable\nprivate fun InlineSightingButton")

    assertFalse(cardSource.contains("AppElevation.card"))
    assertFalse(cardSource.contains("shape = AppShapes.card"))
    assertTrue(cardSource.contains("modifier = Modifier.fillMaxSize()"))
    assertTrue(cardSource.contains("verticalScroll(rememberScrollState())"))
    assertTrue(cardSource.contains("aspectRatio(AppSpacing.cardImageAspectRatio)"))
    assertTrue(cardSource.contains("padding(horizontal = AppSpacing.md)"))
    assertFalse(cardSource.contains("top = AppSpacing.sm"))
    assertTrue(cardSource.contains("bottom = AppSpacing.cardContentVertical"))
    assertTrue(cardSource.contains("clip(AppShapes.card)"))
    assertTrue(cardSource.contains("showIcon = false"))
    assertTrue(homeSource.contains("AppActionChip("))
    assertTrue(homeSource.contains("He visto a esta mascota"))
    assertTrue(homeSource.contains("Icons.Outlined.Info"))
    assertTrue(homeSource.contains("AppSpacing.locationGap"))
    assertFalse(homeSource.contains("text = \"La vi\""))
    assertTrue(homeSource.contains("Última vez visto"))
  }

  @Test
  fun homeHeader_isRemovedAndFeedKeepsSafeAreaSpacing() {
    val homeSource = File(repoRoot(), "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()

    assertTrue(homeSource.contains("contentWindowInsets = WindowInsets.safeDrawing"))
    assertTrue(homeSource.contains(".padding(top = AppSpacing.md)"))
    assertFalse(homeSource.contains("topBar ="))
    assertFalse(homeSource.contains("TopAppBar("))
    assertFalse(homeSource.contains("AppSpacing.homeHeaderHeight"))
    assertFalse(homeSource.contains("windowInsetsTopHeight(WindowInsets.statusBars)"))
    assertFalse(homeSource.contains("Mascotas Perdidas"))
    assertFalse(homeSource.contains("Red Segura de Búsqueda"))
    assertTrue(homeSource.indexOf(".padding(padding)") < homeSource.indexOf(".padding(top = AppSpacing.md)"))
  }

  @Test
  fun homeHeader_removalKeepsBottomNavigationInShell() {
    val mainActivitySource = File(repoRoot(), "app/src/main/java/com/findyourpet/app/MainActivity.kt").readText()

    assertTrue(mainActivitySource.contains("BottomPrimaryActionBanner("))
    assertTrue(mainActivitySource.contains("onNotificationsClick ="))
    assertTrue(mainActivitySource.contains("onCreatePostClick ="))
  }

  @Test
  fun petPostCard_doesNotExposeRemovedSightingActionOrShareImplementation() {
    val appSource = File(repoRoot(), "app/src/main")
    val forbiddenMarkers = listOf(
      "ACTION_SEND", "createChooser", "buildPetPostShareText", "Icons.Outlined.Share",
      "Compartir publicacion", "text = \"Compartir\"", "SightingActionBar", "¡Lo he visto!"
    )
    val matches = appSource.walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .flatMap { file -> forbiddenMarkers.filter { it in file.readText() }.map { "${file.relativeTo(repoRoot()).path}: $it" } }
      .toList()
    assertTrue("Removed controls must be absent from production Kotlin: $matches", matches.isEmpty())
  }

  private fun samplePost(
    status: String = "PERDIDO",
    latitude: Double = -32.95,
    longitude: Double = -60.66,
    lastSeenLocation: String = "Cerca del Parque Central"
  ) = PetPostEntity(
    id = "post_1", petName = "REX", species = "Perro", breed = "Boxer", color = "Marron",
    features = "Visto por ultima vez cerca del Parque Central. Es asustadizo y llevaba collar azul.",
    status = status, photoUri = "https://example.com/rex.jpg", dateLost = 1785207600000L,
    lastSeenLocation = lastSeenLocation, latitude = latitude, longitude = longitude,
    rewardAmount = "Sin recompensa", ownerId = "owner_1", ownerName = "Persona Responsable"
  )

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
