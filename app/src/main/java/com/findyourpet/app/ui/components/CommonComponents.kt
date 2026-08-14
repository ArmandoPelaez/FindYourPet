package com.findyourpet.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.findyourpet.app.data.remote.BackendSyncState
import com.findyourpet.app.ui.theme.AppColors
import com.findyourpet.app.ui.theme.AppElevation
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.theme.PetStatusColors

@Composable
fun PetStatusChip(
    status: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
) {
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
            if (showIcon) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = statusColors.content,
                    modifier = Modifier.size(AppSpacing.iconSmall)
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
            }
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
    CompactPrimary,
    CompactOutlined,
    Outlined,
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentPaddingOverride: PaddingValues? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val buttonModifier = modifier
        .heightIn(
            min = if (variant == AppButtonVariant.CompactPrimary || variant == AppButtonVariant.CompactOutlined) {
                AppSpacing.compactButtonHeight
            } else {
                AppSpacing.buttonHeight
            }
        )
        .then(
            if (contentDescription == null) {
                Modifier
            } else {
                Modifier.semantics { this.contentDescription = contentDescription }
            }
        )

    when (variant) {
        AppButtonVariant.Outlined,
        AppButtonVariant.CompactOutlined -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = if (variant == AppButtonVariant.CompactOutlined) AppShapes.chip else AppShapes.button,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = if (variant == AppButtonVariant.CompactOutlined) {
                PaddingValues(horizontal = AppSpacing.sm, vertical = AppSpacing.xs)
            } else {
                ButtonDefaults.ContentPadding
            },
            content = content,
        )
        AppButtonVariant.Primary,
        AppButtonVariant.Danger,
        AppButtonVariant.Success,
        AppButtonVariant.Tonal,
        AppButtonVariant.CompactPrimary -> Button(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = if (variant == AppButtonVariant.CompactPrimary) AppShapes.chip else AppShapes.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = when (variant) {
                    AppButtonVariant.Danger -> MaterialTheme.colorScheme.error
                    AppButtonVariant.Success -> AppColors.reunited
                    AppButtonVariant.Tonal -> MaterialTheme.colorScheme.surfaceVariant
                    AppButtonVariant.Primary,
                    AppButtonVariant.Outlined,
                    AppButtonVariant.CompactPrimary,
                    AppButtonVariant.CompactOutlined -> MaterialTheme.colorScheme.primary
                },
                contentColor = when (variant) {
                    AppButtonVariant.Danger -> MaterialTheme.colorScheme.onError
                    AppButtonVariant.Success -> AppColors.onPrimary
                    AppButtonVariant.Tonal -> MaterialTheme.colorScheme.onSurfaceVariant
                    AppButtonVariant.Primary,
                    AppButtonVariant.Outlined,
                    AppButtonVariant.CompactPrimary,
                    AppButtonVariant.CompactOutlined -> MaterialTheme.colorScheme.onPrimary
                },
            ),
            contentPadding = contentPaddingOverride ?: if (variant == AppButtonVariant.CompactPrimary) {
                PaddingValues(horizontal = AppSpacing.sm, vertical = AppSpacing.xs)
            } else {
                ButtonDefaults.ContentPadding
            },
            content = content,
        )
    }
}

@Composable
fun AppActionChip(
    onClick: () -> Unit,
    label: String,
    leadingIcon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = onClick,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(AppSpacing.iconMedium),
            )
        },
        shape = AppShapes.chip,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primary,
            labelColor = MaterialTheme.colorScheme.onPrimary,
            leadingIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = null,
        border = null,
    )
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
    onNotificationsClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    selectedDestination: BottomNavigationDestination = BottomNavigationDestination.Home,
    modifier: Modifier = Modifier
) {
    val navigationSurfaceColor = bottomNavigationSurfaceColor()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .semantics { contentDescription = "Acciones principales" },
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val horizontalMargin = when {
                maxWidth <= AppSpacing.bottomNavigationSmallBreakpoint -> AppSpacing.sm
                maxWidth < AppSpacing.bottomNavigationLargeBreakpoint -> AppSpacing.md
                else -> AppSpacing.lg
            }
            val availableWidth = maxWidth - (horizontalMargin * 2)
            val navigationWidth = minOf(availableWidth, AppSpacing.bottomNavigationMaxWidth)

            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .widthIn(max = navigationWidth),
                shape = AppShapes.button,
                color = navigationSurfaceColor,
                tonalElevation = AppSpacing.none,
                shadowElevation = AppElevation.bottomNavigation,
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    BottomNavigationTopDivider()
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(AppSpacing.bannerHeight),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BottomNavigationItem(
                                selected = selectedDestination == BottomNavigationDestination.Home,
                                label = "Inicio",
                                contentDescription = "Inicio",
                                icon = Icons.Filled.Home,
                                onClick = onHomeClick,
                            )
                            BottomNavigationItem(
                                selected = selectedDestination == BottomNavigationDestination.Profile,
                                label = "Perfil",
                                contentDescription = "Perfil",
                                icon = Icons.Outlined.Person,
                                onClick = onProfileClick,
                            )
                            BottomNavigationItem(
                                selected = selectedDestination == BottomNavigationDestination.Create,
                                label = "Reportar",
                                contentDescription = "Reportar",
                                icon = Icons.Filled.Pets,
                                isCreateAction = true,
                                onClick = onCreatePostClick,
                            )
                            BottomNavigationItem(
                                selected = selectedDestination == BottomNavigationDestination.Chats,
                                label = "Mensajes",
                                contentDescription = "Chats Privados",
                                icon = Icons.AutoMirrored.Outlined.Chat,
                                onClick = onChatClick,
                            )
                            BottomNavigationItem(
                                selected = selectedDestination == BottomNavigationDestination.Notifications,
                                label = "Alertas",
                                contentDescription = "Alertas",
                                icon = Icons.Outlined.NotificationsNone,
                                unreadCount = unreadNotificationsCount,
                                onClick = onNotificationsClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class BottomNavigationDestination {
    Home,
    Profile,
    Create,
    Chats,
    Notifications,
}

@Composable
private fun RowScope.BottomNavigationItem(
    selected: Boolean,
    label: String,
    contentDescription: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isCreateAction: Boolean = false,
    unreadCount: Int = 0,
) {
    val itemModifier = Modifier
        .weight(1f)
        .height(AppSpacing.bannerHeight)

    if (isCreateAction) {
        Box(
            modifier = itemModifier,
            contentAlignment = Alignment.Center,
        ) {
            BottomNavigationItemContent(
                selected = selected,
                label = label,
                contentDescription = contentDescription,
                icon = icon,
                onClick = onClick,
                isCreateAction = true,
                unreadCount = unreadCount,
            )
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = itemModifier,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                BottomNavigationItemContent(
                    selected = selected,
                    label = label,
                    contentDescription = contentDescription,
                    icon = icon,
                    onClick = onClick,
                    isCreateAction = false,
                    unreadCount = unreadCount,
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationItemContent(
    selected: Boolean,
    label: String,
    contentDescription: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isCreateAction: Boolean,
    unreadCount: Int,
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val navigationSurfaceColor = bottomNavigationSurfaceColor()

    if (isCreateAction) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacing.bannerHeight),
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(AppSpacing.bottomNavigationWellSize)
                    .offset(y = -AppSpacing.bottomNavigationActionLift),
                shape = CircleShape,
                color = navigationSurfaceColor,
                tonalElevation = AppSpacing.none,
                shadowElevation = AppElevation.bottomNavigation,
            ) {}

            FilledIconButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(AppSpacing.bottomNavigationCreateActionSize)
                    .offset(y = -AppSpacing.bottomNavigationActionLift),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(
                        AppSpacing.bottomNavigationCreateIconSize,
                    ),
                )
            }

            Text(
                modifier = Modifier.align(Alignment.BottomCenter),
                text = label,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.height(
                    AppSpacing.bottomNavigationIconSlotHeight,
                ),
                contentAlignment = Alignment.Center,
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                            ) { Text(text = "$unreadCount") }
                        }
                    },
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = contentColor,
                        modifier = Modifier.size(
                            AppSpacing.bottomNavigationIcon,
                        ),
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(
                    AppSpacing.bottomNavigationLabelGap,
                ),
            )

            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
        }
    }
}

@Composable
private fun bottomNavigationSurfaceColor(): Color =
    MaterialTheme.colorScheme.surfaceVariant
        .copy(alpha = AppOpacity.bottomNavigationSurface)
        .compositeOver(MaterialTheme.colorScheme.background)

@Composable
private fun BoxScope.BottomNavigationTopDivider() {
    val dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = AppOpacity.border)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppSpacing.bottomNavigationDividerArcHeight + AppSpacing.borderWidth)
            .offset(y = -AppSpacing.bottomNavigationDividerArcHeight),
    ) {
        val dividerY = AppSpacing.bottomNavigationDividerArcHeight.toPx()
        val radius = AppSpacing.bottomNavigationWellSize.toPx() / 2f
        val centerX = size.width / 2f
        val strokeWidth = AppSpacing.borderWidth.toPx()
        val arcHeight = AppSpacing.bottomNavigationDividerArcHeight.toPx()
        val path = Path().apply {
            moveTo(0f, dividerY)
            lineTo(centerX - radius, dividerY)
            cubicTo(
                centerX - radius * 0.58f,
                dividerY,
                centerX - radius * 0.52f,
                dividerY - arcHeight,
                centerX,
                dividerY - arcHeight,
            )
            cubicTo(
                centerX + radius * 0.52f,
                dividerY - arcHeight,
                centerX + radius * 0.58f,
                dividerY,
                centerX + radius,
                dividerY,
            )
            lineTo(size.width, dividerY)
        }

        drawPath(
            path = path,
            color = dividerColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
        )
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

