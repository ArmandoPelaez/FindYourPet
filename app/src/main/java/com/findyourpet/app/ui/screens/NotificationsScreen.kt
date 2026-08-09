package com.findyourpet.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.ui.components.EmptyState
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
fun NotificationsScreen(
    viewModel: PetViewModel,
    onBackClick: () -> Unit,
    onNotificationClick: (String) -> Unit
) {
    val notifications by viewModel.allNotifications.collectAsState()
    val notificationsState by viewModel.notificationsState.collectAsState()
    val visibleNotifications = notifications.filter { it.type != "CONTACT_SHARED" }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            SyncStatusBanner(state = notificationsState)
            if (visibleNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = if (notificationsState.isLoading) "Cargando notificaciones" else "No tienes notificaciones recibidas",
                        message = "Las alertas y novedades aparecerán aquí.",
                        icon = Icons.Outlined.NotificationsNone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.lg),
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(AppSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.listGap)
                ) {
                    items(visibleNotifications, key = { it.id }) { notif ->
                        NotificationCard(
                            notification = notif,
                            onClick = {
                                viewModel.markNotificationAsRead(notif.id)
                                onNotificationClick(notif.targetId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: AppNotificationEntity,
    onClick: () -> Unit
) {
    val formattedTime = remember(notification.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale("es", "ES"))
        sdf.format(Date(notification.timestamp))
    }

    val (icon, color, contentColor) = when (notification.type) {
        "ALERT" -> Triple(Icons.Filled.NotificationsActive, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)
        else -> Triple(Icons.Filled.Chat, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = AppShapes.content,
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) color.copy(alpha = AppOpacity.unreadSurface) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.compactCardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color,
                shape = CircleShape,
                modifier = Modifier.size(AppSpacing.notificationAvatar)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(AppSpacing.iconMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppSpacing.fieldGap))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.microGap))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
