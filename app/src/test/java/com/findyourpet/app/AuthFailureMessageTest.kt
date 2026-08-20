package com.findyourpet.app

import com.findyourpet.app.data.auth.AuthDomainException
import com.findyourpet.app.data.auth.AuthFailure
import com.findyourpet.app.data.auth.AuthMessages
import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.ui.screens.shouldClearEmailPasswordAfterFallback
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthFailureMessageTest {
  @Test
  fun knownProviderConflicts_haveFunctionalMessages_withoutFirebaseDetails() {
    val passwordMessage = AuthMessages.forFailure(
      AuthDomainException(AuthFailure.EmailPasswordRequired, IllegalStateException("Firebase raw error"))
    )
    val googleMessage = AuthMessages.forFailure(
      AuthDomainException(AuthFailure.GoogleRequired, IllegalStateException("Firebase raw error"))
    )

    assertTrue(passwordMessage.contains("correo y contraseña"))
    assertTrue(passwordMessage.contains("contraseña"))
    assertTrue(googleMessage.contains("Google"))
    assertFalse(passwordMessage.contains("Firebase"))
    assertFalse(googleMessage.contains("raw error"))
  }

  @Test
  fun unknownFailure_hasRecoverableGenericMessage_andNeverUsesRawExceptionText() {
    val message = AuthMessages.forFailure(IllegalStateException("Firebase: secret details"))

    assertEquals(
      "No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.",
      message
    )
    assertEquals(AuthMessages.authenticationFailed, message)
    assertFalse(message.contains("secret details"))
  }

  @Test
  fun fieldsAreCleared_onlyForLoginEmailFallback_andMessageRemainsAvailable() {
    val fallbackState = AuthUiState.Error(AuthMessages.authenticationFailed)

    assertTrue(
      shouldClearEmailPasswordAfterFallback(
        isSignUp = false,
        isEmailAttempt = true,
        authState = fallbackState,
        authMessage = AuthMessages.authenticationFailed,
      )
    )
    assertTrue(
      shouldClearEmailPasswordAfterFallback(
        isSignUp = false,
        isEmailAttempt = true,
        authState = fallbackState,
        authMessage = null,
      )
    )
    assertFalse(
      shouldClearEmailPasswordAfterFallback(
        isSignUp = true,
        isEmailAttempt = true,
        authState = fallbackState,
        authMessage = AuthMessages.authenticationFailed,
      )
    )
    assertFalse(
      shouldClearEmailPasswordAfterFallback(
        isSignUp = false,
        isEmailAttempt = false,
        authState = fallbackState,
        authMessage = AuthMessages.authenticationFailed,
      )
    )
    assertFalse(
      shouldClearEmailPasswordAfterFallback(
        isSignUp = false,
        isEmailAttempt = true,
        authState = fallbackState,
        authMessage = AuthMessages.googleRequired,
      )
    )
    assertEquals(AuthMessages.authenticationFailed, fallbackState.message)
  }
}
