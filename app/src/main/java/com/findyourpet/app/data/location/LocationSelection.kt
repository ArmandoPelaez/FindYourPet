package com.findyourpet.app.data.location

import com.findyourpet.app.data.product.LocationCapture
import com.findyourpet.app.data.product.LocationSource

data class LocationSelection(
    val displayText: String,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val source: LocationSource
) {
    val hasCoordinates: Boolean
        get() = latitude != null && longitude != null

    val isValid: Boolean
        get() = displayText.isNotBlank() && source != LocationSource.NONE

    fun persistedLatitude(): Double = latitude ?: 0.0

    fun persistedLongitude(): Double = longitude ?: 0.0

    companion object {
        fun fromCapture(capture: LocationCapture): LocationSelection? =
            capture.takeIf { it.source != LocationSource.NONE && it.label.isNotBlank() }?.let {
                LocationSelection(
                    displayText = it.label,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    source = it.source
                )
            }

        fun manualReference(value: String): LocationSelection? =
            value.trim().takeIf { it.isNotBlank() }?.let {
                LocationSelection(displayText = it, source = LocationSource.MANUAL_COARSE)
            }
    }
}
