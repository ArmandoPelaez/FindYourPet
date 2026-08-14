package com.findyourpet.app.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

suspend fun reverseGeocode(
    context: Context,
    latitude: Double,
    longitude: Double
): String? {
    if (!Geocoder.isPresent()) return null

    return try {
        val geocoder = Geocoder(context.applicationContext, Locale.getDefault())
        val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (continuation.isActive) continuation.resume(addresses.firstOrNull())
                        }

                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                )
            }
        } else {
            @Suppress("DEPRECATION")
            withContext(Dispatchers.IO) {
                geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
            }
        }

        addressLabel(address)
    } catch (error: CancellationException) {
        throw error
    } catch (_: Exception) {
        null
    }
}

private fun addressLabel(address: Address?): String? =
    compactAddressLabel(address)

internal fun compactAddressLabel(address: Address?): String? {
    val resolvedAddress = address ?: return null
    return compactAddressLabel(
        thoroughfare = resolvedAddress.thoroughfare,
        subThoroughfare = resolvedAddress.subThoroughfare,
        subLocality = resolvedAddress.subLocality,
        locality = resolvedAddress.locality,
        administrativeArea = resolvedAddress.adminArea,
        postalCode = resolvedAddress.postalCode,
        addressLine = resolvedAddress.getAddressLine(0),
    )
}

internal fun compactAddressLabel(
    thoroughfare: String? = null,
    subThoroughfare: String? = null,
    subLocality: String?,
    locality: String?,
    administrativeArea: String?,
    postalCode: String?,
    addressLine: String?,
): String? {
    val street = listOfNotNull(
        thoroughfare.cleanAddressPart(),
        subThoroughfare.cleanAddressPart(),
    ).joinToString(" ").takeIf { it.isNotBlank() }
    val neighborhood = subLocality.cleanAddressPart()
    val resolvedLocality = locality.cleanAddressPart()
    val resolvedAdministrativeArea = administrativeArea.cleanAddressPart()

    val compactParts = listOfNotNull(
        street,
        neighborhood,
        resolvedLocality ?: resolvedAdministrativeArea,
    ).distinct()

    return compactParts.joinToString(", ")
        .takeIf(String::isNotBlank)
        ?: fallbackAddressLine(addressLine, postalCode)
}

private fun fallbackAddressLine(addressLine: String?, postalCode: String?): String? {
    val line = addressLine?.trim().takeIf { !it.isNullOrBlank() }
        ?: return null

    val withoutPostalCode = postalCode
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?.let { postalCode -> line.replace(postalCode, "", ignoreCase = true) }
        ?: line

    return withoutPostalCode
        .replace(Regex("\\s{2,}"), " ")
        .replace(Regex("\\s*,\\s*,"), ",")
        .trim(' ', ',')
        .takeIf { it.isNotBlank() }
}

private fun String?.cleanAddressPart(): String? =
    this?.trim()?.takeIf { it.isNotBlank() }
