package com.findyourpet.app

import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.domain.DemoPostImporter
import com.findyourpet.app.ui.viewmodel.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class DemoPostImporterTest {
  @Test
  fun seedPostReceivesAuthenticatedOwnerBeforeProductionUse() {
    val productionPost = DemoPostImporter.toAuthenticatedProductionPost(
      seedPost = seedPost(ownerId = "owner_1"),
      user = UserProfile(
        id = "firebase_uid",
        name = "Real User",
        phone = "+555",
        email = "real@example.com"
      )
    )

    assertEquals("firebase_uid", productionPost.ownerId)
    assertEquals("Real User", productionPost.ownerName)
    assertEquals("+555", productionPost.ownerPhone)
    assertEquals("real@example.com", productionPost.ownerEmail)
  }

  @Test(expected = IllegalArgumentException::class)
  fun importRequiresSignedInUser() {
    DemoPostImporter.toAuthenticatedProductionPost(
      seedPost = seedPost(ownerId = "owner_1"),
      user = UserProfile(id = "", name = "", phone = "", email = "")
    )
  }

  private fun seedPost(ownerId: String) =
    PetPostEntity(
      id = "post_1",
      petName = "Max",
      species = "Perro",
      breed = "Golden",
      color = "Dorado",
      features = "Collar rojo",
      status = "PERDIDO",
      photoUri = "",
      dateLost = 1L,
      lastSeenLocation = "Parque",
      latitude = 0.0,
      longitude = 0.0,
      rewardAmount = "",
      ownerId = ownerId,
      ownerName = "Demo Owner",
      ownerPhone = "+111",
      ownerEmail = "demo@example.com",
      ownerAddress = "Demo"
    )
}
