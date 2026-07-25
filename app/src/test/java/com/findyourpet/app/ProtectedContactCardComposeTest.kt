package com.findyourpet.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.findyourpet.app.ui.components.ProtectedContactCard
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ProtectedContactCardComposeTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun protectedContactCard_hiddenStateMasksPhoneAndEmail() {
    renderContactCard(isContactRevealed = false)

    composeTestRule.onNodeWithText("Contacto oculto en esta demo").assertIsDisplayed()
    composeTestRule.onNodeWithText("Car*** (Protegido)").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("+506 8888-9900").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("carlos.ramirez@email.com").assertCountEquals(0)
  }

  @Test
  fun protectedContactCard_visibleStateShowsOwnerContact() {
    renderContactCard(isContactRevealed = true)

    composeTestRule.onNodeWithText("Datos de contacto visibles").assertIsDisplayed()
    composeTestRule.onNodeWithText("Carlos Ramirez").assertIsDisplayed()
    composeTestRule.onNodeWithText("+506 8888-9900").assertIsDisplayed()
    composeTestRule.onNodeWithText("carlos.ramirez@email.com").assertIsDisplayed()
  }

  private fun renderContactCard(isContactRevealed: Boolean) {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        ProtectedContactCard(
          ownerName = "Carlos Ramirez",
          ownerPhone = "+506 8888-9900",
          ownerEmail = "carlos.ramirez@email.com",
          isContactRevealed = isContactRevealed,
          onContactToggle = {},
          onStartChat = {}
        )
      }
    }
  }
}
