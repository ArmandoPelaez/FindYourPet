package com.findyourpet.app

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.SIGHTING_ALERT_MESSAGE_TYPE
import com.findyourpet.app.ui.screens.ChatMessageItem
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ChatSightingAlertComposeTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun alertWithoutPhotoShowsAuthorizedDetailsWithoutImageGap() {
    setMessage(photo = null)

    composeTestRule.onNodeWithTag("sighting-alert-message").assertIsDisplayed()
    composeTestRule.onNodeWithText("Alerta de avistamiento").assertIsDisplayed()
    composeTestRule.onNodeWithText("Ubicacion: Parque Central").assertIsDisplayed()
    composeTestRule.onNodeWithText("Lo vi junto a la entrada.").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("sighting-alert-photo").assertCountEquals(0)
  }

  @Test
  fun alertWithPhotoRendersAttachmentAndDoesNotRenderContactData() {
    setMessage(photo = "https://res.cloudinary.com/example/image/upload/sighting.jpg")

    composeTestRule.onNodeWithTag("sighting-alert-message").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sighting-alert-photo").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("owner@example.com").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("3415551234").assertCountEquals(0)
  }

  @Test
  fun chatKeepsNormalComposerAfterAlert() {
    val source = File(repoRoot(), "app/src/main/java/com/findyourpet/app/ui/screens/ChatDetailScreen.kt").readText()
    assertTrue(source.contains("Escribe un mensaje..."))
    assertTrue(source.contains("viewModel.sendChatMessage("))
    assertTrue(source.contains("onComplete ="))
    assertTrue(source.contains("onError ="))
  }

  private fun setMessage(photo: String?) {
    val message = ChatMessageEntity(
      id = "alert_1",
      chatId = "chat_1",
      postId = "post_1",
      senderId = "uid_reporter",
      senderName = "Reporter",
      text = "Nuevo avistamiento de Milo",
      photoUri = null,
      timestamp = 123L,
      type = SIGHTING_ALERT_MESSAGE_TYPE,
      sightingId = "sighting_1",
      ownerId = "uid_owner",
      reporterId = "uid_reporter",
      snapshotPetName = "Milo",
      photoAttachmentUri = photo,
      locationDisplay = "Parque Central",
      generalDetails = "Lo vi junto a la entrada.",
      snapshotTimestamp = 123L
    )
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        ChatMessageItem(
          message = message,
          currentUserId = "uid_owner",
          context = ApplicationProvider.getApplicationContext<Context>()
        )
      }
    }
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
