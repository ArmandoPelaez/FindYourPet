package com.findyourpet.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Publish
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.ui.media.CameraImageCapture
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.FormPhotoUploadSurface
import com.findyourpet.app.ui.components.FormSectionTitle
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
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
    var showPhotoOptions by remember { mutableStateOf(false) }
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

            FormSectionTitle(text = "Datos de la Mascota")

            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                placeholder = { Text("Nombre de la mascota (Ej. Toby, Mia)") },
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.chip,
                singleLine = true
            )

            OutlinedTextField(
                value = recognitionDetails,
                onValueChange = { recognitionDetails = it },
                label = { Text("Detalles adicionales") },
                supportingText = {
                    Text("Mas detalles utiles para reconocerla. Ej. color, señas particulares, collar o temperamento")
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
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            FormSectionTitle(text = "Ubicacion")

            OutlinedTextField(
                value = lastSeenLocation,
                onValueChange = {
                    lastSeenLocation = it
                    locationSource = LocationSource.MANUAL_COARSE
                    latitude = 0.0
                    longitude = 0.0
                },
                placeholder = { Text("Ultima ubicacion vista (Barrio, Ciudad o referencia)") },
                leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.chip
            )

            AppButton(
                onClick = {
                    val selectedMediaSource = mediaSource
                    if (selectedMediaSource == null) {
                        formMessage = "Adjunta una foto real desde camara o galeria."
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
                enabled = petName.isNotBlank() && lastSeenLocation.isNotBlank() && photoUri.isNotBlank() && !isSubmitting,
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
