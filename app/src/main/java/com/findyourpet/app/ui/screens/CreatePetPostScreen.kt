package com.findyourpet.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.findyourpet.app.data.location.DeviceLocationProvider
import com.findyourpet.app.data.location.LocationSelection
import com.findyourpet.app.data.location.reverseGeocode
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.ui.media.CameraImageCapture
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.FormFieldLabel
import com.findyourpet.app.ui.components.FormFieldPlaceholder
import com.findyourpet.app.ui.components.FormPhotoUploadSurface
import com.findyourpet.app.ui.components.FormSectionTitle
import com.findyourpet.app.ui.components.LocationChoiceSheet
import com.findyourpet.app.ui.components.LocationSelectionField
import com.findyourpet.app.ui.components.ManualLocationSheet
import com.findyourpet.app.ui.components.MapLocationSheet
import com.findyourpet.app.ui.theme.AppFormTypography
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePetPostScreen(
    viewModel: PetViewModel,
    onBackClick: () -> Unit,
    onPostCreated: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var petName by remember { mutableStateOf("") }
    var recognitionDetails by remember { mutableStateOf("") }
    var locationSelection by remember { mutableStateOf<LocationSelection?>(null) }
    var lastSeenLocation by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var locationSource by remember { mutableStateOf(LocationSource.NONE) }
    var photoUri by remember { mutableStateOf("") }
    var mediaSource by remember { mutableStateOf<MediaSource?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var formMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var showLocationChoices by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }
    var showManualLocation by remember { mutableStateOf(false) }
    val authMessage by viewModel.authMessage.collectAsState()

    fun applyLocationSelection(selection: LocationSelection) {
        locationSelection = selection
        lastSeenLocation = selection.displayText
        latitude = selection.persistedLatitude()
        longitude = selection.persistedLongitude()
        locationSource = selection.source
        formMessage = null
    }

    fun captureCurrentLocation() {
        coroutineScope.launch {
            val capture = DeviceLocationProvider.currentLocation(context)
            val selection = LocationSelection.fromCapture(capture)
            if (selection != null) {
                val geocodedLabel = selection.latitude?.let { latitude ->
                    selection.longitude?.let { longitude ->
                        reverseGeocode(context, latitude, longitude)
                    }
                }
                applyLocationSelection(
                    geocodedLabel?.let { selection.copy(displayText = it) } ?: selection
                )
            } else {
                formMessage = when (capture.permissionState) {
                    com.findyourpet.app.data.product.LocationPermissionState.DENIED ->
                        "Permiso de ubicación denegado. Puedes elegir el mapa o escribir una referencia."
                    else -> "La ubicación actual no está disponible. Puedes elegir el mapa o escribir una referencia."
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) captureCurrentLocation() else {
            formMessage = "Permiso de ubicación denegado. Puedes elegir el mapa o escribir una referencia."
        }
    }

    fun requestCurrentLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) captureCurrentLocation() else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

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

    if (showPhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptions = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)
            ) {
                Text("Agregar foto", style = MaterialTheme.typography.titleMedium)
                AppButton(
                    onClick = {
                        showPhotoOptions = false
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    variant = AppButtonVariant.Outlined,
                ) {
                    Icon(Icons.Filled.Collections, contentDescription = null, modifier = Modifier.size(AppSpacing.iconMedium))
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Text("Elegir de la galería")
                }
                AppButton(
                    onClick = {
                        showPhotoOptions = false
                        launchCamera()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    variant = AppButtonVariant.Outlined,
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(AppSpacing.iconMedium))
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Text("Tomar foto con la cámara")
                }
            }
        }
    }

    if (showLocationChoices) {
        LocationChoiceSheet(
            onDismiss = { showLocationChoices = false },
            onCurrentLocation = {
                showLocationChoices = false
                requestCurrentLocation()
            },
            onMap = {
                showLocationChoices = false
                showMapPicker = true
            },
            onManual = {
                showLocationChoices = false
                showManualLocation = true
            }
        )
    }

    if (showMapPicker) {
        MapLocationSheet(
            initialSelection = locationSelection,
            onDismiss = { showMapPicker = false },
            onConfirm = { selection ->
                showMapPicker = false
                applyLocationSelection(selection)
            }
        )
    }

    if (showManualLocation) {
        ManualLocationSheet(
            initialValue = if (locationSource == LocationSource.MANUAL_COARSE && !locationSelection?.hasCoordinates.orFalse()) {
                lastSeenLocation
            } else {
                ""
            },
            onDismiss = { showManualLocation = false },
            onConfirm = { selection ->
                showManualLocation = false
                applyLocationSelection(selection)
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = { Text("Publicar Mascota Perdida") },
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            FormPhotoUploadSurface(
                selectedPhotoUri = photoUri,
                emptyTitle = "Toca para agregar foto",
                emptyDescription = "Elige una foto desde galería o toma una nueva",
                photoContentDescription = "Foto de la mascota",
                onSurfaceClick = { showPhotoOptions = true },
                testTag = "create-post-photo-upload-surface",
                selectedContent = {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.mediaOverlay),
                        shape = AppShapes.chip,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(AppSpacing.mediaOverlayPadding)
                    ) {
                        Text(
                            text = "Toca para cambiar la foto",
                            modifier = Modifier.padding(horizontal = AppSpacing.compactCardPadding, vertical = AppSpacing.sm),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(AppSpacing.headerLogo)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(AppSpacing.iconMedium)
                        )
                    }
                }
                FormSectionTitle(text = "Datos de la mascota")
            }

            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                FormFieldLabel(text = "Nombre", required = true)
                OutlinedTextField(
                    value = petName,
                    onValueChange = {
                        petName = it
                        if (it.isNotBlank() && formMessage == "Campo obligatorio") {
                            formMessage = null
                        }
                    },
                    placeholder = { FormFieldPlaceholder("Ej. Toby, Mia") },
                    textStyle = AppFormTypography.input,
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.chip,
                    singleLine = true
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                FormFieldLabel("Descripcion adicional")
                OutlinedTextField(
                    value = recognitionDetails,
                    onValueChange = { recognitionDetails = limitAdditionalDetailsInput(it) },
                    placeholder = { FormFieldPlaceholder("Contanos cómo reconocerla...") },
                    textStyle = AppFormTypography.input,
                    supportingText = {
                        Text(
                            text = "${recognitionDetails.length}/$AdditionalDetailsMaxLength",
                            style = AppFormTypography.placeholder,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppSpacing.formFieldHeight),
                    shape = AppShapes.chip,
                    minLines = 3,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                FormFieldLabel("¿Dónde fue vista por última vez?", required = true)
                LocationSelectionField(
                    selection = locationSelection,
                    onClick = { showLocationChoices = true }
                )
                locationSelection?.address?.takeIf { it.isNotBlank() }?.let { address ->
                    Text(
                        text = address,
                        style = AppFormTypography.placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AppButton(
                onClick = {
                    val petNameError = requiredPetNameMessage(petName)
                    if (petNameError != null) {
                        formMessage = petNameError
                        return@AppButton
                    }
                    val selectedMediaSource = mediaSource
                    if (selectedMediaSource == null) {
                        formMessage = "Adjunta una foto real desde camara o galeria."
                        return@AppButton
                    }
                    if (locationSelection?.isValid != true) {
                        formMessage = "Selecciona una ubicación antes de publicar."
                        return@AppButton
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
                enabled = locationSelection?.isValid == true && photoUri.isNotBlank() && petName.isNotBlank() && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth(),
                contentDescription = "Publicar ficha",
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(AppSpacing.progressIndicator))
                } else {
                    Icon(Icons.Filled.Publish, contentDescription = null)
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Text(text = "Publicar ficha", style = MaterialTheme.typography.labelLarge)
                }
            }

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
    }
}

internal fun requiredPetNameMessage(petName: String): String? =
    if (petName.isBlank()) "Campo obligatorio" else null

internal const val AdditionalDetailsMaxLength = 500

internal fun limitAdditionalDetailsInput(value: String): String =
    value.take(AdditionalDetailsMaxLength)

private fun Boolean?.orFalse(): Boolean = this == true
