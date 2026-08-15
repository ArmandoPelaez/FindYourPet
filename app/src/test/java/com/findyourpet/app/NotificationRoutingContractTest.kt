package com.findyourpet.app

import com.findyourpet.app.data.local.entity.AppNotificationEntity
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRoutingContractTest {
  @Test
  fun notificationClickPassesFullNotificationAndMarksRead() {
    val notifications = source("app/src/main/java/com/findyourpet/app/ui/screens/NotificationsScreen.kt")
    val mapper = source("app/src/main/java/com/findyourpet/app/data/remote/RemoteMappers.kt")

    assertTrue(notifications.contains("viewModel.markNotificationAsRead(notif.id)"))
    assertTrue(notifications.contains("onNotificationClick(notif)"))
    assertTrue(!notifications.contains("notif.chatId ?: notif.targetId"))
    assertTrue(mapper.contains("chatId = string(\"chatId\").ifBlank { null }"))
    assertTrue(mapper.contains("sightingId = string(\"sightingId\").ifBlank { null }"))
  }

  @Test
  fun sightingNotificationUsesSightingIdBeforeLegacyChatReferences() {
    val notification = notification(
      type = "ALERT",
      targetId = "legacy_target",
      chatId = "chat_legacy",
      sightingId = " sighting_123 "
    )

    assertEquals("sighting/sighting_123", resolveNotificationRoute(notification))
  }

  @Test
  fun chatNotificationKeepsExistingChatRoute() {
    val notification = notification(
      type = "CHAT",
      targetId = "legacy_target",
      chatId = "chat_123"
    )

    assertEquals("chat/chat_123", resolveNotificationRoute(notification))
  }

  @Test
  fun invalidSightingNotificationDoesNotNavigateToChat() {
    val mainActivity = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")
    val chatDetail = source("app/src/main/java/com/findyourpet/app/ui/screens/ChatDetailScreen.kt")

    assertNull(resolveNotificationRoute(notification(type = "ALERT", sightingId = null)))
    assertNull(resolveNotificationRoute(notification(type = "ALERT", sightingId = "   ")))
    assertTrue(mainActivity.contains("Log.w("))
    assertTrue(mainActivity.contains("NOTIFICATION_TAG"))
    assertTrue(mainActivity.contains("id=${'$'}{notification.id}, type=${'$'}{notification.type}"))
    assertTrue(chatDetail.contains("Esta conversacion no esta disponible."))
    assertTrue(chatDetail.contains("!isAuthorizedParticipant"))
  }

  @Test
  fun notificationRouteAndUnavailableTargetAreSafe() {
    val mainActivity = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")

    assertTrue(mainActivity.contains("resolveNotificationRoute(notification)"))
    assertTrue(mainActivity.contains("sightingDetailRoute"))
    assertTrue(mainActivity.contains("chatDetailRoute"))
  }

  private fun notification(
    type: String,
    targetId: String = "target",
    chatId: String? = null,
    sightingId: String? = null
  ) = AppNotificationEntity(
    id = "notification_1",
    title = "title",
    message = "message",
    type = type,
    targetId = targetId,
    timestamp = 1L,
    chatId = chatId,
    sightingId = sightingId
  )

  private fun source(relativePath: String): String = File(repoRoot(), relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
