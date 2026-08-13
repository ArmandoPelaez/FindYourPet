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
    address?.getAddressLine(0)?.trim()?.takeIf { it.isNotBlank() }
        ?: address?.let {
            listOfNotNull(it.subLocality, it.locality, it.adminArea)
                .joinToString(", ")
                .takeIf(String::isNotBlank)
        }
