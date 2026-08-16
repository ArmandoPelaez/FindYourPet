package com.findyourpet.app

import com.findyourpet.app.data.profile.UserProfileDocument
import com.findyourpet.app.data.profile.UserProfileLoadError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileDocumentContractTest {
  @Test
  fun emptyConstructor_keepsFirestoreDefaultsForMissingOptionalFields() {
    val profile = UserProfileDocument()

    assertEquals("", profile.uid)
    assertEquals("", profile.displayName)
    assertEquals("", profile.email)
    assertEquals(0L, profile.createdAt)
    assertEquals(0L, profile.updatedAt)
  }

  @Test
  fun profileLoadFailure_usesControlledSpanishMessageWithoutRawException() {
    val rawFirestoreError = "Class qu4 does not define a no-argument constructor"

    assertEquals("No se pudo cargar tu perfil. Intenta nuevamente.", UserProfileLoadError.userMessage)
    assertTrue(UserProfileLoadError.userMessage !in rawFirestoreError)
    assertTrue(!UserProfileLoadError.userMessage.contains("qu4"))
    assertTrue(!UserProfileLoadError.userMessage.contains("constructor"))
  }
}
