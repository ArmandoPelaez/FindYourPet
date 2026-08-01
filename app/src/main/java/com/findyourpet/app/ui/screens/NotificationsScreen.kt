package com.findyourpet.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AlertRed
import com.findyourpet.app.ui.theme.CoralPrimary
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
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold) },
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
        ) {
            SyncStatusBanner(state = notificationsState)
            if (visibleNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (notificationsState.isLoading) "Cargando notificaciones" else "No tienes notificaciones recibidas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

    val (icon, color) = when (notification.type) {
        "ALERT" -> Pair(Icons.Filled.NotificationsActive, AlertRed)
        else -> Pair(Icons.Filled.Chat, CoralPrimary)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
