package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ModerationContractTest {
  @Test
  fun moderationPersistenceUsesDeterministicIdsAndRoomMigration() {
    val entities = source("app/src/main/java/com/findyourpet/app/data/local/entity/Entities.kt")
    val dao = source("app/src/main/java/com/findyourpet/app/data/local/dao/PetDao.kt")
    val database = source("app/src/main/java/com/findyourpet/app/data/local/AppDatabase.kt")
    val collections = source("app/src/main/java/com/findyourpet/app/data/remote/BackendCollections.kt")

    assertTrue(entities.contains("data class ContentReportEntity"))
    assertTrue(entities.contains("data class UserBlockEntity"))
    assertTrue(entities.contains("Index(value = [\"sightingId\", \"reportingUserId\", \"reason\"], unique = true)"))
    assertTrue(entities.contains("Index(value = [\"blockerUserId\", \"blockedUserId\"], unique = true)"))
    assertTrue(dao.contains("getUserBlock(blockerUserId: String, blockedUserId: String)"))
    assertTrue(database.contains("version = 10"))
    assertTrue(database.contains("Migration(8, 9)"))
    assertTrue(collections.contains("const val CONTENT_REPORTS = \"contentReports\""))
    assertTrue(collections.contains("const val USER_BLOCKS = \"userBlocks\""))
    assertTrue(collections.contains("fun contentReportId"))
    assertTrue(collections.contains("fun userBlockId"))
  }

  @Test
  fun repositoryChecksBlockBeforeUploadAndKeepsChatOutOfSubmission() {
    val repository = source("app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt")
    val blockCheck = repository.indexOf("require(!isUserBlocked(resolvedOwnerId, reporterId))")
    val upload = repository.indexOf("val uploadedPhoto")
    val sighting = repository.indexOf("val sighting = SightingAlertEntity")

    assertTrue(blockCheck >= 0)
    assertTrue(blockCheck < upload)
    assertTrue(blockCheck < sighting)
    assertTrue(repository.contains("BLOCKED_SIGHTING_MESSAGE"))
    assertTrue(repository.contains("reportSightingContent"))
    assertTrue(repository.contains("blockSightingReporter"))
    assertTrue(repository.contains("isUserBlocked"))
  }

  @Test
  fun reunificationCleansOnlySightingsAndRelatedOwnerNotifications() {
    val dao = source("app/src/main/java/com/findyourpet/app/data/local/dao/PetDao.kt")
    val repository = source("app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt")

    assertTrue(dao.contains("clearSightingsForPost(postId: String)"))
    assertTrue(dao.contains("clearNotificationsForPost(postId: String)"))
    assertTrue(dao.contains("clearNotificationsForSightings(sightingIds: List<String>)"))
    assertTrue(repository.contains("whereEqualTo(\"postId\", postId)"))
    assertTrue(repository.contains("snapshot.getString(\"postId\") == postId"))
    assertTrue(repository.contains("snapshot.getString(\"sightingId\")?.let(sightingIds::contains) == true"))
    assertTrue(repository.contains("database.withTransaction"))
    assertTrue(repository.contains("FIRESTORE_BATCH_DELETE_LIMIT"))
  }

  @Test
  fun detailModerationIsOwnerOnlyAndDoesNotUseChatIdentifiers() {
    val detail = source("app/src/main/java/com/findyourpet/app/ui/screens/SightingDetailScreen.kt")
    val viewModel = source("app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt")
    val mapper = source("app/src/main/java/com/findyourpet/app/data/remote/RemoteMappers.kt")

    assertTrue(detail.contains("sighting-detail-moderation-menu"))
    assertTrue(detail.contains("Reportar contenido"))
    assertTrue(detail.contains("Bloquear usuario"))
    assertTrue(detail.contains("val canModerate = sighting?.ownerId == currentUser.id"))
    assertTrue(detail.contains("!reporterBlocked"))
    assertTrue(viewModel.contains("reportSightingContent"))
    assertTrue(viewModel.contains("blockSightingReporter"))
    assertTrue(viewModel.contains("ModerationOperationStatus.SUBMITTING"))
    assertTrue(!detail.contains("ChatSessionEntity"))
    assertTrue(!detail.contains("ChatMessageEntity"))
    assertTrue(mapper.contains("fun ContentReportEntity.toDocument"))
    assertTrue(mapper.contains("fun UserBlockEntity.toDocument"))
  }

  @Test
  fun rulesDenyDirectBlockedSightingAndKeepModerationImmutable() {
    val rules = source("firestore.rules")

    assertTrue(rules.contains("function hasActiveBlock"))
    assertTrue(rules.contains("!hasActiveBlock(request.resource.data.ownerId, request.resource.data.reporterId)"))
    assertTrue(rules.contains("function validContentReportCreate"))
    assertTrue(rules.contains("function validUserBlockCreate"))
    assertTrue(rules.contains("allow get: if signedIn()"))
    assertTrue(rules.contains("!exists(/databases/$(database)/documents/userBlocks/$(blockId))"))
    assertTrue(rules.contains("allow list: if false"))
    assertTrue(rules.contains("request.resource.data.reportingUserId == sighting.ownerId"))
    assertTrue(rules.contains("request.resource.data.blockedUserId == sighting.reporterId"))
    assertTrue(rules.contains("allow update, delete: if false"))
  }

  private fun source(relativePath: String): String = File(repoRoot(), relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
