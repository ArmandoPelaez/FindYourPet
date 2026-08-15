package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivityContractStaticTest {
  private val root: File = repoRoot()

  @Test
  fun ownerScopedSightings_areNewestFirstInDaoAndRepository() {
    val dao = source("app/src/main/java/com/findyourpet/app/data/local/dao/PetDao.kt")
    val repository = source("app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt")

    assertTrue(dao.contains("WHERE ownerId = :ownerId ORDER BY timestamp DESC"))
    assertTrue(dao.contains("fun getSightingsForOwner(ownerId: String)"))
    assertTrue(repository.contains("whereEqualTo(\"ownerId\", ownerId)"))
    assertTrue(repository.contains("orderBy(\"timestamp\", Query.Direction.DESCENDING)"))
    assertTrue(repository.contains("petDao.getSightingsForOwner(ownerId).toLocalState(emptyList())"))
    assertTrue(repository.contains("petDao.clearSightingsForOwner(ownerId)"))
  }

  @Test
  fun viewModelScopesActivityStateToAuthenticatedOwnerAndPropagatesSyncState() {
    val viewModel = source("app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt")

    assertTrue(viewModel.contains("val receivedSightingsState"))
    assertTrue(viewModel.contains("currentUser.flatMapLatest { user ->"))
    assertTrue(viewModel.contains("if (user.id.isBlank())"))
    assertTrue(viewModel.contains("repository.getSightingsForOwnerState(user.id)"))
    assertTrue(viewModel.contains("BackendSyncState.loading(emptyList(), repository.usesRemoteBackend)"))
    assertTrue(viewModel.contains("val receivedSightings: StateFlow<List<SightingAlertEntity>>"))
  }

  @Test
  fun activityScreenRendersMetadataStatesAndPreservesSightingIdentityWithoutMessagingData() {
    val activity = source("app/src/main/java/com/findyourpet/app/ui/screens/ActivityScreen.kt")

    listOf(
      "SyncStatusBanner(state = sightingsState)",
      "ActivityEmptyState(",
      "isLoading = sightingsState.isLoading",
      "hasError = sightingsState.hasError",
      "items(sightings, key = { it.id })",
      "testTag(\"activity-item-\${sighting.id}\")",
      "sighting.locationName",
      "sighting.timestamp",
      "sighting.photoUri",
      "petPost?.photoUri",
      "value = \"Avistamiento\""
    ).forEach { marker ->
      assertTrue("Missing Activity contract marker: $marker", activity.contains(marker))
    }

    listOf("ChatSessionEntity", "ChatMessageEntity", "lastMessage", "chatId", "reply", "send input", "typing")
      .forEach { forbidden ->
        assertTrue("Activity must not depend on $forbidden", !activity.contains(forbidden))
      }
  }

  @Test
  fun primaryNavigationReplacesOnlyTheFourthDestinationAndKeepsLegacyChatRoute() {
    val main = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")
    val banner = source("app/src/main/java/com/findyourpet/app/ui/components/CommonComponents.kt")

    assertTrue(main.contains("private const val ROUTE_ACTIVITY = \"activity\""))
    assertTrue(main.contains("onActivityClick = { navController.navigateToPrimaryDestination(ROUTE_ACTIVITY) }"))
    assertTrue(main.contains("composable(ROUTE_ACTIVITY)"))
    assertTrue(main.contains("ActivityScreen("))
    assertTrue(main.contains("composable(ROUTE_CHATS)"))
    assertTrue(main.contains("ChatListScreen("))
    assertTrue(banner.contains("label = \"Actividad\""))
    assertTrue(banner.contains("contentDescription = \"Actividad\""))
    assertTrue(!banner.contains("label = \"Mensajes\""))
    assertTrue(!banner.contains("onChatClick"))
  }

  private fun source(relativePath: String): String = File(root, relativePath).readText().replace("\r\n", "\n")

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
