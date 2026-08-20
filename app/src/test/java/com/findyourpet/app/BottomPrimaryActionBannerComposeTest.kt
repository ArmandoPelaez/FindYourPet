package com.findyourpet.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findyourpet.app.ui.components.BottomPrimaryActionBanner
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import org.junit.Assert.assertEquals
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
class BottomPrimaryActionBannerComposeTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomPrimaryActionBanner_displaysAccessibleActions() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = {},
          onProfileClick = {},
          onCreatePostClick = {},
          onActivityClick = {},
          onNotificationsClick = {}
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Acciones principales").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Inicio").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Perfil").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Reportar").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Actividad").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Alertas").assertIsDisplayed()
    composeTestRule.onAllNodesWithContentDescription("Inicio").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Perfil").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Reportar").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Actividad").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Alertas").assertCountEquals(1)
  }

  @Test
  fun bottomPrimaryActionBanner_invokesPrimaryActionCallbacks() {
    var profileClicks = 0
    var createPostClicks = 0
    var activityClicks = 0
    var notificationClicks = 0
    var homeClicks = 0

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = { homeClicks++ },
          onProfileClick = { profileClicks++ },
          onCreatePostClick = { createPostClicks++ },
          onActivityClick = { activityClicks++ },
          onNotificationsClick = { notificationClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Inicio").performClick()
    composeTestRule.onNodeWithContentDescription("Perfil").performClick()
    composeTestRule.onNodeWithContentDescription("Reportar").performClick()
    composeTestRule.onNodeWithContentDescription("Actividad").performClick()
    composeTestRule.onNodeWithContentDescription("Alertas").performClick()

    assertEquals(1, homeClicks)
    assertEquals(1, profileClicks)
    assertEquals(1, createPostClicks)
    assertEquals(1, activityClicks)
    assertEquals(1, notificationClicks)
  }

  @Test
  fun bottomPrimaryActionBanner_isLegibleInLightTheme() {
    composeTestRule.setContent {
      MascotasPerdidasTheme(darkTheme = false) {
        BottomPrimaryActionBanner(
          onHomeClick = {},
          onProfileClick = {},
          onCreatePostClick = {},
          onActivityClick = {}
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Acciones principales").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Inicio").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Perfil").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Reportar").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Actividad").assertIsDisplayed()
  }

  @Test
  fun bottomPrimaryActionBanner_isLegibleInDarkThemeWithDedicatedNonOpaqueToken() {
    composeTestRule.setContent {
      MascotasPerdidasTheme(darkTheme = true) {
        BottomPrimaryActionBanner(
          onHomeClick = {},
          onProfileClick = {},
          onCreatePostClick = {},
          onActivityClick = {}
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Acciones principales").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Inicio").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Perfil").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Reportar").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Actividad").assertIsDisplayed()
    assertTrue(AppOpacity.bottomNavigation in 0f..1f)
    assertTrue(AppOpacity.bottomNavigation < 1f)
  }

  @Test
  fun bottomPrimaryActionBanner_keepsPublishCtaOutOfNavigation() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = {},
          onProfileClick = {},
          onCreatePostClick = {},
          onActivityClick = {},
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Reportar").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Publicar ficha").assertCountEquals(0)
    composeTestRule.onAllNodesWithContentDescription("Publicar ficha").assertCountEquals(0)
  }

  @Test
  fun bottomPrimaryActionBanner_keepsDestinationsIndependentAfterReportAction() {
    var reportClicks = 0
    var homeClicks = 0
    var profileClicks = 0
    var activityClicks = 0
    var notificationClicks = 0

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = { homeClicks++ },
          onProfileClick = { profileClicks++ },
          onCreatePostClick = { reportClicks++ },
          onActivityClick = { activityClicks++ },
          onNotificationsClick = { notificationClicks++ },
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Reportar").performClick()
    composeTestRule.onNodeWithContentDescription("Inicio").performClick()
    composeTestRule.onNodeWithContentDescription("Perfil").performClick()
    composeTestRule.onNodeWithContentDescription("Actividad").performClick()
    composeTestRule.onNodeWithContentDescription("Alertas").performClick()

    assertEquals(1, reportClicks)
    assertEquals(1, homeClicks)
    assertEquals(1, profileClicks)
    assertEquals(1, activityClicks)
    assertEquals(1, notificationClicks)
  }

  @Test
  fun bottomPrimaryActionBanner_usesSharedContentInsetForCompactWidth() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(modifier = Modifier.requiredWidth(360.dp).height(160.dp)) {
          BottomPrimaryActionBanner(
            onHomeClick = {},
            onProfileClick = {},
            onCreatePostClick = {},
            onActivityClick = {},
          )
        }
      }
    }

    composeTestRule
      .onNodeWithTag("bottom-navigation-surface")
      .assertWidthIsEqualTo(328.dp)

  }

  @Test
  fun bottomPrimaryActionBanner_respectsMaximumWidthForWideLayout() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Box(modifier = Modifier.requiredWidth(800.dp).height(160.dp)) {
          BottomPrimaryActionBanner(
            onHomeClick = {},
            onProfileClick = {},
            onCreatePostClick = {},
            onActivityClick = {},
          )
        }
      }
    }

    composeTestRule
      .onNodeWithTag("bottom-navigation-surface")
      .assertWidthIsEqualTo(720.dp)
  }
}
