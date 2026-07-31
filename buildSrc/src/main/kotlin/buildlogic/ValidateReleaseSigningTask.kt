package buildlogic

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class ValidateReleaseSigningTask : DefaultTask() {
  @get:Input
  abstract val keystorePath: Property<String>

  @get:Internal
  abstract val storePassword: Property<String>

  @get:Internal
  abstract val keyAlias: Property<String>

  @get:Internal
  abstract val keyPassword: Property<String>

  @TaskAction
  fun validate() {
    val missing = buildList {
      if (!File(keystorePath.get()).isFile) add("KEYSTORE_PATH file")
      if (storePassword.orNull.isNullOrBlank()) add("STORE_PASSWORD")
      if (keyAlias.orNull.isNullOrBlank()) add("KEY_ALIAS")
      if (keyPassword.orNull.isNullOrBlank()) add("KEY_PASSWORD")
    }

    check(missing.isEmpty()) {
      "Release signing is not ready. Missing: ${missing.joinToString()}. " +
        "Set secrets outside the repository before building a release artifact."
    }
  }
}
