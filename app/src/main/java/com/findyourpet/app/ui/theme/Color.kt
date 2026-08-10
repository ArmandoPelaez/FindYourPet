package com.findyourpet.app.ui.theme

import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2B2B2B)

val ElegantPurplePrimary = Color(0xFFF35D38)
val ElegantPurpleContainer = Color(0xFF3A1E13)
val OnElegantPurpleContainer = Color(0xFFFFE1D9)

val CoralPrimary = Color(0xFFF35D38)
val CoralPrimaryContainer = Color(0xFFFFE1D9)
val OnCoralPrimary = Color(0xFFFFFFFF)

val TealSecondary = Color(0xFF4FC3B6)
val TealSecondaryContainer = Color(0xFF0C3C36)
val OnTealSecondary = Color(0xFF001312)

val AlertRed = Color(0xFFCF2A2A)
val AlertRedContainer = Color(0xFF4E1717)
val ReunitedGreen = Color(0xFF4CAF50)
val ReunitedGreenContainer = Color(0xFF113D12)

val WarmBackgroundLight = Color(0xFF171717)
val SurfaceLight = Color(0xFF202020)
val SurfaceVariantLight = Color(0xFF2A2A2A)

/** Brand and semantic colors that are not part of MaterialTheme.colorScheme. */
object AppColors {
    val primary: Color = CoralPrimary
    val primaryContainer: Color = CoralPrimaryContainer
    val onPrimary: Color = OnCoralPrimary
    val alert: Color = AlertRed
    val alertContainer: Color = AlertRedContainer
    val reunited: Color = ReunitedGreen
    val reunitedContainer: Color = ReunitedGreenContainer
    val secondary: Color = TealSecondary
    val secondaryContainer: Color = TealSecondaryContainer
}

