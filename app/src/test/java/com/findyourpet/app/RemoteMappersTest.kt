package com.findyourpet.app

import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.remote.RemoteMappers.toDocument
import com.findyourpet.app.data.remote.RemoteMappers.toNotificationEntity
import com.findyourpet.app.data.remote.RemoteMappers.toPetPostEntity
import com.findyourpet.app.data.remote.RemoteMappers.toSightingEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteMappersTest {
  @Test
  fun petPostDocumentKeepsOwnerIdentityAndOmitsPublicContactFields() {
    val post = samplePost(ownerId = "uid_owner")
    val document = post.toDocument(createdAt = 100L)
    val mapped = document.toPetPostEntity("post_remote")

    assertEquals("uid_owner", mapped.ownerId)
    assertNull(document["ownerPhone"])
    assertNull(document["ownerEmail"])
    assertNull(document["ownerAddress"])
    assertEquals(post.latitude, document["latitude"])
  }

  @Test
  fun sightingDocumentPreservesSightingAndConsentMetadata() {
    val sighting = SightingAlertEntity(
      id = "sighting_1", postId = "post_1", ownerId = "uid_owner",
      reporterId = "uid_reporter", reporterName = "Reporter", photoUri = "photo",
      locationName = "Corner", latitude = 1.0, longitude = 2.0,
      notes = "Possible match", timestamp = 123L
    )

    val document = sighting.toDocument(
      ownerId = "uid_owner", createdAt = 100L,
      mediaProvider = "CLOUDINARY", mediaPublicId = "findyourpet/sighting",
      mediaContentType = "image/jpeg", mediaSource = "GALLERY",
      locationSource = "DEVICE_GPS", preciseLocationConsented = true
    )
    val mapped = document.toSightingEntity("sighting_1")

    assertEquals("uid_owner", mapped.ownerId)
    assertEquals("uid_reporter", mapped.reporterId)
    assertEquals("CLOUDINARY", document["mediaProvider"])
    assertEquals("DEVICE_GPS", document["locationSource"])
    assertEquals(true, document["preciseLocationConsented"])
  }

  @Test
  fun sightingNotificationStoresOnlySightingRoutingReferences() {
    val notification = AppNotificationEntity(
      id = "notification_sighting", recipientId = "uid_owner",
      title = "Nuevo avistamiento", message = "preview", type = "ALERT",
      targetId = "sighting_1", timestamp = 123L,
      sightingId = "sighting_1", postId = "post_1"
    )

    val document = notification.toDocument(createdAt = 100L)
    val mapped = document.toNotificationEntity("notification_sighting")

    assertEquals("sighting_1", document["targetId"])
    assertEquals("sighting_1", document["sightingId"])
    assertEquals("post_1", document["postId"])
    assertNull(document["chatId"])
    assertEquals("ALERT", mapped.type)
  }

  @Test
  fun historicalChatNotificationDecodesWithoutActiveWriteOrRouteContract() {
    val mapped = mapOf<String, Any?>(
      "id" to "notification_legacy", "recipientId" to "uid_owner",
      "title" to "Nuevo mensaje", "type" to "CHAT", "targetId" to "chat_1",
      "chatId" to "chat_1", "postId" to "post_1"
    ).toNotificationEntity()

    assertEquals("chat_1", mapped.chatId)
    assertEquals("Esta notificacion historica ya no tiene una accion disponible.", mapped.message)
  }

  @Test(expected = IllegalArgumentException::class)
  fun retiredChatNotificationCannotBeWritten() {
    AppNotificationEntity(
      id = "chat_notification", recipientId = "uid_owner", title = "legacy",
      message = "legacy", type = "CHAT", targetId = "chat_1", timestamp = 123L
    ).toDocument(createdAt = 100L)
  }

  private fun samplePost(ownerId: String): PetPostEntity = PetPostEntity(
    id = "post_1", petName = "Milo", species = "Perro", breed = "Mestizo",
    color = "Cafe", features = "Collar rojo", status = "PERDIDO", photoUri = "photo",
    dateLost = 123L, lastSeenLocation = "Barrio Central", latitude = 1.0,
    longitude = 2.0, rewardAmount = "Sin recompensa", ownerId = ownerId, ownerName = "Owner"
  )
}
