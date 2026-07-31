package com.findyourpet.app.data.product

import com.findyourpet.app.domain.OwnershipPolicy

object RealProductValidators {
    private val demoPhotoMarkers = listOf(
        "images.unsplash.com",
        "auto=format&fit=crop",
        "preset",
        "sample"
    )

    fun validatePost(
        petName: String,
        photoUri: String,
        ownerId: String,
        locationName: String
    ): FormValidationResult {
        if (ownerId.isBlank()) return FormValidationResult(false, "Inicia sesion antes de publicar.")
        if (petName.isBlank()) return FormValidationResult(false, "Indica el nombre de la mascota.")
        if (locationName.isBlank()) return FormValidationResult(false, "Indica una ubicacion aproximada.")
        if (!isRealMediaUri(photoUri)) return FormValidationResult(false, "Adjunta una foto real desde camara o galeria.")
        return FormValidationResult.Valid
    }

    fun validateSighting(
        reporterId: String,
        postId: String,
        ownerId: String,
        locationName: String,
        locationSource: LocationSource,
        photoUri: String?
    ): FormValidationResult {
        if (reporterId.isBlank()) return FormValidationResult(false, "Inicia sesion antes de reportar.")
        if (postId.isBlank() || ownerId.isBlank()) return FormValidationResult(false, "No se pudo identificar la publicacion.")
        if (!OwnershipPolicy.canReportSighting(reporterId, ownerId)) {
            return FormValidationResult(false, "No puedes reportar avistamientos de tu propia publicacion.")
        }
        if (locationName.isBlank()) return FormValidationResult(false, "Indica donde viste la mascota.")
        if (locationSource == LocationSource.NONE) return FormValidationResult(false, "Usa ubicacion actual o una ubicacion manual.")
        if (!photoUri.isNullOrBlank() && !isRealMediaUri(photoUri)) {
            return FormValidationResult(false, "La foto del avistamiento debe venir de camara o galeria.")
        }
        return FormValidationResult.Valid
    }

    fun isRealMediaUri(value: String): Boolean {
        val uri = value.trim()
        if (uri.isBlank()) return false
        if (demoPhotoMarkers.any { marker -> uri.contains(marker, ignoreCase = true) }) return false
        return uri.startsWith("content://") || uri.startsWith("file://")
    }
}
