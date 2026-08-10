package com.findyourpet.app.ui.screens

import android.content.Intent
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
import com.findyourpet.app.ui.theme.AppElevation
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
    onNavigateToNotifications: () -> Unit
) {
    val posts by viewModel.filteredPosts.collectAsState()
    val feedState by viewModel.postFeedState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSpecies by viewModel.selectedSpecies.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()

    val unreadNotificationsCount = notifications.count { !it.isRead }

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
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                        if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text(text = "$unreadNotificationsCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.topBar),
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.topBar)
                )
            )
        }
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
                val pagerState = rememberPagerState(pageCount = { posts.size })

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = AppSpacing.pagerBottom),
                    contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    pageSpacing = AppSpacing.md
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
    onAlertClick: () -> Unit
) {
    val context = LocalContext.current
    val formattedDate = remember(post.dateLost) {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale("es", "ES"))
        sdf.format(Date(post.dateLost))
    }
    val shareText = remember(post) { buildPetPostShareText(post) }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = AppShapes.card,
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppSpacing.cardImageMinHeight, max = AppSpacing.cardImageMaxHeight)
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(AppSpacing.imageOverlay)
                ) {
                    PetStatusChip(
                        status = post.status,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.cardContentVertical)
            ) {
                PetIdentitySection(post = post)

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

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(AppSpacing.cardContentVertical))

                if (post.status != "REUNIDO" && canReportSighting) {
                    AppButton(
                        onClick = onAlertClick,
                        modifier = Modifier
                            .fillMaxWidth(),
                        variant = AppButtonVariant.Danger,
                        contentDescription = "Reportar avistamiento de ${post.petName}",
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(AppSpacing.iconMedium)
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text(
                            text = "¡Lo he visto!",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.actionGap))
                }

                AppButton(
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(
                            Intent.createChooser(sendIntent, "Compartir publicacion")
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    variant = AppButtonVariant.Outlined,
                    contentDescription = "Compartir publicacion de ${post.petName}",
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        modifier = Modifier.size(AppSpacing.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Text(
                        text = "Compartir",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.actionBottom))
            }
        }
    }
}

@Composable
private fun PetIdentitySection(post: PetPostEntity) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = post.petName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
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
    }
}

fun buildPetPostShareText(post: PetPostEntity): String {
    return listOf(
        "Mascota perdida: ${post.petName}",
        "Ultima ubicacion vista: ${post.lastSeenLocation}",
        "Si la viste, usa FindYourPet para reportar el avistamiento."
    ).joinToString(separator = "\n")
}
