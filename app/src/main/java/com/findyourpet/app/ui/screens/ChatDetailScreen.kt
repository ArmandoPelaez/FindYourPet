package com.findyourpet.app.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.ui.theme.AlertRed
import com.findyourpet.app.ui.theme.CoralPrimary
import com.findyourpet.app.ui.theme.ReunitedGreen
import com.findyourpet.app.ui.theme.ReunitedGreenContainer
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
    onBackClick: () -> Unit,
    onViewPetDetailClick: (String) -> Unit
) {
    val messages by viewModel.activeChatMessages.collectAsState()
    val chatSession by viewModel.activeChatSession.collectAsState()
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
    val isOwner = currentUser.id == session?.ownerId || currentUser.id == "owner_1"
    val isContactShared = session?.isContactSharedByOwner == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            session?.postId?.let { onViewPetDetailClick(it) }
                        }
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
                                text = if (isOwner) "Contacto con: ${session?.reporterName}" else "Contacto con el Dueño",
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
            // Demo contact visibility control.
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isContactShared) ReunitedGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isContactShared) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                contentDescription = null,
                                tint = if (isContactShared) ReunitedGreen else CoralPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isContactShared) "Contacto directo compartido" else "Contacto oculto en esta demo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isContactShared) ReunitedGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isContactShared)
                                        "Teléfono: +506 8888-9900 • Email: carlos.ramirez@email.com"
                                    else
                                        "Tus datos de teléfono y correo no se muestran en la ficha pública.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isOwner) {
                            Switch(
                                checked = isContactShared,
                                onCheckedChange = { viewModel.toggleContactSharing(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = ReunitedGreen)
                            )
                        }
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatMessageItem(
                        message = msg,
                        currentUserId = currentUser.id,
                        context = context
                    )
                }
            }

            // Chat Input Bar
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
                    IconButton(onClick = {
                        viewModel.sendChatMessage("📸 [Imagen del avistamiento adjunta]", "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80")
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.AddPhotoAlternate,
                            contentDescription = "Adjuntar Foto",
                            tint = TealSecondary
                        )
                    }

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

    if (message.isSystemMessage) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = null,
                        tint = AlertRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MENSAJE DEL SISTEMA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = AlertRed
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (message.photoUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = message.photoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Text(
                    text = formattedTime,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    } else {
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
                            text = message.text,
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
