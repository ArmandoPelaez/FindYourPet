package com.findyourpet.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.ui.media.CameraImageCapture
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

    var petName by remember { mutableStateOf("") }
    var recognitionDetails by remember { mutableStateOf("") }
    var lastSeenLocation by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var locationSource by remember { mutableStateOf(LocationSource.MANUAL_COARSE) }
    var photoUri by remember { mutableStateOf("") }
    var mediaSource by remember { mutableStateOf<MediaSource?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var formMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val authMessage by viewModel.authMessage.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            photoUri = uri.toString()
            mediaSource = MediaSource.GALLERY
            formMessage = null
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            photoUri = uri.toString()
            mediaSource = MediaSource.CAMERA
            formMessage = null
        } else if (uri != null) {
            CameraImageCapture.cleanUp(context, uri)
            formMessage = "No se pudo capturar la foto."
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = CameraImageCapture.createOutputUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            formMessage = "Permiso de camara denegado."
        }
    }

    fun launchCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val uri = CameraImageCapture.createOutputUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (photoUri.isBlank()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(42.dp)
                                )
                                Icon(
                                    imageVector = Icons.Filled.PhotoLibrary,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Toca para agregar foto",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        galleryLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                ) {
                                    Icon(Icons.Filled.Collections, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Galeria")
                                }
                                OutlinedButton(onClick = { launchCamera() }) {
                                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Camara")
                                }
                            }
                        }
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(photoUri).crossfade(true).build(),
                            contentDescription = "Foto de la mascota",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            ) {
                                Icon(Icons.Filled.Collections, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cambiar")
                            }
                            OutlinedButton(onClick = { launchCamera() }) {
                                Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camara")
                            }
                        }
                    }
                }
            }

            Text(text = "Datos de la Mascota", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                placeholder = { Text("Nombre de la mascota (Ej. Toby, Mia)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = recognitionDetails,
                onValueChange = { recognitionDetails = it },
                placeholder = {
                    Text("Mas detalles utiles para reconocerla (ej. color, senas particulares, collar, temperamento)")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Text(text = "Ubicacion", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            OutlinedTextField(
                value = lastSeenLocation,
                onValueChange = {
                    lastSeenLocation = it
                    locationSource = LocationSource.MANUAL_COARSE
                    latitude = 0.0
                    longitude = 0.0
                },
                placeholder = { Text("Ultima ubicacion vista (Barrio, Ciudad o referencia)") },
                leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = CoralPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    val selectedMediaSource = mediaSource
                    if (selectedMediaSource == null) {
                        formMessage = "Adjunta una foto real desde camara o galeria."
                        return@Button
                    }
                    isSubmitting = true
                    viewModel.createNewPetPost(
                        petName = petName,
                        species = "Mascota",
                        breed = "Mestizo",
                        color = "Variado",
                        features = recognitionDetails.ifBlank { "Sin caracteristicas registradas" },
                        photoUri = photoUri,
                        lastSeenLocation = lastSeenLocation,
                        latitude = latitude,
                        longitude = longitude,
                        rewardAmount = "Sin recompensa",
                        mediaSource = selectedMediaSource,
                        locationSource = locationSource,
                        onComplete = {
                            isSubmitting = false
                            onPostCreated()
                        },
                        onError = { message ->
                            isSubmitting = false
                            formMessage = message
                        }
                    )
                },
                enabled = petName.isNotBlank() && lastSeenLocation.isNotBlank() && photoUri.isNotBlank() && !isSubmitting,
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
                    Text(text = "Publicar ficha", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            authMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
            formMessage?.let { message ->
                val isSuccess = message.contains("capturada")
                Text(
                    text = message,
                    color = if (isSuccess) TealSecondary else MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }
    }
}
