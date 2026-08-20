package com.findyourpet.app

import com.findyourpet.app.data.auth.AuthFailure
import com.findyourpet.app.domain.AuthProvider
import com.findyourpet.app.domain.authProvider
import com.findyourpet.app.domain.emailPasswordConflict
import com.findyourpet.app.domain.googleConflict
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthProviderPolicyTest {
  @Test
  fun passwordProvider_requiresPasswordWhenGoogleIsAttempted() {
    assertTrue(listOf("password").authProvider() == AuthProvider.EMAIL_PASSWORD)
    assertTrue(listOf("password").authProvider().googleConflict() == AuthFailure.EmailPasswordRequired)
  }

  @Test
  fun googleProvider_requiresGoogleWhenEmailPasswordIsAttempted() {
    assertTrue(listOf("google.com").authProvider() == AuthProvider.GOOGLE)
    assertTrue(listOf("google.com").authProvider().emailPasswordConflict() == AuthFailure.GoogleRequired)
  }

  @Test
  fun emptyOrMixedProviderInformation_isInconclusive() {
    assertTrue(emptyList<String>().authProvider() == AuthProvider.UNKNOWN)
    assertTrue(listOf("password", "google.com").authProvider() == AuthProvider.UNKNOWN)
    assertFalse(emptyList<String>().authProvider().emailPasswordConflict() != null)
    assertFalse(emptyList<String>().authProvider().googleConflict() != null)
  }
}
