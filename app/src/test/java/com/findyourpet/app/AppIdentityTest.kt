package com.findyourpet.app

import org.junit.Assert.assertEquals
import org.junit.Test

class AppIdentityTest {
  @Test
  fun packageName_matchesProjectNamespace() {
    assertEquals("com.findyourpet.app", BuildConfig.APPLICATION_ID)
  }
}
