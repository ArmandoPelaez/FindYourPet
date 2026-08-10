package com.findyourpet.app

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.findyourpet.app.ui.theme.AppFontFamily
import com.findyourpet.app.ui.theme.AppTypography
import org.junit.Assert.assertTrue
import org.junit.Test

class TypographyConsistencyTest {
  private val styles: List<TextStyle> = with(AppTypography) {
    listOf(
      displayLarge,
      displayMedium,
      displaySmall,
      headlineLarge,
      headlineMedium,
      headlineSmall,
      titleLarge,
      titleMedium,
      titleSmall,
      bodyLarge,
      bodyMedium,
      bodySmall,
      labelLarge,
      labelMedium,
      labelSmall,
    )
  }

  @Test
  fun everyMaterialTypographyRoleUsesTheAppFontFamily() {
    assertTrue(styles.all { it.fontFamily == AppFontFamily })
  }

  @Test
  fun everyTypographyRoleUsesAFontWeightBundledByTheApp() {
    val bundledWeights = setOf(FontWeight.Normal, FontWeight.Medium, FontWeight.Bold)
    assertTrue(styles.all { it.fontWeight in bundledWeights })
  }
}
