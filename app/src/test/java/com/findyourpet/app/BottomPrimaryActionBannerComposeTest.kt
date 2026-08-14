package com.findyourpet.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.findyourpet.app.ui.components.BottomPrimaryActionBanner
import com.findyourpet.app.ui.components.BottomNavigationContextualAction
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
          onChatClick = {},
          onNotificationsClick = {}
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Acciones principales").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Inicio").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Perfil").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Crear publicacion").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Chats Privados").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Alertas").assertIsDisplayed()
    composeTestRule.onAllNodesWithContentDescription("Inicio").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Perfil").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Crear publicacion").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Chats Privados").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Alertas").assertCountEquals(1)
  }

  @Test
  fun bottomPrimaryActionBanner_invokesPrimaryActionCallbacks() {
    var profileClicks = 0
    var createPostClicks = 0
    var chatClicks = 0
    var notificationClicks = 0
    var homeClicks = 0

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = { homeClicks++ },
          onProfileClick = { profileClicks++ },
          onCreatePostClick = { createPostClicks++ },
          onChatClick = { chatClicks++ },
          onNotificationsClick = { notificationClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Inicio").performClick()
    composeTestRule.onNodeWithContentDescription("Perfil").performClick()
    composeTestRule.onNodeWithContentDescription("Crear publicacion").performClick()
    composeTestRule.onNodeWithContentDescription("Chats Privados").performClick()
    composeTestRule.onNodeWithContentDescription("Alertas").performClick()

    assertEquals(1, homeClicks)
    assertEquals(1, profileClicks)
    assertEquals(1, createPostClicks)
    assertEquals(1, chatClicks)
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
          onChatClick = {}
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Acciones principales").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Inicio").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Perfil").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Crear publicacion").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Chats Privados").assertIsDisplayed()
  }

  @Test
  fun bottomPrimaryActionBanner_isLegibleInDarkThemeWithDedicatedNonOpaqueToken() {
    composeTestRule.setContent {
      MascotasPerdidasTheme(darkTheme = true) {
        BottomPrimaryActionBanner(
          onHomeClick = {},
          onProfileClick = {},
          onCreatePostClick = {},
          onChatClick = {}
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Acciones principales").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Inicio").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Perfil").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Crear publicacion").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Chats Privados").assertIsDisplayed()
    assertTrue(AppOpacity.bottomNavigation in 0f..1f)
    assertTrue(AppOpacity.bottomNavigation < 1f)
  }

  @Test
  fun bottomPrimaryActionBanner_rendersContextualPublishActionInTheCenterSlot() {
    var publishClicks = 0

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = {},
          onProfileClick = {},
          onCreatePostClick = {},
          onChatClick = {},
          contextualCreateAction = BottomNavigationContextualAction(
            label = "Publicar ficha",
            enabled = true,
            isBusy = false,
            onClick = { publishClicks++ },
          ),
        )
      }
    }

    composeTestRule.onNodeWithText("Publicar ficha").assertIsDisplayed()
    composeTestRule.onAllNodesWithContentDescription("Publicar ficha").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Crear publicacion").assertCountEquals(0)
    composeTestRule.onNodeWithContentDescription("Publicar ficha").performClick()

    assertEquals(1, publishClicks)
  }

  @Test
  fun bottomPrimaryActionBanner_disablesContextualPublishActionWhenInvalidOrBusy() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = {},
          onProfileClick = {},
          onCreatePostClick = {},
          onChatClick = {},
          contextualCreateAction = BottomNavigationContextualAction(
            label = "Publicar ficha",
            enabled = false,
            isBusy = true,
            onClick = {},
          ),
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Publicar ficha").assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun bottomPrimaryActionBanner_keepsAllSecondaryCallbacksWhenContextualActionIsShown() {
    var homeClicks = 0
    var profileClicks = 0
    var chatClicks = 0
    var notificationClicks = 0

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = { homeClicks++ },
          onProfileClick = { profileClicks++ },
          onCreatePostClick = {},
          onChatClick = { chatClicks++ },
          onNotificationsClick = { notificationClicks++ },
          contextualCreateAction = BottomNavigationContextualAction(
            label = "Publicar ficha",
            enabled = false,
            isBusy = false,
            onClick = {},
          ),
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Inicio").performClick()
    composeTestRule.onNodeWithContentDescription("Perfil").performClick()
    composeTestRule.onNodeWithContentDescription("Chats Privados").performClick()
    composeTestRule.onNodeWithContentDescription("Alertas").performClick()

    assertEquals(1, homeClicks)
    assertEquals(1, profileClicks)
    assertEquals(1, chatClicks)
    assertEquals(1, notificationClicks)
  }
}
