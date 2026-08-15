package com.findyourpet.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.ui.screens.ActivityItem
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ActivityScreenComposeTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun activityItem_isInteractiveAndReadableInBothThemes() {
    var darkTheme by mutableStateOf(false)
    var selectedId: String? = null
    composeTestRule.setContent {
      MascotasPerdidasTheme(darkTheme = darkTheme) {
        Surface {
          ActivityItem(
            sighting = sampleSighting,
            petPost = null,
            onClick = { selectedId = sampleSighting.id },
          )
        }
      }
    }

    val item = composeTestRule.onNodeWithTag("activity-item-${sampleSighting.id}")
    item.assertIsDisplayed().assertHasClickAction().performClick()
    assertEquals(sampleSighting.id, selectedId)

    composeTestRule.runOnIdle { darkTheme = true }
    composeTestRule.waitForIdle()
    item.assertIsDisplayed().assertHasClickAction().performClick()
    assertEquals(sampleSighting.id, selectedId)
  }

  private companion object {
    val sampleSighting = SightingAlertEntity(
      id = "sighting_123",
      postId = "post_123",
      reporterId = "reporter_123",
      reporterName = "Reporter",
      photoUri = "",
      locationName = "Parque Central",
      latitude = -32.95,
      longitude = -60.66,
      notes = "",
      timestamp = 1L,
    )
  }
}
