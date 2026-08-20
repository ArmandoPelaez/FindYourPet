package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class BottomPrimaryActionBannerPresentationStaticTest {
  private val root: File = repoRoot()

  @Test
  fun bottomNavigationUsesSharedReportStructureAndSemanticTokens() {
    val designTokens = source("app/src/main/java/com/findyourpet/app/ui/theme/DesignTokens.kt")
    val components = source("app/src/main/java/com/findyourpet/app/ui/components/CommonComponents.kt")

    assertTrue(designTokens.contains("const val banner = 0.96f"))
    assertTrue(designTokens.contains("const val bottomNavigation = 0.88f"))
    assertTrue(designTokens.contains("val bannerHeight: Dp = 60.dp"))
    assertTrue(designTokens.contains("val bottomNavigation: Dp = 6.dp"))
    assertTrue(designTokens.contains("val bottomNavigationIconSlotHeight: Dp = 40.dp"))
    assertTrue(designTokens.contains("val bottomNavigationIcon: Dp = 22.dp"))
    assertTrue(designTokens.contains("val bottomNavigationReportActionSize: Dp = 40.dp"))
    assertTrue(designTokens.contains("val bottomNavigationTouchTarget: Dp = 48.dp"))
    assertTrue(!designTokens.contains("bottomNavigationWellSize"))
    assertTrue(!designTokens.contains("bottomNavigationActionLift"))
    assertTrue(!designTokens.contains("bottomNavigationDividerArcHeight"))

    val bannerStart = components.indexOf("fun BottomPrimaryActionBanner(")
    val bannerEnd = components.indexOf("fun <T> SyncStatusBanner(")
    assertTrue(bannerStart >= 0)
    assertTrue(bannerEnd > bannerStart)

    val bannerSource = components.substring(bannerStart, bannerEnd)
    assertTrue(bannerSource.contains("bottomNavigationSurfaceColor()"))
    assertTrue(!bannerSource.contains("AppOpacity.banner"))
    assertTrue(bannerSource.contains("navigationBarsPadding()"))
    assertTrue(!bannerSource.contains("AppShapes.card"))
    assertTrue(bannerSource.contains("BoxWithConstraints"))
    assertTrue(bannerSource.contains("shape = AppShapes.button"))
    assertTrue(bannerSource.contains("bottomNavigationMaxWidth"))
    assertTrue(bannerSource.contains("horizontalMargin = AppSpacing.contentInset"))
    assertTrue(!bannerSource.contains("bottomNavigationSmallBreakpoint"))
    assertTrue(!bannerSource.contains("bottomNavigationLargeBreakpoint"))
    assertTrue(!bannerSource.contains("padding(horizontal = AppSpacing.lg)"))
    assertTrue(bannerSource.contains("AppElevation.bottomNavigation"))
    assertTrue(bannerSource.contains("bottomNavigationIconSlotHeight"))
    assertTrue(bannerSource.contains("bottomNavigationReportActionSize"))
    assertTrue(bannerSource.contains("bottomNavigationTouchTarget"))
    val labels = listOf("Inicio", "Perfil", "Reportar", "Actividad", "Alertas")
    var previousIndex = -1
    labels.forEach { label ->
      val index = bannerSource.indexOf("label = \"$label\"")
      assertTrue("Missing bottom navigation label: $label", index >= 0)
      assertTrue("Bottom navigation order is incorrect at $label", index > previousIndex)
      previousIndex = index
    }
    assertTrue(!bannerSource.contains("label = \"Publicar\""))
    assertTrue(bannerSource.contains("contentDescription = \"Reportar\""))
    assertTrue(bannerSource.contains("icon = Icons.Filled.Pets"))
    assertTrue(bannerSource.contains("onNotificationsClick"))
    assertTrue(bannerSource.contains("onActivityClick"))
    assertTrue(bannerSource.contains("unreadNotificationsCount"))
    assertTrue(bannerSource.contains("Icons.Outlined.NotificationsNone"))
    assertTrue(bannerSource.contains("Icons.Filled.Home"))
    assertTrue(!bannerSource.contains("BottomNavigationContextualAction"))
    assertTrue(!bannerSource.contains("contextualCreateAction"))
    assertTrue(bannerSource.contains("overflow = TextOverflow.Clip"))
    assertTrue(!bannerSource.contains("overflow = TextOverflow.Ellipsis"))

    val itemStart = components.indexOf("private fun RowScope.BottomNavigationItem(")
    val contentStart = components.indexOf("private fun BottomNavigationItemContent(", itemStart)
    val contentEnd = components.indexOf("@Composable\nfun <T> SyncStatusBanner", contentStart)
    assertTrue(itemStart >= 0)
    assertTrue(contentStart > itemStart)
    assertTrue(contentEnd > contentStart)

    val itemSource = components.substring(itemStart, contentStart)
    val contentSource = components.substring(contentStart, contentEnd)
    assertTrue(itemSource.contains("IconButton("))
    assertTrue(itemSource.contains("BottomNavigationItemContent("))
    assertTrue(itemSource.contains("bottomNavigationTouchTarget"))
    assertTrue(!itemSource.contains("FilledIconButton"))
    assertTrue(!itemSource.contains("if (isCreateAction)"))
    assertTrue(contentSource.contains("Column("))
    assertTrue(contentSource.contains("bottomNavigationIconSlotHeight"))
    assertTrue(contentSource.contains("bottomNavigationLabelGap"))
    assertTrue(contentSource.contains("bottomNavigationReportActionSize"))
    assertTrue(contentSource.contains("bottomNavigationIcon"))
    assertTrue(contentSource.contains("MaterialTheme.colorScheme.primary"))
    assertTrue(contentSource.contains("MaterialTheme.colorScheme.onPrimary"))
    assertTrue(!contentSource.contains("FilledIconButton"))
    assertTrue(!contentSource.contains("bottomNavigationWellSize"))
    assertTrue(!contentSource.contains("bottomNavigationActionLift"))
    assertTrue(!contentSource.contains(".offset("))
    assertTrue(!contentSource.contains("BottomNavigationTopDivider"))
    assertTrue(!components.contains("BottomNavigationTopDivider"))
    assertTrue(!components.contains("Canvas("))
    assertTrue(!components.contains("Path"))
  }

  private fun source(relativePath: String): String = File(root, relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
