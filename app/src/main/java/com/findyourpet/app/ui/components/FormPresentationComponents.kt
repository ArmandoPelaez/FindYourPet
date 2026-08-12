package com.findyourpet.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.ui.theme.AppFormTypography
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing

@Composable
fun FormSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun FormFieldLabel(
    text: String,
    required: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.microGap)
    ) {
        Text(
            text = text,
            style = AppFormTypography.label,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (required) {
            Text(
                text = "*",
                style = AppFormTypography.label,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FormFieldPlaceholder(text: String) {
    Text(
        text = text,
        style = AppFormTypography.placeholder,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun FormPhotoUploadSurface(
    selectedPhotoUri: String,
    emptyTitle: String,
    emptyDescription: String? = null,
    photoContentDescription: String,
    onSurfaceClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String,
    emptyStateTestTag: String? = null,
    selectedPhotoTestTag: String? = null,
    emptyContent: (@Composable ColumnScope.() -> Unit)? = null,
    selectedContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val context = LocalContext.current

    Surface(
        shape = AppShapes.content,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(AppSpacing.borderWidth, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .height(AppSpacing.mediaHeight)
            .clip(AppShapes.content)
            .clickable(onClick = onSurfaceClick)
            .testTag(testTag)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedPhotoUri.isBlank()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppSpacing.lg)
                        .then(emptyStateTestTag?.let { Modifier.testTag(it) } ?: Modifier)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(AppSpacing.iconLarge)
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.fieldGap))
                    Text(
                        text = emptyTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    emptyDescription?.let { description ->
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (emptyContent != null) {
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                    }
                    emptyContent?.invoke(this)
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(selectedPhotoUri).crossfade(true).build(),
                    contentDescription = photoContentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(selectedPhotoTestTag?.let { Modifier.testTag(it) } ?: Modifier)
                )
                selectedContent?.invoke(this)
            }
        }
    }
}
