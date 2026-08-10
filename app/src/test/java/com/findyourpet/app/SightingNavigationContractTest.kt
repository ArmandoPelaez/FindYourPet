package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SightingNavigationContractTest {
  @Test
  fun confirmationExposesSingleSubmissionLifecycleAndStableRetryKey() {
    val viewModel = source("app/src/main/java/com/findyourpet/app/ui/viewmodel/PetViewModel.kt")
    val screen = source("app/src/main/java/com/findyourpet/app/ui/screens/SightingAlertScreen.kt")

    assertTrue(viewModel.contains("SightingSubmissionStatus.SUBMITTING"))
    assertTrue(viewModel.contains("SightingSubmissionStatus.SUCCESS"))
    assertTrue(viewModel.contains("SightingSubmissionStatus.ERROR"))
    assertTrue(viewModel.contains("if (_sightingSubmissionState.value.status == SightingSubmissionStatus.SUBMITTING) return"))
    assertTrue(screen.contains("remember(postId) { UUID.randomUUID().toString() }"))
    assertTrue(screen.contains("idempotencyKey = idempotencyKey"))
    assertTrue(screen.contains("LaunchedEffect(submissionState.status)"))
    assertTrue(screen.contains("SightingSubmissionStatus.SUCCESS"))
  }

  @Test
  fun successfulAlertReturnsHomeAndErrorsStayOnConfirmationRoute() {
    val mainActivity = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")
    val screen = source("app/src/main/java/com/findyourpet/app/ui/screens/SightingAlertScreen.kt")

    assertTrue(mainActivity.contains("onAlertSent = {"))
    assertTrue(mainActivity.contains("navController.navigateToPrimaryDestination(ROUTE_HOME)"))
    assertTrue(mainActivity.contains("popBackStack(ROUTE_HOME, inclusive = false)"))
    assertTrue(screen.contains("finishAlert(chatId)"))
    assertTrue(screen.contains("status != com.findyourpet.app.ui.viewmodel.SightingSubmissionStatus.SUCCESS"))
    assertTrue(screen.contains("onError = { message ->"))
    assertTrue(screen.contains("formMessage = message"))
  }

  private fun source(relativePath: String): String = File(repoRoot(), relativePath).readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
