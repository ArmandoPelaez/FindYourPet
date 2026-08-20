package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthExclusivityContractTest {
  @Test
  fun firebaseRepository_rejectsLinkingAndUsesSafeProviderDecisions() {
    val source = source("app/src/main/java/com/findyourpet/app/data/auth/FirebaseAuthRepository.kt")
    val viewModelSource = source("app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt")
    val loginSource = source("app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt")

    assertTrue(source.contains("fetchSignInMethodsForEmail"))
    assertTrue(source.contains("provider.googleConflict()"))
    assertTrue(source.contains("providerConflictForEmail(email)"))
    assertTrue(source.contains("AuthFailure.AuthenticationFailed"))
    assertTrue(source.contains("firebaseAuth.currentUser?.toAuthState()"))
    listOf(
      "createUserWithEmailAndPassword",
      "signInWithEmailAndPassword",
      "signInWithCredential",
      "FirebaseAuthUserCollisionException",
      "CancellationException",
    ).forEach { marker ->
      assertTrue("Authentication flow contract is missing: $marker", source.contains(marker))
    }
    assertTrue(loginSource.contains("GetCredentialCancellationException"))
    assertTrue(loginSource.contains("AuthMessages.authenticationFailed"))
    assertFalse(viewModelSource.contains("authRepository.signInWithEmail(email, password)\n                .onFailure { _authMessage.value = it.message"))
    assertFalse(viewModelSource.contains("authRepository.signInWithGoogleIdToken(idToken)\n                .onFailure { _authMessage.value = it.message"))
    assertFalse(loginSource.contains("error.message ?:"))

    listOf(
      "pendingGoogleLink",
      "PendingGoogleLink",
      "linkWithCredential",
      "AccountLinkRequiredException",
    ).forEach { marker ->
      assertFalse("Exclusive auth contract must not contain: $marker", source.contains(marker))
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
