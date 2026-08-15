package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SightingFanOutContractTest {
  private val repositoryText = File(
    repoRoot(),
    "app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt"
  ).readText()
  private val submissionText = repositoryText.substring(
    repositoryText.indexOf("suspend fun submitSightingAlert"),
    repositoryText.indexOf("suspend fun sendChatMessage")
  )

  @Test
  fun localFanOutIsWrappedInRoomTransaction() {
    assertTrue(submissionText.contains("database.withTransaction"))
    assertTrue(submissionText.contains("petDao.insertSighting(sighting)"))
    assertTrue(submissionText.contains("petDao.insertNotification(notification)"))
    assertTrue(submissionText.indexOf("petDao.insertSighting(sighting)") < submissionText.indexOf("petDao.insertNotification(notification)"))
    assertTrue(!submissionText.contains("petDao.insertChatSession(chatSession)"))
    assertTrue(!submissionText.contains("petDao.insertMessage(alertMessage)"))
    assertTrue(!submissionText.contains("ChatSessionEntity"))
    assertTrue(!submissionText.contains("ChatMessageEntity"))
  }

  @Test
  fun remoteFanOutWritesOnlySightingAndNotificationInOneBatch() {
    val batchIndex = submissionText.indexOf("db.runBatch { batch ->")
    assertTrue(batchIndex >= 0)
    assertTrue(submissionText.indexOf("sighting.toDocument", batchIndex) > batchIndex)
    assertTrue(submissionText.indexOf("notification.toDocument", batchIndex) > batchIndex)
    assertTrue(!submissionText.contains("chatRef"))
    assertTrue(!submissionText.contains("chatSession.toDocument()"))
    assertTrue(!submissionText.contains("alertMessage.toDocument()"))
  }

  @Test
  fun retryUsesStableIdsAndValidatesBeforeCreatingRecords() {
    assertTrue(repositoryText.contains("idempotencyKey: String? = null"))
    assertTrue(repositoryText.contains("val sightingId = \"sighting_${'$'}stableSubmissionKey\""))
    assertTrue(repositoryText.contains("id = \"${'$'}{sightingId}_notification\""))

    val validationIndex = repositoryText.indexOf("require(OwnershipPolicy.canReportSighting")
    val entityIndex = repositoryText.indexOf("val sighting = SightingAlertEntity")
    assertTrue(validationIndex >= 0)
    assertTrue(entityIndex > validationIndex)
  }

  @Test
  fun newNotificationTargetsSightingAndDoesNotCopyNotesToChat() {
    assertTrue(submissionText.contains("targetId = sightingId"))
    assertTrue(submissionText.contains("sightingId = sightingId"))
    assertTrue(submissionText.contains("postId = postId"))
    assertTrue(!submissionText.contains("chatId = chatId"))
    assertTrue(!submissionText.contains("generalDetails = notes"))
  }

  @Test
  fun successReturnsSightingIdWithoutChangingActiveChat() {
    val viewModelText = File(
      repoRoot(),
      "app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt"
    ).readText()
    assertTrue(viewModelText.contains("onSuccess { sightingId ->"))
    assertTrue(viewModelText.contains("onComplete(sightingId)"))
    assertTrue(!viewModelText.contains("activeChatId.value = sightingId"))
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
