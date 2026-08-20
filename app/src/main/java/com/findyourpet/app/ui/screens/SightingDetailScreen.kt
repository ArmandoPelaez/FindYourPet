package com.findyourpet.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.EmptyState
import com.findyourpet.app.ui.components.ReadOnlyMapSheet
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel
import com.findyourpet.app.ui.viewmodel.ContentReportReason
import com.findyourpet.app.ui.viewmodel.ModerationOperationStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SightingDetailScreen(
    viewModel: PetViewModel,
    sightingId: String,
    onBackClick: () -> Unit
) {
    val sightingState by viewModel.sightingDetailState.collectAsState()
    val petContextState by viewModel.sightingDetailPostState.collectAsState()
    val sighting = sightingState.data
    val petPost = petContextState.data
    val currentUser by viewModel.currentUser.collectAsState()
    val reporterBlocked by viewModel.sightingReporterBlocked.collectAsState()
    val reportState by viewModel.reportOperationState.collectAsState()
    val blockState by viewModel.blockOperationState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocation by remember(sightingId) { mutableStateOf(false) }
    var showActionsMenu by remember(sightingId) { mutableStateOf(false) }
    var showReportDialog by remember(sightingId) { mutableStateOf(false) }
    var showBlockDialog by remember(sightingId) { mutableStateOf(false) }
    var selectedReason by remember(sightingId) { mutableStateOf<ContentReportReason?>(null) }
    val canModerate = sighting?.ownerId == currentUser.id
    val canBlock = canModerate && sighting?.reporterId?.isNotBlank() == true && !reporterBlocked
    val navigateBack = {
        viewModel.clearSightingDetail()
        onBackClick()
    }

    LaunchedEffect(sightingId) {
        viewModel.selectSightingDetail(sightingId)
    }

    LaunchedEffect(
        sightingId,
        sightingState.isLoading,
        sightingState.data,
        sightingState.errorMessage
    ) {
        if (!sightingState.isLoading && sightingState.data == null && sightingState.hasError) {
            navigateBack()
        }
    }

    LaunchedEffect(sighting?.id, currentUser.id, sighting?.reporterId) {
        sighting?.let(viewModel::refreshSightingModeration)
    }

    LaunchedEffect(reportState.status, reportState.message) {
        if (reportState.status != ModerationOperationStatus.SUBMITTING && reportState.message != null) {
            snackbarHostState.showSnackbar(reportState.message!!)
            if (reportState.status == ModerationOperationStatus.SUCCESS) {
                showReportDialog = false
                selectedReason = null
            }
            viewModel.resetReportOperationState()
        }
    }

    LaunchedEffect(blockState.status, blockState.message) {
        if (blockState.status != ModerationOperationStatus.SUBMITTING && blockState.message != null) {
            snackbarHostState.showSnackbar(blockState.message!!)
            if (blockState.status == ModerationOperationStatus.SUCCESS) {
                showBlockDialog = false
                showActionsMenu = false
            }
            viewModel.resetBlockOperationState()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Text(
                        text = "Detalle del avistamiento",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = AppSpacing.sm)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (canModerate) {
                        Box {
                            IconButton(
                                onClick = { showActionsMenu = true },
                                modifier = Modifier.testTag("sighting-detail-moderation-menu")
                            ) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Acciones de moderacion")
                            }
                            DropdownMenu(
                                expanded = showActionsMenu,
                                onDismissRequest = { showActionsMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Reportar contenido") },
                                    onClick = {
                                        showActionsMenu = false
                                        selectedReason = null
                                        showReportDialog = true
                                    },
                                    modifier = Modifier.testTag("sighting-detail-report-action")
                                )
                                if (canBlock) {
                                    DropdownMenuItem(
                                        text = { Text("Bloquear usuario") },
                                        onClick = {
                                            showActionsMenu = false
                                            showBlockDialog = true
                                        },
                                        modifier = Modifier.testTag("sighting-detail-block-action")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SyncStatusBanner(state = sightingState)
            when {
                sightingState.isLoading && sighting == null -> SightingDetailLoading()
                sightingState.hasError || sighting == null -> SightingDetailError(
                    message = sightingState.errorMessage ?: "No se pudo cargar el avistamiento."
                )
                else -> SightingDetailContent(
                    sighting = sighting,
                    petPost = petPost,
                    petContextLoading = petContextState.isLoading,
                    petContextError = petContextState.hasError,
                    context = context,
                    onShowLocation = { showLocation = true }
                )
            }
        }
    }

    if (showLocation && sighting?.hasUsableLocation() == true) {
        ReadOnlyMapSheet(
            locationName = sighting.locationName,
            latitude = sighting.latitude.takeIf { sighting.hasUsableCoordinates() },
            longitude = sighting.longitude.takeIf { sighting.hasUsableCoordinates() },
            onDismiss = { showLocation = false }
        )
    }

    if (showReportDialog && sighting != null) {
        AlertDialog(
            onDismissRequest = {
                if (reportState.status != ModerationOperationStatus.SUBMITTING) {
                    showReportDialog = false
                    selectedReason = null
                }
            },
            title = { Text("Reportar contenido") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    Text("Selecciona un motivo para este reporte.")
                    ContentReportReason.entries.forEach { reason ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                enabled = reportState.status != ModerationOperationStatus.SUBMITTING
                            )
                            Text(reason.label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedReason?.let { viewModel.reportSightingContent(sighting.id, it) }
                    },
                    enabled = selectedReason != null && reportState.status != ModerationOperationStatus.SUBMITTING
                ) {
                    if (reportState.status == ModerationOperationStatus.SUBMITTING) {
                        CircularProgressIndicator(modifier = Modifier.size(AppSpacing.iconMedium))
                    } else {
                        Text("Confirmar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (reportState.status != ModerationOperationStatus.SUBMITTING) {
                            showReportDialog = false
                            selectedReason = null
                        }
                    },
                    enabled = reportState.status != ModerationOperationStatus.SUBMITTING
                ) { Text("Cancelar") }
            }
        )
    }

    if (showBlockDialog && sighting != null && canBlock) {
        AlertDialog(
            onDismissRequest = {
                if (blockState.status != ModerationOperationStatus.SUBMITTING) showBlockDialog = false
            },
            title = { Text("Bloquear usuario") },
            text = { Text("No recibiras nuevos avistamientos de este usuario para tus publicaciones.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.blockSightingReporter(sighting.id) },
                    enabled = blockState.status != ModerationOperationStatus.SUBMITTING
                ) {
                    if (blockState.status == ModerationOperationStatus.SUBMITTING) {
                        CircularProgressIndicator(modifier = Modifier.size(AppSpacing.iconMedium))
                    } else {
                        Text("Bloquear")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { if (blockState.status != ModerationOperationStatus.SUBMITTING) showBlockDialog = false },
                    enabled = blockState.status != ModerationOperationStatus.SUBMITTING
                ) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SightingDetailLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sighting-detail-loading"),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(AppSpacing.progressIndicator))
    }
}

@Composable
private fun SightingDetailError(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg)
            .testTag("sighting-detail-error"),
        contentAlignment = Alignment.Center
    ) {
        EmptyState(
            title = "Avistamiento no disponible",
            message = message,
            icon = Icons.Filled.ErrorOutline,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SightingDetailContent(
    sighting: SightingAlertEntity,
    petPost: PetPostEntity?,
    petContextLoading: Boolean,
    petContextError: Boolean,
    context: android.content.Context,
    onShowLocation: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sighting-detail-content"),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sectionGap)
    ) {
        item {
            if (petPost != null) {
                PetContextCard(post = petPost, context = context)
            } else {
                MissingPetContextCard(isLoading = petContextLoading, hasError = petContextError)
            }
        }
        item {
            SightingInfoCard(
                sighting = sighting,
                onShowLocation = onShowLocation
            )
        }
        if (sighting.photoUri.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sighting-detail-photo"),
                    shape = AppShapes.content,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(sighting.photoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto del avistamiento",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppSpacing.mediaHeight)
                            .clip(AppShapes.content)
                    )
                }
            }
        }
        if (sighting.notes.isNotBlank()) {
            item {
                DetailTextCard(
                    title = "Comentario",
                    value = sighting.notes,
                    icon = Icons.Filled.Description,
                    testTag = "sighting-detail-notes"
                )
            }
        }
    }
}

@Composable
private fun PetContextCard(post: PetPostEntity, context: android.content.Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sighting-detail-pet"),
        shape = AppShapes.content,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (post.photoUri.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(post.photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = post.petName,
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
            Column {
                Text(post.petName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = post.species,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MissingPetContextCard(isLoading: Boolean, hasError: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sighting-detail-pet-context-missing"),
        shape = AppShapes.content,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.cardPadding)) {
            Text(
                text = if (isLoading) "Cargando mascota" else "Mascota no disponible",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = when {
                    isLoading -> "La información de la mascota se está cargando."
                    hasError -> "El avistamiento está disponible, pero no se pudo cargar la publicación asociada."
                    else -> "No hay información contextual de la mascota."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SightingInfoCard(
    sighting: SightingAlertEntity,
    onShowLocation: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.content,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)
        ) {
            DetailRow(
                icon = Icons.Filled.LocationOn,
                label = "Ubicación",
                value = sighting.locationName.ifBlank { "Sin referencia disponible" }
            )
            DetailRow(
                icon = Icons.Filled.AccessTime,
                label = "Fecha y hora",
                value = formatTimestamp(sighting.timestamp)
            )
            if (sighting.hasUsableLocation()) {
                AppButton(
                    onClick = onShowLocation,
                    variant = AppButtonVariant.Outlined,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sighting-detail-location-action")
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(AppSpacing.iconMedium))
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Text("Ver ubicación")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(AppSpacing.iconMedium)
        )
        Spacer(modifier = Modifier.width(AppSpacing.fieldGap))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailTextCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = AppShapes.content,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(AppSpacing.cardPadding), verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AppSpacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(AppSpacing.fieldGap))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(value, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES")).format(Date(timestamp))

private fun SightingAlertEntity.hasUsableCoordinates(): Boolean =
    latitude.isFinite() && longitude.isFinite() && !(latitude == 0.0 && longitude == 0.0)

private fun SightingAlertEntity.hasUsableLocation(): Boolean =
    locationName.isNotBlank() || hasUsableCoordinates()
