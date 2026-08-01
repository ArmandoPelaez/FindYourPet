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
}
