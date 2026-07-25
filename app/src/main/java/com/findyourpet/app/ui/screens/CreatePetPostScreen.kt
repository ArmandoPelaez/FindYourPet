package com.findyourpet.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
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
import com.findyourpet.app.ui.theme.CoralPrimary
import com.findyourpet.app.ui.theme.TealSecondary
import com.findyourpet.app.ui.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePetPostScreen(
    viewModel: PetViewModel,
    onBackClick: () -> Unit,
    onPostCreated: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    var petName by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf("Mascota") }
    var breed by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var features by remember { mutableStateOf("") }
    var lastSeenLocation by remember { mutableStateOf("") }
    var rewardAmount by remember { mutableStateOf("") }

    val presetPhotos = listOf(
        "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1561037404-61cd46aa615b?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80"
    )
    var photoUri by remember { mutableStateOf(presetPhotos.first()) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Mascota Perdida", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Demo contact visibility notice.
            Card(
                colors = CardDefaults.cardColors(containerColor = TealSecondary.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = TealSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Contacto limitado en esta demo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TealSecondary
                        )
                        Text(
                            text = "La ficha pública oculta teléfono y email hasta que decidas mostrarlos dentro del flujo de chat local.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Photo Selection Section
            Text(text = "Fotografía Principal", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de la mascota",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(text = "Seleccionar foto de muestra:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presetPhotos) { uri ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { photoUri = uri }
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

            // Pet Details Fields
            Text(text = "Datos de la Mascota", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text("Nombre de la mascota (Ej. Toby, Mia)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Raza (Ej. Poodle)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color principal") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = features,
                onValueChange = { features = it },
                label = { Text("Características distintivas (manchas, collar, cicatriz...)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            // Location & Reward
            Text(text = "Ubicación y Recompensa", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            OutlinedTextField(
                value = lastSeenLocation,
                onValueChange = { lastSeenLocation = it },
                label = { Text("Última ubicación vista (Barrio, Ciudad, Calle)") },
                leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = CoralPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = rewardAmount,
                onValueChange = { rewardAmount = it },
                label = { Text("Recompensa ofrecida (Opcional, Ej. $100 USD)") },
                leadingIcon = { Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = CoralPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submit Button
            Button(
                onClick = {
                    if (petName.isNotBlank() && lastSeenLocation.isNotBlank()) {
                        isSubmitting = true
                        viewModel.createNewPetPost(
                            petName = petName,
                            species = selectedSpecies,
                            breed = breed.ifBlank { "Mestizo" },
                            color = color.ifBlank { "Variado" },
                            features = features.ifBlank { "Sin características registradas" },
                            photoUri = photoUri,
                            lastSeenLocation = lastSeenLocation,
                            latitude = 9.9333,
                            longitude = -84.0833,
                            rewardAmount = rewardAmount.ifBlank { "Sin recompensa" },
                            onComplete = {
                                isSubmitting = false
                                onPostCreated()
                            }
                        )
                    }
                },
                enabled = petName.isNotBlank() && lastSeenLocation.isNotBlank() && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Filled.Publish, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publicar ficha",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
