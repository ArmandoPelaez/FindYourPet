package com.findyourpet.app

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.findyourpet.app.ui.components.PetStatusChip
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
class PetStatusChipComposeTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun petStatusChip_displaysKnownAndUnknownStatuses() {
    val statuses = listOf("PERDIDO", "AVISTADO", "REUNIDO", "EN_REVISION")

    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Column {
          statuses.forEach { status ->
            PetStatusChip(status = status)
          }
        }
      }
    }

    statuses.forEach { status ->
      composeTestRule.onNodeWithText(status).assertIsDisplayed()
    }
  }
}
