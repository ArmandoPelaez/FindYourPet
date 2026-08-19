package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthScopedFeedContractTest {
  @Test
  fun viewModelBindsFeedListenerToAuthenticatedSession() {
    val source = source("app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt")

    assertTrue(source.contains("flatMapLatestForAuthenticatedUser"))
    assertTrue(source.contains("signedOut ="))
    assertTrue(source.contains("signedIn = { repository.observePostFeedState() }"))
  }

  @Test
  fun repositoryCreatesFeedObserverPerSubscription_andRemovesListenerOnCancellation() {
    val source = source("app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt")

    assertTrue(source.contains("fun observePostFeedState()"))
    assertTrue(source.contains("query.addSnapshotListener(MetadataChanges.INCLUDE)"))
    assertTrue(source.contains("awaitClose { registration.remove() }"))
    assertFalse(source.contains("val postFeedState: Flow<BackendSyncState<List<PetPostEntity>>>") )
  }

  private fun source(relativePath: String): String =
    File(repoRoot(), relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
