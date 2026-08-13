package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PetPostParticularMarksStaticTest {
  private val root: File = projectRoot()

  @Test
  fun roomMigration_addsNonNullParticularMarksWithEmptyDefaultAndIsRegistered() {
    val source = File(root, "app/src/main/java/com/findyourpet/app/data/local/AppDatabase.kt").readText()

    assertTrue(source.contains("version = 7"))
    assertTrue(source.contains("Migration(6, 7)"))
    assertTrue(source.contains("ALTER TABLE pet_posts ADD COLUMN particularMarks TEXT NOT NULL DEFAULT ''"))
    assertTrue(source.contains("MIGRATION_6_7"))
  }

  @Test
  fun persistenceAndViewModel_keepParticularMarksIndependentFromCharacteristicsAndFeatures() {
    val entitySource = File(root, "app/src/main/java/com/findyourpet/app/data/local/entity/Entities.kt").readText()
    val viewModelSource = File(root, "app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt").readText()
    val mapperSource = File(root, "app/src/main/java/com/findyourpet/app/data/remote/RemoteMappers.kt").readText()

    assertTrue(entitySource.contains("val characteristics: String = \"\""))
    assertTrue(entitySource.contains("val particularMarks: String = \"\""))
    assertTrue(viewModelSource.contains("particularMarks: String = \"\""))
    assertTrue(viewModelSource.contains("particularMarks = particularMarks"))
    assertTrue(mapperSource.contains("\"particularMarks\" to particularMarks"))
    assertTrue(mapperSource.contains("particularMarks = string(\"particularMarks\")"))
  }

  private fun projectRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
