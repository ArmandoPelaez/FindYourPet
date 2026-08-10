package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SightingFanOutContractTest {
  private val repositoryText = File(
    repoRoot(),
    "app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt"
  ).readText()

  @Test
  fun localFanOutIsWrappedInRoomTransaction() {
    assertTrue(repositoryText.contains("database.withTransaction"))
    assertTrue(repositoryText.contains("petDao.insertSighting(sighting)"))
    assertTrue(repositoryText.contains("petDao.insertMessage(alertMessage)"))
    assertTrue(repositoryText.contains("petDao.insertNotification(notification)"))
  }

  @Test
  fun remoteFanOutWritesSightingChatAlertAndNotificationInOneBatch() {
    val batchIndex = repositoryText.indexOf("db.runBatch { batch ->")
    assertTrue(batchIndex >= 0)
    assertTrue(repositoryText.indexOf("sighting.toDocument", batchIndex) > batchIndex)
    assertTrue(repositoryText.indexOf("alertMessage.toDocument", batchIndex) > batchIndex)
    assertTrue(repositoryText.indexOf("notification.toDocument", batchIndex) > batchIndex)
  }

  @Test
  fun retryUsesStableIdsAndValidatesBeforeCreatingRecords() {
    assertTrue(repositoryText.contains("idempotencyKey: String? = null"))
    assertTrue(repositoryText.contains("val sightingId = \"sighting_${'$'}stableSubmissionKey\""))
    assertTrue(repositoryText.contains("id = \"${'$'}{sightingId}_alert\""))
    assertTrue(repositoryText.contains("id = \"${'$'}{sightingId}_notification\""))

    val validationIndex = repositoryText.indexOf("require(OwnershipPolicy.canReportSighting")
    val entityIndex = repositoryText.indexOf("val sighting = SightingAlertEntity")
    assertTrue(validationIndex >= 0)
    assertTrue(entityIndex > validationIndex)
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
