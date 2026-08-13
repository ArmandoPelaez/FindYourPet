package com.findyourpet.app

import com.findyourpet.app.data.location.LocationSelection
import com.findyourpet.app.data.product.LocationPermissionState
import com.findyourpet.app.data.product.LocationCapture
import com.findyourpet.app.data.product.LocationSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationSelectionTest {
    @Test
    fun manualReference_clearsCoordinatesAndUsesManualSource() {
        val selection = LocationSelection.manualReference("Palermo, CABA")

        assertEquals("Palermo, CABA", selection?.displayText)
        assertEquals(LocationSource.MANUAL_COARSE, selection?.source)
        assertFalse(selection?.hasCoordinates == true)
        assertEquals(0.0, selection?.persistedLatitude())
        assertEquals(0.0, selection?.persistedLongitude())
    }

    @Test
    fun emptyManualReference_isNotAValidSelection() {
        assertNull(LocationSelection.manualReference("  "))
    }

    @Test
    fun deviceCapture_preservesCoordinatesAndGpsSource() {
        val selection = LocationSelection.fromCapture(
            LocationCapture(
                label = "Ubicación actual capturada",
                latitude = -34.6,
                longitude = -58.4,
                source = LocationSource.DEVICE_GPS,
                permissionState = LocationPermissionState.GRANTED
            )
        )

        assertTrue(selection?.isValid == true)
        assertTrue(selection?.hasCoordinates == true)
        assertEquals(LocationSource.DEVICE_GPS, selection?.source)
        assertEquals(-34.6, selection?.persistedLatitude())
        assertEquals(-58.4, selection?.persistedLongitude())
    }

    @Test
    fun unavailableCapture_doesNotCreateEmptySelection() {
        val selection = LocationSelection.fromCapture(
            LocationCapture(
                label = "",
                source = LocationSource.NONE,
                permissionState = LocationPermissionState.UNAVAILABLE
            )
        )

        assertNull(selection)
    }
}
