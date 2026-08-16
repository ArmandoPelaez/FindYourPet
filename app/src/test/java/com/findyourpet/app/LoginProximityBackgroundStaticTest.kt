package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginProximityBackgroundStaticTest {
  @Test
  fun proximityBackground_isLocalAdaptiveAndThemeAware() {
    val source = sourceFile("app/src/main/java/com/findyourpet/app/ui/components/LoginProximityBackground.kt")

    listOf(
      "Canvas(",
      ".fillMaxSize()",
      "drawLine(",
      "drawCircle(",
      "size.minDimension",
      "MaterialTheme.colorScheme",
      "AppOpacity",
      "AppSpacing.borderWidth",
    ).forEach { marker ->
      assertTrue("Proximity background must contain: $marker", source.contains(marker))
    }

    listOf("GoogleMap", "Maps", "network", "Location", "Firebase", "ViewModel").forEach { forbidden ->
      assertFalse("Proximity background must not depend on: $forbidden", source.contains(forbidden))
    }
  }

  @Test
  fun authScreen_integratesDecorationWithoutReplacingAuthenticationActions() {
    val source = sourceFile("app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt")

    assertTrue(source.contains("LoginProximityBackground()"))
    listOf(
      "viewModel.signInWithEmail(email, password)",
      "viewModel.signUpWithEmail(email, password, displayName)",
      "viewModel.signInWithGoogleIdToken(idToken)",
      "contentDescription = \"Continuar con Google\"",
    ).forEach { marker ->
      assertTrue("AuthScreen must preserve: $marker", source.contains(marker))
    }
  }

  private fun sourceFile(path: String): String {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    val root = generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
    return File(root, path).readText()
  }
}
