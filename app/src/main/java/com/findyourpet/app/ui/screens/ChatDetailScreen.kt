package com.findyourpet.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.SIGHTING_ALERT_MESSAGE_TYPE
import com.findyourpet.app.domain.OwnershipPolicy
import com.findyourpet.app.ui.components.SyncStatusBanner
import com.findyourpet.app.ui.theme.AlertRed
import com.findyourpet.app.ui.theme.AppElevation
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.theme.CoralPrimary
import com.findyourpet.app.ui.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: PetViewModel,
    chatId: String,
    onBackClick: () -> Unit
) {
    val messages by viewModel.activeChatMessages.collectAsState()
    val messagesState by viewModel.activeChatMessagesState.collectAsState()
    val chatSession by viewModel.activeChatSession.collectAsState()
    val chatSessionState by viewModel.activeChatSessionState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    val listState = rememberLazyListState()
    var textInput by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        viewModel.selectChat(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val session = chatSession
    val isOwner = session?.let { OwnershipPolicy.canManagePost(currentUser.id, it.ownerId) } == true
    val isAuthorizedParticipant = session?.let {
        OwnershipPolicy.isChatParticipant(currentUser.id, it.ownerId, it.reporterId)
    } == true
    val chatUnavailable = !chatSessionState.isLoading && (session == null || !isAuthorizedParticipant)

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(session?.petPhotoUri ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = session?.petName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(AppSpacing.chatHeaderAvatar)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.titleGap))
                        Column {
                            Text(
                                text = "Chat sobre ${session?.petName ?: "Mascota"}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = if (isOwner) "Conversacion con: ${session?.reporterName}" else "Conversacion con el dueno",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
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
            SyncStatusBanner(state = chatSessionState)
            SyncStatusBanner(state = messagesState)

            if (chatUnavailable) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Esta conversacion no esta disponible.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(AppSpacing.lg)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppSpacing.narrowInset, vertical = AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.listGap)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Text(
                                text = if (messagesState.isLoading) "Cargando mensajes." else "Sin mensajes todavia.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(AppSpacing.narrowInset)
                            )
                        }
                    }
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageItem(
                            message = msg,
                            currentUserId = currentUser.id,
                            context = context
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = AppElevation.inputBar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = AppSpacing.narrowInset, vertical = AppSpacing.sm)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Escribe un mensaje...") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = AppSpacing.textFieldInset),
                            shape = AppShapes.circularInput,
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.inputSurface),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.inputSurface)
                            )
                        )
                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    viewModel.sendChatMessage(textInput)
                                    textInput = ""
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Enviar",
                                modifier = Modifier.size(AppSpacing.sendIcon)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessageEntity,
    currentUserId: String,
    context: android.content.Context
) {
    val isMyMessage = message.senderId == currentUserId
    val formattedTime = remember(message.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale("es", "ES"))
        sdf.format(Date(message.timestamp))
    }

    if (message.type == SIGHTING_ALERT_MESSAGE_TYPE) {
        SightingAlertMessageCard(
            message = message,
            currentUserId = currentUserId,
            context = context
        )
    } else {
        val displayText = if (message.isSystemMessage) "Mensaje anterior" else message.text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.microGap),
            contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Column(
                horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start,
                modifier = Modifier.widthIn(max = AppSpacing.messageMaxWidth)
            ) {
                Surface(
                    color = if (isMyMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isMyMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    shape = if (isMyMessage) AppShapes.messageMine else AppShapes.messageOther
                ) {
                    Column(modifier = Modifier.padding(horizontal = AppSpacing.narrowInset, vertical = AppSpacing.sm)) {
                        if (!isMyMessage) {
                            Text(
                                text = message.senderName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.microGap))
                        }
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isMyMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        if (message.photoUri != null) {
                            Spacer(modifier = Modifier.height(AppSpacing.compactGap))
                            AsyncImage(
                                model = message.photoUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(AppSpacing.messageImageHeight)
                                    .clip(AppShapes.photoThumbnail)
                            )
                        }
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isMyMessage) MaterialTheme.colorScheme.onPrimary.copy(alpha = AppOpacity.timestamp) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = AppSpacing.microGap)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SightingAlertMessageCard(
    message: ChatMessageEntity,
    currentUserId: String,
    context: android.content.Context
) {
    val isMyMessage = message.senderId == currentUserId
    val formattedTime = remember(message.snapshotTimestamp ?: message.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale("es", "ES"))
        sdf.format(Date(message.snapshotTimestamp ?: message.timestamp))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.microGap)
            .testTag("sighting-alert-message"),
        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isMyMessage) CoralPrimary.copy(alpha = 0.12f)
                else AlertRed.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = AlertRed)
                    Spacer(modifier = Modifier.width(AppSpacing.compactGap))
                    Text("Alerta de avistamiento", fontWeight = FontWeight.Bold)
                }
                message.snapshotPetName?.takeIf { it.isNotBlank() }?.let {
                    Text("Mascota: $it", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = AppSpacing.compactGap))
                }
                message.locationDisplay?.takeIf { it.isNotBlank() }?.let {
                    Text("Ubicacion: $it", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = AppSpacing.microGap))
                }
                message.generalDetails?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = AppSpacing.microGap))
                }
                message.photoAttachmentUri?.takeIf { it.isNotBlank() }?.let { photoUri ->
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(photoUri).crossfade(true).build(),
                        contentDescription = "Foto del avistamiento",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(top = AppSpacing.compactGap)
                            .clip(AppShapes.photoThumbnail)
                            .testTag("sighting-alert-photo")
                    )
                }
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.End).padding(top = AppSpacing.compactGap)
                )
            }
        }
    }
}
