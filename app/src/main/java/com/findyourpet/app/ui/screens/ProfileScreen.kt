package com.findyourpet.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.EmptyState
import com.findyourpet.app.ui.components.PetStatusChip
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.theme.bottomNavigationSurfaceColor
import com.findyourpet.app.ui.viewmodel.PetViewModel
import com.findyourpet.app.ui.viewmodel.PostStatusUpdateStatus

@Composable
fun ProfileScreen(
    viewModel: PetViewModel,
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val ownedPosts by viewModel.ownedPosts.collectAsState()
    val ownedPostsState by viewModel.ownedPostsState.collectAsState()
    val postStatusUpdateState by viewModel.postStatusUpdateState.collectAsState()
    var pendingReunitedPostId by remember { mutableStateOf<String?>(null) }

    val myPosts = ownedPosts.filter { it.ownerId == currentUser.id }

    Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { padding ->
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.emptyState,
                    colors = CardDefaults.cardColors(containerColor = bottomNavigationSurfaceColor())
                ) {
                    Row(
                        modifier = Modifier.padding(AppSpacing.cardPadding),
                        verticalAlignment = Alignment.CenterVertically
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

                        Spacer(modifier = Modifier.width(AppSpacing.fieldGap))
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                            Text(text = currentUser.name, style = MaterialTheme.typography.titleLarge)
                            Surface(
                                shape = AppShapes.chip,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary,
                            ) {
                                Text(
                                    text = "Colaborador",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(
                                        horizontal = AppSpacing.md,
                                        vertical = AppSpacing.xs,
                                    ),
                                )
                            }
                        }
                    }
                }
            }

            item {
                SyncStatusBanner(state = ownedPostsState)
            }

            item {
                Text(
                    text = "\uD83D\uDC3E Mis publicaciones (${myPosts.size})",
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
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary,
                                shape = CircleShape,
                                modifier = Modifier.size(AppSpacing.avatarMedium),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Pets,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppSpacing.iconMedium),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(AppSpacing.fieldGap))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = pet.petName, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                PetStatusChip(
                                    status = pet.status,
                                    showIcon = false,
                                )
                            }

                            if (pet.status.equals("PERDIDO", ignoreCase = true)) {
                                val isUpdating = postStatusUpdateState.status == PostStatusUpdateStatus.SUBMITTING &&
                                    postStatusUpdateState.postId == pet.id
                                AppButton(
                                    onClick = { pendingReunitedPostId = pet.id },
                                    enabled = !isUpdating,
                                    variant = AppButtonVariant.CompactOutlined,
                                    contentDescription = "Marcar reunida ${pet.petName}"
                                ) {
                                    Text("Marcar reunida", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }

            if (postStatusUpdateState.status == PostStatusUpdateStatus.ERROR &&
                postStatusUpdateState.message != null
            ) {
                item {
                    Text(
                        text = postStatusUpdateState.message.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                TextButton(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(AppSpacing.iconMedium),
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.xs))
                    Text("Cerrar sesi\u00F3n")
                }
            }
        }
    }

    val pendingPost = myPosts.firstOrNull { it.id == pendingReunitedPostId }
    if (pendingPost != null) {
        AlertDialog(
            onDismissRequest = { pendingReunitedPostId = null },
            title = { Text("Marcar como reunida") },
            text = {
                Text(
                    "Esta publicaci\u00F3n dejar\u00E1 de ser visible p\u00FAblicamente y seguir\u00E1 disponible en tus publicaciones."
                )
            },
            dismissButton = {
                TextButton(onClick = { pendingReunitedPostId = null }) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.markPetAsReunited(pendingPost.id)
                        pendingReunitedPostId = null
                    }
                ) {
                    Text("Confirmar")
                }
            }
        )
    }
}
