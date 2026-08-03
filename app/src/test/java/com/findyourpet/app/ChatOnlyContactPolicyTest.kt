package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatOnlyContactPolicyTest {
  private val root: File = repoRoot()

  @Test
  fun retiredContactDisclosureUiIsAbsentFromMainSources() {
    val mainSources = File(root, "app/src/main/java")
      .walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .joinToString("\n") { it.readText() }

    listOf(
      "ProtectedContactCard",
      "activeContactGrant",
      "toggleContactSharing",
      "toggleChatContactSharing",
      "isContactSharedByOwner",
      "ownerPhone =",
      "ownerEmail =",
      "ownerAddress ="
    ).forEach { marker ->
      assertFalse("Retired contact disclosure marker remains: $marker", mainSources.contains(marker))
    }
  }

  @Test
  fun chatDetailDoesNotShowBoundedUserResponsibilityCopy() {
    val chatDetail = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/ChatDetailScreen.kt").readText()

    assertFalse(chatDetail.contains("FindYourPet no solicita ni comparte telefono, email o direccion"))
    assertFalse(chatDetail.contains("esa decision es tu responsabilidad"))
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
