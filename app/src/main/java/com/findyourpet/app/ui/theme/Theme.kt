package com.findyourpet.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElegantPurplePrimary,
    onPrimary = OnCoralPrimary,
    primaryContainer = ElegantPurpleContainer,
    onPrimaryContainer = OnElegantPurpleContainer,
    secondary = TealSecondary,
    onSecondary = OnTealSecondary,
    secondaryContainer = TealSecondaryContainer,
    background = DarkBackground,
    onBackground = Color(0xFFF2F2F2),
    surface = DarkSurface,
    onSurface = Color(0xFFF2F2F2),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCFCFCF),
    error = AlertRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = AlertRedContainer,
    onErrorContainer = Color(0xFFFFE6E6)
)

private val LightColorScheme = lightColorScheme(
    primary = ElegantPurplePrimary,
    onPrimary = OnCoralPrimary,
    primaryContainer = ElegantPurpleContainer,
    onPrimaryContainer = OnElegantPurpleContainer,
    secondary = TealSecondary,
    onSecondary = OnTealSecondary,
    secondaryContainer = TealSecondaryContainer,
    background = Color(0xFFF7F2ED),
    onBackground = Color(0xFF1D1B20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFFF1EAE4),
    onSurfaceVariant = Color(0xFF4A453E),
    error = AlertRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = AlertRedContainer,
    onErrorContainer = Color(0xFF4E1717)
)

@Composable
fun MascotasPerdidasTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = Shapes(
            extraSmall = AppShapes.chip,
            small = AppShapes.chip,
            medium = AppShapes.content,
            large = AppShapes.emptyState,
            extraLarge = AppShapes.card,
        ),
        content = content
    )
}
