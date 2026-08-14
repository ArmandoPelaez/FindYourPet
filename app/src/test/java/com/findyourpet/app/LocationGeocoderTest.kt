package com.findyourpet.app

import com.findyourpet.app.data.location.compactAddressLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationGeocoderTest {
    @Test
    fun compactAddressLabel_prefersStreetNeighborhoodAndLocality() {
        assertEquals(
            "Av. Santa Fe 1234, Palermo, Buenos Aires",
            compactAddressLabel(
                thoroughfare = "Av. Santa Fe",
                subThoroughfare = "1234",
                subLocality = "Palermo",
                locality = "Buenos Aires",
                administrativeArea = "Ciudad Autónoma de Buenos Aires",
                postalCode = "C1425",
                addressLine = "Av. Santa Fe 1234, C1425 Buenos Aires, Argentina",
            ),
        )
    }

    @Test
    fun compactAddressLabel_removesPostalCodeFromFallbackLine() {
        assertEquals(
            "Av. Santa Fe 1234, Buenos Aires",
            compactAddressLabel(
                thoroughfare = null,
                subThoroughfare = null,
                subLocality = null,
                locality = null,
                administrativeArea = null,
                postalCode = "C1425",
                addressLine = "Av. Santa Fe 1234, C1425 Buenos Aires",
            ),
        )
    }
}
