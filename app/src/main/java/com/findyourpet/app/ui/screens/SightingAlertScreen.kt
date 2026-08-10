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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.findyourpet.app.data.location.DeviceLocationProvider
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.domain.OwnershipPolicy
import com.findyourpet.app.ui.media.CameraImageCapture
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.theme.AppElevation
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
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
                    title = { Text(text = "Alerta de Avistamiento", color = MaterialTheme.colorScheme.error) },
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
                    .padding(AppSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No puedes reportar avistamientos de tu propia publicacion.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(AppSpacing.fieldGap))
                Text(
                    text = "Puedes gestionar esta mascota desde su ficha y reportar avistamientos en publicaciones de otros usuarios.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(AppSpacing.md + AppSpacing.xs))
                AppButton(onClick = onBackClick) {
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
                windowInsets = WindowInsets.safeDrawing,
                title = { Text(text = "Alerta de Avistamiento", color = MaterialTheme.colorScheme.error) },
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
                .imePadding()
        )
    }
}

@Composable
fun SightingAlertAdaptiveContent(
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
        val useExpanded = availableWidth >= AppSpacing.adaptiveBreakpoint && availableHeight >= AppSpacing.expandedMinHeight
        val useCenteredColumn = availableWidth >= AppSpacing.adaptiveBreakpoint && !useExpanded
        val layoutTag = when {
            useExpanded -> "sighting-layout-expanded"
            useCenteredColumn -> "sighting-layout-centered"
            else -> "sighting-layout-compact"
        }
        val horizontalPadding = when {
            useExpanded -> AppSpacing.expandedInset
            useCenteredColumn -> AppSpacing.centeredInset
            else -> AppSpacing.compactInset
        }
        val contentMaxWidth = if (useExpanded) AppSpacing.expandedContentMaxWidth else AppSpacing.contentMaxWidth

        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag(layoutTag),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .then(if (availableWidth >= AppSpacing.adaptiveBreakpoint) Modifier.widthIn(max = contentMaxWidth) else Modifier)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = if (useExpanded) AppSpacing.lg else AppSpacing.md)
                    .testTag("sighting-detail-column"),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sectionGap)
            ) {
                SightingAlertDetails(
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

@Composable
private fun ColumnScope.SightingAlertDetails(
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
    Text("Foto del avistamiento (opcional)", style = MaterialTheme.typography.titleSmall)
    SightingPhotoUploadSurface(
        selectedPhotoUri = selectedPhotoUri,
        onGalleryClick = onGalleryClick,
        onCameraClick = onCameraClick
    )

    Text("Ubicacion del avistamiento", style = MaterialTheme.typography.titleSmall)
    OutlinedTextField(
        value = locationName,
        onValueChange = onLocationNameChange,
        label = { Text("Punto de referencia, barrio o calle") },
        leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.chip
    )

    AppButton(
        onClick = onLocationClick,
        modifier = Modifier.fillMaxWidth(),
        variant = AppButtonVariant.Outlined,
        contentDescription = "Usar ubicación actual",
    ) {
        Icon(Icons.Filled.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(AppSpacing.sm))
        Text(if (locationSource == LocationSource.DEVICE_GPS) "Ubicacion GPS capturada" else "Usar ubicacion actual")
    }

    Text("Detalles adicionales", style = MaterialTheme.typography.titleSmall)
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Describe el estado de la mascota o hacia donde iba") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppSpacing.notesMinHeight),
        shape = AppShapes.chip,
        maxLines = 5
    )

    authMessage?.let { message ->
        Text(text = message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
    formMessage?.let { message ->
        val isSuccess = message.contains("capturada")
        Text(
            text = message,
            color = if (isSuccess) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SightingPhotoUploadSurface(
    selectedPhotoUri: String,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val context = LocalContext.current
    Surface(
        shape = AppShapes.content,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(AppSpacing.borderWidth, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .height(AppSpacing.mediaHeight)
            .clip(AppShapes.content)
            .clickable(onClick = onGalleryClick)
            .testTag("sighting-photo-upload-surface")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedPhotoUri.isBlank()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppSpacing.lg)
                        .testTag("sighting-photo-empty-state")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(AppSpacing.iconLarge)
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.fieldGap))
                    Text(
                        text = "Toca para agregar foto opcional",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    SightingPhotoActions(
                        galleryLabel = "Galeria",
                        cameraLabel = "Camara",
                        onGalleryClick = onGalleryClick,
                        onCameraClick = onCameraClick
                    )
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(selectedPhotoUri).crossfade(true).build(),
                    contentDescription = "Foto del avistamiento",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("sighting-attachment-photo")
                )
                SightingPhotoActions(
                    galleryLabel = "Cambiar",
                    cameraLabel = "Camara",
                    onGalleryClick = onGalleryClick,
                    onCameraClick = onCameraClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(AppSpacing.mediaOverlayPadding)
                )
            }
        }
    }
}

@Composable
private fun SightingPhotoActions(
    galleryLabel: String,
    cameraLabel: String,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm), modifier = modifier) {
        AppButton(
            onClick = onGalleryClick,
            modifier = Modifier.testTag("sighting-gallery-action"),
            variant = AppButtonVariant.Outlined,
            contentDescription = galleryLabel,
        ) {
            Icon(Icons.Filled.Collections, contentDescription = null, modifier = Modifier.size(AppSpacing.iconMedium))
            Spacer(modifier = Modifier.width(AppSpacing.compactGap))
            Text(galleryLabel)
        }
        AppButton(
            onClick = onCameraClick,
            modifier = Modifier.testTag("sighting-camera-action"),
            variant = AppButtonVariant.Outlined,
            contentDescription = cameraLabel,
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(AppSpacing.iconMedium))
            Spacer(modifier = Modifier.width(AppSpacing.compactGap))
            Text(cameraLabel)
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
        tonalElevation = AppElevation.card,
        shadowElevation = AppElevation.card + AppSpacing.sm,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.fieldGap),
            contentAlignment = Alignment.Center
        ) {
            AppButton(
                onClick = onSubmit,
                enabled = enabled,
                modifier = Modifier
                    .widthIn(max = AppSpacing.submitMaxWidth)
                    .fillMaxWidth()
                    .height(AppSpacing.submitButtonHeight)
                    .testTag("sighting-primary-action"),
                variant = AppButtonVariant.Danger,
                contentDescription = "Enviar alerta",
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onError, modifier = Modifier.size(AppSpacing.progressIndicator))
                } else {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(AppSpacing.submitIcon))
                    Spacer(modifier = Modifier.width(AppSpacing.titleGap))
                    Text(text = "ENVIAR ALERTA", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
