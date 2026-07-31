package com.findyourpet.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.location.DeviceLocationProvider
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.domain.OwnershipPolicy
import com.findyourpet.app.ui.components.PetStatusChip
import com.findyourpet.app.ui.media.CameraImageCapture
import com.findyourpet.app.ui.theme.AlertRed
import com.findyourpet.app.ui.theme.CoralPrimary
import com.findyourpet.app.ui.theme.TealSecondary
import com.findyourpet.app.ui.viewmodel.PetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SightingAlertScreen(
    viewModel: PetViewModel,
    postId: String,
    onBackClick: () -> Unit,
    onAlertSent: (String) -> Unit
) {
    val post by viewModel.selectedPost.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(postId) {
        viewModel.selectPost(postId)
    }

    val pet = post ?: return

    if (!OwnershipPolicy.canReportSighting(currentUser.id, pet.ownerId)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Alerta de Avistamiento", fontWeight = FontWeight.Bold, color = AlertRed) },
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No puedes reportar avistamientos de tu propia publicacion.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Puedes gestionar esta mascota desde su ficha y reportar avistamientos en publicaciones de otros usuarios.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onBackClick) {
                    Text("Volver")
                }
            }
        }
        return
    }

    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var locationSource by remember { mutableStateOf(LocationSource.NONE) }
    var notes by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf("") }
    var mediaSource by remember { mutableStateOf<MediaSource?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var formMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val authMessage by viewModel.authMessage.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
            mediaSource = MediaSource.GALLERY
            formMessage = null
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            selectedPhotoUri = uri.toString()
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
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) {
            scope.launch {
                val captured = DeviceLocationProvider.currentLocation(context)
                if (captured.source == LocationSource.DEVICE_GPS) {
                    latitude = captured.latitude
                    longitude = captured.longitude
                    locationSource = captured.source
                    if (locationName.isBlank()) locationName = captured.label
                    formMessage = "Ubicacion actual capturada."
                } else {
                    formMessage = "No se pudo obtener la ubicacion actual. Puedes ingresar una referencia manual."
                }
            }
        } else {
            formMessage = "Permiso de ubicacion denegado. Puedes ingresar una referencia manual."
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

    fun requestCurrentLocation() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val hasLocation = permissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (hasLocation) {
            scope.launch {
                val captured = DeviceLocationProvider.currentLocation(context)
                if (captured.source == LocationSource.DEVICE_GPS) {
                    latitude = captured.latitude
                    longitude = captured.longitude
                    locationSource = captured.source
                    if (locationName.isBlank()) locationName = captured.label
                    formMessage = "Ubicacion actual capturada."
                } else {
                    formMessage = "No se pudo obtener la ubicacion actual. Puedes ingresar una referencia manual."
                }
            }
        } else {
            locationPermissionLauncher.launch(permissions)
        }
    }

    fun submitAlert() {
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
            mediaSource = mediaSource,
            locationSource = locationSource,
            onComplete = { chatId ->
                isSubmitting = false
                onAlertSent(chatId)
            },
            onError = { message ->
                isSubmitting = false
                formMessage = message
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(text = "Alerta de Avistamiento", fontWeight = FontWeight.Bold, color = AlertRed) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            SightingSubmitActionBar(
                isSubmitting = isSubmitting,
                enabled = !isSubmitting && locationName.isNotBlank() && locationSource != LocationSource.NONE,
                onSubmit = { submitAlert() }
            )
        }
    ) { padding ->
        SightingAlertAdaptiveContent(
            pet = pet,
            selectedPhotoUri = selectedPhotoUri,
            locationName = locationName,
            locationSource = locationSource,
            notes = notes,
            authMessage = authMessage,
            formMessage = formMessage,
            onGalleryClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            onCameraClick = { launchCamera() },
            onLocationClick = { requestCurrentLocation() },
            onLocationNameChange = {
                locationName = it
                if (it.isNotBlank() && locationSource != LocationSource.DEVICE_GPS) {
                    locationSource = LocationSource.MANUAL_COARSE
                } else if (it.isBlank()) {
                    locationSource = LocationSource.NONE
                }
            },
            onNotesChange = { notes = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun SightingAlertAdaptiveContent(
    pet: PetPostEntity,
    selectedPhotoUri: String,
    locationName: String,
    locationSource: LocationSource,
    notes: String,
    authMessage: String?,
    formMessage: String?,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onLocationClick: () -> Unit,
    onLocationNameChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    availableWidthOverride: Dp? = null,
    availableHeightOverride: Dp? = null
) {
    BoxWithConstraints(modifier = modifier) {
        val availableWidth = availableWidthOverride ?: maxWidth
        val availableHeight = availableHeightOverride ?: maxHeight
        val useTwoColumn = availableWidth >= 600.dp && availableHeight >= 520.dp
        val useCenteredColumn = availableWidth >= 600.dp && !useTwoColumn
        val capMediaHeight = availableWidth > availableHeight || !useTwoColumn && availableWidth >= 600.dp
        val mediaMaxHeight = if (capMediaHeight) availableHeight * 0.45f else null

        if (useTwoColumn) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("sighting-layout-expanded"),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                        .padding(start = 24.dp, top = 24.dp, bottom = 24.dp)
                        .testTag("sighting-media-column")
                ) {
                    SightingPetMediaHeader(
                        pet = pet,
                        maxHeight = mediaMaxHeight,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 24.dp, end = 24.dp, bottom = 24.dp)
                        .testTag("sighting-detail-column"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SightingAlertDetails(
                        pet = pet,
                        selectedPhotoUri = selectedPhotoUri,
                        locationName = locationName,
                        locationSource = locationSource,
                        notes = notes,
                        authMessage = authMessage,
                        formMessage = formMessage,
                        onGalleryClick = onGalleryClick,
                        onCameraClick = onCameraClick,
                        onLocationClick = onLocationClick,
                        onLocationNameChange = onLocationNameChange,
                        onNotesChange = onNotesChange
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(if (useCenteredColumn) "sighting-layout-centered" else "sighting-layout-compact"),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .then(if (useCenteredColumn) Modifier.widthIn(max = 640.dp) else Modifier)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = if (useCenteredColumn) 24.dp else 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SightingPetMediaHeader(
                        pet = pet,
                        maxHeight = mediaMaxHeight,
                        modifier = Modifier.fillMaxWidth()
                    )
                    SightingAlertDetails(
                        pet = pet,
                        selectedPhotoUri = selectedPhotoUri,
                        locationName = locationName,
                        locationSource = locationSource,
                        notes = notes,
                        authMessage = authMessage,
                        formMessage = formMessage,
                        onGalleryClick = onGalleryClick,
                        onCameraClick = onCameraClick,
                        onLocationClick = onLocationClick,
                        onLocationNameChange = onLocationNameChange,
                        onNotesChange = onNotesChange
                    )
                }
            }
        }
    }
}

@Composable
private fun SightingPetMediaHeader(
    pet: PetPostEntity,
    maxHeight: Dp?,
    modifier: Modifier = Modifier
) {
    val mediaModifier = if (maxHeight != null) modifier.heightIn(max = maxHeight) else modifier
    Box(
        modifier = mediaModifier
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(20.dp))
            .testTag("sighting-media-header")
    ) {
        val context = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(context).data(pet.photoUri).crossfade(true).build(),
            contentDescription = "Foto principal de ${pet.petName}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        PetStatusChip(
            status = pet.status,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .testTag("sighting-status-badge")
        )
    }
}

@Composable
private fun ColumnScope.SightingAlertDetails(
    pet: PetPostEntity,
    selectedPhotoUri: String,
    locationName: String,
    locationSource: LocationSource,
    notes: String,
    authMessage: String?,
    formMessage: String?,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onLocationClick: () -> Unit,
    onLocationNameChange: (String) -> Unit,
    onNotesChange: (String) -> Unit
) {
    SightingPetSummaryCard(pet = pet)

    Text("Foto del avistamiento (opcional)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    SightingPhotoAttachment(selectedPhotoUri = selectedPhotoUri)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onGalleryClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Galeria")
        }
        OutlinedButton(onClick = onCameraClick, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Camara")
        }
    }

    Text("Ubicacion del avistamiento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    OutlinedTextField(
        value = locationName,
        onValueChange = onLocationNameChange,
        label = { Text("Punto de referencia, barrio o calle") },
        leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = CoralPrimary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )

    OutlinedButton(onClick = onLocationClick, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Filled.MyLocation, contentDescription = null, tint = TealSecondary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (locationSource == LocationSource.DEVICE_GPS) "Ubicacion GPS capturada" else "Usar ubicacion actual")
    }

    Text("Detalles adicionales", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Describe el estado de la mascota o hacia donde iba") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 5
    )

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

@Composable
private fun SightingPetSummaryCard(pet: PetPostEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sighting-info-card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Reportando avistamiento de: ${pet.petName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                text = "Raza: ${pet.breed}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Ultima referencia: ${pet.lastSeenLocation}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SightingPhotoAttachment(selectedPhotoUri: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(16.dp))
            .testTag("sighting-attachment-photo")
    ) {
        if (selectedPhotoUri.isBlank()) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxSize()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Sin foto adjunta", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context).data(selectedPhotoUri).crossfade(true).build(),
                contentDescription = "Foto del avistamiento",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SightingSubmitActionBar(
    isSubmitting: Boolean,
    enabled: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("sighting-bottom-action-bar"),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onSubmit,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("sighting-primary-action")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "ENVIAR ALERTA", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
            }
        }
    }
}
