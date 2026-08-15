package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SightingDetailContractTest {
  @Test
  fun detailRouteAndViewModelUseSightingIdWithoutSelectingChat() {
    val mainActivity = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")
    val viewModel = source("app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt")

    assertTrue(mainActivity.contains("ROUTE_SIGHTING_DETAIL = \"sighting/{sightingId}\""))
    assertTrue(mainActivity.contains("SightingDetailScreen("))
    assertTrue(mainActivity.contains("getString(\"sightingId\")"))
    assertTrue(viewModel.contains("val selectedSightingId = MutableStateFlow<String?>(null)"))
    assertTrue(viewModel.contains("repository.getSightingByIdState(id)"))
    assertTrue(viewModel.contains("fun selectSightingDetail(sightingId: String)"))
    assertFalse(viewModel.contains("activeChatId.value = sightingId"))
  }

  @Test
  fun detailScreenIsReadOnlyAndSourcesContentFromSighting() {
    val detail = source("app/src/main/java/com/findyourpet/app/ui/screens/SightingDetailScreen.kt")
    val repository = source("app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt")

    assertTrue(detail.contains("viewModel.sightingDetailState"))
    assertTrue(detail.contains("sighting.notes"))
    assertTrue(detail.contains("sighting.locationName"))
    assertTrue(detail.contains("sighting.timestamp"))
    assertTrue(detail.contains("sighting.photoUri"))
    assertTrue(detail.contains("ReadOnlyMapSheet"))
    assertTrue(detail.contains("sighting-detail-loading"))
    assertTrue(detail.contains("sighting-detail-error"))
    assertTrue(detail.contains("Ver ubicación"))
    assertTrue(detail.contains("MaterialTheme.colorScheme"))
    assertFalse(detail.contains("ExperimentalMaterial3Api"))
    assertFalse(detail.contains("Color("))
    assertFalse(Regex("\\d+\\.dp").containsMatchIn(detail))
    assertFalse(Regex("\\d+\\.sp").containsMatchIn(detail))
    assertFalse(detail.contains("ChatMessageEntity"))
    assertFalse(detail.contains("ChatSessionEntity"))
    assertFalse(detail.contains("generalDetails"))
    assertTrue(repository.contains("BackendCollections.SIGHTINGS).document(sightingId)"))
    assertTrue(repository.contains("petDao.getSightingById(sightingId)"))
  }

  private fun source(relativePath: String): String = File(repoRoot(), relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
