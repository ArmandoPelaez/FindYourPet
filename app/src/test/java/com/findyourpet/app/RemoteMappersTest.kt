package com.findyourpet.app

import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.local.entity.SIGHTING_ALERT_MESSAGE_TYPE
import com.findyourpet.app.data.remote.RemoteMappers.toChatMessageEntity
import com.findyourpet.app.data.remote.RemoteMappers.toChatSessionEntity
import com.findyourpet.app.data.remote.RemoteMappers.toDocument
import com.findyourpet.app.data.remote.RemoteMappers.toNotificationEntity
import com.findyourpet.app.data.remote.RemoteMappers.toPetPostEntity
import com.findyourpet.app.data.remote.RemoteMappers.toSightingEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteMappersTest {
  @Test
  fun petPostDocumentKeepsOwnerIdentityAndOmitsPublicContactFields() {
    val post = samplePost(ownerId = "uid_owner")

    val document = post.toDocument(createdAt = 100L)
    val mapped = document.toPetPostEntity("post_remote")

    assertEquals("uid_owner", document["ownerId"])
    assertEquals("uid_owner", mapped.ownerId)
    assertNull(document["ownerPhone"])
    assertNull(document["ownerEmail"])
    assertNull(document["ownerAddress"])
    assertNull(document["isContactRevealedToAll"])
    assertNull(document["latitude"])
    assertNull(document["longitude"])
  }

  @Test
  fun petPostMapDefaultsStatusWhenMissing() {
    val mapped = mapOf(
      "id" to "post_1",
      "ownerId" to "uid_owner",
      "petName" to "Milo"
    ).toPetPostEntity("post_1")

    assertEquals("PERDIDO", mapped.status)
    assertEquals("uid_owner", mapped.ownerId)
    assertEquals("", mapped.characteristics)
  }

  @Test
  fun petPostCharacteristicsRoundTripsAsIndependentRemoteField() {
    val post = samplePost(ownerId = "uid_owner").copy(characteristics = "Negro, mediano, 4 años")

    val document = post.toDocument(createdAt = 100L)
    val mapped = document.toPetPostEntity("post_characteristics")

    assertEquals("Negro, mediano, 4 años", document["characteristics"])
    assertEquals("Negro, mediano, 4 años", mapped.characteristics)
    assertEquals(post.features, document["features"])
  }

  @Test
  fun legacyPetPostWithoutCharacteristicsMapsToEmptyValue() {
    val mapped = mapOf(
      "id" to "post_legacy",
      "petName" to "Milo",
      "features" to "Collar rojo"
    ).toPetPostEntity("post_legacy")

    assertEquals("Collar rojo", mapped.features)
    assertEquals("", mapped.characteristics)
  }

  @Test
  fun mediaAndLocationMetadataAreMappedForProductionDocuments() {
    val post = samplePost(ownerId = "uid_owner").copy(photoUri = "https://res.cloudinary.com/mqt4dzrt/image/upload/example.jpg")

    val document = post.toDocument(
      createdAt = 100L,
      mediaProvider = "CLOUDINARY",
      mediaPublicId = "findyourpet/example",
      mediaContentType = "image/jpeg",
      mediaSource = "CAMERA",
      locationSource = "MANUAL_COARSE"
    )

    assertEquals("CLOUDINARY", document["mediaProvider"])
    assertEquals("findyourpet/example", document["mediaPublicId"])
    assertEquals("image/jpeg", document["mediaContentType"])
    assertEquals("CAMERA", document["mediaSource"])
    assertEquals("MANUAL_COARSE", document["locationSource"])
    assertEquals("Barrio Central", document["publicLocationName"])
  }

  @Test
  fun sightingDocumentRequiresReporterAndDerivedOwner() {
    val sighting = SightingAlertEntity(
      id = "sighting_1",
      postId = "post_1",
      ownerId = "uid_owner",
      reporterId = "uid_reporter",
      reporterName = "Reporter",
      photoUri = "photo",
      locationName = "Corner",
      latitude = 1.0,
      longitude = 2.0,
      notes = "Possible match",
      timestamp = 123L
    )

    val document = sighting.toDocument(ownerId = "uid_owner", createdAt = 100L)
    val mapped = document.toSightingEntity("sighting_1")

    assertEquals("uid_owner", document["ownerId"])
    assertEquals("uid_reporter", document["reporterId"])
    assertEquals("uid_owner", mapped.ownerId)
  }

  @Test
  fun sightingDocumentStoresMediaAndConsentedLocationMetadata() {
    val sighting = SightingAlertEntity(
      id = "sighting_1",
      postId = "post_1",
      ownerId = "uid_owner",
      reporterId = "uid_reporter",
      reporterName = "Reporter",
      photoUri = "https://res.cloudinary.com/mqt4dzrt/image/upload/sighting.jpg",
      locationName = "Corner",
      latitude = 1.0,
      longitude = 2.0,
      notes = "Possible match",
      timestamp = 123L
    )

    val document = sighting.toDocument(
      ownerId = "uid_owner",
      createdAt = 100L,
      mediaProvider = "CLOUDINARY",
      mediaPublicId = "findyourpet/sighting",
      mediaContentType = "image/jpeg",
      mediaSource = "GALLERY",
      locationSource = "DEVICE_GPS",
      preciseLocationConsented = true
    )

    assertEquals("CLOUDINARY", document["mediaProvider"])
    assertEquals("findyourpet/sighting", document["mediaPublicId"])
    assertEquals("GALLERY", document["mediaSource"])
    assertEquals("DEVICE_GPS", document["locationSource"])
    assertEquals(true, document["preciseLocationConsented"])
  }

  @Test
  fun chatSessionDocumentContainsExactlyOwnerAndReporterParticipants() {
    val session = ChatSessionEntity(
      id = "post_1_uid_reporter",
      postId = "post_1",
      petName = "Milo",
      petPhotoUri = "photo",
      ownerId = "uid_owner",
      reporterId = "uid_reporter",
      reporterName = "Reporter",
      lastMessage = "Nuevo mensaje en el chat",
      lastMessageTimestamp = 123L
    )

    val document = session.toDocument(createdAt = 100L)
    val mapped = document.toChatSessionEntity("post_1_uid_reporter")

    assertEquals(listOf("uid_owner", "uid_reporter"), document["participantIds"])
    assertNull(document["isContactSharedByOwner"])
    assertEquals("uid_owner", mapped.ownerId)
    assertEquals("uid_reporter", mapped.reporterId)
  }

  @Test
  fun messageDocumentKeepsSenderAndChatIds() {
    val message = ChatMessageEntity(
      id = "message_1",
      chatId = "chat_1",
      postId = "post_1",
      senderId = "uid_reporter",
      senderName = "Reporter",
      text = "Hello",
      photoUri = null,
      timestamp = 123L
    )

    val document = message.toDocument(createdAt = 100L)
    val mapped = document.toChatMessageEntity("message_1")

    assertEquals("uid_reporter", document["senderId"])
    assertEquals("chat_1", mapped.chatId)
  }

  @Test
  fun sightingAlertMessageDocumentContainsAuthorizedSnapshotAndMapsBack() {
    val message = ChatMessageEntity(
      id = "alert_1",
      chatId = "chat_1",
      postId = "post_1",
      senderId = "uid_reporter",
      senderName = "Reporter",
      text = "Nuevo avistamiento de Milo",
      photoUri = null,
      timestamp = 123L,
      type = SIGHTING_ALERT_MESSAGE_TYPE,
      sightingId = "sighting_1",
      ownerId = "uid_owner",
      reporterId = "uid_reporter",
      snapshotPetName = "Milo",
      photoAttachmentUri = "https://res.cloudinary.com/example/image/upload/sighting.jpg",
      locationDisplay = "Parque Central",
      generalDetails = "Lo vi junto a la entrada.",
      snapshotTimestamp = 123L
    )

    val document = message.toDocument(createdAt = 100L)
    @Suppress("UNCHECKED_CAST")
    val snapshot = document["snapshot"] as Map<String, Any?>
    val mapped = document.toChatMessageEntity("alert_1")

    assertEquals(SIGHTING_ALERT_MESSAGE_TYPE, document["type"])
    assertEquals("sighting_1", document["sightingId"])
    assertEquals("uid_owner", document["ownerId"])
    assertEquals("uid_reporter", document["reporterId"])
    assertEquals("Parque Central", snapshot["locationDisplay"])
    assertNull(snapshot["phone"])
    assertNull(snapshot["latitude"])
    assertEquals("Milo", mapped.snapshotPetName)
    assertEquals("Lo vi junto a la entrada.", mapped.generalDetails)
  }

  @Test
  fun legacyMessageWithoutNewFieldsFallsBackToText() {
    val mapped = mapOf(
      "id" to "legacy_1",
      "chatId" to "chat_1",
      "senderId" to "uid_reporter",
      "text" to "Mensaje historico"
    ).toChatMessageEntity("legacy_1")

    assertEquals("text", mapped.type)
    assertNull(mapped.sightingId)
    assertNull(mapped.photoAttachmentUri)
  }

  @Test
  fun notificationDocumentIsRecipientScopedAndPreviewOnly() {
    val notification = AppNotificationEntity(
      id = "notification_1",
      recipientId = "uid_owner",
      title = "Nuevo mensaje",
      message = "Tienes un nuevo mensaje en una conversacion.",
      type = "CHAT",
      targetId = "chat_1",
      timestamp = 123L
    )

    val document = notification.toDocument(createdAt = 100L)
    val mapped = document.toNotificationEntity("notification_1")

    assertEquals("uid_owner", document["recipientId"])
    assertEquals("uid_owner", mapped.recipientId)
    assertFalse(mapped.message.contains("+506"))
    assertFalse(mapped.message.contains("1.0, 2.0"))
  }

  @Test
  fun sightingNotificationStoresOnlyRoutingReferences() {
    val notification = AppNotificationEntity(
      id = "notification_sighting",
      recipientId = "uid_owner",
      title = "Nuevo avistamiento",
      message = "Recibiste un nuevo avistamiento en tu publicacion.",
      type = "ALERT",
      targetId = "chat_1",
      timestamp = 123L,
      chatId = "chat_1",
      sightingId = "sighting_1",
      postId = "post_1"
    )

    val document = notification.toDocument(createdAt = 100L)

    assertEquals("chat_1", document["chatId"])
    assertEquals("sighting_1", document["sightingId"])
    assertEquals("post_1", document["postId"])
    assertEquals("Recibiste un nuevo avistamiento en tu publicacion.", document["message"])
    assertNull(document["photoUri"])
    assertNull(document["latitude"])
  }

  @Test
  fun legacyContactFieldsAreIgnoredByCurrentMappers() {
    val post = mapOf(
      "id" to "post_legacy",
      "ownerId" to "uid_owner",
      "ownerName" to "Owner",
      "ownerPhone" to "+506 7000-0000",
      "ownerEmail" to "owner@example.com",
      "ownerAddress" to "Address",
      "isContactRevealedToAll" to true
    ).toPetPostEntity("post_legacy")
    val chat = mapOf(
      "id" to "chat_legacy",
      "ownerId" to "uid_owner",
      "reporterId" to "uid_reporter",
      "participantIds" to listOf("uid_owner", "uid_reporter"),
      "isContactSharedByOwner" to true
    ).toChatSessionEntity("chat_legacy")

    assertEquals("uid_owner", post.ownerId)
    assertEquals("uid_owner", chat.ownerId)
    assertNull(post.toDocument(createdAt = 100L)["ownerPhone"])
    assertNull(post.toDocument(createdAt = 100L)["ownerEmail"])
    assertNull(post.toDocument(createdAt = 100L)["ownerAddress"])
    assertNull(post.toDocument(createdAt = 100L)["isContactRevealedToAll"])
    assertNull(chat.toDocument(createdAt = 100L)["isContactSharedByOwner"])
  }

  @Test(expected = IllegalArgumentException::class)
  fun contactSharedNotificationTypeIsRetiredFromWrites() {
    AppNotificationEntity(
      id = "share_notification",
      recipientId = "uid_reporter",
      title = "Actividad de chat",
      message = "Tienes actividad historica en una conversacion.",
      type = "CONTACT_SHARED",
      targetId = "chat_1",
      timestamp = 123L
    ).toDocument(createdAt = 100L)
  }

  private fun samplePost(ownerId: String): PetPostEntity =
    PetPostEntity(
      id = "post_1",
      petName = "Milo",
      species = "Perro",
      breed = "Mestizo",
      color = "Cafe",
      features = "Collar rojo",
      characteristics = "Cafe, mediano",
      status = "PERDIDO",
      photoUri = "photo",
      dateLost = 123L,
      lastSeenLocation = "Barrio Central",
      latitude = 1.0,
      longitude = 2.0,
      rewardAmount = "Sin recompensa",
      ownerId = ownerId,
      ownerName = "Owner"
    )
}
