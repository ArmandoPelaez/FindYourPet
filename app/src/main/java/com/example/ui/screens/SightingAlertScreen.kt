package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SightingAlertScreen(
    viewModel: PetViewModel,
    postId: String,
    onBackClick: () -> Unit,
    onAlertSent: (String) -> Unit
) {
    val post by viewModel.selectedPost.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(postId) {
        viewModel.selectPost(postId)
    }

    val pet = post ?: return

    var locationName by remember { mutableStateOf("Frente a Farmacia La Central, San José") }
    var latitude by remember { mutableStateOf(9.9345) }
    var longitude by remember { mutableStateOf(-84.0815) }
    var notes by remember { mutableStateOf("Vi un perrito igualito a ${pet.petName} cerca de las 10:30 AM. Estaba caminando despacio hacia el norte.") }

    val presetPhotos = listOf(
        "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=600&q=80"
    )
    var selectedPhotoUri by remember { mutableStateOf(presetPhotos.first()) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🚨 Botón de Alerta de Avistamiento",
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Pet Header Box
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(pet.photoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = pet.petName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Reportando avistamiento de: ${pet.petName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Raza: ${pet.breed} • Perderse en: ${pet.lastSeenLocation}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Photo Attachment Section
            Text(
                text = "📸 Adjunta una foto tomada en el lugar",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "Selecciona una fotografía del avistamiento para que el dueño pueda confirmar.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Photo Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(selectedPhotoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto elegida",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Foto Capturada",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Photo Presets
            Text(text = "Elegir foto de prueba / Galería:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presetPhotos) { uri ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedPhotoUri = uri }
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // GPS Realtime Location Section
            Text(
                text = "📍 Ubicación en Tiempo Real",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Punto de referencia / Calle exacto") },
                leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = CoralPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = TealSecondary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = null,
                        tint = TealSecondary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GPS Geolocalizado:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealSecondary
                        )
                        Text(
                            text = "Lat: $latitude, Long: $longitude",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    TextButton(onClick = {
                        latitude += (Math.random() - 0.5) * 0.005
                        longitude += (Math.random() - 0.5) * 0.005
                    }) {
                        Text("Actualizar GPS", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notes Section
            Text(
                text = "📝 Detalles Adicionales",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Describe el estado de la mascota, comportamiento, etc.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Urgent Submit Button
            Button(
                onClick = {
                    isSubmitting = true
                    viewModel.submitSightingAlert(
                        postId = pet.id,
                        petName = pet.petName,
                        photoUri = selectedPhotoUri,
                        locationName = locationName,
                        latitude = latitude,
                        longitude = longitude,
                        notes = notes,
                        ownerId = pet.ownerId,
                        onComplete = { chatId ->
                            isSubmitting = false
                            onAlertSent(chatId)
                        }
                    )
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "🚨 ENVIAR ALERTA EN TIEMPO REAL",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
