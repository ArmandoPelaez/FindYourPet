package com.findyourpet.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.findyourpet.app.ui.components.BottomPrimaryActionBanner
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
          onChatClick = {}
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Acciones principales").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Inicio").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Perfil").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Crear publicacion").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Chats Privados").assertIsDisplayed()
    composeTestRule.onAllNodesWithContentDescription("Inicio").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Perfil").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Crear publicacion").assertCountEquals(1)
    composeTestRule.onAllNodesWithContentDescription("Chats Privados").assertCountEquals(1)
  }

  @Test
  fun bottomPrimaryActionBanner_invokesPrimaryActionCallbacks() {
    var profileClicks = 0
    var createPostClicks = 0
    var chatClicks = 0
    var homeClicks = 0

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        BottomPrimaryActionBanner(
          onHomeClick = { homeClicks++ },
          onProfileClick = { profileClicks++ },
          onCreatePostClick = { createPostClicks++ },
          onChatClick = { chatClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithContentDescription("Inicio").performClick()
    composeTestRule.onNodeWithContentDescription("Perfil").performClick()
    composeTestRule.onNodeWithContentDescription("Crear publicacion").performClick()
    composeTestRule.onNodeWithContentDescription("Chats Privados").performClick()

    assertEquals(1, homeClicks)
    assertEquals(1, profileClicks)
    assertEquals(1, createPostClicks)
    assertEquals(1, chatClicks)
  }
}
