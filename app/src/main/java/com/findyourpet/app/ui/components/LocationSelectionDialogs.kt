package com.findyourpet.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.findyourpet.app.BuildConfig
import com.findyourpet.app.data.location.LocationSelection
import com.findyourpet.app.data.location.reverseGeocode
import com.findyourpet.app.ui.theme.AppFormTypography
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun LocationSelectionField(
    selection: LocationSelection?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        OutlinedTextField(
            value = selection?.displayText.orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = { FormFieldPlaceholder("Seleccionar ubicación") },
            textStyle = AppFormTypography.input,
            modifier = Modifier.fillMaxWidth(),
            shape = AppShapes.chip
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationChoiceSheet(
    onDismiss: () -> Unit,
    onCurrentLocation: () -> Unit,
    onMap: () -> Unit,
    onManual: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)
        ) {
            Text("Seleccionar ubicación", style = MaterialTheme.typography.titleMedium)
            LocationChoiceButton("Usar mi ubicación actual", onCurrentLocation)
            LocationChoiceButton("Elegir en el mapa", onMap)
            LocationChoiceButton("Escribir una referencia", onManual)
        }
    }
}

@Composable
private fun LocationChoiceButton(text: String, onClick: () -> Unit) {
    AppButton(
        onClick = onClick,
        variant = AppButtonVariant.Outlined,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualLocationSheet(
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (LocationSelection) -> Unit
) {
    var value by remember(initialValue) { mutableStateOf(initialValue) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)
        ) {
            Text("Escribir una referencia", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                placeholder = { FormFieldPlaceholder("Ej. Palermo, CABA") },
                textStyle = AppFormTypography.input,
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.chip,
                singleLine = true
            )
            AppButton(
                onClick = {
                    LocationSelection.manualReference(value)?.let(onConfirm)
                },
                enabled = value.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(AppSpacing.iconMedium))
                Text("Confirmar referencia")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLocationSheet(
    initialSelection: LocationSelection?,
    onDismiss: () -> Unit,
    onConfirm: (LocationSelection) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val initialPoint = remember(initialSelection) {
        initialSelection?.latitude?.let { latitude ->
            initialSelection.longitude?.let { longitude -> LatLng(latitude, longitude) }
        } ?: DefaultMapCenter
    }
    var pendingPoint by remember(initialPoint) { mutableStateOf<LatLng?>(null) }
    var detectedLabel by remember(initialPoint) { mutableStateOf<String?>(null) }
    var isResolvingReference by remember(initialPoint) { mutableStateOf(false) }
    var geocoderJob by remember { mutableStateOf<Job?>(null) }
    var mapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPoint, DefaultMapZoom)
    }
    val markerState = remember { MarkerState(position = initialPoint) }
    val mapsConfigured = BuildConfig.MAPS_API_KEY.isNotBlank() && BuildConfig.MAPS_API_KEY != DefaultApiKey

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Elegir en el mapa", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }
            if (!mapsConfigured) {
                Text(
                    "El mapa no está configurado. Cierra esta ventana y elige otra opción.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }
            Box(modifier = Modifier.fillMaxWidth().height(AppSpacing.mediaHeight)) {
                GoogleMap(
                    modifier = Modifier.fillMaxWidth(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isBuildingEnabled = false),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true),
                    onMapClick = { point ->
                        pendingPoint = point
                        markerState.position = point
                        detectedLabel = null
                        isResolvingReference = true
                        geocoderJob?.cancel()
                        geocoderJob = coroutineScope.launch {
                            val label = reverseGeocode(context, point.latitude, point.longitude)
                            if (!currentCoroutineContext().isActive) return@launch
                            detectedLabel = label
                            isResolvingReference = false
                        }
                    },
                    onMapLoaded = { mapLoaded = true }
                ) {
                    if (pendingPoint != null) {
                        Marker(state = markerState, title = "Ubicación seleccionada")
                    }
                }
            }
            if (!mapLoaded) {
                Text("Cargando mapaâ€¦", style = MaterialTheme.typography.bodySmall)
            }
            when {
                isResolvingReference -> {
                    Text("Buscando una referenciaâ€¦", style = MaterialTheme.typography.bodySmall)
                }
                detectedLabel != null -> {
                    Text(
                        text = "Referencia detectada: $detectedLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                pendingPoint != null -> {
                    Text(
                        text = "Punto seleccionado en el mapa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            val confirmedLabel = detectedLabel ?: pendingPoint?.let { "Punto seleccionado en el mapa" }
            AppButton(
                onClick = {
                    val point = pendingPoint ?: return@AppButton
                    val label = confirmedLabel ?: return@AppButton
                    onConfirm(
                        LocationSelection(
                            displayText = label,
                            latitude = point.latitude,
                            longitude = point.longitude,
                            source = com.findyourpet.app.data.product.LocationSource.MANUAL_COARSE
                        )
                    )
                },
                enabled = pendingPoint != null && !isResolvingReference && confirmedLabel != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar ubicación")
            }
        }
    }
}

private const val DefaultApiKey = "DEFAULT_API_KEY"
private const val DefaultMapZoom = 12f
private val DefaultMapCenter = LatLng(-34.6037, -58.3816)
