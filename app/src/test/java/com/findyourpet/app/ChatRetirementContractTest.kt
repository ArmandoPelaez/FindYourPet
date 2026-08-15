package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Contract audit for the retired Chat surface; remaining historical fields are tested separately. */
class ChatRetirementContractTest {
  private val root = repoRoot()

  @Test
  fun activeNavigationHasOnlyFiveProductDestinationsAndSightingRouting() {
    val main = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")
    val banner = source("app/src/main/java/com/findyourpet/app/ui/components/CommonComponents.kt")

    assertTrue(main.contains("ROUTE_HOME"))
    assertTrue(main.contains("ROUTE_PROFILE"))
    assertTrue(main.contains("ROUTE_CREATE"))
    assertTrue(main.contains("ROUTE_ACTIVITY"))
    assertTrue(main.contains("ROUTE_NOTIFICATIONS"))
    assertTrue(main.contains("ROUTE_SIGHTING_DETAIL"))
    assertFalse(main.contains("ROUTE_CHATS"))
    assertFalse(main.contains("ROUTE_CHAT_DETAIL"))
    assertFalse(main.contains("ChatListScreen"))
    assertFalse(main.contains("ChatDetailScreen"))
    assertTrue(banner.contains("label = \"Inicio\""))
    assertTrue(banner.contains("label = \"Perfil\""))
    assertTrue(banner.contains("label = \"Reportar\""))
    assertTrue(banner.contains("label = \"Actividad\""))
    assertTrue(banner.contains("label = \"Alertas\""))
  }

  @Test
  fun activeRuntimeHasNoChatScreensRoutesOrSendOperations() {
    val sourceFiles = listOf(
      "app/src/main/java/com/findyourpet/app/MainActivity.kt",
      "app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt",
      "app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt",
      "app/src/main/java/com/findyourpet/app/data/remote/RemoteMappers.kt",
      "app/src/main/java/com/findyourpet/app/data/remote/BackendCollections.kt"
    ).map(::source)

    sourceFiles.forEach { text ->
      assertFalse(text.contains("sendChatMessage"))
      assertFalse(text.contains("ROUTE_CHAT"))
      assertFalse(text.contains("ChatSessionEntity"))
      assertFalse(text.contains("ChatMessageEntity"))
    }
    assertFalse(File(root, "app/src/main/java/com/findyourpet/app/ui/screens/ChatListScreen.kt").exists())
    assertFalse(File(root, "app/src/main/java/com/findyourpet/app/ui/screens/ChatDetailScreen.kt").exists())
  }

  @Test
  fun historicalChatNotificationIsDecodedButNeverRoutedOrWritten() {
    val main = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")
    val mapper = source("app/src/main/java/com/findyourpet/app/data/remote/RemoteMappers.kt")

    assertTrue(mapper.contains("chatId = string(\"chatId\").ifBlank { null }"))
    assertTrue(mapper.contains("supportedNotificationTypes = setOf(\"ALERT\")"))
    assertTrue(main.contains("else {\n        null\n    }"))
    assertFalse(main.contains("chatDetailRoute"))
  }

  private fun source(relativePath: String): String = File(root, relativePath).readText().replace("\r\n", "\n")

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
