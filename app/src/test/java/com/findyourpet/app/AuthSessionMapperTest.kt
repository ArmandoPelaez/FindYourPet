package com.findyourpet.app

import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.data.auth.AuthUser
import com.findyourpet.app.data.profile.UserProfileDocument
import com.findyourpet.app.domain.AuthSessionMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthSessionMapperTest {
  @Test
  fun signedOutStateClearsAuthenticatedProfile() {
    val profile = AuthSessionMapper.activeUser(
      AuthUiState.SignedOut,
      UserProfileDocument(
        uid = "real_uid",
        displayName = "Saved User",
        email = "saved@example.com"
      )
    )

    assertEquals("", profile.id)
    assertEquals("", profile.email)
    assertFalse(AuthSessionMapper.isAuthenticated(AuthUiState.SignedOut))
  }

  @Test
  fun signedInStateUsesFirebaseUidAndProfileFields() {
    val profile = AuthSessionMapper.activeUser(
      AuthUiState.SignedIn(
        AuthUser(
          uid = "firebase_uid",
          displayName = "Auth Name",
          email = "auth@example.com"
        )
      ),
      UserProfileDocument(
        uid = "firebase_uid",
        displayName = "Profile Name",
        email = "profile@example.com"
      )
    )

    assertEquals("firebase_uid", profile.id)
    assertEquals("Profile Name", profile.name)
    assertEquals("profile@example.com", profile.email)
    assertTrue(
      AuthSessionMapper.isAuthenticated(
        AuthUiState.SignedIn(AuthUser(uid = "firebase_uid", displayName = "", email = ""))
      )
    )
  }
}
