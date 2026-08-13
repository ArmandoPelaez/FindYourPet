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
    assertTrue(bannerSource.contains("bottomNavigationSurfaceColor()"))
    assertTrue(bannerSource.contains("BottomNavigationTopDivider()"))
    assertTrue(!bannerSource.contains("AppOpacity.banner"))
    assertTrue(bannerSource.contains("navigationBarsPadding()"))
    assertTrue(!bannerSource.contains("AppShapes.card"))
    assertTrue(!bannerSource.contains("shape = AppShapes."))
    assertTrue(!bannerSource.contains("padding(horizontal = AppSpacing.lg)"))
    assertTrue(bannerSource.contains("bottomNavigationWellSize"))
    assertTrue(bannerSource.contains("bottomNavigationActionLift"))
    assertTrue(bannerSource.contains("bottomNavigationCreateActionSize"))
    assertTrue(bannerSource.contains("bottomNavigationIconSlotHeight"))
    val labels = listOf("Inicio", "Perfil", "Publicar", "Mensajes", "Alertas")
    var previousIndex = -1
    labels.forEach { label ->
      val index = bannerSource.indexOf("label = \"$label\"")
      assertTrue("Missing bottom navigation label: $label", index >= 0)
      assertTrue("Bottom navigation order is incorrect at $label", index > previousIndex)
      previousIndex = index
    }
    assertTrue(bannerSource.contains("onNotificationsClick"))
    assertTrue(bannerSource.contains("unreadNotificationsCount"))
    assertTrue(bannerSource.contains("Icons.Outlined.NotificationsNone"))
    assertTrue(bannerSource.contains("Icons.Filled.Home"))
    assertTrue(bannerSource.contains("AppSpacing.bottomNavigationCreateActionSize"))
  }

  private fun source(relativePath: String): String = File(root, relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
