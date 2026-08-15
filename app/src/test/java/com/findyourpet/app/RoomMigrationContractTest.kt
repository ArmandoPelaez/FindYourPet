package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomMigrationContractTest {
  @Test
  fun directedMigrationDropsOnlyRetiredChatTablesAndPreservesRoomSafety() {
    val database = File(
      repoRoot(),
      "app/src/main/java/com/findyourpet/app/data/local/AppDatabase.kt"
    ).readText()

    assertTrue(database.contains("version = 10"))
    assertTrue(database.contains("Migration(9, 10)"))
    assertTrue(database.contains("DROP TABLE IF EXISTS chat_messages"))
    assertTrue(database.contains("DROP TABLE IF EXISTS chat_sessions"))
    assertTrue(database.contains("MIGRATION_9_10"))
    assertTrue(!database.contains("fallbackToDestructiveMigration"))
    assertTrue(database.contains("ContentReportEntity::class"))
    assertTrue(database.contains("UserBlockEntity::class"))
    assertTrue(database.contains("SightingAlertEntity::class"))
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
