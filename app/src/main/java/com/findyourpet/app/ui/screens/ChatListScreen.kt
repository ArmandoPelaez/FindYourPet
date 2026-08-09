package com.findyourpet.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.ui.components.EmptyState
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: PetViewModel,
    onBackClick: (() -> Unit)? = null,
    onChatSelect: (String) -> Unit
) {
    val chatSessions by viewModel.userChatSessions.collectAsState()
    val chatSessionsState by viewModel.userChatSessionsState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = { Text("Conversaciones") },
                navigationIcon = {
                    val navigateBack = onBackClick
                    if (navigateBack != null) {
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
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
            SyncStatusBanner(state = chatSessionsState)
            if (chatSessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "Sin conversaciones activas",
                        message = if (chatSessionsState.isLoading) "Cargando conversaciones." else "Cuando envies o recibas una alerta de avistamiento, se abrira una conversacion aqui.",
                        icon = Icons.Outlined.ChatBubbleOutline,
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
                    items(chatSessions, key = { it.id }) { session ->
                        ChatSessionCard(
                            session = session,
                            context = context,
                            onClick = { onChatSelect(session.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatSessionCard(
    session: ChatSessionEntity,
    context: android.content.Context,
    onClick: () -> Unit
) {
    val formattedTime = remember(session.lastMessageTimestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale("es", "ES"))
        sdf.format(Date(session.lastMessageTimestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = AppShapes.content,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.compactCardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(session.petPhotoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = session.petName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(AppSpacing.avatarMedium)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(AppSpacing.fieldGap))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mascota: ${session.petName}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.microGap))

                Text(
                    text = "Buscador: ${session.reporterName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                if (session.lastMessage.isNotBlank()) {
                    Text(
                        text = session.lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = AppSpacing.microGap)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Chat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(AppSpacing.iconExtraSmall)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.xs))
                    Text(
                        text = "Contacto por chat interno",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}
