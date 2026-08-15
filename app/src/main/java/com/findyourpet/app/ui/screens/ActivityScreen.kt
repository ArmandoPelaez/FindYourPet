package com.findyourpet.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.ui.components.EmptyState
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityScreen(
    viewModel: PetViewModel,
    onBackClick: (() -> Unit)? = null,
    onSightingClick: (String) -> Unit = {},
) {
    val sightingsState by viewModel.receivedSightingsState.collectAsState()
    val sightings = sightingsState.data
    val posts by viewModel.allPosts.collectAsState()
    val postsById = remember(posts) { posts.associateBy(PetPostEntity::id) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.topBar),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .height(AppSpacing.homeHeaderHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    onBackClick?.let { navigateBack ->
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                    Text(
                        text = "Actividad",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = AppSpacing.sm)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SyncStatusBanner(state = sightingsState)
            when {
                sightings.isEmpty() -> ActivityEmptyState(
                    isLoading = sightingsState.isLoading,
                    hasError = sightingsState.hasError,
                    message = sightingsState.errorMessage
                )
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("activity-list"),
                    contentPadding = PaddingValues(
                        start = AppSpacing.md,
                        top = AppSpacing.md,
                        end = AppSpacing.md,
                        bottom = AppSpacing.actionBottom,
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.listGap)
                ) {
                    items(sightings, key = { it.id }) { sighting ->
                        ActivityItem(
                            sighting = sighting,
                            petPost = postsById[sighting.postId],
                            onClick = { onSightingClick(sighting.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityEmptyState(
    isLoading: Boolean,
    hasError: Boolean,
    message: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg)
            .testTag("activity-empty-state"),
        contentAlignment = Alignment.Center
    ) {
        EmptyState(
            title = when {
                hasError -> "No se pudo cargar la actividad"
                isLoading -> "Cargando actividad"
                else -> "Sin actividad recibida"
            },
            message = when {
                hasError -> message ?: "Intenta nuevamente más tarde."
                isLoading -> "Estamos buscando avistamientos recibidos."
                else -> "Los nuevos avistamientos de tus publicaciones aparecerán aquí."
            },
            icon = if (hasError) Icons.Filled.ErrorOutline else Icons.AutoMirrored.Outlined.EventNote,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun ActivityItem(
    sighting: SightingAlertEntity,
    petPost: PetPostEntity?,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val imageUri = sighting.photoUri.ifBlank { petPost?.photoUri.orEmpty() }
    val formattedTime = remember(sighting.timestamp) {
        SimpleDateFormat("dd MMM, HH:mm", Locale("es", "ES")).format(Date(sighting.timestamp))
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity-item-${sighting.id}"),
        shape = AppShapes.content,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.compactCardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUri.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = petPost?.petName ?: "Imagen del avistamiento",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(AppSpacing.avatarMedium)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(AppSpacing.avatarMedium)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.fieldGap))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                Text(
                    text = petPost?.petName?.takeIf { it.isNotBlank() } ?: "Avistamiento recibido",
                    style = MaterialTheme.typography.titleSmall
                )
                ActivityMetadataRow(
                    icon = Icons.AutoMirrored.Outlined.EventNote,
                    value = "Avistamiento"
                )
                ActivityMetadataRow(
                    icon = Icons.Filled.LocationOn,
                    value = sighting.locationName.ifBlank { "Ubicación no disponible" }
                )
                ActivityMetadataRow(
                    icon = Icons.Filled.AccessTime,
                    value = formattedTime
                )
            }
        }
    }
}

@Composable
private fun ActivityMetadataRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(AppSpacing.iconSmall)
        )
        Spacer(modifier = Modifier.width(AppSpacing.sm))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
