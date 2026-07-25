package com.findyourpet.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.findyourpet.app.ui.components.PetStatusChip
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class PetStatusChipScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun petStatusChip_lost_screenshot() {
    composeTestRule.setContent {
      MascotasPerdidasTheme {
        Surface {
          PetStatusChip(status = "PERDIDO", modifier = Modifier.padding(16.dp))
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/pet-status-chip-lost.png")
  }
}
