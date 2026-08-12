package com.findyourpet.app

import com.findyourpet.app.ui.screens.requiredPetNameMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CreatePetPostValidationTest {
  @Test
  fun requiredPetNameMessage_rejectsEmptyAndWhitespaceOnlyNames() {
    assertEquals("Campo obligatorio", requiredPetNameMessage(""))
    assertEquals("Campo obligatorio", requiredPetNameMessage("   \t"))
  }

  @Test
  fun requiredPetNameMessage_acceptsValidName() {
    assertNull(requiredPetNameMessage("Milo"))
  }
}
