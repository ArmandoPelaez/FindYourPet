package com.findyourpet.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElegantPurplePrimary,
    primaryContainer = ElegantPurpleContainer,
    onPrimary = Color(0xFF381E72),
    onPrimaryContainer = OnElegantPurpleContainer,
    secondary = TealSecondary,
    secondaryContainer = TealSecondaryContainer,
    onSecondary = OnTealSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = AlertRed,
    errorContainer = AlertRedContainer
)

private val LightColorScheme = darkColorScheme(
    primary = ElegantPurplePrimary,
    primaryContainer = ElegantPurpleContainer,
    onPrimary = Color(0xFF381E72),
    onPrimaryContainer = OnElegantPurpleContainer,
    secondary = TealSecondary,
    secondaryContainer = TealSecondaryContainer,
    onSecondary = OnTealSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = AlertRed,
    errorContainer = AlertRedContainer
)

@Composable
fun MascotasPerdidasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        typography = Typography,
        content = content
    )
}
