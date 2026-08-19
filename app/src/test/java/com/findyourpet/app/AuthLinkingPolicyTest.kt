package com.findyourpet.app

import com.findyourpet.app.domain.shouldLinkPendingGoogleCredential
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthLinkingPolicyTest {
  @Test
  fun sameEmail_isEligibleForPendingGoogleLink_caseAndWhitespaceInsensitive() {
    assertTrue(
      shouldLinkPendingGoogleCredential(
        pendingEmail = " pelaezarmando@gmail.com ",
        signedInEmail = "PELAEZARMANDO@GMAIL.COM"
      )
    )
  }

  @Test
  fun differentEmail_doesNotConsumePendingGoogleLink() {
    assertFalse(
      shouldLinkPendingGoogleCredential(
        pendingEmail = "google@example.com",
        signedInEmail = "password@example.com"
      )
    )
  }

  @Test
  fun blankEmail_isNotEligibleForPendingGoogleLink() {
    assertFalse(shouldLinkPendingGoogleCredential("", "user@example.com"))
    assertFalse(shouldLinkPendingGoogleCredential("user@example.com", ""))
  }
}
