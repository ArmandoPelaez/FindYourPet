package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreRulesStaticTest {
  private val rulesText = File(repoRoot(), "firestore.rules").readText()

  @Test
  fun rulesRequireAuthenticationAndDefaultDeny() {
    assertTrue(rulesText.contains("return request.auth != null"))
    assertTrue(rulesText.contains("match /{document=**}"))
    assertTrue(rulesText.contains("allow read, write: if false"))
  }

  @Test
  fun chatWritesAreRetiredWhileHistoricalParticipantReadsRemainExplicit() {
    assertTrue(rulesText.contains("match /chatSessions/{chatId}"))
    assertTrue(rulesText.contains("allow get: if isChatParticipant(resource.data)"))
    assertTrue(rulesText.contains("allow list: if signedIn()"))
    assertTrue(rulesText.contains("allow create, update, delete: if false"))
    assertTrue(rulesText.contains("match /messages/{messageId}"))
    assertTrue(rulesText.contains("allow read: if isChatParticipant(get(/databases/$(database)/documents/chatSessions/$(chatId)).data)"))
    assertTrue(rulesText.contains("allow create, update, delete: if false"))
    assertTrue(rulesText.contains("match /contactGrants/{grantId}"))
    assertTrue(rulesText.contains("allow read, write: if false"))
  }

  @Test
  fun newNotificationsOnlyUseSightingRouting() {
    assertTrue(rulesText.contains("request.resource.data.type == 'ALERT'"))
    assertTrue(rulesText.contains("validSightingNotificationCreate(request.resource.data)"))
    assertTrue(rulesText.contains("data.targetId == data.sightingId"))
    assertTrue(rulesText.contains("!data.keys().hasAny(['chatId'])"))
    assertTrue(!rulesText.contains("validChatNotificationCreate"))
    assertTrue(!rulesText.contains("validLegacySightingNotificationCreate"))
  }

  @Test
  fun nonChatSightingAndModerationRulesRemainPresent() {
    assertTrue(rulesText.contains("match /sightings/{sightingId}"))
    assertTrue(rulesText.contains("!hasActiveBlock(request.resource.data.ownerId, request.resource.data.reporterId)"))
    assertTrue(rulesText.contains("match /contentReports/{reportId}"))
    assertTrue(rulesText.contains("match /userBlocks/{blockId}"))
    assertTrue(rulesText.contains("allow update, delete: if false"))
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
