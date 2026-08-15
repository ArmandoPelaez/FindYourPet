package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PetPostRetiredAttributesStaticTest {
  private val root: File = projectRoot()

  @Test
  fun roomMigration_reconstructsVersionSevenWithoutRetiredColumns() {
    val source = File(root, "app/src/main/java/com/findyourpet/app/data/local/AppDatabase.kt").readText()

    assertTrue(source.contains("version = 10"))
    assertTrue(source.contains("Migration(6, 7)"))
    assertTrue(source.contains("Migration(7, 8)"))
    assertTrue(source.contains("MIGRATION_7_8"))
    assertTrue(source.contains("MIGRATION_8_9"))
    val migrationStart = source.indexOf("private val MIGRATION_7_8")
    val migrationEnd = source.indexOf("private fun dropRetiredContactSharingState", migrationStart)
    val migration = source.substring(migrationStart, migrationEnd)
    assertTrue(migration.contains("INSERT INTO pet_posts_new"))
    assertTrue(migration.contains("SELECT"))
    assertTrue(migration.contains("features, status, photoUri"))
    assertTrue(migration.contains("ownerId, ownerName"))
    assertFalse(migration.contains("characteristics"))
    assertFalse(migration.contains("particularMarks"))
  }

  @Test
  fun currentContracts_doNotModelOrWriteRetiredAttributes() {
    val entitySource = File(root, "app/src/main/java/com/findyourpet/app/data/local/entity/Entities.kt").readText()
    val documentSource = File(root, "app/src/main/java/com/findyourpet/app/data/remote/RemoteDocuments.kt").readText()
    val viewModelSource = File(root, "app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt").readText()
    val mapperSource = File(root, "app/src/main/java/com/findyourpet/app/data/remote/RemoteMappers.kt").readText()

    listOf(entitySource, documentSource, viewModelSource, mapperSource).forEach { source ->
      assertFalse(source.contains("characteristics"))
      assertFalse(source.contains("particularMarks"))
    }
    assertTrue(entitySource.contains("val features: String"))
    assertTrue(mapperSource.contains("\"features\" to features"))
  }

  private fun projectRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
