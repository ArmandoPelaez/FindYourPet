package com.findyourpet.app.data.product

import android.net.Uri

data class MediaReference(
    val localUri: Uri,
    val remoteUrl: String = "",
    val provider: String = "",
    val publicId: String = "",
    val contentType: String = "image/jpeg",
    val source: MediaSource
) {
    val displayUri: String
        get() = remoteUrl.ifBlank { localUri.toString() }

    val isUploaded: Boolean
        get() = remoteUrl.isNotBlank() && provider.isNotBlank() && publicId.isNotBlank()
}

enum class MediaSource {
    CAMERA,
    GALLERY
}

sealed interface MediaUploadState {
    data object Idle : MediaUploadState
    data object Uploading : MediaUploadState
    data class Uploaded(val reference: MediaReference) : MediaUploadState
    data class Failed(val message: String) : MediaUploadState
}

enum class LocationSource {
    NONE,
    DEVICE_GPS,
    MANUAL_COARSE
}

enum class LocationPermissionState {
    UNKNOWN,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED,
    UNAVAILABLE
}

data class LocationCapture(
    val label: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val source: LocationSource,
    val permissionState: LocationPermissionState
) {
    val hasPreciseCoordinates: Boolean
        get() = source == LocationSource.DEVICE_GPS
}

data class FormValidationResult(
    val isValid: Boolean,
    val message: String? = null
) {
    companion object {
        val Valid = FormValidationResult(isValid = true)
    }
}
