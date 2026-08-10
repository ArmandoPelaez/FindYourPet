package com.findyourpet.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.findyourpet.app.ui.theme.CoralPrimary
import com.findyourpet.app.ui.theme.TealSecondary
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
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(session?.petPhotoUri ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = session?.petName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Chat sobre ${session?.petName ?: "Mascota"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isOwner) "Conversacion con: ${session?.reporterName}" else "Conversacion con el dueno",
                                fontSize = 11.sp,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                // Chat Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Text(
                                text = if (messagesState.isLoading) "Cargando mensajes." else "Sin mensajes todavia.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
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

                // Chat Input Bar remains available after a sighting alert.
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Escribe un mensaje...") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )

                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    viewModel.sendChatMessage(textInput)
                                    textInput = ""
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = CoralPrimary, contentColor = Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Enviar",
                                modifier = Modifier.size(18.dp)
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
                .padding(vertical = 2.dp),
            contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Column(
                horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Surface(
                    color = if (isMyMessage) CoralPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMyMessage) 16.dp else 4.dp,
                        bottomEnd = if (isMyMessage) 4.dp else 16.dp
                    )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        if (!isMyMessage) {
                            Text(
                                text = message.senderName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }

                        Text(
                            text = displayText,
                            fontSize = 13.sp,
                            color = if (isMyMessage) Color.White else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )

                        if (message.photoUri != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            AsyncImage(
                                model = message.photoUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        Text(
                            text = formattedTime,
                            fontSize = 9.sp,
                            color = if (isMyMessage) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 2.dp)
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
            .padding(vertical = 2.dp)
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Alerta de avistamiento", fontWeight = FontWeight.Bold)
                }
                message.snapshotPetName?.takeIf { it.isNotBlank() }?.let {
                    Text("Mascota: $it", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                }
                message.locationDisplay?.takeIf { it.isNotBlank() }?.let {
                    Text("Ubicacion: $it", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
                message.generalDetails?.takeIf { it.isNotBlank() }?.let {
                    Text(it, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                }
                message.photoAttachmentUri?.takeIf { it.isNotBlank() }?.let { photoUri ->
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(photoUri).crossfade(true).build(),
                        contentDescription = "Foto del avistamiento",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("sighting-alert-photo")
                    )
                }
                Text(
                    text = formattedTime,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.End).padding(top = 6.dp)
                )
            }
        }
    }
}
