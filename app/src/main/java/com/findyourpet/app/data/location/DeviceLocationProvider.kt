package com.findyourpet.app.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.findyourpet.app.data.product.LocationCapture
import com.findyourpet.app.data.product.LocationPermissionState
import com.findyourpet.app.data.product.LocationSource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

object DeviceLocationProvider {
    suspend fun currentLocation(context: Context): LocationCapture {
        val appContext = context.applicationContext
        val hasFine = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            return LocationCapture(
                label = "",
                source = LocationSource.NONE,
                permissionState = LocationPermissionState.DENIED
            )
        }

        return runCatching { getCurrentLocation(appContext, hasFine) }
            .getOrElse {
                LocationCapture(
                    label = "",
                    source = LocationSource.NONE,
                    permissionState = LocationPermissionState.UNAVAILABLE
                )
            }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(context: Context, precise: Boolean): LocationCapture {
        val priority = if (precise) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY
        val location = LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(priority, null)
            .await()
            ?: return LocationCapture(
                label = "",
                source = LocationSource.NONE,
                permissionState = LocationPermissionState.UNAVAILABLE
            )

        return LocationCapture(
            label = "Ubicacion actual capturada",
            latitude = location.latitude,
            longitude = location.longitude,
            source = LocationSource.DEVICE_GPS,
            permissionState = LocationPermissionState.GRANTED
        )
    }
}
