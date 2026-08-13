package com.findyourpet.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.domain.OwnershipPolicy
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.EmptyState
import com.findyourpet.app.ui.components.PetStatusChip
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PetViewModel,
    onNavigateToAlert: (String) -> Unit,
) {
    val posts by viewModel.filteredPosts.collectAsState()
    val feedState by viewModel.postFeedState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSpecies by viewModel.selectedSpecies.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val pagerState = rememberPagerState(pageCount = { posts.size })

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(AppSpacing.headerLogo)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Pets,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(AppSpacing.iconMedium)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(AppSpacing.titleGap))
                        Column {
                            Text(
                                text = "Mascotas Perdidas",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Red Segura de Búsqueda",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.topBar),
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.topBar)
                )
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            SyncStatusBanner(state = feedState)
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.lg),
                        title = if (feedState.isLoading) "Cargando publicaciones" else "No hay publicaciones de mascotas perdidas",
                        message = if (feedState.errorMessage != null) "Revisa tu conexion o vuelve a intentarlo." else "Tus publicaciones propias aparecen en Perfil; aca veras fichas de otros usuarios.",
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.none),
                    pageSpacing = AppSpacing.none
                ) { page ->
                    val post = posts[page]
                    PetPostCard(
                        post = post,
                        canReportSighting = OwnershipPolicy.canReportSighting(currentUser.id, post.ownerId),
                        onAlertClick = {
                            viewModel.selectPost(post.id)
                            onNavigateToAlert(post.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PetPostCard(
    post: PetPostEntity,
    canReportSighting: Boolean = true,
    onAlertClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val formattedDate = remember(post.dateLost) {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale("es", "ES"))
        sdf.format(Date(post.dateLost))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(AppSpacing.cardImageAspectRatio)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(post.photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = post.petName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                PetStatusChip(
                    status = post.status,
                    showIcon = false,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(AppSpacing.imageOverlay)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.cardContentVertical)
            ) {
                PetIdentitySection(
                    post = post,
                    formattedDate = formattedDate,
                    canReportSighting = canReportSighting,
                    onAlertClick = onAlertClick
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                Text(
                    text = "Información reportada",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.content,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.subtleSurface)
                    )
                ) {
                    Text(
                        text = post.features,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(AppSpacing.md)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.cardContentVertical))

                Spacer(modifier = Modifier.height(AppSpacing.actionBottom))
            }
        }
    }
}

@Composable
private fun InlineSightingButton(
    petName: String,
    onClick: () -> Unit,
) {
    var sightingOpened by remember(petName) { mutableStateOf(false) }

    AppButton(
        onClick = {
            sightingOpened = true
            onClick()
        },
        variant = AppButtonVariant.Outlined,
        contentDescription = "La vi: reportar avistamiento de $petName",
    ) {
        Icon(
            imageVector = if (sightingOpened) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
            contentDescription = null,
            modifier = Modifier.size(AppSpacing.iconMedium)
        )
        Spacer(modifier = Modifier.width(AppSpacing.xs))
        Text(
            text = "La vi",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun PetIdentitySection(
    post: PetPostEntity,
    formattedDate: String,
    canReportSighting: Boolean,
    onAlertClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = post.petName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (post.status != "REUNIDO" && canReportSighting) {
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                InlineSightingButton(
                    petName = post.petName,
                    onClick = onAlertClick
                )
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AppSpacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(AppSpacing.locationGap))
            Text(
                text = post.lastSeenLocation,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AppSpacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(AppSpacing.locationGap))
            Text(
                text = "Última vez visto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(AppSpacing.sm))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
