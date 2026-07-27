package com.findyourpet.app

import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.ContactGrantEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.remote.RemoteMappers.toChatMessageEntity
import com.findyourpet.app.data.remote.RemoteMappers.toChatSessionEntity
import com.findyourpet.app.data.remote.RemoteMappers.toContactGrantEntity
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
    assertEquals("", mapped.ownerPhone)
    assertEquals("", mapped.ownerEmail)
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
  fun contactGrantDocumentIsScopedToOneChat() {
    val grant = ContactGrantEntity(
      id = "ownerContact",
      chatId = "chat_1",
      postId = "post_1",
      ownerId = "uid_owner",
      reporterId = "uid_reporter",
      sharedBy = "uid_owner",
      sharedAt = 123L,
      revokedAt = null,
      isActive = true,
      ownerName = "Owner",
      ownerPhone = "+506 7000-0000",
      ownerEmail = "owner@example.com"
    )

    val document = grant.toDocument(createdAt = 100L)
    val mapped = document.toContactGrantEntity("ownerContact")

    assertEquals("chat_1", document["chatId"])
    assertEquals("post_1", document["postId"])
    assertEquals("uid_owner", document["ownerId"])
    assertEquals("uid_reporter", document["reporterId"])
    assertEquals("uid_owner", document["sharedBy"])
    assertTrue(mapped.isActive)
    assertEquals("+506 7000-0000", mapped.ownerPhone)
  }

  @Test
  fun contactShareAndRevokeNotificationsRemainGeneric() {
    val contactNotifications = listOf(
      AppNotificationEntity(
        id = "share_notification",
        recipientId = "uid_reporter",
        title = "Contacto actualizado",
        message = "El dueno habilito contacto dentro de la conversacion.",
        type = "CONTACT_SHARED",
        targetId = "chat_1",
        timestamp = 123L
      ),
      AppNotificationEntity(
        id = "revoke_notification",
        recipientId = "uid_reporter",
        title = "Contacto actualizado",
        message = "El dueno actualizo la disponibilidad de contacto.",
        type = "CONTACT_SHARED",
        targetId = "chat_1",
        timestamp = 124L
      )
    )

    contactNotifications.forEach { notification ->
      val document = notification.toDocument(createdAt = 100L)
      val message = document["message"].toString()

      assertFalse(message.contains("+506"))
      assertFalse(message.contains("@"))
      assertFalse(message.contains("owner@example.com"))
      assertFalse(message.contains("7000-0000"))
    }
  }

  private fun samplePost(ownerId: String): PetPostEntity =
    PetPostEntity(
      id = "post_1",
      petName = "Milo",
      species = "Perro",
      breed = "Mestizo",
      color = "Cafe",
      features = "Collar rojo",
      status = "PERDIDO",
      photoUri = "photo",
      dateLost = 123L,
      lastSeenLocation = "Barrio Central",
      latitude = 1.0,
      longitude = 2.0,
      rewardAmount = "Sin recompensa",
      ownerId = ownerId,
      ownerName = "Owner",
      ownerPhone = "+506 7000-0000",
      ownerEmail = "owner@example.com",
      ownerAddress = "Address"
    )
}
