package com.findyourpet.app

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

class StaticProjectGuardrailsTest {
  private val root: File = repoRoot()

  @Test
  fun manifest_declaresOnlyImplementedProductionPermissions() {
    val androidNamespace = "http://schemas.android.com/apk/res/android"
    val permissions = manifestDocument().getElementsByTagName("uses-permission").let { nodes ->
      (0 until nodes.length)
        .map { nodes.item(it) as Element }
        .map { it.getAttributeNS(androidNamespace, "name") }
        .sorted()
    }

    assertEquals(
      listOf(
        "android.permission.ACCESS_COARSE_LOCATION",
         "android.permission.ACCESS_FINE_LOCATION",
         "android.permission.CAMERA",
         "android.permission.INTERNET",
         "android.permission.POST_NOTIFICATIONS"
      ),
      permissions
    )
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
      Regex("""id\("com\.google\.android\.libraries\.mapsplatform\.secrets-gradle-plugin"\)"""),
      Regex("""libs\.plugins\.secrets"""),
      Regex("""libs\.firebase\.ai"""),
      Regex("""libs\.firebase\.appcheck"""),
      Regex("""libs\.retrofit"""),
      Regex("""libs\.converter[.-]?moshi"""),
      Regex("""libs\.moshi"""),
      Regex("""libs\.okhttp"""),
      Regex("""libs\.logging[.-]?interceptor"""),
      Regex("""libs\.accompanist[.-]?permissions"""),
      Regex("""libs\.androidx[.-]?camera"""),
      Regex("""libs\.firebase\.messaging"""),
      Regex("""libs\.firebase\.storage"""),
      Regex("""googleServices\.missing\.passthrough""")
    )

    val matches = forbiddenPatterns.mapNotNull { pattern ->
      pattern.find(activeConfig)?.value
    }

    assertTrue("Unexpected active future-feature Gradle entries: $matches", matches.isEmpty())
  }

  @Test
  fun firebaseAuthAndFirestoreDependencies_haveRealImplementationFiles() {
    val activeConfig = listOf(
      "build.gradle.kts",
      "app/build.gradle.kts",
      "gradle/libs.versions.toml"
    ).joinToString("\n") { relativePath ->
      File(root, relativePath).readText()
    }

    val authDependenciesPresent = listOf(
      "libs.plugins.google.services",
      "libs.firebase.bom",
      "libs.firebase.auth",
      "libs.firebase.firestore",
      "libs.androidx.credentials",
      "libs.googleid"
    ).all { it in activeConfig }

    val implementationFilesPresent = listOf(
      "app/src/main/java/com/findyourpet/app/data/auth/FirebaseAuthRepository.kt",
      "app/src/main/java/com/findyourpet/app/data/profile/FirestoreUserProfileRepository.kt",
      "app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt",
      "firestore.rules"
    ).all { relativePath -> File(root, relativePath).isFile }

    assertTrue(
      "Firebase Auth/Firestore dependencies require real auth, profile, UI, and rules implementation files.",
      !authDependenciesPresent || implementationFilesPresent
    )
  }

  @Test
  fun mainSource_doesNotGrantOwnerPermissionsFromDemoIds() {
    val checkedFiles = File(root, "app/src/main")
      .walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .toList()

    val forbiddenOwnerGrantPatterns = listOf(
      Regex("""currentUser\.id\s*==\s*"owner_1""""),
      Regex("""currentUser\.id\s*==\s*"user_1""""),
      Regex("""ownerId\s*==\s*"owner_1""""),
      Regex("""ownerId\s*==\s*"user_1""""),
      Regex("""\.id\.startsWith\("owner"\)""")
    )

    val failures = checkedFiles.flatMap { file ->
      val text = file.readText()
      forbiddenOwnerGrantPatterns.mapNotNull { pattern ->
        pattern.find(text)?.value?.let { "${file.relativeTo(root).path}: $it" }
      }
    }

    assertTrue("Unexpected demo-id owner grants: $failures", failures.isEmpty())
  }

  @Test
  fun contactSharing_isRetiredFromAppManagedState() {
    val repositoryText = File(root, "app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt").readText()
    val viewModelText = File(root, "app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt").readText()
    val componentText = File(root, "app/src/main/java/com/findyourpet/app/ui/components/CommonComponents.kt").readText()
    val chatText = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/ChatDetailScreen.kt").readText()
    val homeText = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()

    assertTrue(!repositoryText.contains("CONTACT_GRANTS"))
    assertTrue(!repositoryText.contains("OWNER_CONTACT_GRANT"))
    assertTrue(!repositoryText.contains("toggleChatContactSharing"))
    assertTrue(!repositoryText.contains("ContactGrantEntity"))
    assertTrue(!viewModelText.contains("activeContactGrant"))
    assertTrue(!viewModelText.contains("toggleContactSharing"))
    assertTrue(!componentText.contains("ProtectedContactCard"))
    assertFalse(chatText.contains("FindYourPet no solicita ni comparte telefono, email o direccion"))
    assertFalse(chatText.contains("esa decision es tu responsabilidad"))
    assertTrue(!homeText.contains("Dueño: ${'$'}{post.ownerName.take(3)}*** (Protegido)"))
    assertTrue(!homeText.contains("post.ownerPhone"))
    assertTrue(!homeText.contains("post.ownerEmail"))
    assertTrue(!homeText.contains("onContactToggle"))
  }

  @Test
  fun selfSightings_areBlockedAcrossAppEntryPoints() {
    val policyText = File(root, "app/src/main/java/com/findyourpet/app/domain/OwnershipPolicy.kt").readText()
    val validatorText = File(root, "app/src/main/java/com/findyourpet/app/data/product/RealProductValidators.kt").readText()
    val repositoryText = File(root, "app/src/main/java/com/findyourpet/app/data/repository/PetRepository.kt").readText()
    val homeText = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()
    val alertText = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/SightingAlertScreen.kt").readText()

    assertTrue(policyText.contains("fun canReportSighting"))
    assertTrue(policyText.contains("currentUid != ownerId"))
    assertTrue(validatorText.contains("OwnershipPolicy.canReportSighting(reporterId, ownerId)"))
    assertTrue(repositoryText.contains("require(OwnershipPolicy.canReportSighting(reporterId, resolvedOwnerId))"))
    assertTrue(repositoryText.indexOf("require(OwnershipPolicy.canReportSighting(reporterId, resolvedOwnerId))") < repositoryText.indexOf("val sighting = SightingAlertEntity"))
    assertTrue(homeText.contains("canReportSighting = OwnershipPolicy.canReportSighting(currentUser.id, post.ownerId)"))
    assertTrue(homeText.contains("post.status != \"REUNIDO\" && canReportSighting"))
    assertTrue(alertText.contains("if (!OwnershipPolicy.canReportSighting(currentUser.id, pet.ownerId))"))
  }

  @Test
  fun petDetailRoute_isNotPartOfPrimaryNavigation() {
    val mainActivityText = File(root, "app/src/main/java/com/findyourpet/app/MainActivity.kt").readText()
    val homeText = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()

    assertTrue(!mainActivityText.contains("route = \"detail/{postId}\""))
    assertTrue(!mainActivityText.contains("navigate(\"detail/"))
    assertTrue(!homeText.contains("Ver Ficha"))
    assertTrue(homeText.contains("Información reportada"))
    assertTrue(!homeText.contains("Ubicación en la que se perdió"))
  }

  @Test
  fun homePetCard_omitsRemovedVisualElements() {
    val homeText = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()

    assertTrue(!homeText.contains("Desliza a los lados"))
    assertTrue(!homeText.contains("pagerState.currentPage + 1"))
    assertTrue(!homeText.contains("Dueño: ${'$'}{post.ownerName.take(3)}*** (Protegido)"))
    assertTrue(!homeText.contains("label = \"Tipo\""))
    assertTrue(!homeText.contains("label = \"Color\""))
    assertTrue(!homeText.contains("label = \"Señas\""))
    assertTrue(!homeText.contains("PetAttributeGrid"))
    assertTrue(!homeText.contains("InfoPill"))
    assertTrue(!homeText.contains("post.breed"))
    assertTrue(homeText.contains("post.lastSeenLocation"))
  }

  @Test
  fun discoveryFeed_excludesCurrentUsersOwnPostsButProfileKeepsThem() {
    val policyText = File(root, "app/src/main/java/com/findyourpet/app/domain/OwnershipPolicy.kt").readText()
    val viewModelText = File(root, "app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt").readText()
    val profileText = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/ProfileScreen.kt").readText()

    assertTrue(policyText.contains("fun canAppearInDiscoveryFeed"))
    assertTrue(policyText.contains("currentUid != ownerId"))
    assertTrue(viewModelText.contains("currentUser,"))
    assertTrue(viewModelText.contains("OwnershipPolicy.canAppearInDiscoveryFeed(user.id, post.ownerId)"))
    assertTrue(profileText.contains("val myPosts = allPosts.filter { it.ownerId == currentUser.id }"))
    assertTrue(profileText.contains("Estado: ${'$'}{pet.status}"))
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
