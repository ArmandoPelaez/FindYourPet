package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class BottomPrimaryActionBannerPresentationStaticTest {
  private val root: File = repoRoot()

  @Test
  fun bottomNavigationUsesDedicatedOpacityWithoutChangingSharedBannerToken() {
    val designTokens = source("app/src/main/java/com/findyourpet/app/ui/theme/DesignTokens.kt")
    val components = source("app/src/main/java/com/findyourpet/app/ui/components/CommonComponents.kt")

    assertTrue(designTokens.contains("const val banner = 0.96f"))
    assertTrue(designTokens.contains("const val bottomNavigation = 0.88f"))

    val bannerStart = components.indexOf("fun BottomPrimaryActionBanner(")
    val bannerEnd = components.indexOf("fun <T> SyncStatusBanner(")
    assertTrue(bannerStart >= 0)
    assertTrue(bannerEnd > bannerStart)

    val bannerSource = components.substring(bannerStart, bannerEnd)
    assertTrue(bannerSource.contains("surfaceVariant.copy(alpha = AppOpacity.bottomNavigation)"))
    assertTrue(!bannerSource.contains("AppOpacity.banner"))
    assertTrue(bannerSource.contains("navigationBarsPadding()"))
    assertTrue(bannerSource.contains("AppShapes.card"))
  }

  private fun source(relativePath: String): String = File(root, relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
