package com.findyourpet.app

import com.findyourpet.app.util.CrashReporter
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseReadinessStaticTest {
  private val root: File = repoRoot()

  @Test
  fun releaseBuild_isMinifiedSignedAndPreflighted() {
    val appGradle = File(root, "app/build.gradle.kts").readText()
    val proguard = File(root, "app/proguard-rules.pro").readText()

    assertTrue(appGradle.contains("isMinifyEnabled = true"))
    assertTrue(appGradle.contains("getDefaultProguardFile(\"proguard-android-optimize.txt\")"))
    assertTrue(appGradle.contains("signingConfig = signingConfigs.getByName(\"release\")"))
    assertTrue(appGradle.contains("validateReleaseSigning"))
    assertTrue(appGradle.contains("assembleRelease"))
    assertTrue(appGradle.contains("bundleRelease"))

    listOf("KEYSTORE_PATH", "STORE_PASSWORD", "KEY_ALIAS", "KEY_PASSWORD").forEach { envName ->
      assertTrue("Missing release signing preflight for $envName", appGradle.contains(envName))
    }

    assertTrue(proguard.contains("-keepattributes SourceFile,LineNumberTable"))
  }

  @Test
  fun crashlytics_isConfiguredForReleaseDiagnostics() {
    val rootGradle = File(root, "build.gradle.kts").readText()
    val appGradle = File(root, "app/build.gradle.kts").readText()
    val catalog = File(root, "gradle/libs.versions.toml").readText()

    assertTrue(catalog.contains("firebaseCrashlyticsGradlePlugin"))
    assertTrue(catalog.contains("firebase-crashlytics"))
    assertTrue(rootGradle.contains("libs.plugins.firebase.crashlytics"))
    assertTrue(appGradle.contains("pluginManager.apply(\"com.google.firebase.crashlytics\")"))
    assertTrue(appGradle.contains("implementation(libs.firebase.crashlytics)"))
  }

  @Test
  fun crashMetadataSanitizer_redactsSensitiveValues() {
    val sensitiveValues = listOf(
      "owner@example.com",
      "+506 8888-9900",
      "9.933300, -84.083300",
      "https://res.cloudinary.com/example/private.jpg",
      "token=abc123"
    )

    sensitiveValues.forEach { value ->
      assertEquals("[redacted]", CrashReporter.sanitizeForCrashMetadata(value))
    }
    assertEquals("feed_loading", CrashReporter.sanitizeForCrashMetadata("feed_loading"))
  }

  @Test
  fun privacyPolicyAndPermissionInventory_arePreparedForPlayReview() {
    val firebaseConfig = File(root, "firebase.json").readText()
    val markdownPolicy = File(root, "docs/privacy-policy.md").readText()
    val publicPolicy = File(root, "public/privacy-policy.html").readText()
    val permissionInventory = File(root, "docs/google-play-permissions.md").readText()
    val dataSafetyDraft = File(root, "docs/google-play-data-safety-draft.md").readText()
    val validationNotes = File(root, "docs/release-validation-prepare-production-release.md").readText()

    assertTrue(firebaseConfig.contains("\"hosting\""))
    assertTrue(firebaseConfig.contains("\"public\": \"public\""))
    assertTrue(publicPolicy.contains("<title>Politica de Privacidad - FindYourPet</title>"))
    assertTrue(markdownPolicy.contains("Firebase Crashlytics"))
    assertTrue(markdownPolicy.contains("Google Play Internal testing"))

    listOf(
      "android.permission.INTERNET",
      "android.permission.CAMERA",
      "android.permission.ACCESS_COARSE_LOCATION",
      "android.permission.ACCESS_FINE_LOCATION"
    ).forEach { permission ->
      assertTrue("Permission inventory missing $permission", permissionInventory.contains(permission))
    }

    assertTrue(validationNotes.contains("findyourpet-db301"))
    assertTrue(validationNotes.contains("Internal testing"))
    assertTrue(validationNotes.contains("public/privacy-policy.html"))

    listOf("Informacion personal", "Fotos y videos", "Ubicacion", "Mensajes", "Diagnostico").forEach { category ->
      assertTrue("Data safety draft missing $category", dataSafetyDraft.contains(category))
    }
  }

  @Test
  fun primaryActions_haveAccessibleLabelsOrVisibleText() {
    val mainSources = File(root, "app/src/main/java/com/findyourpet/app/ui")
      .walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .joinToString("\n") { it.readText() }

    listOf(
      "contentDescription = \"Volver\"",
      "contentDescription = \"Notificaciones\"",
      "contentDescription = \"Chats Privados\"",
      "contentDescription = \"Perfil\"",
      "contentDescription = \"Crear publicacion\"",
      "contentDescription = \"Enviar\"",
      "Foto de la mascota",
      "Foto del avistamiento",
      "Usar ubicacion actual",
      "ENVIAR ALERTA"
    ).forEach { label ->
      assertTrue("Missing accessibility/visible label: $label", mainSources.contains(label))
    }
  }

  @Test
  fun homeTopBar_doesNotDuplicateBottomPrimaryActions() {
    val homeSource = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt").readText()

    assertTrue(homeSource.contains("BottomPrimaryActionBanner("))
    assertTrue(homeSource.contains("onProfileClick = onNavigateToProfile"))
    assertTrue(homeSource.contains("onCreatePostClick = onNavigateToCreate"))
    assertTrue(homeSource.contains("onChatClick = onNavigateToChatList"))
    assertTrue(homeSource.contains("contentDescription = \"Notificaciones\""))
    assertTrue(!homeSource.contains("IconButton(onClick = onNavigateToChatList)"))
    assertTrue(!homeSource.contains("IconButton(onClick = onNavigateToProfile)"))
    assertTrue(!homeSource.contains("ExtendedFloatingActionButton("))
  }

  @Test
  fun createPostForm_omitsCurrentLocationAndRewardInputs() {
    val createPostSource = File(root, "app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt").readText()

    assertTrue(createPostSource.contains("Text(text = \"Ubicacion\""))
    assertTrue(createPostSource.contains("rewardAmount = \"Sin recompensa\""))
    assertTrue(!createPostSource.contains("Usar ubicacion actual"))
    assertTrue(!createPostSource.contains("Ubicacion GPS capturada"))
    assertTrue(!createPostSource.contains("Recompensa ofrecida"))
    assertTrue(!createPostSource.contains("requestCurrentLocation"))
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
