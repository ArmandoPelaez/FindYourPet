package com.findyourpet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.EmptyState
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: PetViewModel,
    onBackClick: (() -> Unit)? = null
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allPosts by viewModel.allPosts.collectAsState()
    val postFeedState by viewModel.postFeedState.collectAsState()

    val myPosts = allPosts.filter { it.ownerId == currentUser.id }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = { Text("Mi Perfil y Colaboración") },
                navigationIcon = {
                    val navigateBack = onBackClick
                    if (navigateBack != null) {
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.signOut() }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Salir")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                top = AppSpacing.md,
                end = AppSpacing.md,
                bottom = AppSpacing.actionBottom,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            // User Card
            item {
                SyncStatusBanner(state = postFeedState)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.emptyState,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(AppSpacing.cardPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(AppSpacing.avatarLarge)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(AppSpacing.avatarIcon)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.fieldGap))

                        Text(
                            text = currentUser.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Miembro Colaborador",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.md))

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Spacer(modifier = Modifier.height(AppSpacing.md))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(AppSpacing.iconProfile)
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.sm))
                                Text(
                                    text = "Email de cuenta: ",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = currentUser.email,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Contact notice.
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.content,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = AppOpacity.syncSurface))
                ) {
                    Row(
                        modifier = Modifier.padding(AppSpacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(AppSpacing.xl)
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.fieldGap))
                        Column {
                            Text(
                                text = "Comunidad colaborativa",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.microGap))
                            Text(
                                text = "El contacto entre dueno y reportero ocurre por chat interno. El email de cuenta no se muestra en fichas, chats ni notificaciones como metodo de contacto.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // My Posts Header
            item {
                Text(
                    text = "🐾 Mis Mascotas Publicadas (${myPosts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = AppSpacing.sm)
                )
            }

            if (myPosts.isEmpty()) {
                item {
                    EmptyState(
                        title = "Sin publicaciones",
                        message = "No has publicado fichas con este usuario.",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(myPosts, key = { it.id }) { pet ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.content
                    ) {
                        Row(
                            modifier = Modifier.padding(AppSpacing.compactCardPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pet.petName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Estado: ${pet.status}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            AppButton(
                                onClick = {
                                    val newStatus = if (pet.status == "REUNIDO") "PERDIDO" else "REUNIDO"
                                    viewModel.updatePetStatus(pet.id, newStatus)
                                },
                                variant = if (pet.status == "REUNIDO") AppButtonVariant.Tonal else AppButtonVariant.Success,
                                contentDescription = if (pet.status == "REUNIDO") "Reabrir publicación de ${pet.petName}" else "Marcar reunido ${pet.petName}"
                            ) {
                                Text(
                                    text = if (pet.status == "REUNIDO") "Reabrir" else "¡Marcar Reunido!",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
