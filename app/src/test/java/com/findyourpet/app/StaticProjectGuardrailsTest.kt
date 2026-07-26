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
    val androidNamespace = "http://schemas.android.com/apk/res/android"
    val permissions = manifestDocument().getElementsByTagName("uses-permission").let { nodes ->
      (0 until nodes.length)
        .map { nodes.item(it) as Element }
        .map { it.getAttributeNS(androidNamespace, "name") }
        .sorted()
    }

    assertEquals(listOf("android.permission.INTERNET"), permissions)
  }

  @Test
  fun manifest_disablesAndroidBackup() {
    val androidNamespace = "http://schemas.android.com/apk/res/android"
    val application = manifestDocument().getElementsByTagName("application").item(0) as Element

    assertEquals("false", application.getAttributeNS(androidNamespace, "allowBackup"))
    assertEquals("@xml/backup_rules", application.getAttributeNS(androidNamespace, "fullBackupContent"))
    assertEquals("@xml/data_extraction_rules", application.getAttributeNS(androidNamespace, "dataExtractionRules"))
  }

  @Test
  fun backupRules_excludeSensitiveLocalStorageDomains() {
    val document = parseXml("app/src/main/res/xml/backup_rules.xml")
    val rootElement = document.documentElement
    val excludedDomains = rootElement.excludedDomains()

    assertEquals("full-backup-content", rootElement.tagName)
    assertEquals(sensitiveBackupDomains, excludedDomains)
  }

  @Test
  fun dataExtractionRules_excludeSensitiveLocalStorageDomainsForCloudAndTransfer() {
    val document = parseXml("app/src/main/res/xml/data_extraction_rules.xml")
    val rootElement = document.documentElement

    assertEquals("data-extraction-rules", rootElement.tagName)
    assertEquals(sensitiveBackupDomains, rootElement.childElement("cloud-backup").excludedDomains())
    assertEquals(sensitiveBackupDomains, rootElement.childElement("device-transfer").excludedDomains())
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
    val unsupportedClaims = listOf(
      "privacidad",
      "cifrado",
      "cifrada",
      "encript",
      "tiempo real",
      "realtime",
      "autorizacion",
      "autorización",
      "verificado",
      "verificada"
    )
    val failures = checkedFiles.flatMap { file ->
      val text = file.readText()
      val lowerText = text.lowercase()
      val badEncoding = mojibakeMarkers.filter { it in text }
      val badClaims = unsupportedClaims.filter { it in lowerText }
      (badEncoding + badClaims).map { marker -> "${file.relativeTo(root).path}: $marker" }
    }

    assertTrue("Unexpected text markers in main sources: $failures", failures.isEmpty())
  }

  private val sensitiveBackupDomains = setOf("root", "file", "database", "sharedpref", "external")

  private fun manifestDocument() = parseXml("app/src/main/AndroidManifest.xml")

  private fun parseXml(relativePath: String) =
    DocumentBuilderFactory.newInstance().apply {
      isNamespaceAware = true
    }.newDocumentBuilder().parse(File(root, relativePath))

  private fun Element.childElement(tagName: String): Element {
    val nodes = getElementsByTagName(tagName)
    require(nodes.length == 1) { "Expected exactly one <$tagName> element" }
    return nodes.item(0) as Element
  }

  private fun Element.excludedDomains(): Set<String> {
    val excludedDomains = getElementsByTagName("exclude").let { nodes ->
      (0 until nodes.length)
        .map { nodes.item(it) as Element }
        .filter { it.getAttribute("path") == "." }
        .map { it.getAttribute("domain") }
        .toSet()
    }
    return excludedDomains
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
