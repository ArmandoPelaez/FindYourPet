package com.findyourpet.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MainActivityRobolectricTest {
  @Test
  fun mainActivity_startsWithoutDeviceOrEmulator() {
    val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()

    assertEquals("com.findyourpet.app", activity.packageName)
    assertFalse(activity.isFinishing)

    controller.pause().stop().destroy()
  }
}
