package com.findyourpet.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.findyourpet.app.data.remote.BackendSyncState
import com.findyourpet.app.ui.theme.AppColors
import com.findyourpet.app.ui.theme.AppElevation
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.theme.PetStatusColors

@Composable
fun PetStatusChip(status: String, modifier: Modifier = Modifier) {
    val statusColors = PetStatusColors.forStatus(status)
    val (label, icon) = when (status.uppercase()) {
        "PERDIDO" -> "PERDIDO" to Icons.Filled.Warning
        "AVISTADO" -> "AVISTADO" to Icons.Filled.Visibility
        "REUNIDO" -> "REUNIDO" to Icons.Filled.CheckCircle
        else -> status to Icons.Filled.Info
    }

    Surface(
        color = statusColors.container,
        contentColor = statusColors.content,
        shape = AppShapes.chip,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppSpacing.md - AppSpacing.xs, vertical = AppSpacing.xs)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColors.content,
                modifier = Modifier.size(AppSpacing.iconSmall)
            )
            Spacer(modifier = Modifier.width(AppSpacing.xs))
            Text(
                text = label,
                color = statusColors.content,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

enum class AppButtonVariant {
    Primary,
    Danger,
    Success,
    Tonal,
    Outlined,
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val buttonModifier = modifier
        .heightIn(min = AppSpacing.buttonHeight)
        .then(
            if (contentDescription == null) {
                Modifier
            } else {
                Modifier.semantics { this.contentDescription = contentDescription }
            }
        )

    when (variant) {
        AppButtonVariant.Outlined -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = AppShapes.button,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            content = content,
        )
        AppButtonVariant.Primary,
        AppButtonVariant.Danger,
        AppButtonVariant.Success,
        AppButtonVariant.Tonal -> Button(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = AppShapes.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = when (variant) {
                    AppButtonVariant.Danger -> MaterialTheme.colorScheme.error
                    AppButtonVariant.Success -> AppColors.reunited
                    AppButtonVariant.Tonal -> MaterialTheme.colorScheme.surfaceVariant
                    AppButtonVariant.Primary,
                    AppButtonVariant.Outlined -> MaterialTheme.colorScheme.primary
                },
                contentColor = when (variant) {
                    AppButtonVariant.Danger -> MaterialTheme.colorScheme.onError
                    AppButtonVariant.Success -> AppColors.onPrimary
                    AppButtonVariant.Tonal -> MaterialTheme.colorScheme.onSurfaceVariant
                    AppButtonVariant.Primary,
                    AppButtonVariant.Outlined -> MaterialTheme.colorScheme.onPrimary
                },
            ),
            content = content,
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Pets,
) {
    Card(
        modifier = modifier,
        shape = AppShapes.emptyState,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.card),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppSpacing.iconLarge),
                tint = MaterialTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun BottomPrimaryActionBanner(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg)
            .navigationBarsPadding()
            .semantics { contentDescription = "Acciones principales" },
        shape = AppShapes.card,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.banner),
        tonalElevation = AppElevation.card,
        shadowElevation = AppElevation.card + AppSpacing.xs
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacing.bannerHeight)
                .padding(horizontal = AppSpacing.bannerHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier.size(AppSpacing.md + AppSpacing.xl)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(AppSpacing.xl - AppSpacing.xs)
                )
            }

            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(AppSpacing.md + AppSpacing.xl)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(AppSpacing.xl - AppSpacing.xs)
                )
            }

            FilledIconButton(
                onClick = onCreatePostClick,
                modifier = Modifier.size(AppSpacing.iconLarge),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Crear publicacion",
                    modifier = Modifier.size(AppSpacing.xl)
                )
            }

            IconButton(
                onClick = onChatClick,
                modifier = Modifier.size(AppSpacing.md + AppSpacing.xl)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = "Chats Privados",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(AppSpacing.xl - AppSpacing.xs)
                )
            }
        }
    }
}

@Composable
fun <T> SyncStatusBanner(
    state: BackendSyncState<T>,
    modifier: Modifier = Modifier
) {
    val message = when {
        state.errorMessage != null -> state.errorMessage
        state.isLoading -> "Cargando datos"
        state.hasPendingWrites -> "Guardando cambios"
        state.isFromCache && state.isRemoteBackend -> "Mostrando datos en cache"
        !state.isRemoteBackend -> "Modo local"
        else -> null
    } ?: return

    val color = when {
        state.errorMessage != null -> MaterialTheme.colorScheme.error
        state.hasPendingWrites -> MaterialTheme.colorScheme.primary
        state.isLoading -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }

    Surface(
        color = color.copy(alpha = AppOpacity.syncSurface),
        contentColor = color,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (state.errorMessage != null) Icons.Filled.Error else Icons.Filled.Sync,
                contentDescription = null,
                modifier = Modifier.size(AppSpacing.iconMedium - AppSpacing.xs)
            )
            Spacer(modifier = Modifier.width(AppSpacing.sm))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

