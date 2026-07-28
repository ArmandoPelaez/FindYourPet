package com.findyourpet.app

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.ui.viewmodel.PetViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PetViewModelStateTest {
  @Test
  fun initialSignedOutState_exposesEmptyCriticalFlows() {
    val viewModel = PetViewModel(ApplicationProvider.getApplicationContext<Application>())

    assertFalse(viewModel.isAuthenticated.value)
    assertEquals("", viewModel.currentUser.value.id)
    assertTrue(viewModel.filteredPosts.value.isEmpty())
    assertTrue(viewModel.userChatSessions.value.isEmpty())
    assertTrue(viewModel.allNotifications.value.isEmpty())
    assertEquals(null, viewModel.selectedPost.value)
    assertEquals(null, viewModel.activeChatSession.value)
    assertEquals(null, viewModel.activeContactGrant.value)
  }

  @Test
  fun createPostWithoutAuthentication_returnsValidationError() {
    val viewModel = PetViewModel(ApplicationProvider.getApplicationContext<Application>())
    var errorMessage = ""
    var completed = false

    viewModel.createNewPetPost(
      petName = "Milo",
      species = "Perro",
      breed = "Mestizo",
      color = "Cafe",
      features = "Collar rojo",
      photoUri = "content://photo",
      lastSeenLocation = "Barrio Central",
      latitude = 0.0,
      longitude = 0.0,
      rewardAmount = "Sin recompensa",
      mediaSource = MediaSource.GALLERY,
      locationSource = LocationSource.MANUAL_COARSE,
      onComplete = { completed = true },
      onError = { errorMessage = it }
    )

    assertFalse(completed)
    assertEquals("Inicia sesion antes de publicar.", errorMessage)
  }

  @Test
  fun submitSightingWithoutAuthentication_returnsValidationError() {
    val viewModel = PetViewModel(ApplicationProvider.getApplicationContext<Application>())
    var chatId = ""
    var errorMessage = ""

    viewModel.submitSightingAlert(
      postId = "post_1",
      petName = "Milo",
      photoUri = "content://photo",
      locationName = "Barrio Central",
      latitude = 0.0,
      longitude = 0.0,
      notes = "Posible avistamiento",
      ownerId = "owner_uid",
      mediaSource = MediaSource.GALLERY,
      locationSource = LocationSource.MANUAL_COARSE,
      onComplete = { chatId = it },
      onError = { errorMessage = it }
    )

    assertEquals("", chatId)
    assertEquals("Inicia sesion antes de reportar.", errorMessage)
  }
}
