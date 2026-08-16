package com.findyourpet.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.MaterialTheme
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppSpacing

/** Decorative, local visual that suggests proximity without representing real geography. */
@Composable
fun LoginProximityBackground(modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(
        modifier = modifier.fillMaxSize(),
    ) {
        val width = size.width
        val height = size.height
        val minimumDimension = size.minDimension
        val strokeWidth = AppSpacing.borderWidth.toPx()
        val zoneRadius = minimumDimension * 0.16f

        val mainMarker = Offset(width * 0.72f, height * 0.24f)
        val nodes = listOf(
            Offset(width * 0.20f, height * 0.28f),
            Offset(width * 0.42f, height * 0.16f),
            Offset(width * 0.27f, height * 0.52f),
            Offset(width * 0.58f, height * 0.48f),
            Offset(width * 0.82f, height * 0.62f),
        )

        listOf(
            Offset(width * 0.25f, height * 0.28f),
            Offset(width * 0.68f, height * 0.42f),
            Offset(width * 0.76f, height * 0.70f),
        ).forEach { center ->
            drawCircle(
                color = secondaryColor.copy(alpha = AppOpacity.iconSurface),
                radius = zoneRadius,
                center = center,
            )
            drawCircle(
                color = secondaryColor.copy(alpha = AppOpacity.border),
                radius = zoneRadius,
                center = center,
                style = Stroke(width = strokeWidth),
            )
        }

        nodes.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = outlineColor.copy(alpha = AppOpacity.border),
                start = start,
                end = end,
                strokeWidth = strokeWidth,
            )
        }

        nodes.take(3).forEach { node ->
            drawLine(
                color = primaryColor.copy(alpha = AppOpacity.iconSurface),
                start = node,
                end = mainMarker,
                strokeWidth = strokeWidth,
            )
        }

        nodes.forEach { node ->
            drawCircle(
                color = primaryColor.copy(alpha = AppOpacity.subtleSurface),
                radius = minimumDimension * 0.018f,
                center = node,
            )
        }

        drawCircle(
            color = primaryColor.copy(alpha = AppOpacity.iconSurface),
            radius = zoneRadius * 0.46f,
            center = mainMarker,
        )
        drawCircle(
            color = primaryColor.copy(alpha = AppOpacity.border),
            radius = zoneRadius * 0.46f,
            center = mainMarker,
            style = Stroke(width = strokeWidth),
        )
        drawCircle(
            color = primaryColor,
            radius = minimumDimension * 0.026f,
            center = mainMarker,
        )
    }
}
