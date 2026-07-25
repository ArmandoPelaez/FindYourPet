package com.findyourpet.app

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

class StaticProjectGuardrailsTest {
  private val root: File = repoRoot()

  @Test
  fun manifest_declaresOnlyInternetPermission() {
    val manifest = File(root, "app/src/main/AndroidManifest.xml")
    val document = DocumentBuilderFactory.newInstance().apply {
      isNamespaceAware = true
    }.newDocumentBuilder().parse(manifest)
    val androidNamespace = "http://schemas.android.com/apk/res/android"
    val permissions = document.getElementsByTagName("uses-permission").let { nodes ->
      (0 until nodes.length)
        .map { nodes.item(it) as Element }
        .map { it.getAttributeNS(androidNamespace, "name") }
        .sorted()
    }

    assertEquals(listOf("android.permission.INTERNET"), permissions)
  }

  @Test
  fun activeGradleConfiguration_doesNotReintroduceFutureFeatureDependencies() {
    val activeConfig = listOf(
      "settings.gradle.kts",
      "build.gradle.kts",
      "app/build.gradle.kts",
      "gradle.properties"
    ).joinToString("\n") { relativePath ->
      File(root, relativePath).readText()
    }

    val forbiddenPatterns = listOf(
      Regex("""id\("com\.google\.gms\.google-services"\)"""),
      Regex("""id\("com\.google\.android\.libraries\.mapsplatform\.secrets-gradle-plugin"\)"""),
      Regex("""libs\.plugins\.google[.-]?services"""),
      Regex("""libs\.plugins\.secrets"""),
      Regex("""libs\.firebase"""),
      Regex("""libs\.retrofit"""),
      Regex("""libs\.converter[.-]?moshi"""),
      Regex("""libs\.moshi"""),
      Regex("""libs\.okhttp"""),
      Regex("""libs\.logging[.-]?interceptor"""),
      Regex("""libs\.accompanist[.-]?permissions"""),
      Regex("""libs\.play[.-]?services[.-]?location"""),
      Regex("""libs\.androidx[.-]?camera"""),
      Regex("""googleServices\.missing\.passthrough""")
    )

    val matches = forbiddenPatterns.mapNotNull { pattern ->
      pattern.find(activeConfig)?.value
    }

    assertTrue("Unexpected active future-feature Gradle entries: $matches", matches.isEmpty())
  }

  @Test
  fun mainSourceText_doesNotContainMojibakeOrUnsupportedPrivacyClaims() {
    val sourceRoot = File(root, "app/src/main")
    val checkedFiles = sourceRoot
      .walkTopDown()
      .filter { it.isFile && it.extension in setOf("kt", "xml") }
      .toList()

    val mojibakeMarkers = listOf("Ã", "Â", "\uFFFD")
    val unsupportedClaims = listOf("privacidad", "cifrado", "cifrada", "encript", "tiempo real", "realtime")
    val failures = checkedFiles.flatMap { file ->
      val text = file.readText()
      val lowerText = text.lowercase()
      val badEncoding = mojibakeMarkers.filter { it in text }
      val badClaims = unsupportedClaims.filter { it in lowerText }
      (badEncoding + badClaims).map { marker -> "${file.relativeTo(root).path}: $marker" }
    }

    assertTrue("Unexpected text markers in main sources: $failures", failures.isEmpty())
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
