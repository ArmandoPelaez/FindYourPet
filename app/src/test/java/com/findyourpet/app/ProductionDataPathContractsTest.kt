package com.findyourpet.app

import com.findyourpet.app.data.remote.BackendSyncState
import com.findyourpet.app.data.remote.RemoteMappers.toChatMessageEntity
import com.findyourpet.app.data.remote.RemoteMappers.toChatSessionEntity
import com.findyourpet.app.data.remote.RemoteMappers.toNotificationEntity
import com.findyourpet.app.data.remote.RemoteMappers.toPetPostEntity
import com.findyourpet.app.data.remote.RemoteMappers.toSightingEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionDataPathContractsTest {
  @Test
  fun backendSyncStateRepresentsLoadingSuccessAndError() {
    val loading = BackendSyncState.loading(emptyList<String>(), isRemoteBackend = true)
    val success = BackendSyncState.data(listOf("post_1"), isFromCache = false, hasPendingWrites = false)
    val error = BackendSyncState.error(emptyList<String>(), "Backend read failed.")

    assertTrue(loading.isLoading)
    assertFalse(loading.hasError)
    assertEquals(listOf("post_1"), success.data)
    assertFalse(success.isLoading)
    assertTrue(error.hasError)
    assertEquals("Backend read failed.", error.errorMessage)
  }

  @Test
  fun emptyRemoteDocumentsMapToSafeEmptyStates() {
    val post = emptyMap<String, Any?>().toPetPostEntity("post_empty")
    val sighting = emptyMap<String, Any?>().toSightingEntity("sighting_empty")
    val chat = emptyMap<String, Any?>().toChatSessionEntity("chat_empty")
    val message = emptyMap<String, Any?>().toChatMessageEntity("message_empty")
    val notification = emptyMap<String, Any?>().toNotificationEntity("notification_empty")

    assertEquals("post_empty", post.id)
    assertEquals("PERDIDO", post.status)
    assertEquals("sighting_empty", sighting.id)
    assertEquals("chat_empty", chat.id)
    assertEquals("message_empty", message.id)
    assertNull(message.photoUri)
    assertEquals("notification_empty", notification.id)
    assertFalse(notification.isRead)
  }

  @Test
  fun sightingDetailMappingPreservesDirectReadFields() {
    val sighting = mapOf<String, Any?>(
      "id" to "sighting_21",
      "postId" to "post_7",
      "ownerId" to "owner_7",
      "reporterId" to "reporter_3",
      "reporterName" to "Ana",
      "photoUri" to "https://example.test/sighting.jpg",
      "locationName" to "Plaza Italia",
      "latitude" to -34.5811,
      "longitude" to -58.4233,
      "notes" to "Lo vi junto a la entrada norte.",
      "timestamp" to 1_723_456_789_000L,
      "idempotencyKey" to "submission-21"
    ).toSightingEntity("sighting_21")

    assertEquals("sighting_21", sighting.id)
    assertEquals("post_7", sighting.postId)
    assertEquals("Plaza Italia", sighting.locationName)
    assertEquals(-34.5811, sighting.latitude, 0.0)
    assertEquals(-58.4233, sighting.longitude, 0.0)
    assertEquals("Lo vi junto a la entrada norte.", sighting.notes)
    assertEquals(1_723_456_789_000L, sighting.timestamp)
    assertEquals("https://example.test/sighting.jpg", sighting.photoUri)
  }
}
