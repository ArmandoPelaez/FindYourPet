package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthLinkingContractTest {
  @Test
  fun firebaseRepository_storesPendingGoogleCredentialAndLinksAfterPasswordSignIn() {
    val source = source("app/src/main/java/com/findyourpet/app/data/auth/FirebaseAuthRepository.kt")

    listOf(
      "FirebaseAuthUserCollisionException",
      "pendingGoogleLink",
      "user.linkWithCredential(pendingLink.credential).await()",
      "AccountLinkRequiredException",
      "pendingGoogleLink = null"
    ).forEach { marker ->
      assertTrue("Auth linking contract is missing: $marker", source.contains(marker))
    }
  }

  private fun source(relativePath: String): String =
    File(repoRoot(), relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
