package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRoutingContractTest {
  @Test
  fun notificationClickMarksReadAndPrefersAuthorizedChatReference() {
    val notifications = source("app/src/main/java/com/findyourpet/app/ui/screens/NotificationsScreen.kt")
    val mapper = source("app/src/main/java/com/findyourpet/app/data/remote/RemoteMappers.kt")

    assertTrue(notifications.contains("viewModel.markNotificationAsRead(notif.id)"))
    assertTrue(notifications.contains("notif.chatId ?: notif.targetId"))
    assertTrue(mapper.contains("chatId = string(\"chatId\").ifBlank { null }"))
    assertTrue(mapper.contains("sightingId = string(\"sightingId\").ifBlank { null }"))
  }

  @Test
  fun notificationRouteAndUnavailableTargetAreSafe() {
    val mainActivity = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")
    val chatDetail = source("app/src/main/java/com/findyourpet/app/ui/screens/ChatDetailScreen.kt")

    assertTrue(mainActivity.contains("navController.navigate(chatDetailRoute(targetId))"))
    assertTrue(chatDetail.contains("Esta conversacion no esta disponible."))
    assertTrue(chatDetail.contains("!isAuthorizedParticipant"))
  }

  private fun source(relativePath: String): String = File(repoRoot(), relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
